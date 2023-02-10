package org.cospina.test.springboot.app.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cospina.test.springboot.app.models.Account;
import org.cospina.test.springboot.app.models.TransactionDTO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class AccountControllerWebTestClientTests {
    @Autowired
    private WebTestClient client;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Order(1)
    @Test
    void testTransfer() throws JsonProcessingException {
        // Given
        TransactionDTO dto = new TransactionDTO();
        dto.setSourceAccountId(1L);
        dto.setTargetAccountId(2L);
        dto.setBankId(1L);
        dto.setAmount(new BigDecimal("100"));

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "ok");
        response.put("message", "Transferencia realizada con exito");
        response.put("transaction", dto);

        // When
        client.post().uri("/api/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()

                // Then
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
//                .expectBody(String.class)
                .expectBody()
                .consumeWith(resp -> {
                    try {
//                        String jsonStr = resp.getResponseBody();
//                        JsonNode json = objectMapper.readTree(jsonStr);
                        JsonNode json = objectMapper.readTree(resp.getResponseBody());
                        assertEquals("Transferencia realizada con exito", json.path("message").asText());
                        assertEquals(1L, json.path("transaction").path("sourceAccountId").asLong());
                        assertEquals(LocalDate.now().toString(), json.path("date").asText());
                        assertEquals("100", json.path("transaction").path("amount").asText());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .jsonPath("$.message").isNotEmpty()
                .jsonPath("$.message").value(is("Transferencia realizada con exito"))
                .jsonPath("$.message").value(value -> assertEquals("Transferencia realizada con exito", value))
                .jsonPath("$.message").isEqualTo("Transferencia realizada con exito")
                .jsonPath("$.transaction.sourceAccountId").isEqualTo(dto.getSourceAccountId())
                .jsonPath("$.date").isEqualTo(LocalDate.now().toString())
                .json(objectMapper.writeValueAsString(response));
    }

    @Order(2)
    @Test
    void testDetail() throws JsonProcessingException {
        Account account = new Account(1L, "Andres", new BigDecimal("900"));

        client.get().uri("api/accounts/1").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.person").isEqualTo("Andres")
                .jsonPath("$.balance").isEqualTo(900)
                .json(objectMapper.writeValueAsString(account));
    }

    @Order(3)
    @Test
    void testDetail2() {
        client.get().uri("api/accounts/2").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Account.class)
                .consumeWith(resp -> {
                    Account account = resp.getResponseBody();
                    assertNotNull(account);
                    assertEquals("Jhon", account.getPerson());
                    assertEquals("2100.00", account.getBalance().toPlainString());
                })
        ;
    }

    @Order(4)
    @Test
    void testShowAll() {
        client.get().uri("api/accounts").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].person").isEqualTo("Andres")
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].balance").isEqualTo(900)
                .jsonPath("$[1].person").isEqualTo("Jhon")
                .jsonPath("$[1].id").isEqualTo(2)
                .jsonPath("$[1].balance").isEqualTo(2100)
                .jsonPath("$").isArray()
                .jsonPath("$").value(hasSize(2));
    }

    @Order(5)
    @Test
    void testShowAll2() {
        client.get().uri("api/accounts").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Account.class)
                .consumeWith(resp -> {
                    List<Account> accounts = resp.getResponseBody();
                    assertNotNull(accounts);
                    assertEquals(2, accounts.size());
                    assertEquals(1L, accounts.get(0).getId());
                    assertEquals("Andres", accounts.get(0).getPerson());
                    assertEquals(900, accounts.get(0).getBalance().intValue());
                    assertEquals(2L, accounts.get(1).getId());
                    assertEquals("Jhon", accounts.get(1).getPerson());
                    assertEquals(2100, accounts.get(1).getBalance().intValue());
                })
                .hasSize(2)
                .value(hasSize(2));
    }

    @Order(6)
    @Test
    void testSave() {
        // Given
        Account account = new Account(null, "Pepe", new BigDecimal("3000"));

        // When
        client.post().uri("api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(account)
                .exchange()
                // Then
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(3)
                .jsonPath("$.person").value(is("Pepe"))
                .jsonPath("$.balance").isEqualTo(3000);
    }

    @Order(7)
    @Test
    void testSave2() {
        // Given
        Account account = new Account(null, "Pepa", new BigDecimal("3500"));

        // When
        client.post().uri("api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(account)
                .exchange()
                // Then
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Account.class)
                .consumeWith(resp -> {
                    Account c = resp.getResponseBody();
                    assertNotNull(c);
                    assertEquals(4L, c.getId());
                    assertEquals("Pepa", c.getPerson());
                    assertEquals("3500", c.getBalance().toPlainString());
                });
    }

    @Order(8)
    @Test
    void testDelete() {
        client.get().uri("api/accounts").exchange()
                .expectStatus().isOk()
                .expectBodyList(Account.class)
                .hasSize(4);

        client.delete().uri("api/accounts/3")
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        client.get().uri("api/accounts").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Account.class)
                .hasSize(3);

        client.get().uri("api/accounts/3").exchange()
//                .expectStatus().is5xxServerError()
                .expectStatus().isNotFound()
                .expectBody().isEmpty();
    }
}