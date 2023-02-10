package org.cospina.test.springboot.app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    void testTransfer() {
        // Given
        TransactionDTO dto = new TransactionDTO();
        dto.setAmount(new BigDecimal("100"));
        dto.setSourceAccountId(1L);
        dto.setTargetAccountId(2L);
        dto.setBankId(1L);

        // When
        ResponseEntity<String> responseEntity = client.
                postForEntity(createUri("/api/accounts/transfer"), dto, String.class);
        System.out.println(port);
        String json = responseEntity.getBody();
        assertNotNull(json);
        System.out.println(json);
        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        assertTrue(json.contains("Transferencia realizada con exito"));
    }

    private String createUri(String uri) {
        return "http://localhost:" + port + uri;
    }
}