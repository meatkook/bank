package org.clever_bank.repository;

import org.clever_bank.entities.Account;
import org.clever_bank.entities.Bank;
import org.clever_bank.entities.Customer;
import org.clever_bank.services.AppConfig;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerRepository {
    /**
     * Application configuration.
     * This class reads the configuration from the 'application.yml' file located in the resources' directory.
     */
    private static final AppConfig appConfig = new AppConfig();

    /**
     * URL for connecting to the database.
     */
    private static final String url = appConfig.getFullUrl();

    /**
     * Username for connecting to the database.
     */
    private static final String username = appConfig.getUsername();

    /**
     * Username for connecting to the database.
     */
    private static final String password = appConfig.getPassword();

    /**
     * Creates a new customer in the database.
     *
     * @param customer is the customer entity to be created in DB.
     */
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

    /**
     * Retrieves all customers from the database.
     *
     * @return A list of Customer entities representing all customers in the database.
     */
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

    /**
     * Retrieves a customer from the database based on the specified ID.
     *
     * @param id The ID of the customer to retrieve.
     * @return The Customer entity representing the customer with the specified ID, or null if not found.
     */
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

    /**
     * Updates a customer in the database.
     *
     * @param customer The customer entity to be updated.
     */
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
            AccountRepository.update(account);
        }
    }

    /**
     * Deletes a customer from the database.
     *
     * @param customer The customer entity to be deleted.
     */
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

    /**
     * Retrieves a list of accounts associated with a customer ID from the database.
     *
     * @param customerId The ID of the customer.
     * @return A list of Account entities associated with the customer ID.
     * @throws SQLException If an SQL exception occurs.
     */
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

    /**
     * Retrieves a bank based on the specified bank ID from the database.
     *
     * @param customerId The ID of the bank.
     * @return The Bank entity with empty list of accounts representing the bank with the specified ID, or null if not found.
     */
    private static Bank getBankByBankId (int customerId) {
        String sqlQuery = "SELECT * FROM banks WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setInt(1, customerId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()){
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                return new Bank(id, name, new ArrayList<>());
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
