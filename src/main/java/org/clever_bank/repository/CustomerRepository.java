package org.clever_bank.repository;

import org.clever_bank.entities.Account;
import org.clever_bank.entities.Bank;
import org.clever_bank.entities.Customer;
import org.clever_bank.utility_classes.AppConfig;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerRepository {
    private static final AppConfig appConfig = new AppConfig();
    private static final String url = appConfig.getUrl();
    private static final String username = appConfig.getUsername();
    private static final String password = appConfig.getPassword();

    public static void createCustomer (Customer customer) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO customers (name) values (?)")) {
            statement.setString(1, customer.getName());
            statement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Customer> getAllCustomers () {
        List<Customer> customerDTOList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM customers")) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                List<Account> accounts = getAccountsByCustomerId(id);
                customerDTOList.add(new Customer (id, name, accounts));
            }

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return customerDTOList;
    }

    public static Customer getCustomerById (int id) {
        Customer customer = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM customers WHERE id = ?")) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                List<Account> accounts = getAccountsByCustomerId(id);
                customer = new Customer(id, name, accounts);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customer;
    }

    public static Customer getCustomerByName (String name) {
        Customer customer = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM customers WHERE name = ?")) {
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                List<Account> accounts = getAccountsByCustomerId(id);
                customer = new Customer(id, name, accounts);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customer;
    }

    public static void updateCustomer (Customer customer) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("UPDATE customers SET name = ? WHERE id = ?")) {
            statement.setString(1, customer.getName());
            statement.setInt(2, customer.getId());
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        for (Account account : customer.getAccounts()) {
            String sqlQuery = "UPDATE accounts SET number = ?, balance = ?, currency = ?, open_date = ?, " +
                    "id_bank = ? WHERE id = ?";

            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement(sqlQuery)) {

                statement.setString(1, account.getAccountNumber());
                statement.setBigDecimal(2, account.getBalance());
                statement.setString(3, account.getCurrency());
                statement.setDate(4, new java.sql.Date(account.getOpeningDate().getTime()));
                statement.setInt(5, account.getBank().getId());
                statement.setInt(6, account.getId());

                statement.executeUpdate();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void deleteCustomer (Customer customer) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM customers WHERE id = ?")) {
            statement.setInt(1, customer.getId());
            statement.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Account> getAccountsByCustomerId (int customerId) throws SQLException {
        String selectQuery = "SELECT * FROM accounts WHERE id_customer = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(selectQuery)) {
            statement.setInt(1, customerId);
            ResultSet resultSet = statement.executeQuery();

            List<Account> accounts = new ArrayList<>();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String number = resultSet.getString("number");
                BigDecimal balance = resultSet.getBigDecimal("balance");
                String currency = resultSet.getString("currency");
                Date openDate = resultSet.getDate("open_date");
                Bank bank = getBankByBankId(resultSet.getInt("id_bank"));
                Account account = new Account(id, number, balance, currency, openDate, bank, null);
                accounts.add(account);
            }
            return accounts;
        }
    }

    private static Bank getBankByBankId (int customerId) {
        String sqlQuery = "SELECT * FROM banks WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setInt(1, customerId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()){
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                return new Bank(id, name, new ArrayList<Account>());
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
