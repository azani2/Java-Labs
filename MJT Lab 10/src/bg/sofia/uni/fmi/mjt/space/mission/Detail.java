package bg.sofia.uni.fmi.mjt.space.mission;

public record Detail(String rocketName, String payload) {

    public static Detail of(String rocketName, String payload) {
        return new Detail(rocketName, payload);
    }

    public static Detail of(String line) {
        final String[] tokens = line.split("\\|");
        String rocketName = tokens[0].trim();
        String payload = tokens[1].trim();
        return new Detail(rocketName, payload);
    }
}