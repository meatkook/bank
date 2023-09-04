package org.clever_bank.repository;

import org.clever_bank.entities.TransactionType;
import org.clever_bank.utility_classes.AppConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionTypeRepository {

    private static final AppConfig appConfig = new AppConfig();
    private static final String url = appConfig.getFullUrl();
    private static final String username = appConfig.getUsername();
    private static final String password = appConfig.getPassword();

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

