package bg.sofia.uni.fmi.mjt.udemy;

import bg.sofia.uni.fmi.mjt.udemy.account.Account;
import bg.sofia.uni.fmi.mjt.udemy.course.Category;
import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.exception.AccountNotFoundException;
import bg.sofia.uni.fmi.mjt.udemy.exception.CourseNotFoundException;

import java.util.Arrays;


public class Udemy implements LearningPlatform {
    private Account[] accounts;
    private Course[] courses;

    public Udemy(Account[] accounts, Course[] courses) {
        this.accounts = accounts;
        this.courses = courses;
    }

    @Override
    public Course findByName(String name) throws CourseNotFoundException {
        for (Course c : courses) {
            if (c.getName().equals(name))
                return c;
        }
        throw new CourseNotFoundException("Course not found.");
    }

    @Override
    public Course[] findByKeyword(String keyword) {
        Course[] foundCourses = new Course[courses.length];
        int foundCount = 0;
        for (Course c : courses) {
            if (c.getName().contains(keyword) || c.getDescription().contains(keyword))
                foundCourses[foundCount++] = c;
        }
        Course[] foundCoursesFinal = new Course[foundCount];
        System.arraycopy(foundCourses, 0, foundCoursesFinal, 0, foundCount);
        return foundCoursesFinal;
    }

    @Override
    public Course[] getAllCoursesByCategory(Category category) {
        Course[] foundCourses = new Course[courses.length];
        int foundCount = 0;
        for (Course c : courses) {
            if (c.getCategory() == category)
                foundCourses[foundCount++] = c;
        }
        Course[] foundCoursesFinal = new Course[foundCount];
        System.arraycopy(foundCourses, 0, foundCoursesFinal, 0, foundCount);
        return foundCoursesFinal;
    }

    @Override
    public Account getAccount(String name) throws AccountNotFoundException {
        for (Account acc : accounts) {
            if (acc.getUsername().equals(name))
                return acc;
        }
        throw new AccountNotFoundException("Account not found.");
    }

    @Override
    public Course getLongestCourse() {
        if (courses.length == 0)
            return null;

        Course longestFound = courses[0];
        for (Course c : courses) {
            if(c.getTotalTime().longerOrEqualTo(longestFound.getTotalTime()))
                longestFound = c;
        }
        return longestFound;
    }

    @Override
    public Course getCheapestByCategory(Category category) {
        if (courses.length == 0)
            return null;

        Course cheapestFound = courses[0];
        for (Course c : courses) {
            if(c.getPrice() <= cheapestFound.getPrice())
                cheapestFound = c;
        }
        return cheapestFound;
    }
}
