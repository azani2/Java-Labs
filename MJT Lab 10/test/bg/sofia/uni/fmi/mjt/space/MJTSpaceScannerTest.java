package bg.sofia.uni.fmi.mjt.space;

import bg.sofia.uni.fmi.mjt.space.exception.TimeFrameMismatchException;
import bg.sofia.uni.fmi.mjt.space.mission.Detail;
import bg.sofia.uni.fmi.mjt.space.mission.Mission;
import bg.sofia.uni.fmi.mjt.space.mission.MissionStatus;
import bg.sofia.uni.fmi.mjt.space.rocket.Rocket;
import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import javax.crypto.spec.SecretKeySpec;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class MJTSpaceScannerTest {
    @Mock
    private SecretKeySpec secretKey;
    @InjectMocks
    private MJTSpaceScanner scanner;

    @Test
    void testGetAllMissionsNoMissions() throws FileNotFoundException {
        try (var missionsReader = new FileReader("scanner-test-1-missions.csv");
             var rocketsReader = new FileReader("scanner-test-1-rockets.csv")) {
            scanner = new MJTSpaceScanner(missionsReader, rocketsReader, secretKey);

            assertTrue(scanner.getAllMissions() != null,
                "An empty collection was expected when there are no missions but it was null.");
            assertTrue(scanner.getAllMissions().isEmpty(),
                "An empty collectin was expected when there are no missions but the collection size was: " +
                    scanner.getAllMissions().size());
        } catch (IOException e) {
            throw new RuntimeException("An error occurred when initializing data from files.");
        }
    }

    @Test
    void testGetAllRocketsNoRockets() {
        try (var missionsReader = new FileReader("scanner-test-1-missions.csv");
             var rocketsReader = new FileReader("scanner-test-1-rockets.csv")) {
            scanner = new MJTSpaceScanner(missionsReader, rocketsReader, secretKey);

            assertTrue(scanner.getAllRockets() != null,
                "An empty collection was expected when there are no missions but it was null.");
            assertTrue(scanner.getAllRockets().isEmpty(),
                "An empty collectin was expected when there are no missions but the collection size was: " +
                    scanner.getAllRockets().size());
        } catch (IOException e) {
            throw new RuntimeException("An error occurred when initializing data from files.");
        }
    }

    @Test
    void testGetAllMissionsSomeMissions() {
        try (var missionsReader = new FileReader("scanner-test-2-missions.csv");
             var rocketsReader = new FileReader("scanner-test-1-rockets.csv")) {
            scanner = new MJTSpaceScanner(missionsReader, rocketsReader, secretKey);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd, yyyy").withLocale(Locale.ENGLISH);
            LocalDate date1 = LocalDate.parse("Fri Aug 07, 2020", formatter);
            LocalDate date2 = LocalDate.parse("Thu Aug 06, 2020", formatter);
            LocalDate date3 = LocalDate.parse("Tue Aug 04, 2020", formatter);
            Detail detail1 = Detail.of("Falcon 9 Block 5", "Starlink V1 L9 & BlackSky");
            Detail detail2 = Detail.of("Long March 2D", "Gaofen-9 04 & Q-SAT");
            Detail detail3 = Detail.of("Starship Prototype", "150 Meter Hop");

            Mission mission1 = Mission.of("0", "SpaceX",
                "LC-39A, Kennedy Space Center, Florida, USA", date1, detail1,
                RocketStatus.STATUS_ACTIVE, Optional.of(50.0), MissionStatus.SUCCESS);
            Mission mission2 = Mission.of("1", "CASC",
                "Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China", date2, detail2,
                RocketStatus.STATUS_ACTIVE, Optional.of(29.75), MissionStatus.SUCCESS);
            Mission mission3 = Mission.of("2", "SpaceX",
                "Pad A, Boca Chica, Texas, USA", date3, detail3,
                RocketStatus.STATUS_ACTIVE, Optional.empty(), MissionStatus.SUCCESS);

            List<Mission> expectedList = List.of(mission1, mission2, mission3);

            assertIterableEquals(expectedList, scanner.getAllMissions(),
                "Expected collection does not match actual.");
        } catch (IOException e) {
            throw new RuntimeException("An error occurred when initializing data from files.");
        }
    }

    @Test
    void testGetAllRocketsSomeRockets() {
        try (var missionsReader = new FileReader("scanner-test-1-missions.csv");
             var rocketsReader = new FileReader("scanner-test-2-rockets.csv")) {
            scanner = new MJTSpaceScanner(missionsReader, rocketsReader, secretKey);

            Rocket rocket1 = Rocket.of("0", "Tsyklon-3",
                Optional.of("https://en.wikipedia.org/wiki/Tsyklon-3"), Optional.of(39.0));
            Rocket rocket2 = Rocket.of("1", "Tsyklon-4M",
                Optional.of("https://en.wikipedia.org/wiki/Cyclone-4M"), Optional.of(38.7));
            Rocket rocket3 = Rocket.of("2", "Unha-2",
                Optional.of("https://en.wikipedia.org/wiki/Unha"), Optional.of(28.0));

            List<Rocket> expectedList = List.of(rocket1, rocket2, rocket3);

            assertIterableEquals(expectedList, scanner.getAllRockets(),
                "Expected collection does not match actual.");
        } catch (IOException e) {
            throw new RuntimeException("An error occurred when initializing data from files.");
        }
    }

    @Test
    void testGetAllMissionsWithStatusNull() {
        try (var missionsReader = new FileReader("scanner-test-1-missions.csv");
             var rocketsReader = new FileReader("scanner-test-1-rockets.csv")) {
            scanner = new MJTSpaceScanner(missionsReader, rocketsReader, secretKey);
            assertThrows(IllegalArgumentException.class, () -> scanner.getAllMissions(null),
                "IllegalArguumentException was expected when passing null to missions getter but was not thrown.");
        } catch (IOException e) {
            throw new RuntimeException("An error occurred when initializing data from files.");
        }
    }

    @Test
    void testGetCompanyWithMostSuccessfulMissionsFromDateNull() {
        try (var missionsReader = new FileReader("scanner-test-1-missions.csv");
             var rocketsReader = new FileReader("scanner-test-1-rockets.csv")) {
            scanner = new MJTSpaceScanner(missionsReader, rocketsReader, secretKey);
            LocalDate to = LocalDate.of(2000, 1, 1);
            assertThrows(IllegalArgumentException.class, () -> scanner.getCompanyWithMostSuccessfulMissions(null, to),
                "IllegalArgumentException was expected when passing null as start date but was not thrown.");
        } catch (IOException e) {
            throw new RuntimeException("An error occurred when initializing data from files.");
        }
    }

    @Test
    void testGetCompanyWithMostSuccessfulMissionsToDateNull() {
        try (var missionsReader = new FileReader("scanner-test-1-missions.csv");
             var rocketsReader = new FileReader("scanner-test-1-rockets.csv")) {
            scanner = new MJTSpaceScanner(missionsReader, rocketsReader, secretKey);
            LocalDate from = LocalDate.of(2000, 1, 1);
            assertThrows(IllegalArgumentException.class, () -> scanner.getCompanyWithMostSuccessfulMissions(from, null),
                "IllegalArgumentException was expected when passing null as end date but was not thrown.");
        } catch (IOException e) {
            throw new RuntimeException("An error occurred when initializing data from files.");
        }
    }

    @Test
    void testGetLocationWithMostSuccessfulMissionsPerCompanyFromDateNull() {
        try (var missionsReader = new FileReader("scanner-test-1-missions.csv");
             var rocketsReader = new FileReader("scanner-test-1-rockets.csv")) {
            scanner = new MJTSpaceScanner(missionsReader, rocketsReader, secretKey);
            LocalDate to = LocalDate.of(2000, 1, 1);
            assertThrows(IllegalArgumentException.class,
                () -> scanner.getLocationWithMostSuccessfulMissionsPerCompany(null, to),
                "IllegalArgumentException was expected when passing null as start date but was not thrown.");
        } catch (IOException e) {
            throw new RuntimeException("An error occurred when initializing data from files.");
        }
    }

    @Test
    void testGetLocationWithMostSuccessfulMissionsPerCompanyToDateNull() {
        try (var missionsReader = new FileReader("scanner-test-1-missions.csv");
             var rocketsReader = new FileReader("scanner-test-1-rockets.csv")) {
            scanner = new MJTSpaceScanner(missionsReader, rocketsReader, secretKey);
            LocalDate from = LocalDate.of(2000, 1, 1);
            assertThrows(IllegalArgumentException.class,
                () -> scanner.getLocationWithMostSuccessfulMissionsPerCompany(from, null),
                "IllegalArgumentException was expected when passing null as end date but was not thrown.");
        } catch (IOException e) {
            throw new RuntimeException("An error occurred when initializing data from files.");
        }
    }

    @Test
    void testGetTopNLeastExpensiveMissionsNLessThanZero() {
        try (var missionsReader = new FileReader("scanner-test-1-missions.csv");
             var rocketsReader = new FileReader("scanner-test-1-rockets.csv")) {
            scanner = new MJTSpaceScanner(missionsReader, rocketsReader, secretKey);

            assertThrows(IllegalArgumentException.class,
                () -> scanner.getTopNLeastExpensiveMissions(-2, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE),
                "IllegalArgumentException was expected when passing negative number as collection size limit but was not thrown.");
        } catch (IOException e) {
            throw new RuntimeException("An error occurred when initializing data from files.");
        }
    }

    @Test
    void testGetWikiPagesForRocketsUsedInMostExpensiveMissionsNLessThanZero() {
        try (var missionsReader = new FileReader("scanner-test-1-missions.csv");
             var rocketsReader = new FileReader("scanner-test-1-rockets.csv")) {
            scanner = new MJTSpaceScanner(missionsReader, rocketsReader, secretKey);

            assertThrows(IllegalArgumentException.class,
                () -> scanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(-2, MissionStatus.SUCCESS,
                    RocketStatus.STATUS_ACTIVE),
                "IllegalArgumentException was expected when passing negative number as collection size limit but was not thrown.");
        } catch (IOException e) {
            throw new RuntimeException("An error occurred when initializing data from files.");
        }
    }

    @Test
    void testGetTopNLeastExpensiveMissionsNIsZero() {
        try (var missionsReader = new FileReader("scanner-test-1-missions.csv");
             var rocketsReader = new FileReader("scanner-test-1-rockets.csv")) {
            scanner = new MJTSpaceScanner(missionsReader, rocketsReader, secretKey);

            assertThrows(IllegalArgumentException.class,
                () -> scanner.getTopNLeastExpensiveMissions(0, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE),
                "IllegalArgumentException was expected when passing 0 as collection size limit but was not thrown.");
        } catch (IOException e) {
            throw new RuntimeException("An error occurred when initializing data from files.");
        }
    }

    @Test
    void testGetWikiPagesForRocketsUsedInMostExpensiveMissionsNIsZero() {
        try (var missionsReader = new FileReader("scanner-test-1-missions.csv");
             var rocketsReader = new FileReader("scanner-test-1-rockets.csv")) {
            scanner = new MJTSpaceScanner(missionsReader, rocketsReader, secretKey);

            assertThrows(IllegalArgumentException.class,
                () -> scanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(0, MissionStatus.SUCCESS,
                    RocketStatus.STATUS_ACTIVE),
                "IllegalArgumentException was expected when passing 0 as collection size limit but was not thrown.");
        } catch (IOException e) {
            throw new RuntimeException("An error occurred when initializing data from files.");
        }
    }

    @Test
    void testGetTopNLeastExpensiveMissionsMissionStatusNull() {
        try (var missionsReader = new FileReader("scanner-test-1-missions.csv");
             var rocketsReader = new FileReader("scanner-test-1-rockets.csv")) {
            scanner = new MJTSpaceScanner(missionsReader, rocketsReader, secretKey);

            assertThrows(IllegalArgumentException.class,
                () -> scanner.getTopNLeastExpensiveMissions(4, null, RocketStatus.STATUS_ACTIVE),
                "IllegalArgumentException was expected when passing null as mission status but was not thrown.");
        } catch (IOException e) {
            throw new RuntimeException("An error occurred when initializing data from files.");
        }
    }

    @Test
    void testGetWikiPagesForRocketsUsedInMostExpensiveMissionsMissionStatusNull() {
        try (var missionsReader = new FileReader("scanner-test-1-missions.csv");
             var rocketsReader = new FileReader("scanner-test-1-rockets.csv")) {
            scanner = new MJTSpaceScanner(missionsReader, rocketsReader, secretKey);

            assertThrows(IllegalArgumentException.class,
                () -> scanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(4, null, RocketStatus.STATUS_ACTIVE),
                "IllegalArgumentException was expected when passing null as mission status but was not thrown.");
        } catch (IOException e) {
            throw new RuntimeException("An error occurred when initializing data from files.");
        }
    }

    @Test
    void testGetTopNLeastExpensiveMissionsRocketStatusNull() {
        try (var missionsReader = new FileReader("scanner-test-1-missions.csv");
             var rocketsReader = new FileReader("scanner-test-1-rockets.csv")) {
            scanner = new MJTSpaceScanner(missionsReader, rocketsReader, secretKey);

            assertThrows(IllegalArgumentException.class,
                () -> scanner.getTopNLeastExpensiveMissions(4, MissionStatus.SUCCESS, null),
                "IllegalArgumentException was expected when passing null as rocket status but was not thrown.");
        } catch (IOException e) {
            throw new RuntimeException("An error occurred when initializing data from files.");
        }
    }

    @Test
    void testGetWikiPagesForRocketsUsedInMostExpensiveMissionsRocketStatusNull() {
        try (var missionsReader = new FileReader("scanner-test-1-missions.csv");
             var rocketsReader = new FileReader("scanner-test-1-rockets.csv")) {
            scanner = new MJTSpaceScanner(missionsReader, rocketsReader, secretKey);

            assertThrows(IllegalArgumentException.class,
                () -> scanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(4, MissionStatus.SUCCESS, null),
                "IllegalArgumentException was expected when passing null as rocket status but was not thrown.");
        } catch (IOException e) {
            throw new RuntimeException("An error occurred when initializing data from files.");
        }
    }

    @Test
    void testGetCompanyWithMostSuccessfulMissionsFromAfterToDate() {
        try (var missionsReader = new FileReader("scanner-test-1-missions.csv");
             var rocketsReader = new FileReader("scanner-test-1-rockets.csv")) {
            scanner = new MJTSpaceScanner(missionsReader, rocketsReader, secretKey);
            LocalDate from = LocalDate.of(2001, 1, 1);
            LocalDate to = LocalDate.of(2000, 1, 1);
            assertThrows(TimeFrameMismatchException.class,
                () -> scanner.getCompanyWithMostSuccessfulMissions(from, to),
                "TimeFrameMismatchException was expected when passing start date later than end date but was not thrown.");
        } catch (IOException e) {
            throw new RuntimeException("An error occurred when initializing data from files.");
        }
    }

    @Test
    void testGetLocationWithMostSuccessfulMissionsPerCompanyFromAfterToDate() {
        try (var missionsReader = new FileReader("scanner-test-1-missions.csv");
             var rocketsReader = new FileReader("scanner-test-1-rockets.csv")) {
            scanner = new MJTSpaceScanner(missionsReader, rocketsReader, secretKey);
            LocalDate from = LocalDate.of(2001, 1, 1);
            LocalDate to = LocalDate.of(2000, 1, 1);
            assertThrows(TimeFrameMismatchException.class,
                () -> scanner.getLocationWithMostSuccessfulMissionsPerCompany(from, to),
                "TimeFrameMismatchException was expected when passing start date later than end date but was not thrown.");
        } catch (IOException e) {
            throw new RuntimeException("An error occurred when initializing data from files.");
        }
    }

    @Test
    void testGetAllMissionsSuccess() {
        try (var missionsReader = new FileReader("scanner-test-3-missions.csv");
             var rocketsReader = new FileReader("scanner-test-1-rockets.csv")) {
            scanner = new MJTSpaceScanner(missionsReader, rocketsReader, secretKey);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd, yyyy").withLocale(Locale.ENGLISH);
            LocalDate date1 = LocalDate.parse("Fri Aug 07, 2020", formatter);
            LocalDate date2 = LocalDate.parse("Thu Aug 06, 2020", formatter);
            LocalDate date3 = LocalDate.parse("Tue Aug 04, 2020", formatter);
            Detail detail1 = Detail.of("Falcon 9 Block 5", "Starlink V1 L9 & BlackSky");
            Detail detail2 = Detail.of("Long March 2D", "Gaofen-9 04 & Q-SAT");
            Detail detail3 = Detail.of("Starship Prototype", "150 Meter Hop");

            Mission mission1 = Mission.of("0", "SpaceX",
                "LC-39A, Kennedy Space Center, Florida, USA", date1, detail1,
                RocketStatus.STATUS_ACTIVE, Optional.of(50.0), MissionStatus.SUCCESS);
            Mission mission2 = Mission.of("2", "SpaceX",
                "Pad A, Boca Chica, Texas, USA", date3, detail3,
                RocketStatus.STATUS_ACTIVE, Optional.empty(), MissionStatus.SUCCESS);

            List<Mission> expectedList = List.of(mission1, mission2);

            assertIterableEquals(expectedList, scanner.getAllMissions(MissionStatus.SUCCESS),
                "Expected collection does not match actual.");
        } catch (IOException e) {
            throw new RuntimeException("An error occurred when initializing data from files.");
        }
    }

    @Test
    void testGetCompanyWithMostSuccessfulMissionsNone() {
        try (var missionsReader = new FileReader("scanner-test-2-missions.csv");
             var rocketsReader = new FileReader("scanner-test-1-rockets.csv")) {
            scanner = new MJTSpaceScanner(missionsReader, rocketsReader, secretKey);

            LocalDate from = LocalDate.of(2000, 1, 1);
            LocalDate to = LocalDate.of(2000, 1, 2);
            assertEquals("", scanner.getCompanyWithMostSuccessfulMissions(from, to),
                "Empty string was expected but was '"  + scanner.getCompanyWithMostSuccessfulMissions(from, to) + "'.");
        } catch (IOException e) {
            throw new RuntimeException("An error occurred when initializing data from files.");
        }
    }

    @Test
    void testGetCompanyWithMostSuccessfulMissions() {
        try (var missionsReader = new FileReader("scanner-test-2-missions.csv");
             var rocketsReader = new FileReader("scanner-test-1-rockets.csv")) {
            scanner = new MJTSpaceScanner(missionsReader, rocketsReader, secretKey);

            LocalDate from = LocalDate.of(2020, 1, 1);
            LocalDate to = LocalDate.of(2020, 12, 10);
            assertEquals("SpaceX", scanner.getCompanyWithMostSuccessfulMissions(from, to),
                "Expected company name was 'SpaceX' but actual was ," + scanner.getCompanyWithMostSuccessfulMissions(from, to) + ",.");
        } catch (IOException e) {
            throw new RuntimeException("An error occurred when initializing data from files.");
        }
    }

    @Test
    void testGetMissionsPerCountry() {
        try (var missionsReader = new FileReader("scanner-test-2-missions.csv");
             var rocketsReader = new FileReader("scanner-test-1-rockets.csv")) {
            scanner = new MJTSpaceScanner(missionsReader, rocketsReader, secretKey);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd, yyyy").withLocale(Locale.ENGLISH);
            LocalDate date1 = LocalDate.parse("Fri Aug 07, 2020", formatter);
            LocalDate date2 = LocalDate.parse("Thu Aug 06, 2020", formatter);
            LocalDate date3 = LocalDate.parse("Tue Aug 04, 2020", formatter);
            Detail detail1 = Detail.of("Falcon 9 Block 5", "Starlink V1 L9 & BlackSky");
            Detail detail2 = Detail.of("Long March 2D", "Gaofen-9 04 & Q-SAT");
            Detail detail3 = Detail.of("Starship Prototype", "150 Meter Hop");

            Mission mission1 = Mission.of("0", "SpaceX",
                "LC-39A, Kennedy Space Center, Florida, USA", date1, detail1,
                RocketStatus.STATUS_ACTIVE, Optional.of(50.0), MissionStatus.SUCCESS);
            Mission mission2 = Mission.of("1", "CASC",
                "Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China", date2, detail2,
                RocketStatus.STATUS_ACTIVE, Optional.of(29.75), MissionStatus.SUCCESS);
            Mission mission3 = Mission.of("2", "SpaceX",
                "Pad A, Boca Chica, Texas, USA", date3, detail3,
                RocketStatus.STATUS_ACTIVE, Optional.empty(), MissionStatus.SUCCESS);

            Map<String, Collection<Mission>> expectedMap = new HashMap<>();
            expectedMap.put("USA", List.of(mission1, mission3));
            expectedMap.put("China", List.of(mission2));

            assertTrue(2 == scanner.getMissionsPerCountry().entrySet().size(),
                "Expected collection size was 2 but actual was: " + scanner.getMissionsPerCountry().entrySet().size());
            assertTrue(expectedMap.values().containsAll(scanner.getMissionsPerCountry().values()),
                "Expected map values does not contain all values of actual map.");
            assertTrue(expectedMap.keySet().containsAll(scanner.getMissionsPerCountry().keySet()),
                "Expected map key set does not contain all keys of actual map. \nActual key set: \n" +
                    scanner.getMissionsPerCountry().keySet().toString() +
                    "\nExpected key set:\n" +
                    expectedMap.keySet().toString());
        } catch (IOException e) {
            throw new RuntimeException("An error occurred when initializing data from files.");
        }
    }

    @Test
    void testGetTopNLeastExpensiveMissions() {
        try (var missionsReader = new FileReader("scanner-test-4-missions.csv");
             var rocketsReader = new FileReader("scanner-test-1-rockets.csv")) {
            scanner = new MJTSpaceScanner(missionsReader, rocketsReader, secretKey);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd, yyyy").withLocale(Locale.ENGLISH);
            LocalDate date1 = LocalDate.parse("Thu Aug 06, 2020", formatter);
            LocalDate date2 = LocalDate.parse("Wed Jul 15, 2020", formatter);
            Detail detail1 = Detail.of("Long March 2D", "Gaofen-9 04 & Q-SAT");
            Detail detail2 = Detail.of("Minotaur IV", "NROL-129");

            Mission mission1 = Mission.of("1", "CASC",
                "Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China",
                date1, detail1,
                RocketStatus.STATUS_ACTIVE, Optional.of(29.75), MissionStatus.SUCCESS);
            Mission mission2 = Mission.of("10", "Northrop",
                "LP-0B, Wallops Flight Facility, Virginia, USA",
                date2, detail2,
                RocketStatus.STATUS_ACTIVE, Optional.of(46.0) , MissionStatus.SUCCESS);

            List<Mission> expectedList = List.of(mission1, mission2);

            assertIterableEquals(expectedList, scanner.getTopNLeastExpensiveMissions(2, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE),
                "Expected collection does not match actual.");
        } catch (IOException e) {
            throw new RuntimeException("An error occurred when initializing data from files.");
        }
    }

    @Test
    void testGetMostDesiredLocationForMissionsPerCompany() {
        try (var missionsReader = new FileReader("scanner-test-5-missions.csv");
             var rocketsReader = new FileReader("scanner-test-1-rockets.csv")) {
            scanner = new MJTSpaceScanner(missionsReader, rocketsReader, secretKey);

            Map<String, String> expectedMap = new HashMap<>();
            expectedMap.put("Roscosmos", "Site 81/24, Baikonur Cosmodrome, Kazakhstan");
            expectedMap.put("CASC", "LC-3, Xichang Satellite Launch Center, China");
            expectedMap.put("SpaceX", "LC-39A, Kennedy Space Center, Florida, USA");

            assertTrue(3 == scanner.getMostDesiredLocationForMissionsPerCompany().entrySet().size(),
                "Expected collection size was 2 but actual was: " + scanner.getMostDesiredLocationForMissionsPerCompany().entrySet().size());
            assertTrue(expectedMap.values().containsAll(scanner.getMostDesiredLocationForMissionsPerCompany().values()),
                "Expected map values does not contain all values of actual map. \n Actual key set: \n" +
                    scanner.getMostDesiredLocationForMissionsPerCompany().keySet().toString() +
                    "\nExpected key set:\n" +
                    expectedMap.keySet().toString());
            assertTrue(expectedMap.keySet().containsAll(scanner.getMostDesiredLocationForMissionsPerCompany().keySet()),
                "Expected map key set does not contain all keys of actual map. \nActual key set: \n" +
                    scanner.getMostDesiredLocationForMissionsPerCompany().keySet().toString() +
                    "\nExpected key set:\n" +
                    expectedMap.keySet().toString());
        } catch (IOException e) {
            throw new RuntimeException("An error occurred when initializing data from files.");
        }
    }

    @Test
    void testGetLocationWithMostSuccessfulMissionsPerCompany() {
        try (var missionsReader = new FileReader("scanner-test-6-missions.csv");
             var rocketsReader = new FileReader("scanner-test-1-rockets.csv")) {
            scanner = new MJTSpaceScanner(missionsReader, rocketsReader, secretKey);

            LocalDate from =LocalDate.of(2018, 1, 1);
            LocalDate to = LocalDate.of(2021, 1, 1);
            Map<String, String> expectedMap = new HashMap<>();
            expectedMap.put("Roscosmos", "Site 1S, Vostochny Cosmodrome, Russia");
            expectedMap.put("CASC", "LC-3, Xichang Satellite Launch Center, China");
            expectedMap.put("SpaceX", "LC-39A, Kennedy Space Center, Florida, USA");

            assertTrue(3 == scanner.getLocationWithMostSuccessfulMissionsPerCompany(from, to).entrySet().size(),
                "Expected collection size was 2 but actual was: " + scanner.getLocationWithMostSuccessfulMissionsPerCompany(from, to).entrySet().size());
            assertIterableEquals(expectedMap.values(), scanner.getLocationWithMostSuccessfulMissionsPerCompany(from, to).values(),
                "Expected map values does not contain all values of actual map. \n Actual key set: \n" +
                    scanner.getLocationWithMostSuccessfulMissionsPerCompany(from, to).keySet().toString() +
                    "\nExpected key set:\n" +
                    expectedMap.keySet().toString());
            assertIterableEquals(expectedMap.keySet(), scanner.getLocationWithMostSuccessfulMissionsPerCompany(from, to).keySet(),
                "Expected map key set does not contain all keys of actual map. \nActual key set: \n" +
                    scanner.getLocationWithMostSuccessfulMissionsPerCompany(from, to).keySet().toString() +
                    "\nExpected key set:\n" +
                    expectedMap.keySet().toString());
        } catch (IOException e) {
            throw new RuntimeException("An error occurred when initializing data from files.");
        }
    }

    @Test
    void testGetTopNTallestRockets() {
        try (var missionsReader = new FileReader("scanner-test-1-missions.csv");
             var rocketsReader = new FileReader("scanner-test-3-rockets.csv")) {
            scanner = new MJTSpaceScanner(missionsReader, rocketsReader, secretKey);

            Rocket rocket1 = Rocket.of("0", "Tsyklon-3",
                Optional.of("https://en.wikipedia.org/wiki/Tsyklon-3"),
                Optional.of(39.0));
            Rocket rocket2 = Rocket.of("1", "Tsyklon-4M",
                Optional.of("https://en.wikipedia.org/wiki/Cyclone-4M"),
                Optional.of(38.7));
            Rocket rocket3 = Rocket.of("12", "Unha-2",
                Optional.of("https://en.wikipedia.org/wiki/Unha"),
                Optional.of(28.0));
            List<Rocket> expectedList = List.of(rocket1, rocket2, rocket3);

            assertIterableEquals(expectedList, scanner.getTopNTallestRockets(3),
                "Expected collection does not match actual.");
        } catch (IOException e) {
            throw new RuntimeException("An error occurred when initializing data from files.");
        }
    }

    @Test
    void testGetTopNTallestRocketsNLessThanZero() {
        try (var missionsReader = new FileReader("scanner-test-1-missions.csv");
             var rocketsReader = new FileReader("scanner-test-1-rockets.csv")) {
            scanner = new MJTSpaceScanner(missionsReader, rocketsReader, secretKey);

            assertThrows(IllegalArgumentException.class,
                () -> scanner.getTopNTallestRockets(-2),
                "IllegalArgumentException was expected when passing negative number as collection size limit but was not thrown.");
        } catch (IOException e) {
            throw new RuntimeException("An error occurred when initializing data from files.");
        }
    }

    @Test
    void testGetTopNTallestRocketsNIsZero() {
        try (var missionsReader = new FileReader("scanner-test-1-missions.csv");
             var rocketsReader = new FileReader("scanner-test-1-rockets.csv")) {
            scanner = new MJTSpaceScanner(missionsReader, rocketsReader, secretKey);

            assertThrows(IllegalArgumentException.class,
                () -> scanner.getTopNTallestRockets(0),
                "IllegalArgumentException was expected when passing 0 as collection size limit but was not thrown.");
        } catch (IOException e) {
            throw new RuntimeException("An error occurred when initializing data from files.");
        }
    }

    @Test
    void testGetWikiPageForRocket() {
        try (var missionsReader = new FileReader("scanner-test-1-missions.csv");
             var rocketsReader = new FileReader("scanner-test-4-rockets.csv")) {
            scanner = new MJTSpaceScanner(missionsReader, rocketsReader, secretKey);

            Map<String, Optional<String>> expectedMap = new HashMap<>();
            expectedMap.put("Tsyklon-3", Optional.of("https://en.wikipedia.org/wiki/Tsyklon-3"));
            expectedMap.put("Tsyklon-4M", Optional.of("https://en.wikipedia.org/wiki/Cyclone-4M"));
            expectedMap.put("Vector-R", Optional.of("https://en.wikipedia.org/wiki/Vector-R"));

            assertTrue(3 == scanner.getWikiPageForRocket().entrySet().size(),
                "Expected collection size was 3 but actual was: " + scanner.getWikiPageForRocket().entrySet().size());
            assertIterableEquals(expectedMap.values(), scanner.getWikiPageForRocket().values(),
                "Expected map values does not contain all values of actual map. \n Actual key set: \n" +
                    scanner.getWikiPageForRocket().keySet().toString() +
                    "\nExpected key set:\n" +
                    expectedMap.keySet().toString());
            assertIterableEquals(expectedMap.keySet(), scanner.getWikiPageForRocket().keySet(),
                "Expected map key set does not contain all keys of actual map. \nActual key set: \n" +
                    scanner.getWikiPageForRocket().keySet().toString() +
                    "\nExpected key set:\n" +
                    expectedMap.keySet().toString());
        } catch (IOException e) {
            throw new RuntimeException("An error occurred when initializing data from files.");
        }
    }

    @Test
    void testGetWikiPagesForRocketsUsedInMostExpensiveMissions() {
        try (var missionsReader = new FileReader("scanner-test-7-missions.csv");
             var rocketsReader = new FileReader("scanner-test-4-rockets.csv")) {
            scanner = new MJTSpaceScanner(missionsReader, rocketsReader, secretKey);

            MissionStatus s1 = MissionStatus.SUCCESS;
            RocketStatus s2 = RocketStatus.STATUS_ACTIVE;
            List<String> expectedList = List.of("https://en.wikipedia.org/wiki/Tsyklon-3", "https://en.wikipedia.org/wiki/Vector-R");

            assertIterableEquals(expectedList, scanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(2, s1, s2),
                "Expected collection does not match actual.");
        } catch (IOException e) {
            throw new RuntimeException("An error occurred when initializing data from files.");
        }
    }
}
