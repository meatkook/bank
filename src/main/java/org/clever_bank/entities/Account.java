package org.clever_bank.entities;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
