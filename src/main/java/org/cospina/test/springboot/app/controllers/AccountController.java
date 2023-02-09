package org.cospina.test.springboot.app.controllers;

import static org.springframework.http.HttpStatus.*;

import org.cospina.test.springboot.app.models.Account;
import org.cospina.test.springboot.app.models.TransactionDTO;
import org.cospina.test.springboot.app.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @GetMapping
    @ResponseStatus(OK)
    public List<Account> showAll() {
        return accountService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(OK)
    public Account detail(@PathVariable(name = "id") Long id) {
        return accountService.findById(id);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public Account save(@RequestBody Account account){
        return null;
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody TransactionDTO dto) {
        accountService.transfer(dto.getSourceAccountId(), dto.getTargetAccountId(), dto.getAmount(), dto.getBankId());
        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "ok");
        response.put("message", "Transferencia realizada con exito");
        response.put("transaction", dto);

        return ResponseEntity.ok(response);
    }
}
