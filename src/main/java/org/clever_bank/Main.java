package org.clever_bank;

import org.clever_bank.db.DatabaseSetup;
import org.clever_bank.ui.UserInterface;

public class Main {
    public static void main(String[] args) {
        DatabaseSetup.setupDatabase();
        UserInterface.start();
    }
}