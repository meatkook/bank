package org.clever_bank.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Customer {
    private int id;
    private String name;
    private List<Account> accounts;
}
