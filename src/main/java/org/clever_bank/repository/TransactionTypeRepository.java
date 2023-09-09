package org.clever_bank.repository;

import org.clever_bank.entities.TransactionType;
import org.clever_bank.services.AppConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The TransactionTypeRepository class is responsible for retrieving transaction types from the database.
 */
public class TransactionTypeRepository {

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
     * Retrieves all transaction types from the database.
     *
     * @return A list of TransactionType objects representing all transaction types.
     */
    public static List<TransactionType> readAllTransactionTypes() {
        List<TransactionType> transactionTypes = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT  * FROM types")) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                transactionTypes.add(new TransactionType(id, name));
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return transactionTypes;
    }

    /**
     * Retrieves a transaction type from the database based on its ID.
     *
     * @param id The ID of the transaction type to retrieve.
     * @return A TransactionType object representing the transaction type with the specified ID, or null if not found.
     */
    public static TransactionType readTransactionTypeById (int id) {
        String sqlQuery = "SELECT * FROM types\n" +
                "WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("name");
                return new TransactionType(id, name);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves a transaction type from the database based on its name.
     *
     * @param name The name of the transaction type to retrieve.
     * @return A TransactionType object representing the transaction type with the specified name, or null if not found.
     */
    public static TransactionType getTransactionTypeByName (String name) {
        String sqlQuery = "SELECT * FROM types\n" +
                "WHERE types.name = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                return new TransactionType(id, name);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

