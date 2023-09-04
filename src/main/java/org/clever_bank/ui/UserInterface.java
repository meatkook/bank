package org.clever_bank.ui;

import org.clever_bank.utility_classes.DateConstructor;
import org.clever_bank.Statement;
import org.clever_bank.entities.Account;
import org.clever_bank.entities.Transaction;
import org.clever_bank.repository.AccountRepository;
import org.clever_bank.repository.TransactionRepository;
import org.clever_bank.repository.TransactionTypeRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.Scanner;

public class UserInterface {
    public static void start () {
        Scanner in = new Scanner(System.in);
        System.out.println("\nПожалуйста введите свой номер банковского счёта:");
        String choice = in.nextLine();
        Account account = AccountRepository.read(choice);
        if (account == null){
            System.out.println("Такого номера не существует");
            return;
        }

        System.out.println("\nЗдравствуйте " + account.getCustomer().getName() + " (" + account.getBank().getName() + ")");
        System.out.println("Баланс: " + account.getBalance() + " " + account.getCurrency());
        System.out.println("Какую операцию желаете совершить?");
        System.out.println("1. Пополнить счёта");
        System.out.println("2. Снять средства со счёта");
        System.out.println("3. Перевести деньги на другой счёт");
        System.out.println("4. Выписка по счёту");
        System.out.println("5. Получить количество потраченных и полученных средств за определенный период времени");
        choice = in.next();
        switch (choice) {
            case "1" -> deposit(account);
            case "2" -> subtraction(account);
            case "3" -> transferMoney(account);
            case "4" -> statement(account);
            case "5" -> statementMoney(account);
            default -> System.out.println("Выбранного варианта меню не существует");
        }
    }

    private static void statementMoney(Account account) {
        Instant accountOpenDate = DateConstructor.createInstantDate(account.getOpeningDate());
        Instant currentDate = Instant.now();
        Instant oneMonthAgo = getMonthAgo(account);
        Instant oneYearAgo = getYearAgo(account);

        String choice = statementMenu(account);
        switch (choice) {
            case "1" -> Statement.createMoneyReport(account, oneMonthAgo, currentDate);
            case "2" -> Statement.createMoneyReport(account, oneYearAgo, currentDate);
            default -> Statement.createMoneyReport(account, accountOpenDate, currentDate);
        }
    }

    private static void statement(Account account){
        Instant accountOpenDate = DateConstructor.createInstantDate(account.getOpeningDate());
        Instant currentDate = Instant.now();
        Instant oneMonthAgo = getMonthAgo(account);
        Instant oneYearAgo = getYearAgo(account);

        String choice = statementMenu(account);
        switch (choice) {
            case "1" -> Statement.createReport(account, oneMonthAgo, currentDate);
            case "2" -> Statement.createReport(account, oneYearAgo, currentDate);
            default -> Statement.createReport(account, accountOpenDate, currentDate);
        }
    }

    private static Instant getMonthAgo (Account account) {
        Instant accountOpenDate = DateConstructor.createInstantDate(account.getOpeningDate());
        Instant oneMonthAgo = DateConstructor.getPreviousMonthInstant();

        if (oneMonthAgo.isBefore(accountOpenDate)){
            oneMonthAgo = accountOpenDate;
        }
        return oneMonthAgo;
    }

    private static Instant getYearAgo (Account account) {
        Instant accountOpenDate = DateConstructor.createInstantDate(account.getOpeningDate());
        Instant oneYearAgo = DateConstructor.getPreviousYearInstant();

        if (oneYearAgo.isBefore(accountOpenDate)){
            oneYearAgo = accountOpenDate;
        }

        return oneYearAgo;
    }

    private static String statementMenu (Account account) {
        Scanner in = new Scanner(System.in);
        Instant accountOpenDate = DateConstructor.createInstantDate(account.getOpeningDate());
        Instant currentDate = Instant.now();
        Instant oneMonthAgo = DateConstructor.getPreviousMonthInstant();
        Instant oneYearAgo = DateConstructor.getPreviousYearInstant();

        if (oneMonthAgo.isBefore(accountOpenDate)){
            oneMonthAgo = accountOpenDate;
        }

        if (oneYearAgo.isBefore(accountOpenDate)){
            oneYearAgo = accountOpenDate;
        }

        System.out.println("1. Создать выписку за последний месяц");
        System.out.println("2. Создать выписку за последний год");
        System.out.println("3. Создать выписку за всё время");
        return in.nextLine();
    }

    private static void deposit (Account account) {
        System.out.println("\nВведите сумму для пополнения счёта");
        BigDecimal money = getMoney();
        Transaction transaction = new Transaction();
        transaction.setMoney(money);
        transaction.setAccountSender(account);
        transaction.setAccountRecipient(account);
        transaction.setType(TransactionTypeRepository.readTransactionTypeById(1));
        int transactionId = TransactionRepository.create(transaction);
        System.out.println("Вы добавили на счёт " + money + " " + account.getCurrency());
        account = AccountRepository.read(account.getId());
        System.out.println("Баланс:" + account.getBalance() + " " + account.getCurrency()) ;
        Statement.createTransactionCheck(Objects.requireNonNull(TransactionRepository.readTransaction(transactionId)));
    }

    private static void subtraction (Account account) {
        System.out.println("\nВведите для снятия средств со счёта");
        BigDecimal money = getMoney();

        if (money == null) {
            return;
        }

        Transaction transaction = new Transaction();
        transaction.setMoney(money);
        transaction.setAccountSender(account);
        transaction.setAccountRecipient(account);
        transaction.setType(TransactionTypeRepository.readTransactionTypeById(2));

        int transactionId = TransactionRepository.create(transaction);
        System.out.println("Вы сняли со счёта " + money + " " + account.getCurrency());
        account = AccountRepository.read(account.getId());
        System.out.println("Баланс:" + account.getBalance() + " " + account.getCurrency()) ;
        Statement.createTransactionCheck(Objects.requireNonNull(TransactionRepository.readTransaction(transactionId)));
    }

    private static void transferMoney (Account account) {
        Scanner in = new Scanner(System.in);
        Transaction transaction = new Transaction();
        transaction.setAccountSender(account);
        transaction.setType(TransactionTypeRepository.readTransactionTypeById(3));
        System.out.println("\nВведите номер счёта получателя:");
        String choice = in.nextLine();
        Account accountRecipient = AccountRepository.read(choice);
        if (accountRecipient == null) {
            System.out.println("Такого номера счёта не существует");
            return;
        }
        if (account.getId() == accountRecipient.getId()){
            System.out.println("Нельзя провести операцию " + account.getAccountNumber() + " → " + accountRecipient.getAccountNumber());
            return;
        }
        transaction.setAccountRecipient(accountRecipient);

        if (!account.getCurrency().equals(accountRecipient.getCurrency())) {
            System.out.println("У получателя счёт в другой валюте (" + accountRecipient.getCurrency() + ")");
            System.out.println("На данный момент переводы на счета другой валюты не поддерживаются (нет данных о курсах)");
            return;
        }
        System.out.println("Введите сумму");
        choice = in.next();
        BigDecimal money = new BigDecimal(choice);
        transaction.setMoney(money);

        int transactionId = TransactionRepository.create(transaction);
        System.out.println("Вы отправили " + money + " " + account.getCurrency() + " → " + accountRecipient.getCustomer().getName());

        Statement.createTransactionCheck(Objects.requireNonNull(TransactionRepository.readTransaction(transactionId)));
    }

    private static BigDecimal getMoney (){
        Scanner in = new Scanner(System.in);
        String choice = in.next();
        BigDecimal money = new BigDecimal(choice);
        if (money.compareTo(BigDecimal.valueOf(0)) <= 0) {
            System.out.println("Сумма должна быть больше 0");
            return null;
        }
        return money;
    }
}
