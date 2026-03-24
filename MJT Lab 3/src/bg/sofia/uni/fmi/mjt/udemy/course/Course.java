package bg.sofia.uni.fmi.mjt.udemy.course;

import bg.sofia.uni.fmi.mjt.udemy.course.duration.CourseDuration;
import bg.sofia.uni.fmi.mjt.udemy.exception.ResourceNotFoundException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Course implements Purchasable, Completable {
    private final String name;
    private final String description;
    private final double price;
    private final Resource[] content;
    Category category;
    private final CourseDuration totalTime;
    private int completionPercentage;
    private boolean isPurchased;
    private double grade;

    public Course(String name, String description, double price, Resource[] content, Category category) {
        if (price < 0) {
            throw new IllegalArgumentException("Course price must be a non negative number.");
        }
        this.name = name;
        this.description = description;
        this.price = price;
        this.content = new Resource[content.length];
        System.arraycopy(content, 0, this.content, 0, content.length);
        this.category = category;
        isPurchased = false;
        calculateCompletionPercentage();
        grade = 0;
        totalTime = CourseDuration.of(content);
    }

    /**
     * Returns the name of the course.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the description of the course.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the price of the course.
     */
    public double getPrice() {
        return price;
    }

    /**
     * Returns the category of the course.
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Returns the content of the course.
     */
    public Resource[] getContent() {
        return content;
    }

    /**
     * Returns the total duration of the course.
     */
    public CourseDuration getTotalTime() {
        return totalTime;
    }

    private void calculateCompletionPercentage() {
        int resourcesCount = content.length;
        if (resourcesCount == 0) {
            completionPercentage = 100;
            return;
        }

        int completedResourcesCount = 0;
        for (Resource r : content) {
            if (r.isCompleted()) {
                completedResourcesCount++;
            }
        }
        double completionPartition = (double) completedResourcesCount / resourcesCount;
        BigDecimal formatter = new BigDecimal(completionPartition).setScale(2, RoundingMode.HALF_UP);
        completionPercentage = (int) (formatter.doubleValue() * 100);
    }

    /**
     * Completes a resource from the course.
     *
     * @param resourceToComplete the resource which will be completed.
     * @throws IllegalArgumentException  if resourceToComplete is null.
     * @throws ResourceNotFoundException if the resource could not be found in the course.
     */
    public void completeResource(Resource resourceToComplete) throws ResourceNotFoundException {
        if (resourceToComplete == null) {
            throw new IllegalArgumentException("Resource to complete was null.");
        }

        for (Resource r : content) {
            if (r.equals(resourceToComplete)) {
                r.complete();
                calculateCompletionPercentage();
                return;
            }
        }
        throw new ResourceNotFoundException("Resource not found");
    }

    public void setGrade(double grade) {
        if (grade < 2.0 || grade > 6.0) {
            throw new IllegalArgumentException("Grade must be a floating point number between 2 and 6");
        }
        this.grade = grade;
    }

    public double getGrade() {
        return grade;
    }

    @Override
    public boolean isCompleted() {
        return (completionPercentage == 100);
    }

    @Override
    public int getCompletionPercentage() {
        calculateCompletionPercentage();
        return completionPercentage;
    }

    @Override
    public void purchase() {
        isPurchased = true;
    }

    @Override
    public boolean isPurchased() {
        return isPurchased;
    }
}
