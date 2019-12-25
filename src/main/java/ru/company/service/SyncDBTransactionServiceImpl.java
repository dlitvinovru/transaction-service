package ru.company.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.company.dao.AccountDao;
import ru.company.entity.Account;
import ru.company.exceptions.AccountNotFound;
import ru.company.exceptions.NotEnoughMoney;

import java.math.BigDecimal;

@Slf4j
@Service("syncDB")
@RequiredArgsConstructor
public class SyncDBTransactionServiceImpl implements TransactionService {

    private final AccountDao accountDao;

    @Override
    public Account findById(Long accountId) {
        return accountDao.findAccountById(accountId)
                .orElseThrow(() -> new AccountNotFound(accountId));
    }

    @Override
    @Transactional
    public Account transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        Account fromAccount;
        Account toAccount;
        // specific lock order to avoid thread lock
        if (fromAccountId < toAccountId) {
            fromAccount = findById(fromAccountId);
            toAccount = findById(toAccountId);
        } else {
            toAccount = findById(toAccountId);
            fromAccount = findById(fromAccountId);
        }
        BigDecimal fromBalance = fromAccount.getBalance();
        BigDecimal toBalance = toAccount.getBalance();
        if (fromBalance.compareTo(amount) < 0) {
            throw new NotEnoughMoney(fromBalance, amount);
        }
        fromAccount.setBalance(fromBalance.subtract(amount));
        toAccount.setBalance(toBalance.add(amount));
        accountDao.save(fromAccount);
        accountDao.save(toAccount);
        return fromAccount;
    }

    @Override
    @Transactional
    public Account putMoney(Long accountId, BigDecimal amount) {
        Account account = findById(accountId);
        BigDecimal balance = account.getBalance();
        account.setBalance(balance.add(amount));
        return accountDao.save(account);
    }

    @Override
    @Transactional
    public Account withdraw(Long accountId, BigDecimal withdrawal) {
        Account account = findById(accountId);
        BigDecimal balance = account.getBalance();
        if (balance.compareTo(withdrawal) < 0) {
            throw new NotEnoughMoney(balance, withdrawal);
        }
        account.setBalance(balance.subtract(withdrawal));
        return accountDao.save(account);
    }
}
