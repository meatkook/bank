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

/**
 * Repository class for working with accounts in the database.
 */
public class AccountRepository {
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
     * Method to create a new account in the database.
     *
     * @param account The account to create.
     */
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

    /**
     * Retrieves an account from the database based on the account ID.
     *
     * @param accountId The ID of the account to retrieve.
     * @return The retrieved account, or null if not found.
     */
    public static Account read (int accountId) {
        String sqlQuery = "SELECT * FROM accounts WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setInt(1, accountId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return getAccountFromResultSet(resultSet);
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves an account from the database based on the account number.
     *
     * @param number The account number to retrieve.
     * @return The retrieved account, or null if not found.
     */
    public static Account read (String number) {
        String sqlQuery = "SELECT * FROM accounts WHERE number = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setString(1, number);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return getAccountFromResultSet(resultSet);
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Method to read all accounts from the database.
     *
     * @return List of all accounts.
     */
    public static List<Account> readAll () {
        List<Account> accounts = new ArrayList<>();
        String sqlQuery = "SELECT * FROM accounts";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery (sqlQuery)) {
            while (resultSet.next()) {
                Account account = getAccountFromResultSet(resultSet);
                accounts.add(account);
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return accounts;
    }

    /**
     * Retrieves all accounts associated with a specific customer.
     *
     * @param customerId The ID of the customer.
     * @return A list of accounts associated with the customer.
     */
    public static List<Account> redAllByCustomer (int customerId) {
        List<Account> accounts = new ArrayList<>();
        String sqlQuery = "SELECT * FROM accounts WHERE id_customer = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setInt(1, customerId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                Account account = getAccountFromResultSet(resultSet);
                accounts.add(account);
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return accounts;
    }

    /**
     * Updates an existing account in the database.
     *
     * @param accountDTO The updated account information.
     */
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

    /**
     * Deletes an account from the database.
     *
     * @param id The ID of the account to delete.
     */
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

    /**
     * Retrieves a bank from the database based on the bank ID.
     *
     * @param bankId The ID of the bank to retrieve.
     * @return The bank entity with empty account list if found, null otherwise.
     */
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

    /**
     * Retrieves a customer from the database based on the customer ID.
     *
     * @param customerId The ID of the customer to retrieve.
     * @return The customer entity with empty account list if found, null otherwise.
     */
    private static Customer getCustomerById(int customerId) {
        Customer customer = null;
        String sqlQuery = "SELECT * FROM customers WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setInt(1, customerId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                List<Account> accounts = new ArrayList<>();
                customer = new Customer(customerId, name, accounts);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customer;
    }

    /**
     * Retrieves an Account object from a ResultSet.
     * @param resultSet The ResultSet that resulting data set containing the account data.
     * @return An Account entity if the data is successfully retrieved, otherwise null.
     */
    private static Account getAccountFromResultSet (ResultSet resultSet){
        try {
            int id = resultSet.getInt("id");
            String number = resultSet.getString ("number");
            BigDecimal balance = resultSet.getBigDecimal("balance");
            String currency = resultSet.getString("currency");
            Date open_date = resultSet.getDate("open_date");
            int bankId = resultSet.getInt("id_bank");
            Bank bank = getBankById(bankId);
            int customerId = resultSet.getInt("id_customer");
            Customer customer = getCustomerById(customerId);
            return new Account(id, number, balance, currency, open_date, bank, customer);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
