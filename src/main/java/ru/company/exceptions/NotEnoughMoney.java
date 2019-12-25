package ru.company.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.math.BigDecimal;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NotEnoughMoney extends RuntimeException {

    public NotEnoughMoney(BigDecimal balance, BigDecimal withdrawal) {
        super("Not enough balance=" + balance + ", money to withdraw=" + withdrawal);
    }
}
