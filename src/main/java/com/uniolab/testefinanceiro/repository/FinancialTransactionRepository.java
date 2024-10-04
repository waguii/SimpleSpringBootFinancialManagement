package com.uniolab.testefinanceiro.repository;

import com.uniolab.testefinanceiro.enums.FinancialTransactionType;
import com.uniolab.testefinanceiro.model.FinancialAccount;
import com.uniolab.testefinanceiro.model.FinancialTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Repository
public interface FinancialTransactionRepository extends JpaRepository<FinancialTransaction, Long> {

    @Query("SELECT t FROM FinancialTransaction t " +
            "WHERE t.financialAccount.id = :financialAccountId AND t.date <= :date " +
            "ORDER BY t.date DESC, t.registrationDate DESC " +
            "LIMIT 1")
    Optional<FinancialTransaction> findPreviousTransactionByDateAndFinancialAccount(LocalDateTime date, Long financialAccountId);

    @Query("SELECT t FROM FinancialTransaction t " +
            "WHERE t.financialAccount.id = :financialAccountId AND t.date > :date " +
            "AND t.id != :sourceTransactionId " +
            "ORDER BY t.date ASC, t.registrationDate ASC")
    Set<FinancialTransaction> findNextTransactionsByDateAndFinancialAccount(LocalDateTime date, Long financialAccountId, Long sourceTransactionId);

    @Query("SELECT t FROM FinancialTransaction t " +
            "WHERE t.financialAccount.id = :financialAccountId AND t.date = :balanceDate " +
            "AND t.type = :financialTransactionType " +
            "ORDER BY t.date DESC, t.registrationDate DESC")
    Set<FinancialTransaction> findByFinancialAccountAndDateEqualAndType(Long financialAccountId, LocalDateTime balanceDate, FinancialTransactionType financialTransactionType);
}
