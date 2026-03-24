package bg.sofia.uni.fmi.mjt.udemy;

import bg.sofia.uni.fmi.mjt.udemy.account.Account;
import bg.sofia.uni.fmi.mjt.udemy.account.BusinessAccount;
import bg.sofia.uni.fmi.mjt.udemy.course.Category;
import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.course.Resource;
import bg.sofia.uni.fmi.mjt.udemy.course.duration.ResourceDuration;
import bg.sofia.uni.fmi.mjt.udemy.exception.CourseAlreadyPurchasedException;
import bg.sofia.uni.fmi.mjt.udemy.exception.InsufficientBalanceException;
import bg.sofia.uni.fmi.mjt.udemy.exception.MaxCourseCapacityReachedException;
import bg.sofia.uni.fmi.mjt.udemy.exception.ResourceNotFoundException;

public class Main {
    public static void main(String[] args) {
        Resource reading = new Resource("read this", new ResourceDuration(5));
        Resource writing = new Resource("write a thesis", new ResourceDuration(5));
        Resource listening = new Resource("listen to the dialogue", new ResourceDuration(5));
        Resource[] resources = new Resource[] {reading, writing, listening};

        Course english = new Course("english for kids", "to learn in kindergarten", 12.50, resources, Category.MUSIC);

        try {
            english.completeResource(reading);
            english.completeResource(listening);
        } catch (ResourceNotFoundException e) {
            System.out.println("Couldn't find resource.");
        }
        System.out.println(english.getCompletionPercentage());

        Account pesho = new BusinessAccount("Peshkata", 5000, new Category[]{Category.MUSIC, Category.BUSINESS});
        try {
            pesho.buyCourse(english);
        } catch (InsufficientBalanceException e) {
            System.out.println("Insufficient balance");
        } catch (CourseAlreadyPurchasedException e) {
            System.out.println("Course already purchased");
        } catch (MaxCourseCapacityReachedException e) {
            System.out.println("Max course capacity");
        }
    }
}