package ru.company.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.company.service.TransactionService;

import javax.validation.constraints.Digits;
import java.math.BigDecimal;

@Slf4j
@Validated
@RestController
@RequestMapping("/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(
                             @Qualifier("syncDB")
//                             @Qualifier("syncJava")
                             TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/put")
    public ResponseEntity putMoney(@RequestParam Long accountId,
                                   @RequestParam @Digits(integer=10, fraction = 2) BigDecimal amount) {
        return ResponseEntity.ok(transactionService.putMoney(accountId, amount));
    }

    @GetMapping("/withdraw")
    public ResponseEntity withdraw(@RequestParam Long accountId,
                                   @RequestParam @Digits(integer=10, fraction = 2) BigDecimal amount) {
        return ResponseEntity.ok(transactionService.withdraw(accountId, amount));
    }

    @GetMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestParam Long accountFrom,
                                   @RequestParam Long accountTo,
                                   @RequestParam @Digits(integer=10, fraction = 2) BigDecimal amount) {
        if (accountFrom.equals(accountTo)) {
            return new ResponseEntity<>("accountFrom equals accountTo", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(transactionService.transfer(accountFrom, accountTo, amount));
    }
}
