package bg.sofia.uni.fmi.mjt.udemy.course.duration;

import bg.sofia.uni.fmi.mjt.udemy.course.Resource;

public record CourseDuration(int hours, int minutes) {

    public CourseDuration {
        if (hours < 0 || hours > 24)
            throw new IllegalArgumentException("Hours must be a whole number between 0 and 24.");
        if (minutes < 0 || minutes > 60)
            throw new IllegalArgumentException("Minutes must be a whole number between 0 and 60.");
    }

    public static CourseDuration of(Resource[] content) {
        int totalMinutes = 0;
        for (Resource r : content)
            totalMinutes += r.getDuration().minutes();

        int h = (totalMinutes / 60);
        int m = totalMinutes % 60;
        return new CourseDuration(h, m);
    }

    public boolean longerOrEqualTo(CourseDuration other) {
        if (this.hours() > other.hours())
            return true;
        if (this.hours() == other.hours())
            return (this.minutes() >= other.minutes());
        return false;
    }
}
