package org.clever_bank.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class that represents a type of transaction in a bank.
 * Annotation @Data generates getters, setters, constructors, toString and equals/hashCode.
 * Annotation @NoArgsConstructor generates a constructor without parameters.
 * Annotation @AllArgsConstructor generates a constructor with all fields.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionType {

    /**
     * The unique identifier of the transaction type.
     */
    private int id;

    /**
     * The name of the transaction type, such as DEPOSIT, WITHDRAWAL, TRANSFER.
     */
    private String name;
}
