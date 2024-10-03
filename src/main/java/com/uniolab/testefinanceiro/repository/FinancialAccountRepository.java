package com.uniolab.testefinanceiro.repository;

import com.uniolab.testefinanceiro.enums.FinancialEntryType;
import com.uniolab.testefinanceiro.model.FinancialAccount;
import com.uniolab.testefinanceiro.model.FinancialEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Repository
public interface FinancialAccountRepository extends JpaRepository<FinancialAccount, Long> {
    Optional<FinancialAccount> findByName(String financialAccountName);

    @Query("SELECT e FROM FinancialEntry e WHERE e.financialAccount = :financialAccount AND e.date >= :date AND e.type = :type")
    Set<FinancialEntry> findByFinancialAccountAndDateAfterEqualAndType(FinancialAccount financialAccount, LocalDateTime date, FinancialEntryType type);

    @Query("SELECT e FROM FinancialEntry e WHERE e.financialAccount = :financialAccount AND e.date > :date AND e.type = :type")
    Set<FinancialEntry> findByFinancialAccountAndDateAfterAndType(FinancialAccount financialAccount, LocalDateTime date, FinancialEntryType type);

    @Query("SELECT e FROM FinancialEntry e " +
            "WHERE e.financialAccount = :financialAccount AND e.date >= :date " +
            "AND e.registrationDate > :registrationDate AND e.type = :financialEntryType")
    Set<FinancialEntry> findNextEntriesByFinancialAccountAndDatesAndType(
            FinancialAccount financialAccount,
            LocalDateTime date,
            LocalDateTime registrationDate,
            FinancialEntryType financialEntryType);
}
