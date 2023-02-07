package org.cospina.test.springboot.app;

import org.cospina.test.springboot.app.exceptions.InsufficientMoneyException;
import org.cospina.test.springboot.app.models.Account;
import org.cospina.test.springboot.app.models.Bank;
import org.cospina.test.springboot.app.repositories.AccountRepository;
import org.cospina.test.springboot.app.repositories.BankRepository;
import org.cospina.test.springboot.app.services.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Objects;

import static org.cospina.test.springboot.app.Data.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class SpringbootTestApplicationTests {
    @MockBean
    AccountRepository accountRepository;
    @MockBean
    BankRepository bankRepository;
    @Autowired
    AccountService service;

    @BeforeEach
    void setUp() {
//        accountRepository = mock(AccountRepository.class);
//        bankRepository = mock(BankRepository.class);
//        service = new AccountServiceImpl(accountRepository, bankRepository);
//        Data.ACCOUNT_001.setBalance(new BigDecimal("1000"));
//        Data.ACCOUNT_002.setBalance(new BigDecimal("2000"));
//        Data.BANK.setTotalTranfers(0);
    }

    @Test
    void contextLoads() {
        when(accountRepository.findById(1L)).thenReturn(createAccount001());
        when(accountRepository.findById(2L)).thenReturn(createAccount002());
        when(bankRepository.findById(1L)).thenReturn(createBank());

        BigDecimal sourceBalance = service.checkBalnce(1L);
        BigDecimal targetBalance = service.checkBalnce(2L);
        assertEquals("1000", sourceBalance.toPlainString());
        assertEquals("2000", targetBalance.toPlainString());

        service.transfer(1L, 2L, new BigDecimal(100), 1L);

        sourceBalance = service.checkBalnce(1L);
        targetBalance = service.checkBalnce(2L);

        assertEquals("900", sourceBalance.toPlainString());
        assertEquals("2100", targetBalance.toPlainString());

        int total = service.checkTotalTranfers(1L);
        assertEquals(1, total);
        verify(accountRepository, times(3)).findById(1L);
        verify(accountRepository, times(3)).findById(2L);
        verify(accountRepository, times(2)).save(any(Account.class));

        verify(bankRepository, times(2)).findById(1L);
        verify(bankRepository).update(any(Bank.class));

        verify(accountRepository, times(6)).findById(anyLong());
        verify(accountRepository, never()).findAll();
    }

    @Test
    void contextLoads2() {
        when(accountRepository.findById(1L)).thenReturn(createAccount001());
        when(accountRepository.findById(2L)).thenReturn(createAccount002());
        when(bankRepository.findById(1L)).thenReturn(createBank());

        BigDecimal sourceBalance = service.checkBalnce(1L);
        BigDecimal targetBalance = service.checkBalnce(2L);
        assertEquals("1000", sourceBalance.toPlainString());
        assertEquals("2000", targetBalance.toPlainString());

        assertThrows(InsufficientMoneyException.class, () -> {
            service.transfer(1L, 2L, new BigDecimal(1200), 1L);
        });

        sourceBalance = service.checkBalnce(1L);
        targetBalance = service.checkBalnce(2L);

        assertEquals("1000", sourceBalance.toPlainString());
        assertEquals("2000", targetBalance.toPlainString());

        int total = service.checkTotalTranfers(1L);
        assertEquals(0, total);
        verify(accountRepository, times(3)).findById(1L);
        verify(accountRepository, times(2)).findById(2L);
        verify(accountRepository, never()).save(any(Account.class));

        verify(bankRepository).findById(1L);
        verify(bankRepository, never()).update(any(Bank.class));

        verify(accountRepository, never()).findAll();
        verify(accountRepository, times(5)).findById(anyLong());
    }

    @Test
    void contextLoads3() {
        when(accountRepository.findById(1L)).thenReturn(createAccount001());

        Account account1 = service.findById(1L);
        Account account2 = service.findById(1L);

        assertSame(account1, account2);

        assertTrue(Objects.equals(account1, account2));
        assertTrue(account1 == account2);
        assertEquals("Andres", account1.getPerson());
        assertEquals("Andres", account2.getPerson());

        verify(accountRepository, times(2)).findById(1L);
    }
}
