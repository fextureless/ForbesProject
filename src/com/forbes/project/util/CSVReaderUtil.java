package com.forbes.project.util;

import com.forbes.project.entity.Billionaire;
import com.opencsv.CSVReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class CSVReaderUtil {

    public static List<Billionaire> readBillionaires(String filePath) {
        List<Billionaire> billionaires = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            reader.skip(1); // Пропускаем заголовок
            List<String[]> rows = reader.readAll();

            for (String[] row : rows) {
                if (row.length < 7) continue;

                try {
                    Billionaire billionaire = new Billionaire();
                    billionaire.setRank(Integer.parseInt(row[0].trim()));
                    billionaire.setName(row[1].trim());
                    billionaire.setNetWorth(Double.parseDouble(row[2].trim().replace(",", ".")));

                    String ageStr = row[3].trim();
                    billionaire.setAge(ageStr.isEmpty() ? 0 : Integer.parseInt(ageStr));

                    billionaire.setCountry(row[4].trim());
                    billionaire.setSource(row[5].trim().replace("\"", ""));
                    billionaire.setIndustry(row[6].trim());

                    billionaires.add(billionaire);
                } catch (NumberFormatException e) {
                    // Пропускаем некорректные строки
                }
            }

            System.out.println("Прочитано записей: " + billionaires.size());

        } catch (Exception e) {
            System.err.println("Ошибка при чтении CSV: " + e.getMessage());
        }

        return billionaires;
    }

    public static void printFirstNBillionaires(List<Billionaire> billionaires, int n) {
        System.out.println("Первые " + n + " записей:");
        for (int i = 0; i < Math.min(n, billionaires.size()); i++) {
            System.out.println(billionaires.get(i));
        }
    }
}