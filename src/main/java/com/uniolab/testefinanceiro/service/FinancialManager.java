package com.uniolab.testefinanceiro.service;

import com.uniolab.testefinanceiro.enums.FinancialEntryType;
import com.uniolab.testefinanceiro.model.FinancialAccount;
import com.uniolab.testefinanceiro.model.FinancialEntry;
import com.uniolab.testefinanceiro.repository.FinancialAccountRepository;
import com.uniolab.testefinanceiro.repository.FinancialEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class FinancialManager {

    private final FinancialEntryRepository financialEntryRepository;
    private final FinancialAccountRepository financialAccountRepository;

    public FinancialEntry createEntry(FinancialEntryType type, LocalDateTime date, BigDecimal value, FinancialAccount financialAccount) {

        FinancialEntry financialEntry = new FinancialEntry();

        financialEntry.setDate(date);
        financialEntry.setType(type);
        financialEntry.setDescription("TESTE");
        financialEntry.setValue(value);
        financialEntry.setFinancialAccount(financialAccount);
        financialEntry.setRegistrationDate(LocalDateTime.now());

        FinancialEntry result = financialEntryRepository.save(financialEntry);

        log.info("Financial entry created: {}", result);

        //update financial account balance
        Set<FinancialEntry> balances = financialAccountRepository
                .findByFinancialAccountAndDateAfterEqualAndType(financialAccount, date, FinancialEntryType.BALANCE);

        if (balances.isEmpty()) {
            updateFinancialAccountBalance(financialAccount, value, type);
        }

        return result;
    }

    public FinancialEntry balance(LocalDateTime date, BigDecimal value, FinancialAccount financialAccount) {

        LocalDateTime balanceDate = date.toLocalDate().atStartOfDay();

        FinancialEntry financialEntry = new FinancialEntry();

        financialEntry.setDate(balanceDate);
        financialEntry.setType(FinancialEntryType.BALANCE);
        financialEntry.setDescription("BALANCO");
        financialEntry.setValue(value);
        financialEntry.setFinancialAccount(financialAccount);
        financialEntry.setRegistrationDate(LocalDateTime.now());

        FinancialEntry result = financialEntryRepository.save(financialEntry);
        log.info("Financial entry created: {}", result);

        //update financial account balance
        Set<FinancialEntry> balances = financialAccountRepository
                .findByFinancialAccountAndDateAfterAndType(financialAccount, balanceDate, FinancialEntryType.BALANCE);

        if (balances.isEmpty()) {
            setFinancialAccountBalance(financialAccount, value);
        }

        return result;
    }

//    public BigDecimal calculateCurrentBalance(String accountId) {
//        FinancialAccount account = accountRepository.findById(accountId)
//                .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada"));
//
//        // Obtém o último balanço registrado para a conta
//        Balance lastBalance = balanceRepository.findFirstByAccountOrderByDateDesc(account)
//                .orElse(new Balance(BigDecimal.ZERO, LocalDate.now().minusDays(1), account));
//
//        // Calcula o saldo somando as entradas e subtraindo as saídas após o último balanço
//        BigDecimal currentBalance = lastBalance.getValue();
//        List<FinancialEntry> entriesAfterLastBalance = entryRepository.findByAccountAndDateAfter(account, lastBalance.getDate());
//
//        for (FinancialEntry entry : entriesAfterLastBalance) {
//            if (entry.getValue().compareTo(BigDecimal.ZERO) > 0) {
//                currentBalance = currentBalance.add(entry.getValue());
//            } else {
//                currentBalance = currentBalance.subtract(entry.getValue().abs());
//            }
//        }
//
//        return currentBalance;
//    }

    private void setFinancialAccountBalance(FinancialAccount financialAccount, BigDecimal value) {
        financialAccount.setBalance(value);
        financialAccountRepository.save(financialAccount);
        log.info("Financial account balance updated: {}", financialAccount);
    }

    private void updateFinancialAccountBalance(FinancialAccount financialAccount, BigDecimal value, FinancialEntryType type) {
        BigDecimal balance = financialAccount.getBalance();
        balance = type == FinancialEntryType.IN ? balance.add(value) : balance.subtract(value);
        financialAccount.setBalance(balance);
        financialAccountRepository.save(financialAccount);
        log.info("Financial account balance updated: {}", financialAccount);
    }

    public void deleteEntry(Long id) {

        FinancialEntry financialEntry = financialEntryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Financial entry not found"));

        Set<FinancialEntry> futureBalances = null;
        if (financialEntry.getType() == FinancialEntryType.BALANCE) {
            futureBalances = financialAccountRepository
                    .findNextEntriesByFinancialAccountAndDatesAndType(
                            financialEntry.getFinancialAccount(),
                            financialEntry.getDate(),
                            financialEntry.getRegistrationDate(),
                            FinancialEntryType.BALANCE);
        } else {
            //fazer a query levando em considera;áo que eh uma entrada normal in ou out
            //update financial account balance
            futureBalances = financialAccountRepository
                    .findByFinancialAccountAndDateAfterEqualAndType(
                            financialEntry.getFinancialAccount(),
                            financialEntry.getDate(),
                            FinancialEntryType.BALANCE);
        }

        //TODO: colocar a remoção aqui
        financialEntryRepository.deleteById(id);

        if (futureBalances.isEmpty()) {
            //teoricamente nao precisa desse id aqui pq essa entrada ja foi removida no deleteById que ta comentado acima
            FinancialEntry lastBalance = financialEntryRepository
                    .findLastBalance(financialEntry.getFinancialAccount().getId(),
                            financialEntry.getDate(),
                            financialEntry.getId()).orElse(null);

            LocalDateTime sourceDate = lastBalance != null ? lastBalance.getDate() : financialEntry.getDate();

            Set<FinancialEntry> nextEntries = financialEntryRepository
                    .findInOutAccountEntriesAfter(financialEntry.getFinancialAccount().getId(), sourceDate);

            log.info("Next entries: {}", nextEntries);

            BigDecimal updatedBalance = lastBalance != null ? lastBalance.getValue() : BigDecimal.ZERO;
            for (FinancialEntry nextEntry : nextEntries) {
                BigDecimal nextValue = nextEntry.getValue();
                FinancialEntryType nextType = nextEntry.getType();
                updatedBalance = nextType == FinancialEntryType.IN ? updatedBalance.add(nextValue) : updatedBalance.subtract(nextValue);
            }

            setFinancialAccountBalance(financialEntry.getFinancialAccount(), updatedBalance);
        }

    }
}
