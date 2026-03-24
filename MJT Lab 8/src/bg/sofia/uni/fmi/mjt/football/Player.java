package bg.sofia.uni.fmi.mjt.football;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

public record Player(String name, String fullName, LocalDate birthDate, int age,
                     double heightCm, double weightKg, List<Position> positions,
                     String nationality, int overallRating, int potential, long valueEuro,
                     long wageEuro, Foot preferredFoot) {
    private static final String ATTRIBUTE_DELIMITER = ";";
    private static final String DATE_DELIMITER = "/";
    private static final String POS_DELIMITER = ",";

    public static Player of(String line) {
        final String[] tokens = line.split(ATTRIBUTE_DELIMITER);
        final int nameIdx = 0;
        final int fullNameIdx = 1;
        final int birthDateIdx = 2;
        final int ageIdx = 3;
        final int heightIdx = 4;
        final int weightIdx = 5;
        final int positionsIdx = 6;
        final int nationalityIdx = 7;
        final int ratingIdx = 8;
        final int potentialIdx = 9;
        final int valueIdx = 10;
        final int wageIdx = 11;
        final int footIdx = 12;
        final String[] date = tokens[birthDateIdx].split(DATE_DELIMITER);
        LocalDate birthdate = LocalDate.of(parseInt(date[2]), parseInt(date[0]), parseInt(date[1]));
        BigDecimal heightFormat = new BigDecimal(tokens[heightIdx]);
        BigDecimal weightFormat = new BigDecimal(tokens[weightIdx]);
        List<Position> positions = new ArrayList<>();
        for (String pos : tokens[positionsIdx].split(POS_DELIMITER)) {
            positions.add(Position.valueOf(pos.toUpperCase()));
        }
        Foot foot = Foot.valueOf(tokens[footIdx].toUpperCase());

        return new Player(tokens[nameIdx], tokens[fullNameIdx], birthdate, parseInt(tokens[ageIdx]),
            heightFormat.doubleValue(), weightFormat.doubleValue(), positions,
            tokens[nationalityIdx], parseInt(tokens[ratingIdx]), parseInt(tokens[potentialIdx]),
            parseLong(tokens[valueIdx]), parseLong(tokens[wageIdx]), foot);
    }
}
