package bg.sofia.uni.fmi.mjt.gym;

import bg.sofia.uni.fmi.mjt.gym.member.GymMember;
import bg.sofia.uni.fmi.mjt.gym.member.Address;
import bg.sofia.uni.fmi.mjt.gym.member.SortByName;
import bg.sofia.uni.fmi.mjt.gym.member.SortByProximityToGym;
import bg.sofia.uni.fmi.mjt.gym.workout.Exercise;
import bg.sofia.uni.fmi.mjt.gym.workout.Workout;

import java.time.DayOfWeek;
import java.util.Collections;
import java.util.Collection;
import java.util.TreeSet;
import java.util.SortedSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;

public class Gym implements GymAPI {
    private SortedSet<GymMember> members;
    private final int capacity;
    private final Address address;

    public Gym(int capacity, Address address) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity must be non-negative number.");
        }

        this.capacity = capacity;
        this.address = address;
        this.members = new TreeSet<>();
    }

    @Override
    public SortedSet<GymMember> getMembers() {
        return Collections.unmodifiableSortedSet(members);
    }

    @Override
    public SortedSet<GymMember> getMembersSortedByName() {
        SortedSet<GymMember> sortedMembers = new TreeSet<>(new SortByName());
        sortedMembers.addAll(members);
        return Collections.unmodifiableSortedSet(sortedMembers);
    }

    @Override
    public SortedSet<GymMember> getMembersSortedByProximityToGym() {
        SortedSet<GymMember> sortedMembers = new TreeSet<>(new SortByProximityToGym(this));
        sortedMembers.addAll(members);
        return Collections.unmodifiableSortedSet(sortedMembers);
    }

    @Override
    public void addMember(GymMember member) throws GymCapacityExceededException {
        if (member == null) {
            throw new IllegalArgumentException("Member was null.");
        }

        if (capacity == members.size()) {
            throw new GymCapacityExceededException("Gym capacity reached.");
        }

        members.add(member);
    }

    @Override
    public void addMembers(Collection<GymMember> members) throws GymCapacityExceededException {
        if (members == null) {
            throw new IllegalArgumentException("Members was null.");
        }

        if (capacity < this.members.size() + members.size()) {
            throw new GymCapacityExceededException("Gym capacity exceeded.");
        }

        if (members.isEmpty()) {
            throw new IllegalArgumentException("Members was null or empty.");
        }

        for (GymMember member : members) {
            if (member == null) {
                throw new IllegalArgumentException("Member was null.");
            }
            addMember(member);
        }
    }

    @Override
    public boolean isMember(GymMember member) {
        if (member == null) {
            throw new IllegalArgumentException("Member was null.");
        }
        return members.contains(member);
    }

    @Override
    public boolean isExerciseTrainedOnDay(String exerciseName, DayOfWeek day) {
        if (exerciseName == null || exerciseName.isEmpty()) {
            throw new IllegalArgumentException("Exercise name was null or empty.");
        }

        if (day == null) {
            throw new IllegalArgumentException("Day was null.");
        }

        Exercise exerciseToFind = new Exercise(exerciseName, 1, 1);

        for (GymMember member : members) {
            Workout memberWorkout = member.getTrainingProgram().get(day);
            if (memberWorkout != null) {
                for (Exercise exercise : memberWorkout.exercises()) {
                    if (exercise.equals(exerciseToFind)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public Map<DayOfWeek, List<String>> getDailyListOfMembersForExercise(String exerciseName) {
        if (exerciseName == null || exerciseName.isEmpty()) {
            throw new IllegalArgumentException("Exercise name was null or empty.");
        }

        Map<DayOfWeek, List<String>> dailyListOfMembers = new LinkedHashMap<>();

        List<DayOfWeek> weekDays = List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);

        for (DayOfWeek day : weekDays) {

            for (GymMember member : members) {
                Workout memberWorkout = member.getTrainingProgram().get(day);
                if (memberWorkout != null) {

                    for (Exercise exercise : memberWorkout.exercises()) {
                        if (exercise.name().equals(exerciseName)) {
                            dailyListOfMembers.computeIfAbsent(day, k -> new ArrayList<>());
                            dailyListOfMembers.get(day).add(member.getName());
                        }
                    }
                }
            }
        }

        return dailyListOfMembers;
    }

    public Address getAddress() {
        return address;
    }
}
