package org.clever_bank.entities;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Class that represents an account in a bank.
 * Annotation @Data generates getters, setters, constructors, toString and equals/hashCode.
 * Annotation @AllArgsConstructor generates a constructor with all fields.
 */
@Data
@AllArgsConstructor
public class Account {
    /**
     * The unique identifier of the account.
     */
    private int id;

    /**
     * The account number associated with the account.
     */
    private String accountNumber;

    /**
     * The current balance of the account.
     */
    private BigDecimal balance;

    /**
     * The currency in which the account operates.
     */
    private String currency;

    /**
     * The date when the account was opened.
     */
    private Date openingDate;

    /**
     * The bank to which the account belongs.
     */
    private Bank bank;

    /**
     * The customer who owns the account.
     */
    private Customer customer;
}
