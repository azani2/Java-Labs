package bg.sofia.uni.fmi.mjt.space.mission;

import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

public record Mission(String id, String company, String location, LocalDate date, Detail detail,
                      RocketStatus rocketStatus, Optional<Double> cost, MissionStatus missionStatus) {

    public static Mission of(String id, String company, String location, LocalDate date, Detail detail,
                             RocketStatus rocketStatus, Optional<Double> cost, MissionStatus missionStatus) {
        return new Mission(id, company, location, date, detail, rocketStatus, cost, missionStatus);
    }

    public static Mission of(String line) {
        final String regex = ",(?=(?:[^\"]*\"[^\"]*\")*(?![^\"]*\"))";
        final String[] tokens = line.split(regex);
        int tokenIdx = 0;
        final String id = tokens[tokenIdx++];
        final String company = tokens[tokenIdx++];
        final String location = tokens[tokenIdx++].replaceAll("^\"|\"$", "").trim();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("\"EEE MMM dd, yyyy\"").withLocale(Locale.ENGLISH);
        final LocalDate date = LocalDate.parse(tokens[tokenIdx++], formatter);
        final Detail detail = Detail.of(tokens[tokenIdx++]);
        final RocketStatus rocketStatus = switch (tokens[tokenIdx++]) {
            case "StatusRetired" -> RocketStatus.STATUS_RETIRED;
            default -> RocketStatus.STATUS_ACTIVE;
        };
        Optional<Double> cost;
        String costStr = tokens[tokenIdx++]
            .replaceAll("^\"|\"$", "")
            .trim();
        try {
            cost = Optional.of(Double.parseDouble(costStr));
        } catch (NumberFormatException e) {
            cost = Optional.empty();
        }
        final MissionStatus missionStatus = switch (tokens[tokenIdx]) {
            case "Success" -> MissionStatus.SUCCESS;
            case "Failure" -> MissionStatus.FAILURE;
            case "Partial Failure" -> MissionStatus.PARTIAL_FAILURE;
            default -> MissionStatus.PRELAUNCH_FAILURE;
        };
        return Mission.of(id, company, location, date, detail, rocketStatus, cost, missionStatus);
    }
}