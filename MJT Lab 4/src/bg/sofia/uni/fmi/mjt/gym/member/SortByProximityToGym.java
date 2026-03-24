package bg.sofia.uni.fmi.mjt.gym.member;

import bg.sofia.uni.fmi.mjt.gym.Gym;

import java.util.Comparator;

public class SortByProximityToGym implements Comparator<GymMember> {
    private final Gym gym;

    public SortByProximityToGym(Gym gym) {
        this.gym = gym;
    }

    @Override
    public int compare(GymMember o1, GymMember o2) {
        double dist1 = o1.getAddress().getDistanceTo(gym.getAddress());
        double dist2 = o2.getAddress().getDistanceTo(gym.getAddress());
        return Double.compare(dist1, dist2);
    }
}
