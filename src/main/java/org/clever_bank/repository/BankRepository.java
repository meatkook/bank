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

public class BankRepository {

    private static final AppConfig appConfig = new AppConfig();
    private static final String url = appConfig.getFullUrl();
    private static final String username = appConfig.getUsername();
    private static final String password = appConfig.getPassword();

    public static void create (Bank bank) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO banks (name) values (?)")) {
            statement.setString(1, bank.getName());
            statement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Bank read(int bankId) {
        Bank bank = null;
        String sqlQuery = "SELECT * FROM banks WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setInt(1, bankId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                List<Account> accounts = getAccountsByBankId(bankId);
                bank = new Bank(bankId, name, accounts);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bank;
    }

    public static List<Bank> readAll() {
        List<Bank> bankDTOList = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery ("SELECT * FROM banks")) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                List<Account> accounts = getAccountsByBankId(id);
                Bank bankDTO = new Bank(id, name, accounts);
                bankDTOList.add(bankDTO);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return bankDTOList;
    }

    public static Bank readByName(String name) {
        Bank bank = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM banks WHERE name = ?")) {
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                List<Account> accounts = getAccountsByBankId(id);
                bank = new Bank(id, name, accounts);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bank;
    }

    public static void update (Bank bank) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("UPDATE banks SET name = ? WHERE id = ?")) {
            statement.setString(1, bank.getName());
            statement.setInt(2, bank.getId());
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        for (Account account : bank.getAccounts()) {
            String sqlQuery = "UPDATE accounts SET number = ?, balance = ?, currency = ?, open_date = ?, " +
                    "id_customer = ? WHERE id = ?";

            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement(sqlQuery)) {

                statement.setString(1, account.getAccountNumber());
                statement.setBigDecimal(2, account.getBalance());
                statement.setString(3, account.getCurrency());
                statement.setDate(4, new java.sql.Date(account.getOpeningDate().getTime()));
                statement.setInt(5, account.getCustomer().getId());
                statement.setInt(6, account.getId());

                statement.executeUpdate();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public static void deleteBank (int bankId) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM banks WHERE id = ?")) {
            statement.setInt(1, bankId);
            statement.executeUpdate();

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Account> getAccountsByBankId (int bankId) {
        List<Account> accounts = new ArrayList<>();
        String sqlQuery = "SELECT * FROM accounts WHERE id_bank = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setInt(1, bankId);
            ResultSet resultSet = statement.executeQuery();


            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String number = resultSet.getString("number");
                BigDecimal balance = resultSet.getBigDecimal("balance");
                String currency = resultSet.getString("currency");
                Date openDate = resultSet.getDate("open_date");
                Customer customer = getCustomerByCustomerId(resultSet.getInt("id_customer"));
                Account account = new Account(id, number, balance, currency, openDate, null, customer);
                accounts.add(account);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return accounts;
    }

    private static Customer getCustomerByCustomerId (int customerId) {
        String sqlQuery = "SELECT * FROM customers WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setInt(1, customerId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()){
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                return new Customer(id, name, new ArrayList<Account>());
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
