package com.forbes.project.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import java.io.File;
import java.util.Map;

public class ChartGenerator {

    @SuppressWarnings({"unchecked", "rawtypes", "unused"})
    public static void createCapitalByCountryChart(Map<String, Double> capitalByCountry) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("СОЗДАНИЕ ДИАГРАММ");
        System.out.println("=".repeat(70));

        try {
            // 1. Создаем столбчатую диаграмму (топ-15 стран)
            System.out.println("📊 Создаем столбчатую диаграмму...");
            createBarChart(capitalByCountry);

            // 2. Создаем круговую диаграмму (топ-10 стран)
            System.out.println("📈 Создаем круговую диаграмму...");
            createPieChart(capitalByCountry);

            // 3. Создаем горизонтальную диаграмму (топ-20 стран)
            System.out.println("📋 Создаем горизонтальную диаграмму...");
            createHorizontalBarChart(capitalByCountry);

            System.out.println("✅ Все диаграммы созданы!");

        } catch (Exception e) {
            System.err.println("❌ Ошибка при создании диаграмм: " + e.getMessage());
            printTextChart(capitalByCountry);
        }
    }

    private static void createBarChart(Map<String, Double> capitalByCountry) {
        try {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            System.out.println("\nТоп-15 стран для столбчатой диаграммы:");
            int count = 0;
            for (Map.Entry<String, Double> entry : capitalByCountry.entrySet()) {
                if (count < 15) {
                    String country = entry.getKey();
                    double capital = entry.getValue();
                    dataset.addValue(capital, "Капитал", country);
                    System.out.printf("   %2d. %-20s: $%.1f млрд%n",
                            count + 1, country, capital);
                    count++;
                }
            }

            JFreeChart barChart = ChartFactory.createBarChart(
                    "ТОП-15 СТРАН ПО ОБЩЕМУ КАПИТАЛУ МИЛЛИАРДЕРОВ",
                    "Страна",
                    "Капитал (млрд $)",
                    dataset,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false
            );

            barChart.getCategoryPlot().getDomainAxis()
                    .setMaximumCategoryLabelWidthRatio(0.8f);

            File file = new File("capital_bar_chart.png");
            ChartUtils.saveChartAsPNG(file, barChart, 1400, 800);
            System.out.println("   Сохранено: " + file.getName());

        } catch (Exception e) {
            System.err.println("Ошибка при создании столбчатой диаграммы: " + e.getMessage());
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void createPieChart(Map<String, Double> capitalByCountry) {
        try {
            DefaultPieDataset dataset = new DefaultPieDataset();

            double others = 0;
            int count = 0;

            for (Map.Entry<String, Double> entry : capitalByCountry.entrySet()) {
                if (count < 10) {
                    dataset.setValue(entry.getKey(), entry.getValue());
                    count++;
                } else {
                    others += entry.getValue();
                }
            }

            if (others > 0) {
                dataset.setValue("Другие страны", others);
            }

            JFreeChart pieChart = ChartFactory.createPieChart(
                    "РАСПРЕДЕЛЕНИЕ КАПИТАЛА МИЛЛИАРДЕРОВ ПО СТРАНАМ",
                    dataset,
                    true,
                    true,
                    false
            );

            File file = new File("capital_pie_chart.png");
            ChartUtils.saveChartAsPNG(file, pieChart, 1000, 800);
            System.out.println("   Сохранено: " + file.getName());

        } catch (Exception e) {
            System.err.println("Ошибка при создании круговой диаграммы: " + e.getMessage());
        }
    }

    private static void createHorizontalBarChart(Map<String, Double> capitalByCountry) {
        try {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            int count = 0;
            for (Map.Entry<String, Double> entry : capitalByCountry.entrySet()) {
                if (count < 20) {
                    dataset.addValue(entry.getValue(), "Капитал", entry.getKey());
                    count++;
                }
            }

            JFreeChart chart = ChartFactory.createBarChart(
                    "ТОП-20 СТРАН ПО КАПИТАЛУ МИЛЛИАРДЕРОВ",
                    "Капитал (млрд $)",
                    "Страна",
                    dataset,
                    PlotOrientation.HORIZONTAL,
                    true,
                    true,
                    false
            );

            File file = new File("capital_horizontal_chart.png");
            ChartUtils.saveChartAsPNG(file, chart, 1200, 1000);
            System.out.println("   Сохранено: " + file.getName());

        } catch (Exception e) {
            System.err.println("Ошибка при создании горизонтальной диаграммы: " + e.getMessage());
        }
    }

    private static void printTextChart(Map<String, Double> capitalByCountry) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("ТЕКСТОВАЯ ВИЗУАЛИЗАЦИЯ: Общий капитал по странам");
        System.out.println("=".repeat(80));

        capitalByCountry.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(20)
                .forEach(entry -> {
                    String country = entry.getKey();
                    double capital = entry.getValue();
                    int barLength = (int) (capital / 50);
                    String bar = "█".repeat(Math.max(1, barLength));
                    System.out.printf("%-25s $%12.1f  %s%n", country, capital, bar);
                });
    }
}