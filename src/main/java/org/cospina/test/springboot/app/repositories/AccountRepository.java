package org.cospina.test.springboot.app.repositories;

import org.cospina.test.springboot.app.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
//    List<Account> findAll();
//    Account findById(Long id);
//    void update(Account account);
}
