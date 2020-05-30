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
        // calculate calories per date
        Map<LocalDate, Integer> caloriesPerDate = new HashMap<>();
        for (UserMeal meal : meals) {
            caloriesPerDate.put(
                    getDate(meal),
                    meal.getCalories() + caloriesPerDate.getOrDefault(getDate(meal), 0));
        }

        // fill output collection
        List<UserMealWithExcess> filteredMealsWithExcess = new ArrayList<>();
        for (UserMeal meal : meals) {
            if (TimeUtil.isBetweenHalfOpen(getTime(meal), startTime, endTime)) {
                filteredMealsWithExcess.add(createUserMealWithExcess(meal, caloriesPerDate.get(getDate(meal)) > caloriesPerDay));
            }
        }
        return filteredMealsWithExcess;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // calculate calories per date
        Map<LocalDate, Integer> caloriesPerDate =
                meals.stream().collect(Collectors.toMap(UserMealsUtil::getDate, UserMeal::getCalories, Integer::sum));

        // build and return output collection
        return meals.stream()
                .filter(meal -> TimeUtil.isBetweenHalfOpen(getTime(meal), startTime, endTime))
                .map(meal -> createUserMealWithExcess(meal, caloriesPerDate.get(meal.getDateTime().toLocalDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }

    private static LocalTime getTime(UserMeal meal) {
        return meal.getDateTime().toLocalTime();
    }

    private static LocalDate getDate(UserMeal meal) {
        return meal.getDateTime().toLocalDate();
    }

    private static UserMealWithExcess createUserMealWithExcess(UserMeal meal, boolean excess) {
        return new UserMealWithExcess(
                meal.getDateTime(),
                meal.getDescription(),
                meal.getCalories(),
                excess
        );
    }
}
