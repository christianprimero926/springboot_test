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
import java.util.NoSuchElementException;

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
    public ResponseEntity<?> detail(@PathVariable(name = "id") Long id) {
        Account account = null;
        try {
            account = accountService.findById(id);
        } catch (NoSuchElementException e){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(account);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public Account save(@RequestBody Account account) {
        return accountService.save(account);
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

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable Long id) {
        accountService.deleteById(id);
    }
}
