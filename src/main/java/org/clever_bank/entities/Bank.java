package org.clever_bank.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Class that represents a bank.
 * Annotation @Data generates getters, setters, constructors, toString and equals/hashCode.
 * Annotation @AllArgsConstructor generates a constructor with all fields.
 */
@Data
@AllArgsConstructor
public class Bank {
    /**
     * The unique identifier of the bank.
     */
    private int id;

    /**
     * The name of the bank.
     */
    private String name;

    /**
     * The list of accounts that belong to the bank.
     */
    private List<Account> accounts;
}