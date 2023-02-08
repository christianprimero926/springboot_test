package org.cospina.test.springboot.app;

import org.cospina.test.springboot.app.models.Account;
import org.cospina.test.springboot.app.repositories.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class IntegrationJpaTest {
    @Autowired
    AccountRepository accountRepository;

    @Test
    void testFindById() {
        Optional<Account> account = accountRepository.findById(1L);
        assertTrue(account.isPresent());
        assertEquals("Andres", account.orElseThrow().getPerson());
    }

    @Test
    void testFindByPerson() {
        Optional<Account> account = accountRepository.findByPerson("Andres");
        assertTrue(account.isPresent());
        assertEquals("Andres", account.orElseThrow().getPerson());
        assertEquals("1000.00", account.orElseThrow().getBalance().toPlainString());
    }

    @Test
    void testFindByPersonThrowException() {
        Optional<Account> account = accountRepository.findByPerson("Rod");
        assertThrows(NoSuchElementException.class, account::orElseThrow);
        assertFalse(account.isPresent());
    }

    @Test
    void testFindAll() {
        List<Account> accounts = accountRepository.findAll();
        assertFalse(accounts.isEmpty());
        assertEquals(2, accounts.size());
    }

    @Test
    void testSave() {
        // Given
        Account accountPepe = new Account(null, "Pepe", new BigDecimal("3000"));

        // When
        Account account = accountRepository.save(accountPepe);
        // Account account = accountRepository.findByPerson("Pepe").orElseThrow();
        // Account account = accountRepository.findById(save.getId()).orElseThrow();

        // Then
        assertEquals("Pepe", account.getPerson());
        assertEquals("3000", account.getBalance().toPlainString());
        // assertEquals(3, account.getId());
    }
}
