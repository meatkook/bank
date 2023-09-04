package org.clever_bank.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    private int id;
    private TransactionType type;
    private Instant date;
    private Account accountSender;
    private Account accountRecipient;
    private BigDecimal money;
}
