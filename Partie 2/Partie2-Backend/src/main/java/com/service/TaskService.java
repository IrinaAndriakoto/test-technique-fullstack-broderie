/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.service;

import com.model.Task;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author ASUS
 */
public class TaskService {
        public static List<Task> getBlockedTasksSorted(List<Task> tasks) {
        return tasks.stream()
            .filter(Task::isBlocked)
            .sorted(
                Comparator.comparingInt(Task::getPriority)
                          .thenComparing(Task::getName)
            )
            .collect(Collectors.toList());
    }
}
