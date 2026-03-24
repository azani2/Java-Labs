package bg.sofia.uni.fmi.mjt.gym.member;

public record Address(double longitude, double latitude) {
    public double getDistanceTo(Address other) {
        double xSq = (other.longitude - this.longitude) * (other.longitude - this.longitude);
        double ySq = (other.latitude - this.latitude) * (other.latitude - this.latitude);
        return Math.sqrt(xSq + ySq);
    }
}
