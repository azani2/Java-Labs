package bg.sofia.uni.fmi.mjt.udemy.account;

import bg.sofia.uni.fmi.mjt.udemy.account.type.AccountType;
import bg.sofia.uni.fmi.mjt.udemy.course.Category;
import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.exception.*;

public class BusinessAccount extends AccountBase {
    private final Category[] allowedCategories;

    public BusinessAccount(String username, double balance, Category[] allowedCategories) {
        super(username, balance);
        this.allowedCategories = allowedCategories;
        super.type = AccountType.BUSINESS;
    }

    @Override
    public void buyCourse(Course course)
        throws InsufficientBalanceException, MaxCourseCapacityReachedException, CourseAlreadyPurchasedException {

        boolean allowedToBuyCourse = false;
        for (Category c : allowedCategories) {
            if (c.equals(course.getCategory())) {
                allowedToBuyCourse = true;
                break;
            }
        }

        if (!allowedToBuyCourse)
            throw new IllegalArgumentException("This business account is not allowed to buy courses with this category.");

        super.buyCourse(course);
    }

}

