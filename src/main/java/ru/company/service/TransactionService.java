package ru.company.service;

import ru.company.entity.Account;

import java.math.BigDecimal;

public interface TransactionService {

    Account findById(Long accountId);

    Account transfer(Long fromAccount, Long toAccount, BigDecimal amount);

    Account putMoney(Long accountId, BigDecimal amount);

    Account withdraw(Long accountId, BigDecimal withdrawal);
}
