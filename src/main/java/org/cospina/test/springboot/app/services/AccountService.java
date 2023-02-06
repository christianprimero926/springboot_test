package org.cospina.test.springboot.app.services;

import org.cospina.test.springboot.app.models.Account;

import java.math.BigDecimal;

public interface AccountService {
    Account findById(Long id);
    int checkTotalTranfers(Long bankId);
    BigDecimal checkBalnce(Long accountId);
    void transfer(Long nSourceAccount, Long nTargetAccount, BigDecimal amount, Long bankId);
}
