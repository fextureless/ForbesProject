package com.forbes.project.dao;

import com.forbes.project.entity.Billionaire;
import java.sql.*;
import java.util.*;

@SuppressWarnings({"SqlNoDataSourceInspection", "SqlDialectInspection", "SpellCheckingInspection"})
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:forbes.db";
    private static Connection connection;

    // Подключение к БД
    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Подключение к БД установлено: forbes.db");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite драйвер не найден: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Ошибка подключения к БД: " + e.getMessage());
        }
    }

    // Создание таблицы
    public static void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS billionaires (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                rank INTEGER NOT NULL,
                name TEXT NOT NULL,
                net_worth REAL NOT NULL,
                age INTEGER,
                country TEXT NOT NULL,
                source TEXT,
                industry TEXT
            )
            """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            System.out.println("Таблица 'billionaires' создана");
        } catch (SQLException e) {
            System.err.println("Ошибка создания таблицы: " + e.getMessage());
        }
    }

    // Очистка таблицы
    public static void clearTable() {
        String sql = "DELETE FROM billionaires";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            System.out.println("Таблица очищена");
        } catch (SQLException e) {
            System.err.println("Ошибка очистки таблицы: " + e.getMessage());
        }
    }

    // Сохранение миллиардеров в БД
    public static void saveBillionaires(List<Billionaire> billionaires) {
        String sql = "INSERT INTO billionaires (rank, name, net_worth, age, country, source, industry) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        int savedCount = 0;
        int errorCount = 0;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);

            for (Billionaire b : billionaires) {
                try {
                    pstmt.setInt(1, b.getRank());
                    pstmt.setString(2, b.getName());
                    pstmt.setDouble(3, b.getNetWorth());
                    pstmt.setInt(4, b.getAge());
                    pstmt.setString(5, b.getCountry());
                    pstmt.setString(6, b.getSource());
                    pstmt.setString(7, b.getIndustry());
                    pstmt.addBatch();
                    savedCount++;

                    if (savedCount % 100 == 0) {
                        pstmt.executeBatch();
                    }
                } catch (SQLException e) {
                    errorCount++;
                }
            }

            pstmt.executeBatch();
            connection.commit();

            System.out.println("Сохранение в БД завершено:");
            System.out.println("   Успешно: " + savedCount + " записей");
            System.out.println("   Ошибок: " + errorCount + " записей");

        } catch (SQLException e) {
            System.err.println("Ошибка при сохранении в БД: " + e.getMessage());
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.err.println("Ошибка при откате транзакции: " + ex.getMessage());
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Ошибка при восстановлении autocommit: " + e.getMessage());
            }
        }
    }

    // ЗАДАНИЕ 2: Самый молодой миллиардер из Франции с капиталом > 10 млрд
    public static void findYoungestFrenchBillionaire() {
        System.out.println("\nЗАДАНИЕ 2: Самый молодой миллиардер из Франции (капитал > $10 млрд)");

        String sql = """
            SELECT name, age, net_worth, source, industry 
            FROM billionaires 
            WHERE country = 'France' AND net_worth > 10 
            ORDER BY age ASC 
            LIMIT 1
            """;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                System.out.println("Результат:");
                System.out.println("   Имя:      " + rs.getString("name"));
                System.out.println("   Возраст:  " + rs.getInt("age") + " лет");
                System.out.println("   Капитал:  $" + rs.getDouble("net_worth") + " млрд");
                System.out.println("   Компания: " + rs.getString("source"));
                System.out.println("   Отрасль:  " + rs.getString("industry"));
            }

        } catch (SQLException e) {
            System.err.println("Ошибка выполнения запроса: " + e.getMessage());
        }
    }

    // ЗАДАНИЕ 3: Бизнесмен из США с самым большим капиталом в Energy
    public static void findTopUSEnergyBillionaire() {
        System.out.println("\nЗАДАНИЕ 3: Бизнесмен из США в сфере Energy (максимальный капитал)");

        String sql = """
            SELECT name, source, net_worth, industry 
            FROM billionaires 
            WHERE country = 'United States' 
              AND (LOWER(industry) LIKE '%energy%' 
                   OR LOWER(industry) LIKE '%oil%'
                   OR LOWER(industry) LIKE '%gas%')
            ORDER BY net_worth DESC 
            LIMIT 1
            """;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                System.out.println("Результат:");
                System.out.println("   Имя:      " + rs.getString("name"));
                System.out.println("   Компания: " + rs.getString("source"));
                System.out.println("   Капитал:  $" + rs.getDouble("net_worth") + " млрд");
                System.out.println("   Отрасль:  " + rs.getString("industry"));
            }

        } catch (SQLException e) {
            System.err.println("Ошибка выполнения запроса: " + e.getMessage());
        }
    }

    // ЗАДАНИЕ 1: Получение данных для графика - общий капитал по странам
    public static Map<String, Double> getTotalCapitalByCountry() {
        System.out.println("\nЗАДАНИЕ 1: Подготовка данных для графика (общий капитал по странам)");

        Map<String, Double> capitalByCountry = new LinkedHashMap<>();

        String sql = """
            SELECT country, SUM(net_worth) as total_capital
            FROM billionaires 
            GROUP BY country 
            ORDER BY total_capital DESC
            """;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("Общий капитал по странам:");
            while (rs.next()) {
                String country = rs.getString("country");
                double totalCapital = rs.getDouble("total_capital");
                capitalByCountry.put(country, totalCapital);
                System.out.printf("   %-20s: $%.1f млрд%n", country, totalCapital);
            }

        } catch (SQLException e) {
            System.err.println("Ошибка выполнения запроса: " + e.getMessage());
        }

        return capitalByCountry;
    }

    // Закрытие соединения
    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("\nСоединение с БД закрыто");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка закрытия соединения: " + e.getMessage());
        }
    }
}