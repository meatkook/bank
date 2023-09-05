package org.clever_bank.services;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class InterestCalculator {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void startInterestCalculation() {
        // Запуск задачи каждые 30 секунд
        scheduler.scheduleAtFixedRate(this::calculateInterest, 0, 30, TimeUnit.SECONDS);
    }

    private void calculateInterest() {
        // Код для начисления процентов на остаток счета
        // Получить значение процента из конфигурационного файла и применить его к каждому счету
    }

    public void stopInterestCalculation() {
        scheduler.shutdown();
    }
}
