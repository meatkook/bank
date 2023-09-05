package org.clever_bank.repository;

import org.clever_bank.entities.*;
import org.clever_bank.services.AppConfig;

import java.math.BigDecimal;
import java.sql.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.Date;

public class TransactionRepository {
    private static final AppConfig appConfig = new AppConfig();
    private static final String url = appConfig.getFullUrl();
    private static final String username = appConfig.getUsername();
    private static final String password = appConfig.getPassword();

    public static int create (Transaction transaction) {
        String sqlQuery = "INSERT INTO transactions (date, money, id_type ,id_sender, id_recipient) " +
                "VALUES (?, ?, ?, ?, ?)" +
                "RETURNING id";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {

            ZonedDateTime currentDateTime = ZonedDateTime.now(ZoneId.systemDefault());
            statement.setTimestamp(1, Timestamp.from(Instant.now()));
            statement.setBigDecimal(2, transaction.getMoney());
            statement.setInt(3, transaction.getType().getId());
            statement.setInt(4, transaction.getAccountSender().getId());
            statement.setInt(5, transaction.getAccountRecipient().getId());

            Account accountRecipient = AccountRepository.read(transaction.getAccountRecipient().getId());
            Account accountSender = AccountRepository.read(transaction.getAccountSender().getId());


            if (transaction.getType().getId() == 1) {
                accountRecipient.setBalance(accountRecipient.getBalance().add(transaction.getMoney()));
                AccountRepository.update(accountRecipient);
            }

            if (transaction.getType().getId() == 2) {
                accountRecipient.setBalance(accountRecipient.getBalance().subtract(transaction.getMoney()));
                AccountRepository.update(accountRecipient);
            }
            if (transaction.getType().getId() == 3) {
                accountSender.setBalance(accountSender.getBalance().subtract(transaction.getMoney()));
                accountRecipient.setBalance(accountRecipient.getBalance().add(transaction.getMoney()));
                AccountRepository.update(accountSender);
                AccountRepository.update(accountRecipient);
            }

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static List<Transaction> readTransactions() {
        String sqlQuery = "SELECT t.id, t.date, t.money, t.id_type, t.id_sender, t.id_recipient, ty.name " +
                "FROM transactions t " +
                "JOIN types ty ON t.id_type = ty.id";
        List<Transaction> transactions = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery (sqlQuery)) {

            while (resultSet.next()) {
                // Получение данных о транзакции из результата запроса
                int id = resultSet.getInt("id");
                Instant date = resultSet.getTimestamp("date").toInstant();
                BigDecimal money = resultSet.getBigDecimal("money");
                int typeId = resultSet.getInt("id_type");
                int senderId = resultSet.getInt("id_sender");
                int recipientId = resultSet.getInt("id_recipient");
                String typeName = resultSet.getString("name");

                // Получение аккаунта отправителя и получателя
                Account accountSender = getAccountById(senderId);
                Account accountRecipient = getAccountById(recipientId);
                TransactionType type = new TransactionType(typeId, typeName);

                Transaction transaction = new Transaction(id, type, date, accountSender, accountRecipient, money);
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }

    public static Transaction readTransaction (int id) {

        String sqlQuery = "SELECT t.id, t.date, t.money, t.id_type, t.id_sender, t.id_recipient, ty.name " +
                "FROM transactions t " +
                "JOIN types ty ON t.id_type = ty.id " +
                "WHERE t.id = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                // Получение данных о транзакции из результата запроса
                Instant date = resultSet.getTimestamp("date").toInstant();
                BigDecimal money = resultSet.getBigDecimal("money");
                int typeId = resultSet.getInt("id_type");
                int senderId = resultSet.getInt("id_sender");
                int recipientId = resultSet.getInt("id_recipient");
                String typeName = resultSet.getString("name");

                // Получение аккаунта отправителя и получателя
                Account accountSender = getAccountById(senderId);
                Account accountRecipient = getAccountById(recipientId);
                TransactionType type = new TransactionType(typeId, typeName);

                return new Transaction(id, type, date, accountSender, accountRecipient, money);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static BigDecimal getReceivedForAccount (int accountId, Instant dateStart, Instant dateEnd) {
        String sqlQuery = "SELECT SUM(money) AS total_income " +
                "FROM transactions " +
                "WHERE (id_type = 1 OR (id_type = 3 AND id_recipient = ?)) " +
                "AND id_recipient = ? " +
                "AND date BETWEEN ? AND ?";

        BigDecimal totalIncome = null;

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setInt(1, accountId);
            statement.setInt(2, accountId);
            statement.setTimestamp(3, Timestamp.from(dateStart));
            statement.setTimestamp(4, Timestamp.from(dateEnd));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                totalIncome = resultSet.getBigDecimal("total_income");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return totalIncome;
    }

    public static BigDecimal getWithdrawnForAccount (int accountId, Instant dateStart, Instant dateEnd) {
        String sqlQuery = "SELECT SUM(money) AS total_expense " +
                "FROM transactions " +
                "WHERE (id_type = 2 OR (id_type = 3 AND id_sender = ?)) " +
                "AND id_sender = ? " +
                "AND date BETWEEN ? AND ?";

        BigDecimal totalExpense = null;

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setInt(1, accountId);
            statement.setInt(2, accountId);
            statement.setTimestamp(3, Timestamp.from(dateStart));
            statement.setTimestamp(4, Timestamp.from(dateEnd));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                totalExpense = resultSet.getBigDecimal("total_expense");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return totalExpense;
    }

    public static List<Transaction> readTransactionsPeriod(int accountId, Instant dateStart, Instant dateEnd) { /*, Date dateStart, Date dateEnd*/
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT transact.id, " + //  1
                "transact.id_type, " +          //  2
                "transact.date, " +             //  3
                // Account Sender
                "acc1.id, " +                   //  4
                "acc1.number, " +               //  5
                "acc1.balance, " +              //  6
                "acc1.currency, " +             //  7
                "acc1.open_date, " +            //  8
                // Account Recipient
                "acc2.id, " +                   //  9
                "acc2.number, " +               // 10
                "acc2.balance, " +              // 11
                "acc2.currency, " +             // 12
                "acc2.open_date, " +            // 13
                // Bank Sender
                "bank1.id, " +                  // 14
                "bank1.name, " +                // 15
                // Bank Recipient
                "bank2.id, " +                  // 16
                "bank2.name, " +                // 17
                // Account Sender
                "cust1.id, " +                  // 18
                "cust1.name, " +                // 19
                // AccountRecipient
                "cust2.id, " +                  // 20
                "cust2.name, " +                // 21

                "transact.money " +             // 22


                "FROM transactions transact " +
                "JOIN types typ ON transact.id_type = typ.id " +
                "JOIN accounts acc1 ON transact.id_sender = acc1.id " +
                "JOIN accounts acc2 ON transact.id_recipient = acc2.id " +
                "JOIN banks bank1 ON acc1.id_bank = bank1.id " +
                "JOIN banks bank2 ON acc2.id_bank = bank2.id " +
                "JOIN customers cust1 ON acc1.id_customer = cust1.id " +
                "JOIN customers cust2 ON acc2.id_customer = cust2.id " +
                "WHERE (transact.id_sender = ? OR transact.id_recipient = ?) AND transact.date >= ? AND transact.date <= ?"; //AND transact.date >= ? AND transact.date <= ?
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, accountId);
            statement.setInt(2, accountId);
            statement.setTimestamp(3, Timestamp.from(dateStart));
            statement.setTimestamp(4, Timestamp.from(dateEnd));

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {

                // Bank Sender
                int bankSenderId = resultSet.getInt(14);
                String bankSenderName = resultSet.getString(15);
                Bank bankSender = new Bank(bankSenderId, bankSenderName, new ArrayList<>());
                // Bank Recipient
                int bankRecipientId = resultSet.getInt(16);
                String bankRecipientName = resultSet.getString(17);
                Bank bankRecipient = new Bank(bankRecipientId, bankRecipientName, new ArrayList<>());
                // Customer Sender
                int customerSenderId = resultSet.getInt(18);
                String customerSenderName = resultSet.getString(19);
                Customer customerSender = new Customer(customerSenderId, customerSenderName, new ArrayList<>());
                // Customer Recipient
                int customerRecipientId = resultSet.getInt(20);
                String customerRecipientName = resultSet.getString(21);
                Customer customerRecipient = new Customer(customerRecipientId, customerRecipientName, new ArrayList<>());
                // Account Sender
                int senderAccId = resultSet.getInt(4);
                String senderAccountNumber = resultSet.getString(5);
                BigDecimal senderBalance = resultSet.getBigDecimal(6);
                String senderCurrency = resultSet.getString(7);
                Date senderOpeningDate = resultSet.getDate(8);
                Account accountSender = new Account(senderAccId,
                        senderAccountNumber,
                        senderBalance,
                        senderCurrency,
                        senderOpeningDate,
                        bankSender,
                        customerSender);
                // Account Recipient
                int recipientAccId = resultSet.getInt(9);
                String recipientAccNumber = resultSet.getString(10);
                BigDecimal recipientAccBalance = resultSet.getBigDecimal(11);
                String recipientAccCurrency = resultSet.getString(12);
                Date recipientAccOpeningDate = resultSet.getDate(13);
                Account accountRecipient = new Account(recipientAccId,
                        recipientAccNumber,
                        recipientAccBalance,
                        recipientAccCurrency,
                        recipientAccOpeningDate,
                        bankRecipient,
                        customerRecipient);
                // Transaction
                int transactionId = resultSet.getInt(1);
                int typeId = resultSet.getInt(2);
                TransactionType type = TransactionTypeRepository.readTransactionTypeById(typeId);
                Instant date = resultSet.getTimestamp(3).toInstant();
                BigDecimal money = resultSet.getBigDecimal(22);
                Transaction transaction = new Transaction(transactionId, type, date, accountSender, accountRecipient, money);
                transactions.add(transaction);
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
        }


        return transactions;
    }

    private static Account getAccountById (int accountId) {
        Account account = null;
        String sqlQuery = "SELECT * FROM accounts WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setInt(1, accountId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String number = resultSet.getString ("number");
                BigDecimal balance = resultSet.getBigDecimal("balance");
                String currency = resultSet.getString("currency");
                Date open_date = resultSet.getDate("open_date");
                int bankId = resultSet.getInt("id_bank");
                Bank bank = getBankById(bankId);
                int customerId = resultSet.getInt("id_customer");
                Customer customer = getCustomerById(customerId);
                account = new Account(id, number, balance, currency, open_date, bank, customer);
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return account;
    }

    private static Bank getBankById(int bankId) {
        Bank bank = null;
        String sqlQuery = "SELECT * FROM banks WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setInt(1, bankId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                bank = new Bank(bankId, name, null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bank;
    }

    private static Customer getCustomerById (int id) {
        Customer customer = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM customers WHERE id = ?")) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                List<Account> accounts = new ArrayList<>();
                customer = new Customer(id, name, accounts);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customer;
    }

}
