package bg.sofia.uni.fmi.mjt.gym.member;

import bg.sofia.uni.fmi.mjt.gym.workout.Exercise;
import bg.sofia.uni.fmi.mjt.gym.workout.Workout;

import java.time.DayOfWeek;
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

public class Member implements GymMember, Comparable<GymMember> {
    private final Address address;
    private final String name;
    private final Integer age;
    private final String personalIdNumber;
    private final Gender gender;
    private Map<DayOfWeek, Workout> trainingProgram;

    private void initTrainingProgram() {
        final int maxProgramDays = 7;
        if (trainingProgram == null || trainingProgram.size() == maxProgramDays) {
            return;
        }

        trainingProgram.put(DayOfWeek.MONDAY, null);
        trainingProgram.put(DayOfWeek.TUESDAY, null);
        trainingProgram.put(DayOfWeek.WEDNESDAY, null);
        trainingProgram.put(DayOfWeek.THURSDAY, null);
        trainingProgram.put(DayOfWeek.FRIDAY, null);
        trainingProgram.put(DayOfWeek.SATURDAY, null);
        trainingProgram.put(DayOfWeek.SUNDAY, null);
    }

    public Member(Address address, String name, int age, String personalIdNumber, Gender gender) {
        if (personalIdNumber == null || personalIdNumber.isEmpty()) {
            throw new IllegalArgumentException("Personal ID number name was null or empty.");
        }

        if (age <= 0) {
            throw new IllegalArgumentException("Age must be positive number.");
        }

        this.address = address;
        this.name = name;
        this.age = age;
        this.personalIdNumber = personalIdNumber;
        this.gender = gender;
        trainingProgram = new LinkedHashMap<>();
        initTrainingProgram();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getAge() {
        return age;
    }

    @Override
    public String getPersonalIdNumber() {
        return personalIdNumber;
    }

    @Override
    public Gender getGender() {
        return gender;
    }

    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public Map<DayOfWeek, Workout> getTrainingProgram() {
        return trainingProgram;
    }

    @Override
    public void setWorkout(DayOfWeek day, Workout workout) {
        if (day == null) {
            throw new IllegalArgumentException("Day was null.");
        }

        if (workout == null) {
            throw new IllegalArgumentException("Workout was null.");
        }

        trainingProgram.put(day, workout);
    }

    @Override
    public Collection<DayOfWeek> getDaysFinishingWith(String exerciseName) {
        if (exerciseName == null || exerciseName.isEmpty()) {
            throw new IllegalArgumentException("Exercise name was null or empty.");
        }

        Set<DayOfWeek> daysFound = new HashSet<>();
        for (Map.Entry<DayOfWeek, Workout> entry : trainingProgram.entrySet()) {
            if (entry.getValue() != null && entry.getValue().exercises().getLast().name().equals(exerciseName)) {
                daysFound.add(entry.getKey());
            }
        }
        return daysFound;
    }

    @Override
    public void addExercise(DayOfWeek day, Exercise exercise) {
        if (day == null) {
            throw new IllegalArgumentException("Day was null.");
        }

        if (exercise == null || exercise.name().isEmpty()) {
            throw new IllegalArgumentException("Exercise was null or empty.");
        }

        if (trainingProgram.get(day) == null) {
            throw new DayOffException("No workout to add an exercise to on this day.");
        }

        trainingProgram.get(day).exercises().add(exercise);
    }

    @Override
    public void addExercises(DayOfWeek day, List<Exercise> exercises) {
        if (exercises == null || exercises.isEmpty()) {
            throw new IllegalArgumentException("Exercises was null.");
        }

        for (Exercise exercise : exercises) {
            if (exercise == null || exercise.name().isEmpty()) {
                throw new IllegalArgumentException("Exercise was null or empty.");
            }
            addExercise(day, exercise);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof GymMember that)) {
            return false;
        }

        return this.personalIdNumber.equals(that.getPersonalIdNumber());
    }

    @Override
    public int hashCode() {
        return Objects.hash(personalIdNumber);
    }

    @Override
    public int compareTo(GymMember o) {
        return personalIdNumber.compareTo(o.getPersonalIdNumber());
    }
}
