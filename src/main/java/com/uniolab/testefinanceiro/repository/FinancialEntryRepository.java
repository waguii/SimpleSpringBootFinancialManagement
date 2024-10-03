package com.uniolab.testefinanceiro.repository;

import com.uniolab.testefinanceiro.model.FinancialEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Repository
public interface FinancialEntryRepository extends JpaRepository<FinancialEntry, Long> {
    @Query("SELECT fe FROM FinancialEntry fe " +
            "WHERE fe.financialAccount.id = :financialAccountId " +
            "AND fe.date <= :date AND fe.type = 'BALANCE' " +
            "AND fe.id != :entryId " +
            "ORDER BY fe.date DESC, fe.registrationDate DESC " +
            "LIMIT 1")
    Optional<FinancialEntry> findLastBalance(Long financialAccountId, LocalDateTime date, Long entryId);

    @Query("SELECT fe FROM FinancialEntry fe " +
            "WHERE fe.financialAccount.id = :financialAccountId " +
            "AND fe.date >= :date AND fe.type != 'BALANCE' "+
            "ORDER BY fe.date ASC, fe.registrationDate ASC")
    Set<FinancialEntry> findInOutAccountEntriesAfter(Long financialAccountId, LocalDateTime date);
}
