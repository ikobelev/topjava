package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

        // calculate calories per date and filter meals by time period
        Map<LocalDate, Integer> caloriesPerDate = new HashMap<>();
        List<UserMeal> filteredMeals = new ArrayList<>();
        for (UserMeal meal : meals) {
            caloriesPerDate.put(
                    meal.getDateTime().toLocalDate(),
                    meal.getCalories() + caloriesPerDate.getOrDefault(meal.getDateTime().toLocalDate(), 0));
            if (isBetweenRange(meal.getDateTime(), startTime, endTime)) {
                filteredMeals.add(meal);
            }
        }

        // fill output collection
        List<UserMealWithExcess> filteredMealsWithExcess = new ArrayList<>();
        for (UserMeal meal : filteredMeals) {
            filteredMealsWithExcess.add(
                    new UserMealWithExcess(
                            meal.getDateTime(),
                            meal.getDescription(),
                            meal.getCalories(),
                            caloriesPerDate.get(meal.getDateTime().toLocalDate()) > caloriesPerDay
                    )
            );
        }
        return filteredMealsWithExcess;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesPerDate = new HashMap<>();
        return meals.stream()
                .map(meal -> {
                    caloriesPerDate.put(
                            meal.getDateTime().toLocalDate(),
                            meal.getCalories() + caloriesPerDate.getOrDefault(meal.getDateTime().toLocalDate(), 0));
                    return meal;
                })
                .filter(meal -> isBetweenRange(meal.getDateTime(), startTime, endTime))
                .collect(Collectors.toList())
                .stream()
                .map(meal -> new UserMealWithExcess(
                        meal.getDateTime(),
                        meal.getDescription(),
                        meal.getCalories(),
                        caloriesPerDate.get(meal.getDateTime().toLocalDate()) > caloriesPerDay
                ))
                .collect(Collectors.toList());
    }

    private static boolean isBetweenRange(LocalDateTime dateTime, LocalTime startTime, LocalTime endTime) {
        return dateTime.toLocalTime().compareTo(startTime) >= 0 && dateTime.toLocalTime().compareTo(endTime) <= 0;
    }
}
