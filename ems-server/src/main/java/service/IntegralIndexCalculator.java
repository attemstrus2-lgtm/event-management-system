package com.eventmanagement.service;

public class IntegralIndexCalculator {

    public static double calculate(double f1Closeness, double f2Criticality, double f3Dependent) {
        validateFactor(f1Closeness, "F1 (близькість дедлайну)");
        validateFactor(f2Criticality, "F2 (критичність)");
        validateFactor(f3Dependent, "F3 (кількість залежних завдань)");

        double raw = 0.40 * f1Closeness + 0.35 * f2Criticality + 0.25 * f3Dependent;
        return Math.round(raw * 100.0) / 100.0;
    }

    public static double normalizeDependentTasks(int dependentCount, int maxExpected) {
        if (dependentCount < 0 || maxExpected <= 0) {
            throw new IllegalArgumentException("Некоректні параметри для нормування F3");
        }
        double norm = ((double) dependentCount / maxExpected) * 100.0;
        return Math.max(0.0, Math.min(100.0, Math.round(norm * 100.0) / 100.0));
    }

    private static void validateFactor(double value, String name) {
        if (value < 0.0 || value > 100.0) {
            throw new IllegalArgumentException(name + " має бути в діапазоні 0..100");
        }
    }
}
