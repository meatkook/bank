package org.clever_bank.db;

import org.clever_bank.services.AppConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSetup {
    private static final AppConfig appConfig = new AppConfig();
    private static final String url = appConfig.getUrl();
    private static final String fullUrl = appConfig.getFullUrl();
    private static final String dbName = appConfig.getDbName();
    private static final String username = appConfig.getUsername();
    private static final String password = appConfig.getPassword();
    public static void setupDatabase() {

        try (Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement()){

            statement.execute("CREATE DATABASE " + dbName);
            System.out.printf("DB %s is created", dbName);
        }
        catch (SQLException e){
            System.out.println(dbName);
        }

        try (Connection connection = DriverManager.getConnection(fullUrl, username, password);
             Statement statement = connection.createStatement()) {

            // Создание таблицы banks
            String createBanksTable = "CREATE TABLE IF NOT EXISTS banks (" +
                    "id SERIAL PRIMARY KEY," +
                    "name VARCHAR UNIQUE NOT NULL" +
                    ")";
            statement.executeUpdate(createBanksTable);

            String addBanksData = """
                    INSERT INTO banks (id, name)
                    VALUES
                    (1, 'Belarusbank'),
                    (2, 'Belinvestbank'),
                    (3, 'Clever-Bank'),
                    (4, 'БелВЭБ'),
                    (5, 'Технобанк')
                    ON CONFLICT (id) DO NOTHING;""";
            statement.executeUpdate(addBanksData);

            // Создание таблицы customers
            String createCustomersTable = "CREATE TABLE IF NOT EXISTS customers (" +
                    "id SERIAL PRIMARY KEY," +
                    "name VARCHAR NOT NULL" +
                    ")";
            statement.executeUpdate(createCustomersTable);

            String addCustomersData = """
                    INSERT INTO customers (id, name)
                    VALUES
                    (1, 'Кокотов Артём Семёнович'),
                    (2, 'Астахова Анна Андреевна'),
                    (3, 'Джесика Паркер'),
                    (4, 'Джо Смитт'),
                    (5, 'Сипукин Андрей Петрович'),
                    (6, 'Иванов Иван Иванович'),
                    (7, 'Петров Петр Петрович'),
                    (8, 'Сидоров Сидор Сидорович'),
                    (9, 'Смирнова Елена Александровна'),
                    (10, 'Кузнецова Ольга Владимировна'),
                    (11, 'Васильева Мария Ивановна'),
                    (12, 'Попов Алексей Петрович'),
                    (13, 'Новикова Екатерина Сергеевна'),
                    (14, 'Морозова Анастасия Александровна'),
                    (15, 'Федорова Елизавета Дмитриевна'),
                    (16, 'Михайлова Александра Игоревна'),
                    (17, 'Алексеева Виктория Владимировна'),
                    (18, 'Соколова Алина Алексеевна'),
                    (19, 'Волкова Евгения Андреевна'),
                    (20, 'Козлова Марина Сергеевна')
                    ON CONFLICT (id) DO NOTHING;""";
            statement.executeUpdate(addCustomersData);

            // Создание таблицы accounts
            String createAccountsTable = "CREATE TABLE IF NOT EXISTS accounts (" +
                    "id SERIAL PRIMARY KEY," +
                    "number VARCHAR," +
                    "balance NUMERIC," +
                    "currency VARCHAR NOT NULL," +
                    "open_date DATE," +
                    "id_bank INT REFERENCES banks(id)," +
                    "id_customer INT REFERENCES customers(id)," +
                    "interest_date DATE" +
                    ")";
            statement.executeUpdate(createAccountsTable);
            String addAccountsData = """
                    INSERT INTO accounts (id, number, balance, currency, open_date, id_bank, id_customer, interest_date)
                    VALUES
                    (1, 'AS12 ASDG 1200 2132 ASDA 353A 213W', 100.50, 'BYN', '2019-08-05', 1, 1, '2023-08-31'),
                    (2, '7FR8 AW34 R765 123Q NFYR 6T45 9I87', 2000.00, 'USD', '2020-08-05', 2, 2, '2023-08-31'),
                    (3, 'G5H6 8J9K L0P1 Q2W3 E4R5 T6Y7 U8I9', 500.25, 'BYN', '2017-03-15', 3, 3, '2023-08-31'),
                    (4, 'B2N3 M4K5 J6H7 G8F9 D0S1 A2Q3 W4E5', 1500.75, 'USD', '2018-06-20', 4, 4, '2023-08-31'),
                    (5, 'Z9X8 C7V6 B5N4 M3K2 L1J0 H9G8 F7D6', 250.00, 'BYN', '2021-01-10', 5, 5, '2023-08-31'),
                    (6, 'QW12 ER34 TY56 UI78 OP90 AS12 DF34', 3000.00, 'USD', '2019-11-25', 1, 6, '2023-08-31'),
                    (7, 'GH56 JK78 LM90 ZX12 CV34 BN56 MQ78', 800.50, 'BYN', '2022-04-17', 2, 7, '2023-08-31'),
                    (8, 'PL12 OK34 IU56 YH78 TG90 RF12 ED34', 700.25, 'USD', '2017-09-08', 3, 8, '2023-08-31'),
                    (9, 'WS56 XC78 VB90 NM12 LK34 JH56 GY78', 1200.75, 'BYN', '2020-12-01', 4, 9, '2023-08-31'),
                    (10, 'UI12 OP34 AS56 DF78 GH90 JK12 LM34', 50.00, 'USD', '2018-03-27', 5, 10, '2023-08-31'),
                    (11, 'ZX56 CV78 BN90 MQ12 WS34 XC56 VB78', 1800.50, 'BYN', '2021-06-13', 1, 11, '2023-08-31'),
                    (12, 'NM12 LK34 JH56 GY78 UI12 OP34 AS56', 900.25, 'USD', '2019-08-15', 2, 12, '2023-08-31'),
                    (13, 'DF78 GH90 JK12 LM34 ZX56 CV78 BN90', 300.75, 'BYN', '2022-03-22', 3, 13, '2023-08-31'),
                    (14, 'MQ12 WS34 XC56 VB78 NM12 LK34 JH56', 600.00, 'USD', '2017-08-15', 4, 14, '2023-08-31'),
                    (15, 'GY78 UI12 OP34 AS56 DF78 GH90 JK12', 1500.50, 'BYN', '2020-11-08', 5, 15, '2023-08-31'),
                    (16, 'LM34 ZX56 CV78 BN90 MQ12 WS34 XC56', 400.25, 'USD', '2018-02-01', 1, 16, '2023-08-31'),
                    (17, 'VB78 NM12 LK34 JH56 GY78 UI12 OP34', 800.75, 'BYN', '2021-04-18', 2, 17, '2023-08-31'),
                    (18, 'AS56 DF78 GH90 JK12 LM34 ZX56 CV78', 1000.00, 'USD', '2019-09-10', 3, 18, '2023-08-31'),
                    (19, 'BN90 MQ12 WS34 XC56 VB78 NM12 LK34', 250.50, 'BYN', '2022-12-05', 4, 19, '2023-08-31'),
                    (20, 'JH56 GY78 UI12 OP34 AS56 DF78 GH90', 1800.25, 'USD', '2016-02-03', 5, 20, '2023-08-31'),
                    (21, 'XC56 VB78 NM12 LK34 JH56 GY78 UI12', 700.00, 'BYN', '2021-09-20', 1, 1, '2023-08-31'),
                    (22, 'OP34 AS56 DF78 GH90 JK12 LM34 ZX56', 1200.50, 'USD', '2018-12-13', 2, 2, '2023-08-31'),
                    (23, 'CV78 BN90 MQ12 WS34 XC56 VB78 NM12', 300.25, 'BYN', '2021-03-01', 3, 3, '2023-08-31'),
                    (24, 'AS12 DF34 GH56 JK78 LM90 ZX12 CV34', 600.75, 'USD', '2019-07-18', 4, 4, '2023-08-31'),
                    (25, 'BN56 MQ78 WS56 XC78 VB90 NM12 LK34', 1500.00, 'BYN', '2022-09-25', 5, 5, '2023-08-31'),
                    (26, 'GH90 JK12 LM34 ZX56 CV78 BN90 MQ12', 400.50, 'USD', '2020-09-20', 1, 6, '2023-08-31'),
                    (27, 'WS34 XC56 VB78 NM12 LK34 JH56 GY78', 800.25, 'BYN', '2023-01-10', 2, 7, '2023-08-31'),
                    (28, 'UI12 OP34 AS56 DF78 GH90 JK12 LM34', 1000.75, 'USD', '2019-11-25', 3, 8, '2023-08-31'),
                    (29, 'ZX56 CV78 BN90 MQ12 WS34 XC56 VB78', 250.00, 'BYN', '2022-04-17', 4, 9, '2023-08-31'),
                    (30, 'NM12 LK34 JH56 GY78 UI12 OP34 AS56', 1800.50, 'USD', '2017-09-08', 5, 10, '2023-08-31'),
                    (31, 'DF78 GH90 JK12 LM34 ZX56 CV78 BN90', 700.25, 'BYN', '2020-12-01', 1, 11, '2023-08-31'),
                    (32, 'MQ12 WS34 XC56 VB78 NM12 LK34 JH56', 1200.75, 'USD', '2018-03-27', 2, 12, '2023-08-31'),
                    (33, 'GY78 UI12 OP34 AS56 DF78 GH90 JK12', 300.00, 'BYN', '2021-06-13', 3, 13, '2023-08-31'),
                    (34, 'LM34 ZX56 CV78 BN90 MQ12 WS34 XC56', 600.50, 'USD', '2019-08-15', 4, 14, '2023-08-31'),
                    (35, 'VB78 NM12 LK34 JH56 GY78 UI12 OP34', 1500.25, 'BYN', '2022-03-22', 5, 15, '2023-08-31'),
                    (36, 'AS56 DF78 GH90 JK12 LM34 ZX56 CV78', 400.75, 'USD', '2017-08-15', 1, 16, '2023-08-31'),
                    (37, 'BN90 MQ12 WS34 XC56 VB78 NM12 LK34', 800.00, 'BYN', '2020-11-08', 2, 17, '2023-08-31'),
                    (38, 'JH56 GY78 UI12 OP34 AS56 DF78 GH90', 1000.50, 'USD', '2018-02-01', 3, 18, '2023-08-31'),
                    (39, 'XC56 VB78 NM12 LK34 JH56 GY78 UI12', 250.25, 'BYN', '2021-04-18', 4, 19, '2023-08-31'),
                    (40, 'OP34 AS56 DF78 GH90 JK12 LM34 ZX56', 1800.00, 'USD', '2019-09-10', 5, 20, '2023-08-31')
                    ON CONFLICT (id) DO NOTHING;""";
            statement.executeUpdate(addAccountsData);

            // Создание таблицы types
            String createTypesTable = "CREATE TABLE IF NOT EXISTS types (" +
                    "id SERIAL PRIMARY KEY," +
                    "name VARCHAR" +
                    ")";
            statement.executeUpdate(createTypesTable);

            String addTypesData = """
                    INSERT INTO types (id, name)
                    VALUES
                    (1, 'Депозит'),
                    (2, 'Снятие средств'),
                    (3, 'Перевод')
                    -- Если id уже существует, то ничего не делать
                    ON CONFLICT (id) DO NOTHING;""";
            statement.executeUpdate(addTypesData);

            // Создание таблицы transactions
            String createTransactionsTable = "CREATE TABLE IF NOT EXISTS transactions (" +
                    "id SERIAL PRIMARY KEY," +
                    "date TIMESTAMPTZ," +
                    "money NUMERIC," +
                    "id_type INT REFERENCES types(id)," +
                    "id_sender INT REFERENCES accounts(id)," +
                    "id_recipient INT REFERENCES accounts(id)" +
                    ")";
            statement.executeUpdate(createTransactionsTable);

        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
}
