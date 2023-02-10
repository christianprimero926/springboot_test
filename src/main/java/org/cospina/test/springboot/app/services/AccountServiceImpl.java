package org.cospina.test.springboot.app.services;


import org.cospina.test.springboot.app.models.Account;
import org.cospina.test.springboot.app.models.Bank;
import org.cospina.test.springboot.app.repositories.AccountRepository;
import org.cospina.test.springboot.app.repositories.BankRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {
    private AccountRepository accountRepository;
    private BankRepository bankRepository;

    public AccountServiceImpl(AccountRepository accountRepository, BankRepository bankRepository) {
        this.accountRepository = accountRepository;
        this.bankRepository = bankRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Account findById(Long id) {
        return accountRepository.findById(id).orElseThrow();
    }

    @Override
    @Transactional
    public Account save(Account account) {
        return accountRepository.save(account);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        accountRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public int checkTotalTranfers(Long bankId) {
        Bank bank = bankRepository.findById(bankId).orElseThrow();
        return bank.getTotalTranfers();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal checkBalnce(Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow();
        return account.getBalance();
    }

    @Override
    @Transactional
    public void transfer(Long nSourceAccount, Long nTargetAccount, BigDecimal amount,
                         Long bankId) {
        Account sourceAccount = accountRepository.findById(nSourceAccount).orElseThrow();
        sourceAccount.debit(amount);
        accountRepository.save(sourceAccount);

        Account targerAccount = accountRepository.findById(nTargetAccount).orElseThrow();
        targerAccount.credit(amount);
        accountRepository.save(targerAccount);

        Bank bank = bankRepository.findById(bankId).orElseThrow();
        int totalTransfers = bank.getTotalTranfers();
        bank.setTotalTranfers(++totalTransfers);
        bankRepository.save(bank);
    }
}
