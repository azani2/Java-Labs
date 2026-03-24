package bg.sofia.uni.fmi.mjt.udemy.account;

import bg.sofia.uni.fmi.mjt.udemy.account.type.AccountType;
import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.course.Resource;
import bg.sofia.uni.fmi.mjt.udemy.exception.*;

public abstract class AccountBase implements Account {
    protected AccountType type;
    protected Course[] purchasedCourses;
    protected int purchasedCoursesCount;
    protected final String username;
    protected double balance;

    public AccountBase(String username, double balance) {
        this.username = username;
        this.balance = balance;
        purchasedCourses = new Course[100];
        purchasedCoursesCount = 0;
    }

    @Override
    public void addToBalance(double amount) {
        if (amount < 0)
            throw new IllegalArgumentException("Amount must be non negative number.");
        balance += amount;
    }

    @Override
    public void buyCourse(Course course)
        throws InsufficientBalanceException, CourseAlreadyPurchasedException, MaxCourseCapacityReachedException {
        if (course == null) {
            throw new IllegalArgumentException("Course to buy was null.");
        }

        double discountedPrice = (1 - type.getDiscount()) * course.getPrice();

        if (discountedPrice > balance) {
            throw new InsufficientBalanceException("Not enough balance to buy course.");
        }

        for (int i = 0; i < purchasedCoursesCount; i++) {
            if (purchasedCourses[i].equals(course)) {
                throw new CourseAlreadyPurchasedException("Course has already been purchased.");
            }
        }

        if (purchasedCoursesCount >= purchasedCourses.length)
            throw new MaxCourseCapacityReachedException("Course purchase limit reached.");

        purchasedCourses[purchasedCoursesCount] = course;
        purchasedCourses[purchasedCoursesCount].purchase();
        purchasedCoursesCount++;
        balance -= discountedPrice;
    }

    @Override
    public void completeResourcesFromCourse(Course course, Resource[] resourcesToComplete)
        throws CourseNotPurchasedException, ResourceNotFoundException {
        if (course == null)
            throw new IllegalArgumentException("Course holding resource to complete was null.");

        if (resourcesToComplete == null)
            throw new IllegalArgumentException("Resource to complete was null.");

        boolean courseFound = false;
        for (int i = 0; i < purchasedCoursesCount; i++) {
            if (purchasedCourses[i].equals(course)) {
                courseFound = true;
                for (Resource rToComplete : resourcesToComplete) {
                    //completeResource throws ResourceNotFound
                    purchasedCourses[i].completeResource(rToComplete);
                }
            }
        }
        if(!courseFound)
            throw new CourseNotPurchasedException("Course has not been purchased by account.");
    }

    @Override
    public void completeCourse(Course course, double grade)
        throws CourseNotPurchasedException, CourseNotCompletedException {
        if (course == null)
            throw new IllegalArgumentException("Course to complete was null.");

        for (int i = 0; i < purchasedCoursesCount; i++) {
            if (purchasedCourses[i].equals(course)) {
                if (!purchasedCourses[i].isCompleted())
                    throw new CourseNotCompletedException("Course not completed.");

                purchasedCourses[i].setGrade(grade);
                return;
            }
        }
        throw new CourseNotPurchasedException("Course has not been purchased by account.");
    }

    @Override
    public double getBalance() {
        return balance;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public AccountType getType() {
        return type;
    }

    @Override
    public Course getLeastCompletedCourse() {
        int minCompletionPercentage = 100;
        Course leastCompleted = null;
        for (int i = 0; i < purchasedCoursesCount; i++) {
            if (purchasedCourses[i].getCompletionPercentage() <= minCompletionPercentage) {
                minCompletionPercentage = purchasedCourses[i].getCompletionPercentage();
                leastCompleted = purchasedCourses[i];
            }
        }
        return leastCompleted;
    }
}
