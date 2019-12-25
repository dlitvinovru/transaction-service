package ru.company.dao;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.company.entity.Account;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface AccountDao extends CrudRepository<Account, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Account> findAccountById(Long accountId);
}
