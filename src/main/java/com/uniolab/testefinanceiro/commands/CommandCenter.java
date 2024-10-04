package com.uniolab.testefinanceiro.commands;

import com.uniolab.testefinanceiro.enums.FinancialTransactionType;
import com.uniolab.testefinanceiro.model.FinancialAccount;
import com.uniolab.testefinanceiro.repository.FinancialAccountRepository;
import com.uniolab.testefinanceiro.repository.FinancialTransactionRepository;
import com.uniolab.testefinanceiro.service.FinancialAccountService;
import com.uniolab.testefinanceiro.service.FinancialManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ShellComponent
@RequiredArgsConstructor
@Slf4j
public class CommandCenter {

    private final FinancialAccountRepository financialAccountRepository;
    private final FinancialTransactionRepository financialTransactionRepository;
    private final FinancialAccountService financialAccountService;
    private final FinancialManager financialManager;

    @ShellMethod(key = "init-bd")
    public void initializeDatabase() {
        if (financialAccountRepository.count() > 0 || financialTransactionRepository.count() > 0) {
            log.info("Database already initialized");
            return;
        }

        //create financial account
        FinancialAccount financialAccount = new FinancialAccount();

        financialAccount.setName("CAIXA");

        financialAccountService.create(financialAccount);

        log.info("Database initialized");
    }

    @ShellMethod(key = "add-financial-entry")
    public void add(@ShellOption String financialAccountName,
                    @ShellOption FinancialTransactionType type,
                    @ShellOption String date,
                    @ShellOption BigDecimal value) {

        //transforms date string into a date object
        //format dd/MM/yyyy HH:mm:ss
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(date, formatter);

        FinancialAccount financialAccount = financialAccountRepository
                .findByName(financialAccountName).orElseThrow(() -> new IllegalArgumentException("Account not found"));

        if (type.equals(FinancialTransactionType.OUT)) {
            financialManager.createOutTransaction(localDateTime, value, financialAccount);
        }else{
            financialManager.createInTransaction(localDateTime , value, financialAccount);
        }
    }

    @ShellMethod(key = "del-financial-entry")
    public void delete(@ShellOption Long id) {
        financialManager.deleteTransaction(id);
    }

    @ShellMethod(key = "balance-financial-account")
    public void balance(@ShellOption String financialAccountName,
                        @ShellOption String date,
                        @ShellOption BigDecimal value) {

        //transforms date string into a date object
        //format dd/MM/yyyy HH:mm:ss
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(date, formatter);

        FinancialAccount financialAccount = financialAccountRepository
                .findByName(financialAccountName).orElseThrow(() -> new IllegalArgumentException("Account not found"));

        financialManager.createBalanceTransaction(localDateTime, value, financialAccount);
    }


}
