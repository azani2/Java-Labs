package bg.sofia.uni.fmi.mjt.space.rocket;

import java.util.Optional;

public record Rocket(String id, String name, Optional<String> wiki, Optional<Double> height) {
    public static Rocket of(String id, String name, Optional<String> wiki, Optional<Double> height) {
        return new Rocket(id, name, wiki, height);
    }

    public static Rocket of(String line) {
        final int expectedLength = 4;
        String[] tokens = line.split(",");
        int tokensIdx = 0;
        String id = tokens[tokensIdx++];
        String name = tokens[tokensIdx++];
        Optional<String> wiki;
        if (tokens.length < expectedLength - 1) {
            wiki = Optional.empty();
        } else {
            wiki = Optional.of(tokens[tokensIdx++]);
        }

        Optional<Double> height;
        if (tokens.length < expectedLength || tokens[tokensIdx].isEmpty()) {
            height = Optional.empty();
        } else {
            String heightData = tokens[tokensIdx].split(" ")[0];
            try {
                height = Optional.of(Double.parseDouble(heightData));
            } catch (NumberFormatException e) {
                height = Optional.empty();
            }
        }
        return new Rocket(id, name, wiki, height);
    }
}