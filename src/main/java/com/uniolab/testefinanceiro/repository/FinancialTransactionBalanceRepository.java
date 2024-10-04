package com.uniolab.testefinanceiro.repository;

import com.uniolab.testefinanceiro.model.FinancialTransactionBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FinancialTransactionBalanceRepository extends JpaRepository<FinancialTransactionBalance, Long> {
}
