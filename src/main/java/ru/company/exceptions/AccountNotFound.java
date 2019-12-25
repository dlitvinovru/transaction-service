package ru.company.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AccountNotFound extends RuntimeException {

    private static final String MSG = "Account with id=%d not found";

    public AccountNotFound(Long accountId) {
        super(String.format(MSG, accountId));
    }
}
