package org.cospina.test.springboot.app.services;

import org.cospina.test.springboot.app.models.Account;
import org.cospina.test.springboot.app.models.Bank;
import org.cospina.test.springboot.app.repositories.AccountRepository;
import org.cospina.test.springboot.app.repositories.BankRepository;

import java.math.BigDecimal;

public class AccountServiceImpl implements  AccountService{
    private AccountRepository accountRepository;
    private BankRepository bankRepository;

    public AccountServiceImpl(AccountRepository accountRepository, BankRepository bankRepository) {
        this.accountRepository = accountRepository;
        this.bankRepository = bankRepository;
    }

    @Override
    public Account findById(Long id) {
        return accountRepository.findById(id);
    }

    @Override
    public int checkTotalTranfers(Long bankId) {
        Bank bank = bankRepository.findById(bankId);
        return bank.getTotalTranfers();
    }

    @Override
    public BigDecimal checkBalnce(Long accountId) {
        Account account = accountRepository.findById(accountId);
        return account.getBalance();
    }

    @Override
    public void transfer(Long nSourceAccount, Long nTargetAccount, BigDecimal amount,
                         Long bankId) {
        Account sourceAccount = accountRepository.findById(nSourceAccount);
        sourceAccount.debit(amount);
        accountRepository.update(sourceAccount);

        Account targerAccount = accountRepository.findById(nTargetAccount);
        targerAccount.credit(amount);
        accountRepository.update(targerAccount);

        Bank bank = bankRepository.findById(bankId);
        int totalTransfers = bank.getTotalTranfers();
        bank.setTotalTranfers(++totalTransfers);
        bankRepository.update(bank);
    }
}
