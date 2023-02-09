package org.cospina.test.springboot.app.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cospina.test.springboot.app.models.Account;
import org.cospina.test.springboot.app.models.TransactionDTO;
import org.cospina.test.springboot.app.services.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.cospina.test.springboot.app.Data.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private AccountService accountService;

    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testDetail() throws Exception {
        // Given
        when(accountService.findById(1L)).thenReturn(createAccount001().orElseThrow());

        // When
        mvc.perform(get("/api/accounts/1").contentType(MediaType.APPLICATION_JSON))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.person").value("Andres"))
                .andExpect(jsonPath("$.balance").value("1000"));

        verify(accountService).findById(1L);
    }

    @Test
    void testTransfer() throws Exception {
        // Given
        TransactionDTO dto = new TransactionDTO();
        dto.setSourceAccountId(1L);
        dto.setTargetAccountId(2l);
        dto.setAmount(new BigDecimal("100"));
        dto.setBankId(1L);

        System.out.println(objectMapper.writeValueAsString(dto));

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "ok");
        response.put("message", "Transferencia realizada con exito");
        response.put("transaction", dto);

        System.out.println(objectMapper.writeValueAsString(response));

        //When
        mvc.perform(post("/api/accounts/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))

                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.date").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.message").value("Transferencia realizada con exito"))
                .andExpect(jsonPath("$.transaction.sourceAccountId").value(dto.getSourceAccountId()))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

    }

    @Test
    void testShowAll() throws Exception {
        List<Account> accounts = Arrays.asList(createAccount001().orElseThrow(),
                createAccount002().orElseThrow()
        );
        when(accountService.findAll()).thenReturn(accounts);

        // When
        mvc.perform(get("/api/accounts").contentType(MediaType.APPLICATION_JSON))

                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].person").value("Andres"))
                .andExpect(jsonPath("$[1].person").value("Jhon"))
                .andExpect(jsonPath("$[0].balance").value("1000"))
                .andExpect(jsonPath("$[1].balance").value("2000"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().json(objectMapper.writeValueAsString(accounts)));
    }
}