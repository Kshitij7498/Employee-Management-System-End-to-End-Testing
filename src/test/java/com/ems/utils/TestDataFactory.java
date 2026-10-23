package com.ems.utils;

import com.ems.model.Employee;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/** Generates unique, valid test data so tests never collide with each other. */
public final class TestDataFactory {

    private static final AtomicLong COUNTER = new AtomicLong(System.currentTimeMillis() % 100000000L);
    private static final Random RANDOM = new Random();

    private TestDataFactory() {
    }

    public static Employee validEmployee() {
        long n = COUNTER.incrementAndGet();
        return new Employee()
                .firstName("Alex")
                .lastName("Tester")
                .email("alex.tester." + n + "@company.com")
                .phone("9" + String.format("%09d", RANDOM.nextInt(1000000000)))
                .department("Engineering")
                .designation("QA Engineer")
                .salary("65000")
                .joiningDate("2024-01-15");
    }

    /** Alphanumeric username, 4-15 chars, unique per call. */
    public static String uniqueUsername() {
        return "qa" + COUNTER.incrementAndGet();
    }
}
