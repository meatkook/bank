package org.clever_bank.ui;

import org.clever_bank.services.DateConstructor;
import org.clever_bank.services.Statement;
import org.clever_bank.entities.Account;
import org.clever_bank.entities.Transaction;
import org.clever_bank.repository.AccountRepository;
import org.clever_bank.repository.TransactionRepository;
import org.clever_bank.repository.TransactionTypeRepository;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.Scanner;

/**
 * The UserInterface class represents the user interface for a banking application.
 * It provides methods for handling user input and displaying account information
 * and transaction menus.
 */
public class UserInterface {
    private static Account account = null;

    /**
     * Starts the user interface and handles the main menu loop.
     */
    public static void start () {
        boolean isWorking = true;
        while (isWorking){
            isWorking = accountTransactionMenu();
        }
    }

    /**
     * Displays the account transaction menu and handles user input.
     * @return true if the application should continue running, false otherwise.
     */
    public static boolean accountTransactionMenu(){
        boolean isWorking = true;
        String inputData = "";
        if (account == null) {
            System.out.println("\nПожалуйста введите свой номер банковского счёта или напишите stop:");
            inputData = checkInputData();
            account = AccountRepository.read(inputData);
        }

        if (inputData.equals("stop")){
            return false;
        }

        if (account == null){
            System.out.println("Такого номера не существует");
            return true;
        }

        System.out.println("\n" + account.getCustomer().getName() + " (" + account.getBank().getName() + ")");
        System.out.println("Баланс: " + account.getBalance() + " " + account.getCurrency());
        System.out.println("Какую операцию желаете совершить?");
        System.out.println("1. Пополнить счёта");
        System.out.println("2. Снять средства со счёта");
        System.out.println("3. Перевести деньги на другой счёт");
        System.out.println("4. Выписка по счёту");
        System.out.println("5. Получить количество потраченных и полученных средств за определенный период времени");
        System.out.println("6. Перейти к другому счёту");
        System.out.println("7. Остановить приложение");
        inputData = checkInputData();
        switch (inputData) {
            case "1" -> depositMenu();
            case "2" -> subtractionMenu();
            case "3" -> transferMoneyMenu();
            case "4" -> statement();
            case "5" -> statementMoney();
            case "6" -> { account = null; return true; }
            case "7" -> isWorking = false;
            default -> System.out.println("Выбранного варианта меню не существует");
        }
        return isWorking;
    }

    /**
     * Displays the statement menu and handles user input for generating a statement.
     * @return the user's choice as a string.
     */
    private static String statementMenu () {
        System.out.println("1. Создать выписку за последний месяц");
        System.out.println("2. Создать выписку за последний год");
        System.out.println("3. Создать выписку за всё время");
        return checkInputData();
    }

    /**
     * Generates a statement for money transactions based on the user's choice.
     */
    private static void statementMoney() {
        Instant accountOpenDate = DateConstructor.createInstantDate(account.getOpeningDate());
        Instant currentDate = Instant.now();
        Instant oneMonthAgo = getMonthAgo();
        Instant oneYearAgo = getYearAgo();

        String choice = statementMenu();
        switch (choice) {
            case "1" -> Statement.createMoneyReport(account, oneMonthAgo, currentDate);
            case "2" -> Statement.createMoneyReport(account, oneYearAgo, currentDate);
            default -> Statement.createMoneyReport(account, accountOpenDate, currentDate);
        }
    }

    /**
     * Generates a statement for all transactions based on the user's choice.
     */
    private static void statement(){
        Instant accountOpenDate = DateConstructor.createInstantDate(account.getOpeningDate());
        Instant currentDate = Instant.now();
        Instant oneMonthAgo = getMonthAgo();
        Instant oneYearAgo = getYearAgo();

        String inputDataFromMenu = statementMenu();
        switch (inputDataFromMenu) {
            case "1" -> Statement.createReport(account, oneMonthAgo, currentDate);
            case "2" -> Statement.createReport(account, oneYearAgo, currentDate);
            default -> Statement.createReport(account, accountOpenDate, currentDate);
        }
    }

    /**
     * Returns the instant representing one month ago,
     * taking into account the account's opening date.
     *
     * @return the instant representing one month ago.
     */
    private static Instant getMonthAgo () {
        Instant accountOpenDate = DateConstructor.createInstantDate(account.getOpeningDate());
        Instant oneMonthAgo = DateConstructor.getPreviousMonthInstant();

        if (oneMonthAgo.isBefore(accountOpenDate)){
            oneMonthAgo = accountOpenDate;
        }
        return oneMonthAgo;
    }

    /**
     * Returns the instant representing one year ago,
     * taking into account the account's opening date.
     *
     * @return the instant representing one year ago.
     */
    private static Instant getYearAgo () {
        Instant accountOpenDate = DateConstructor.createInstantDate(account.getOpeningDate());
        Instant oneYearAgo = DateConstructor.getPreviousYearInstant();

        if (oneYearAgo.isBefore(accountOpenDate)){
            oneYearAgo = accountOpenDate;
        }

        return oneYearAgo;
    }

    /**
     * Displays the deposit menu and handles user input
     * for depositing money into the account.
     */
    private static void depositMenu() {
        System.out.println("\nВведите сумму для пополнения счёта");
        BigDecimal money = getMoneyData();
        if (money == null) {
            return;
        }

        Transaction transaction = new Transaction();
        transaction.setMoney(money);
        transaction.setAccountSender(account);
        transaction.setAccountRecipient(account);
        transaction.setType(TransactionTypeRepository.readTransactionTypeById(1));
        int transactionId = TransactionRepository.create(transaction);
        System.out.println("Вы добавили на счёт " + money + " " + account.getCurrency());
        account = AccountRepository.read(account.getId());
        assert account != null;
        System.out.println("Баланс:" + account.getBalance() + " " + account.getCurrency()) ;
        Statement.createTransactionCheck(Objects.requireNonNull(TransactionRepository.readTransaction(transactionId)));
    }

    /**
     * Displays the subtraction menu and handles user input
     * for withdrawing money from the account.
     */
    private static void subtractionMenu() {
        System.out.println("\nВведите для снятия средств со счёта");
        BigDecimal money = getMoneyData();
        Transaction transaction = new Transaction();
        transaction.setMoney(money);
        transaction.setAccountSender(account);
        transaction.setAccountRecipient(account);
        transaction.setType(TransactionTypeRepository.readTransactionTypeById(2));

        int transactionId = TransactionRepository.create(transaction);
        System.out.println("Вы сняли со счёта " + money + " " + account.getCurrency());
        account = AccountRepository.read(account.getId());
        assert account != null;
        System.out.println("Баланс:" + account.getBalance() + " " + account.getCurrency()) ;
        Statement.createTransactionCheck(Objects.requireNonNull(TransactionRepository.readTransaction(transactionId)));
    }

    /**
     * Displays the transfer money menu and handles user input
     * for transferring money to another account.
     */
    private static void transferMoneyMenu() {
        Transaction transaction = new Transaction();
        transaction.setAccountSender(account);
        transaction.setType(TransactionTypeRepository.readTransactionTypeById(3));
        System.out.println("\nВведите номер счёта получателя:");
        String inputData = checkInputData();
        Account accountRecipient = AccountRepository.read(inputData);
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
        BigDecimal money = getMoneyData();
        transaction.setMoney(money);

        int transactionId = TransactionRepository.create(transaction);
        System.out.println("Вы отправили " + money + " " + account.getCurrency() + " → " + accountRecipient.getCustomer().getName());

        Statement.createTransactionCheck(Objects.requireNonNull(TransactionRepository.readTransaction(transactionId)));
    }

    /**
     * Retrieves and validates user input for a money amount.
     * @return the money amount as a BigDecimal, or null if the input is invalid.
     */
    private static @Nullable BigDecimal getMoneyData(){
        try {
            String inputData = checkInputData();
            BigDecimal money = new BigDecimal(inputData);
            if (money.compareTo(BigDecimal.valueOf(0)) <= 0) {
                System.out.println("Сумма должна быть больше 0");
                return null;
            }
            return money;
        } catch (NumberFormatException e) {
            System.out.println("Неверный формат ввода. Пожалуйста, введите число.");
        }
        return null;
    }

    /**
     * Checks and returns the data entered by the user.
     * @return user-entered data as a string.
     */
    private static String checkInputData () {
        Scanner in = new Scanner(System.in);
        if (in.hasNextLine()) {
            return in.nextLine();
        } else {
            System.out.println("Отсутствует ввод данных.");
        }
        return "";
    }
}
