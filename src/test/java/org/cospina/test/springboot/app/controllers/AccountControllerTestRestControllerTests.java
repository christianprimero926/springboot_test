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
    void testShowAll() throws JsonProcessingException {
        ResponseEntity<Account[]> responseEntity = client.getForEntity(createUri("/api/accounts"), Account[].class);
        List<Account> accounts = Arrays.asList(responseEntity.getBody());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());

        assertEquals(2, accounts.size());
        assertEquals("Andres",accounts.get(0).getPerson());
        assertEquals(1L, accounts.get(0).getId());
        assertEquals("900.00", accounts.get(0).getBalance().toPlainString());
        assertEquals("Jhon",accounts.get(1).getPerson());
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

    private String createUri(String uri) {
        return "http://localhost:" + port + uri;
    }
}