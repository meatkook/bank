package org.clever_bank.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Class that represents a customer of a bank.
 * Annotation @Data generates getters, setters, constructors, toString and equals/hashCode.
 * Annotation @AllArgsConstructor generates a constructor with all fields.
 */
@Data
@AllArgsConstructor
public class Customer {

    /**
     * The unique identifier of the customer.
     */
    private int id;

    /**
     * The name of the customer.
     */
    private String name;

    /**
     * The list of accounts that belong to the customer.
     */
    private List<Account> accounts;
}
