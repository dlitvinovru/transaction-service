package ru.company.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import ru.company.concurrent.Synchronizer;
import ru.company.dao.AccountDao;
import ru.company.entity.Account;
import ru.company.exceptions.AccountNotFound;
import ru.company.exceptions.NotEnoughMoney;

import java.math.BigDecimal;

@Slf4j
@Service("syncJava")
public class SyncJavaTransactionServiceImpl implements TransactionService {

    private final AccountDao accountDao;
    private final Synchronizer synchronizer;
    private final TransactionTemplate transactionTemplate;

    public SyncJavaTransactionServiceImpl(AccountDao accountDao, Synchronizer synchronizer, PlatformTransactionManager transactionManager) {
        this.accountDao = accountDao;
        this.synchronizer = synchronizer;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    public Account findById(Long accountId) {
        return accountDao.findById(accountId)
                .orElseThrow(() -> new AccountNotFound(accountId));
    }

    @Override
    public Account transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        try {
            synchronizer.synchronize(fromAccountId, toAccountId);
            return transactionTemplate.execute(status -> {
                Account fromAccount = findById(fromAccountId);
                Account toAccount = findById(toAccountId);
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
            });
        } catch (Exception e) {
            log.error("Cannot transfer money from account={}, to account={}", fromAccountId, toAccountId);
            throw e;
        } finally {
            synchronizer.unlock(fromAccountId, toAccountId);
        }
    }

    @Override
    public Account putMoney(Long accountId, BigDecimal amount) {
        try {
            synchronizer.synchronize(accountId);
            return transactionTemplate.execute(status -> {
                Account account = findById(accountId);
                BigDecimal balance = account.getBalance();
                account.setBalance(balance.add(amount));
                return accountDao.save(account);
            });
        } catch (Exception e) {
            log.error("Cannot put money on the account={}", accountId);
            throw e;
        } finally {
            synchronizer.unlock(accountId);
        }
    }

    @Override
    public Account withdraw(Long accountId, BigDecimal withdrawal) {
        try {
            synchronizer.synchronize(accountId);
            return transactionTemplate.execute(status -> {
                Account account = findById(accountId);
                BigDecimal balance = account.getBalance();
                if (balance.compareTo(withdrawal) < 0) {
                    throw new NotEnoughMoney(balance, withdrawal);
                }
                account.setBalance(balance.subtract(withdrawal));
                return accountDao.save(account);
            });
        } catch (Exception e) {
            log.error("Cannot withdraw money from account={}", accountId);
            throw e;
        } finally {
            synchronizer.unlock(accountId);
        }
    }
}
