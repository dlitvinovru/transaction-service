package ru.company.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

@Data
@Entity
@EqualsAndHashCode(of = "id")
public class Account {
    @Id
    private Long id;
    private BigDecimal balance;
}
