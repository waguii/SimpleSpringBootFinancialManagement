package com.uniolab.testefinanceiro.service;

import com.uniolab.testefinanceiro.model.FinancialAccount;
import com.uniolab.testefinanceiro.repository.FinancialAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FinancialAccountService {

    private final FinancialAccountRepository financialAccountRepository;

    public FinancialAccount create(FinancialAccount financialAccount) {
        return financialAccountRepository.save(financialAccount);
    }
}
