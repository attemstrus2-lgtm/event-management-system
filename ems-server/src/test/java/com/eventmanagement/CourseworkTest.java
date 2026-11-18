package com.eventmanagement;

import com.eventmanagement.model.*;
import com.eventmanagement.service.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CourseworkTest {

    @Test
    void testIntegralIndexCalculationStandard() {
        double result = IntegralIndexCalculator.calculate(85.0, 90.0, 50.0);
        // 0.40 * 85 + 0.35 * 90 + 0.25 * 50 = 34 + 31.5 + 12.5 = 78.0
        assertEquals(78.0, result, 0.001);
    }

    @Test
    void testIntegralIndexBoundaryValues() {
        assertEquals(0.0, IntegralIndexCalculator.calculate(0.0, 0.0, 0.0), 0.001);
        assertEquals(100.0, IntegralIndexCalculator.calculate(100.0, 100.0, 100.0), 0.001);
    }

    @Test
    void testIntegralIndexInvalidFactorThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> IntegralIndexCalculator.calculate(-5.0, 50.0, 50.0));
        assertThrows(IllegalArgumentException.class,
                () -> IntegralIndexCalculator.calculate(50.0, 120.0, 50.0));
    }

    @Test
    void testNormalizeDependentTasks() {
        assertEquals(50.0, IntegralIndexCalculator.normalizeDependentTasks(5, 10), 0.001);
        assertEquals(100.0, IntegralIndexCalculator.normalizeDependentTasks(15, 10), 0.001);
    }

    @Test
    void testAllowedLifecycleTransitions() {
        assertTrue(TaskService.isAllowedTransition(TaskStatus.CREATED, TaskStatus.ASSIGNED));
        assertTrue(TaskService.isAllowedTransition(TaskStatus.ASSIGNED, TaskStatus.IN_PROGRESS));
        assertTrue(TaskService.isAllowedTransition(TaskStatus.IN_PROGRESS, TaskStatus.DONE));
    }

    @Test
    void testForbiddenLifecycleTransitions() {
        assertFalse(TaskService.isAllowedTransition(TaskStatus.CREATED, TaskStatus.DONE));
        assertFalse(TaskService.isAllowedTransition(TaskStatus.DONE, TaskStatus.CREATED));
        assertFalse(TaskService.isAllowedTransition(TaskStatus.IN_PROGRESS, TaskStatus.ASSIGNED));
    }

    @Test
    void testTaskValidationSuccess() {
        Task task = new Task();
        task.setEventId(1);
        task.setResponsibleId(1);
        task.setTitle("Тестове завдання");
        task.setDeadline(LocalDateTime.now().plusDays(2));
        task.setF1Closeness(80.0);
        task.setF2Criticality(70.0);
        task.setF3Dependent(60.0);

        assertDoesNotThrow(() -> TaskService.validate(task));
    }

    @Test
    void testTaskValidationEmptyTitleFails() {
        Task task = new Task();
        task.setEventId(1);
        task.setResponsibleId(1);
        task.setTitle("   ");
        task.setDeadline(LocalDateTime.now().plusDays(1));

        assertThrows(IllegalArgumentException.class, () -> TaskService.validate(task));
    }

    @Test
    void testEventAndPersonValidation() {
        Event invalidEvent = new Event(0, "", "Опис", LocalDateTime.now());
        assertThrows(IllegalArgumentException.class, () -> EventService.validate(invalidEvent));

        ResponsiblePerson invalidPerson = new ResponsiblePerson(0, "Іванов І.І.", "", "Менеджер");
        assertThrows(IllegalArgumentException.class, () -> ResponsiblePersonService.validate(invalidPerson));
    }

    @Test
    void testResourceValidationNegativeQuantityFails() {
        Resource invalidResource = new Resource(0, 1, "Проєктор", "Мультимедіа", -2);
        assertThrows(IllegalArgumentException.class, () -> ResourceService.validate(invalidResource));
    }
}
