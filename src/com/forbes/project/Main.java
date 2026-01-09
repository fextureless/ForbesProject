package com.forbes.project;

import com.forbes.project.chart.ChartGenerator;
import com.forbes.project.dao.DatabaseManager;
import com.forbes.project.entity.Billionaire;
import com.forbes.project.util.CSVReaderUtil;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    @SuppressWarnings({"unused", "EmptyTryBlock", "ResultOfMethodCallIgnored"})
    public static void main(String[] args) {
        System.out.println("ПРОЕКТ: АНАЛИЗ МИЛЛИАРДЕРОВ FORBES");

        String csvPath = "data/Forbes.csv";
        System.out.println("\nПуть к CSV файлу: " + csvPath);

        java.io.File file = new java.io.File(csvPath);
        if (!file.exists()) {
            System.err.println("ОШИБКА: Файл не найден!");
            System.err.println("Искали: " + file.getAbsolutePath());
            return;
        }

        System.out.println("\nШАГ 1: ЧТЕНИЕ ДАННЫХ ИЗ CSV ФАЙЛА");

        List<Billionaire> billionaires = CSVReaderUtil.readBillionaires(csvPath);

        if (billionaires.isEmpty()) {
            System.out.println("ОШИБКА: Не удалось прочитать данные!");
            return;
        }

        CSVReaderUtil.printFirstNBillionaires(billionaires, 5);
        waitForUser("Нажмите Enter для продолжения...");

        System.out.println("\nШАГ 2: РАБОТА С БАЗОЙ ДАННЫХ SQLite");

        DatabaseManager.connect();
        DatabaseManager.createTable();

        System.out.print("\nОчистить таблицу перед загрузкой? (y/n, по умолчанию n): ");
        Scanner scanner = new Scanner(System.in);
        String answer = scanner.nextLine().trim().toLowerCase();

        if (answer.equals("y") || answer.equals("yes") || answer.equals("да")) {
            DatabaseManager.clearTable();
        }

        DatabaseManager.saveBillionaires(billionaires);

        System.out.println("\nШАГ 3: ВЫПОЛНЕНИЕ ЗАДАНИЙ ПО ВАРИАНТУ");
        System.out.println("----------------------------------------");

        DatabaseManager.findYoungestFrenchBillionaire();
        waitForUser("Нажмите Enter для следующего задания...");

        DatabaseManager.findTopUSEnergyBillionaire();
        waitForUser("Нажмите Enter для подготовки данных к графику...");

        Map<String, Double> capitalByCountry = DatabaseManager.getTotalCapitalByCountry();

        if (!capitalByCountry.isEmpty()) {
            waitForUser("Нажмите Enter для создания диаграмм...");

            System.out.println("\nШАГ 4: СОЗДАНИЕ ДИАГРАММ");
            System.out.println("----------------------------------------");

            ChartGenerator.createCapitalByCountryChart(capitalByCountry);
        }

        System.out.println("\nШАГ 5: ЗАВЕРШЕНИЕ РАБОТЫ");
        System.out.println("----------------------------------------");

        DatabaseManager.close();
        scanner.close();

        System.out.println("\nВСЕ ЗАДАНИЯ ВЫПОЛНЕНЫ УСПЕШНО!");
    }

    private static void waitForUser(String message) {
        System.out.print("\n" + message);
        try {
            new Scanner(System.in).nextLine();
        } catch (Exception e) {
        }
    }
}