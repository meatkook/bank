package org.clever_bank.entities;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
//@Setter(AccessLevel.NONE)
public class Account {
    private int id;
    private String accountNumber;
    private BigDecimal balance;
    private String currency;
    private Date openingDate;
    private Bank bank;
    private Customer customer;
}
