package org.cospina.test.springboot.app;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.cospina.test.springboot.app.repositories.AccountRepository;
import org.cospina.test.springboot.app.repositories.BankRepository;
import org.cospina.test.springboot.app.services.AccountService;
import org.cospina.test.springboot.app.services.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest
class SpringbootTestApplicationTests {
    AccountRepository accountRepository;
    BankRepository bankRepository;
    AccountService service;

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepository.class);
        bankRepository = mock(BankRepository.class);
        service = new AccountServiceImpl(accountRepository, bankRepository);
    }

    @Test
    void contextLoads() {
        when(accountRepository.findById(1L)).thenReturn(Data.ACCOUNT_001);
        when(accountRepository.findById(2L)).thenReturn(Data.ACCOUNT_002);
        when(bankRepository.findById(1L)).thenReturn(Data.BANK);

        BigDecimal sourceBalance = service.checkBalnce(1L);
        BigDecimal targetBalance = service.checkBalnce(2L);
        assertEquals("1000", sourceBalance.toPlainString());
        assertEquals("2000", targetBalance.toPlainString());

        service.transfer(1L, 2L, new BigDecimal(100), 1L);

        sourceBalance = service.checkBalnce(1L);
        targetBalance = service.checkBalnce(2L);

        assertEquals("900", sourceBalance.toPlainString());
        assertEquals("2100", targetBalance.toPlainString());

        
    }

}
