package org.clever_bank.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Class that represents a transaction.
 * Annotation @Data generates getters, setters, constructors, toString and equals/hashCode.
 * Annotation @NoArgsConstructor generates a constructor without parameters.
 * Annotation @AllArgsConstructor generates a constructor with all fields.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    /**
     * The unique identifier of the transaction.
     */
    private int id;

    /**
     * The type of the transaction, such as DEPOSIT, WITHDRAWAL, TRANSFER, etc.
     */
    private TransactionType type;

    /**
     * The date and time when the transaction occurred.
     */
    private Instant date;

    /**
     * The account that sent the money in the transaction.
     */
    private Account accountSender;

    /**
     * The account that received the money in the transaction.
     */
    private Account accountRecipient;

    /**
     * The amount of money involved in the transaction.
     */
    private BigDecimal money;
}
