package org.cospina.test.springboot.app.services;

import org.cospina.test.springboot.app.models.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {
    List<Account> findAll();
    Account findById(Long id);
    Account save(Account account);
    int checkTotalTranfers(Long bankId);
    BigDecimal checkBalnce(Long accountId);
    void transfer(Long nSourceAccount, Long nTargetAccount, BigDecimal amount, Long bankId);
}
