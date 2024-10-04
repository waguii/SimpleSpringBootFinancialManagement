package com.uniolab.testefinanceiro.repository;

import com.uniolab.testefinanceiro.model.FinancialAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FinancialAccountRepository extends JpaRepository<FinancialAccount, Long> {
    Optional<FinancialAccount> findByName(String financialAccountName);
}
