package com.uniolab.testefinanceiro.service;

import com.uniolab.testefinanceiro.enums.FinancialTransactionType;
import com.uniolab.testefinanceiro.model.FinancialAccount;
import com.uniolab.testefinanceiro.model.FinancialTransaction;
import com.uniolab.testefinanceiro.model.FinancialTransactionBalance;
import com.uniolab.testefinanceiro.repository.FinancialTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class FinancialManager {

    private final FinancialTransactionRepository financialTransactionRepository;

    public FinancialTransaction createInTransaction(LocalDateTime date, BigDecimal value, FinancialAccount financialAccount) {
        return createTransaction(FinancialTransactionType.IN, date, value, financialAccount);
    }

    public FinancialTransaction createOutTransaction(LocalDateTime date, BigDecimal value, FinancialAccount financialAccount) {
        return createTransaction(FinancialTransactionType.OUT, date, value, financialAccount);
    }

    public FinancialTransaction createBalanceTransaction(LocalDateTime date, BigDecimal value, FinancialAccount financialAccount) {

        LocalDateTime balanceDate = date.toLocalDate().atStartOfDay();

        //check if there is a balance transaction for the same day
        Set<FinancialTransaction> balancesOfDay = financialTransactionRepository
                .findByFinancialAccountAndDateEqualAndType(financialAccount.getId(), balanceDate, FinancialTransactionType.BALANCE);

        BigDecimal lastBalance = balancesOfDay.isEmpty() ? BigDecimal.ZERO : balancesOfDay.iterator().next().getValue();

        //create the balance transaction
        FinancialTransaction transaction = new FinancialTransaction();
        transaction.setDate(balanceDate);
        transaction.setType(FinancialTransactionType.BALANCE);
        transaction.setValue(value);
        transaction.setFinancialAccount(financialAccount);
        transaction.setRegistrationDate(LocalDateTime.now());

        //create the balance
        FinancialTransactionBalance financialTransactionBalance = new FinancialTransactionBalance();

        financialTransactionBalance.setTransaction(transaction);
        financialTransactionBalance.setRegistrationDate(LocalDateTime.now());
        financialTransactionBalance.setValue(lastBalance.add(value));
        financialTransactionBalance.setValueChange(value);

        transaction.setBalance(financialTransactionBalance);

        FinancialTransaction result = financialTransactionRepository.save(transaction);

        log.info("Financial transaction created: {}", result);

        //we need to update the following transactions balance
        updateFollowingTransactionsBalance(result);

        return result;
    }

    public void deleteTransaction(Long id) {
        FinancialTransaction financialTransaction = financialTransactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Financial transaction not found"));

        financialTransactionRepository.deleteById(id);
        updateFollowingTransactionsBalance(financialTransaction);
    }

    public FinancialTransaction updateTransaction(Long id, FinancialAccount financialAccount, LocalDateTime date , BigDecimal value) {

        FinancialTransaction financialTransaction = financialTransactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Financial transaction not found"));

        deleteTransaction(id);

        if (financialTransaction.getType().equals(FinancialTransactionType.BALANCE)) {
            return createBalanceTransaction(date, value, financialAccount);
        } else{
            return createTransaction(financialTransaction.getType(), date, value, financialAccount);
        }
    }

    private FinancialTransaction createTransaction(FinancialTransactionType type,
                                                   LocalDateTime date,
                                                   BigDecimal value,
                                                   FinancialAccount financialAccount) {
        FinancialTransaction transaction = new FinancialTransaction();

        transaction.setDate(date);
        transaction.setType(type);
        transaction.setValue(value);
        transaction.setFinancialAccount(financialAccount);
        transaction.setRegistrationDate(LocalDateTime.now());

        //precisa encontrar a ultima transacao realizada antes dessa para ter acesso ao balanco
        FinancialTransaction previousTransaction = financialTransactionRepository
                .findPreviousTransactionByDateAndFinancialAccount(transaction.getDate(), transaction.getFinancialAccount().getId())
                .orElse(null);

        BigDecimal lastBalance = previousTransaction != null ? previousTransaction.getBalance().getValue() : BigDecimal.ZERO;

        //cria o balanco da transacao
        FinancialTransactionBalance financialTransactionBalance = new FinancialTransactionBalance();

        BigDecimal newBalance = type == FinancialTransactionType.IN ? lastBalance.add(value) : lastBalance.subtract(value);
        BigDecimal change = type == FinancialTransactionType.IN ? value : value.negate();

        financialTransactionBalance.setTransaction(transaction);
        financialTransactionBalance.setRegistrationDate(LocalDateTime.now());
        financialTransactionBalance.setValue(newBalance);
        financialTransactionBalance.setValueChange(change);

        transaction.setBalance(financialTransactionBalance);

        FinancialTransaction result = financialTransactionRepository.save(transaction);

        log.info("Financial transaction created: {}", result);

        //we need to update the following transactions balance
        updateFollowingTransactionsBalance(result);

        return result;
    }

    private void updateFollowingTransactionsBalance(FinancialTransaction baseTransaction) {
        //find all transactions after the current one
        Set<FinancialTransaction> futuresTransactions = financialTransactionRepository
                .findNextTransactionsByDateAndFinancialAccount(
                        baseTransaction.getDate(),
                        baseTransaction.getFinancialAccount().getId(),
                        baseTransaction.getId());

        BigDecimal currentBalance = baseTransaction.getBalance().getValue();

        for (FinancialTransaction futureTransaction : futuresTransactions) {
            if (futureTransaction.getType().equals(FinancialTransactionType.BALANCE)) break;

            BigDecimal newBalance = futureTransaction.getType() == FinancialTransactionType.IN
                    ? currentBalance.add(futureTransaction.getValue())
                    : currentBalance.subtract(futureTransaction.getValue());

            futureTransaction.getBalance().setValue(newBalance);
            financialTransactionRepository.save(futureTransaction);
            currentBalance = newBalance;
        }

    }

}
