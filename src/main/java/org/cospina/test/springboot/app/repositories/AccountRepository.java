package org.cospina.test.springboot.app.repositories;

import org.cospina.test.springboot.app.models.Account;

import java.util.List;

public interface AccountRepository {
    List<Account> findAll();
    Account findById(Long id);
    void update(Account account);
}
