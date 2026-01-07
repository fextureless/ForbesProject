package com.forbes.project.entity;

public class Billionaire {
    private int rank;
    private String name;
    private double netWorth;
    private int age;
    private String country;
    private String source;
    private String industry;

    // Конструктор по умолчанию
    public Billionaire() {}

    // Конструктор со всеми параметрами
    public Billionaire(int rank, String name, double netWorth, int age,
                       String country, String source, String industry) {
        this.rank = rank;
        this.name = name;
        this.netWorth = netWorth;
        this.age = age;
        this.country = country;
        this.source = source;
        this.industry = industry;
    }

    // Геттеры и сеттеры
    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getNetWorth() { return netWorth; }
    public void setNetWorth(double netWorth) { this.netWorth = netWorth; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }

    @Override
    public String toString() {
        return String.format("%3d. %-25s: $%5.1f млрд, %d лет, %s",
                rank, name, netWorth, age, country);
    }
}