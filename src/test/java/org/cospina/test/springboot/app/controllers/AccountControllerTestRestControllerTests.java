package org.cospina.test.springboot.app.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cospina.test.springboot.app.models.Account;
import org.cospina.test.springboot.app.models.TransactionDTO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration_rt")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountControllerTestRestControllerTests {
    @Autowired
    private TestRestTemplate client;
    private ObjectMapper objectMapper;
    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @Order(1)
    void testTransfer() throws JsonProcessingException {
        // Given
        TransactionDTO dto = new TransactionDTO();
        dto.setAmount(new BigDecimal("100"));
        dto.setSourceAccountId(1L);
        dto.setTargetAccountId(2L);
        dto.setBankId(1L);

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "ok");
        response.put("message", "Transferencia realizada con exito");
        response.put("transaction", dto);

        // When
        ResponseEntity<String> responseEntity = client.
                postForEntity(createUri("/api/accounts/transfer"), dto, String.class);

        System.out.println(port);
        String json = responseEntity.getBody();
        System.out.println(json);

        // Then
        assertNotNull(json);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        assertTrue(json.contains("Transferencia realizada con exito"));

        JsonNode jsonNode = objectMapper.readTree(json);
        assertEquals("Transferencia realizada con exito", jsonNode.path("message").asText());
        assertEquals(LocalDate.now().toString(), jsonNode.path("date").asText());
        assertEquals("100", jsonNode.path("transaction").path("amount").asText());
        assertEquals(1L, jsonNode.path("transaction").path("sourceAccountId").asLong());

        assertEquals(objectMapper.writeValueAsString(response), json);


    }

    @Test
    @Order(2)
    void testDetail() {
        ResponseEntity<Account> responseEntity = client.getForEntity(createUri("/api/accounts/1"), Account.class);
        Account account = responseEntity.getBody();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());

        assertNotNull(account);
        assertEquals(1L, account.getId());
        assertEquals("Andres", account.getPerson());
        assertEquals("900.00", account.getBalance().toPlainString());
        assertEquals(new Account(1L, "Andres", new BigDecimal("900.00")), account);
    }

    @Test
    @Order(3)
    void testShowAll() throws JsonProcessingException {
        ResponseEntity<Account[]> responseEntity = client.getForEntity(createUri("/api/accounts"), Account[].class);
        List<Account> accounts = Arrays.asList(responseEntity.getBody());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());

        assertEquals(2, accounts.size());
        assertEquals("Andres", accounts.get(0).getPerson());
        assertEquals(1L, accounts.get(0).getId());
        assertEquals("900.00", accounts.get(0).getBalance().toPlainString());
        assertEquals("Jhon", accounts.get(1).getPerson());
        assertEquals(2L, accounts.get(1).getId());
        assertEquals("2100.00", accounts.get(1).getBalance().toPlainString());

        JsonNode jsonNode = objectMapper.readTree(objectMapper.writeValueAsString(accounts));
        assertEquals(1L, jsonNode.get(0).path("id").asLong());
        assertEquals("Andres", jsonNode.get(0).path("person").asText());
        assertEquals("900.0", jsonNode.get(0).path("balance").asText());
        assertEquals(2L, jsonNode.get(1).path("id").asLong());
        assertEquals("Jhon", jsonNode.get(1).path("person").asText());
        assertEquals("2100.0", jsonNode.get(1).path("balance").asText());
    }

    @Test
    @Order(4)
    void testSave() {
        Account account = new Account(null, "Pepa", new BigDecimal("3800"));
        ResponseEntity<Account> responseEntity = client.postForEntity(createUri("/api/accounts"), account, Account.class);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        Account createdAccount = responseEntity.getBody();
        assertEquals(3L, createdAccount.getId());
        assertEquals("Pepa", createdAccount.getPerson());
        assertEquals("3800", createdAccount.getBalance().toPlainString());
    }

    @Test
    @Order(5)
    void testDelete() {
        ResponseEntity<Account[]> responseEntity = client.getForEntity(createUri("/api/accounts"), Account[].class);
        assertNotNull(responseEntity.getBody());
        List<Account> accounts = Arrays.asList(responseEntity.getBody());
        assertEquals(3, accounts.size());

        //client.delete(createUri("/api/accounts/3"));
        Map<String, Long> pathVariables = new HashMap<>();
        pathVariables.put("id", 3L);
        ResponseEntity<Void> exchange = client.exchange(createUri("/api/accounts/{id}"), HttpMethod.DELETE, null, Void.class,
                pathVariables);

        assertEquals(HttpStatus.NO_CONTENT, exchange.getStatusCode());
        assertFalse(exchange.hasBody());

        responseEntity = client.getForEntity(createUri("/api/accounts"), Account[].class);
        assertNotNull(responseEntity.getBody());
        accounts = Arrays.asList(responseEntity.getBody());

        assertEquals(2, accounts.size());

        ResponseEntity<Account> responseDetail = client.getForEntity(createUri("/api/accounts/3"), Account.class);
        assertEquals(HttpStatus.NOT_FOUND, responseDetail.getStatusCode());
        assertFalse(responseDetail.hasBody());
    }

    private String createUri(String uri) {
        return "http://localhost:" + port + uri;
    }
}