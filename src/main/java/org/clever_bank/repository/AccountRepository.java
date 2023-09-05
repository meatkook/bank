package org.clever_bank.repository;

import org.clever_bank.entities.Account;
import org.clever_bank.entities.Bank;
import org.clever_bank.entities.Customer;
import org.clever_bank.services.AppConfig;
import org.clever_bank.services.DateConstructor;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AccountRepository {
    private static final AppConfig appConfig = new AppConfig();
    private static final String url = appConfig.getFullUrl();
    private static final String username = appConfig.getUsername();
    private static final String password = appConfig.getPassword();

    public static void create (Account account) {
        String sqlQuery = "INSERT INTO accounts (number, balance, currency, open_date, id_bank, id_customer, interest_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {

            statement.setString(1, account.getAccountNumber());
            statement.setBigDecimal(2, account.getBalance());
            statement.setString(3, account.getCurrency());
            statement.setDate(4, new java.sql.Date(account.getOpeningDate().getTime()));
            statement.setInt(5, account.getBank().getId());
            statement.setInt(6, account.getCustomer().getId());
            statement.setDate(7, DateConstructor.createSqlDate(account.getOpeningDate()));

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Account read (int accountId) {
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
                Customer customer = CustomerRepository.getCustomerById(customerId);
                account = new Account(id, number, balance, currency, open_date, bank, customer);
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return account;
    }

    public static Account read (String number) {
        Account account = null;
        String sqlQuery = "SELECT * FROM accounts WHERE number = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setString(1, number);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                BigDecimal balance = resultSet.getBigDecimal("balance");
                String currency = resultSet.getString("currency");
                Date open_date = resultSet.getDate("open_date");
                int bankId = resultSet.getInt("id_bank");
                Bank bank = getBankById(bankId);
                int customerId = resultSet.getInt("id_customer");
                Customer customer = CustomerRepository.getCustomerById(customerId);
                account = new Account(id, number, balance, currency, open_date, bank, customer);
                resultSet.close();
            }

        }
        catch (SQLException e) {
//            e.printStackTrace();
        }
        return account;
    }

    public static List<Account> readAll () {
        List<Account> accounts = new ArrayList<>();
        String sqlQuery = "SELECT * FROM accounts";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery (sqlQuery)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String number = resultSet.getString ("number");
                BigDecimal balance = resultSet.getBigDecimal("balance");
                String currency = resultSet.getString("currency");
                Date open_date = resultSet.getDate("open_date");
                int bankId = resultSet.getInt("id_bank");
                Bank bank = getBankById(bankId);
                int customerId = resultSet.getInt("id_customer");
                Customer customer = getCustomerById(customerId);
                accounts.add(new Account(id, number, balance, currency, open_date, bank, customer));
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return accounts;
    }

    public static List<Account> redAllByCustomer (int customerId) {
        List<Account> accounts = new ArrayList<>();
        String sqlQuery = "SELECT * FROM accounts WHERE id_customer = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setInt(1, customerId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                int id = resultSet.getInt("id");
                String number = resultSet.getString("number");
                BigDecimal balance = resultSet.getBigDecimal("balance");
                String currency = resultSet.getString("currency");
                Date open_date = resultSet.getDate("open_date");
                int bankId = resultSet.getInt("id_bank");
                Bank bank = getBankById(bankId);
                Customer customer = CustomerRepository.getCustomerById(customerId);
                accounts.add(new Account(id, number, balance, currency, open_date, bank, customer));
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return accounts;
    }

    public static void update (Account accountDTO) {
        String sqlQuery = "UPDATE accounts SET number = ?, balance = ?, currency = ?, open_date = ? " +
                " WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {

            statement.setString(1, accountDTO.getAccountNumber());
            statement.setBigDecimal(2, accountDTO.getBalance());
            statement.setString(3, accountDTO.getCurrency());
            statement.setDate(4, new java.sql.Date(accountDTO.getOpeningDate().getTime()));
            statement.setInt(5, accountDTO.getId());

            statement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String query = "DELETE FROM accounts WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
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

    private static Customer getCustomerById(int customerId) {
        Customer customer = null;
        String sqlQuery = "SELECT * FROM customers WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setInt(1, customerId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                customer = new Customer(customerId, name, null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customer;
    }
}
