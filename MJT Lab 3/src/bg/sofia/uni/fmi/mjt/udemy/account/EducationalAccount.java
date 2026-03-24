package bg.sofia.uni.fmi.mjt.udemy.account;

import bg.sofia.uni.fmi.mjt.udemy.account.type.AccountType;
import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.exception.CourseAlreadyPurchasedException;
import bg.sofia.uni.fmi.mjt.udemy.exception.InsufficientBalanceException;
import bg.sofia.uni.fmi.mjt.udemy.exception.MaxCourseCapacityReachedException;

public class EducationalAccount extends AccountBase {
    private int coursesSinceLastDiscount;

    public EducationalAccount(String username, double balance) {
        super(username, balance);
        super.type = AccountType.EDUCATION;
        coursesSinceLastDiscount = 0;
    }

    private boolean hasDiscount() {
        boolean discountAvailable = true;

        if (coursesSinceLastDiscount < 5) {
            discountAvailable = false;
        } else if (coursesSinceLastDiscount == 5) {
            double sumLast5Grades = 0;
            for (int i = 0; i < 5; i++) {
                sumLast5Grades += purchasedCourses[purchasedCoursesCount - 1 - i].getGrade();
            }
            double avgLast5Grade = sumLast5Grades / 5;
            if (avgLast5Grade < 4.50) {
                discountAvailable = false;
            }
        }

        return discountAvailable;
    }

    @Override
    public void buyCourse(Course course)
        throws InsufficientBalanceException, CourseAlreadyPurchasedException, MaxCourseCapacityReachedException {
        if (course == null) {
            throw new IllegalArgumentException("Course to buy was null.");
        }

        if (hasDiscount()) {
            super.buyCourse(course);
            coursesSinceLastDiscount = 0;
            return;
        }

        if (course.getPrice() > balance) {
            throw new InsufficientBalanceException("Not enough balance to buy course.");
        }

        for (int i = 0; i < purchasedCoursesCount; i++) {
            if (purchasedCourses[i].equals(course)) {
                throw new CourseAlreadyPurchasedException("Course has already been purchased.");
            }
        }

        if (purchasedCoursesCount == purchasedCourses.length) {
            throw new MaxCourseCapacityReachedException("Course purchase limit reached");
        }

        purchasedCourses[purchasedCoursesCount] = course;
        purchasedCourses[purchasedCoursesCount].purchase();
        purchasedCoursesCount++;
        balance -= course.getPrice();

        if (coursesSinceLastDiscount == 5) {
            coursesSinceLastDiscount = 0;
        } else if (coursesSinceLastDiscount < 5) {
            coursesSinceLastDiscount++;
        }

    }

}
