package bg.sofia.uni.fmi.mjt.space.mission;

import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class MissionTest {
    @Test
    void testFactoryLineConstructor() {
        String line = null;
        try (var bufferedReader = new BufferedReader(new FileReader("mission-test-1.csv"))) {
            line = bufferedReader.readLine();
        } catch (IOException e) {
            fail("An error occurred getting test data from file.");
        }

        Detail detailExpected = Detail.of("Falcon 9 Block 5", "Starlink V1 L9 & BlackSky");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd, yyyy").withLocale(Locale.ENGLISH);
        final LocalDate date = LocalDate.parse("Fri Aug 07, 2020", formatter);

        Mission missionExpected = Mission.of("0", "SpaceX", "LC-39A, Kennedy Space Center, Florida, USA",
            date, detailExpected, RocketStatus.STATUS_ACTIVE, Optional.of(50.0), MissionStatus.SUCCESS);

        assertEquals(missionExpected, Mission.of(line),
            "Expected mission does not match mission derived from line.");
    }
}
