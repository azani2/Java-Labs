package bg.sofia.uni.fmi.mjt.space;

import bg.sofia.uni.fmi.mjt.space.algorithm.Rijndael;
import bg.sofia.uni.fmi.mjt.space.exception.CipherException;
import bg.sofia.uni.fmi.mjt.space.exception.TimeFrameMismatchException;
import bg.sofia.uni.fmi.mjt.space.mission.Mission;
import bg.sofia.uni.fmi.mjt.space.mission.MissionStatus;
import bg.sofia.uni.fmi.mjt.space.rocket.Rocket;
import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;

import javax.crypto.SecretKey;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MJTSpaceScanner implements SpaceScannerAPI {
    private static final String ROCKET_DATA_FILE = "most-reliable-rocket.txt";
    private final List<Mission> allMissions;
    private final List<Rocket> allRockets;
    private final SecretKey secretKey;

    public MJTSpaceScanner(Reader missionsReader, Reader rocketsReader, SecretKey secretKey) {
        this.secretKey = secretKey;
        try (var bufferedMissionsReader = new BufferedReader(missionsReader);
             var bufferedRocketsReader = new BufferedReader(rocketsReader)) {
            allMissions = bufferedMissionsReader
                .lines()
                .skip(1)
                .map(Mission::of)
                .toList();
            allRockets = bufferedRocketsReader
                .lines()
                .skip(1)
                .map(Rocket::of)
                .toList();
        } catch (IOException e) {
            throw new RuntimeException("A problem occurred when getting missions or rockets from readers.");
        }
    }

    @Override
    public Collection<Mission> getAllMissions() {
        return List.copyOf(allMissions);
    }

    @Override
    public Collection<Mission> getAllMissions(MissionStatus missionStatus) {
        if (missionStatus == null) {
            throw new IllegalArgumentException("Mission status was null.");
        }

        return allMissions.stream()
            .filter(mission -> mission.missionStatus().equals(missionStatus))
            .collect(Collectors.toList());
    }

    private void validStartEndDate(LocalDate from, LocalDate to) {
        if (to == null || from == null) {
            throw new IllegalArgumentException("Start or end date was null.");
        }
        if (from.isAfter(to)) {
            throw new TimeFrameMismatchException("Start date was after end date.");
        }
    }

    @Override
    public String getCompanyWithMostSuccessfulMissions(LocalDate from, LocalDate to) {
        validStartEndDate(from, to);

        return allMissions.stream()
            .filter(mission -> mission.missionStatus().equals(MissionStatus.SUCCESS))
            .filter(mission -> mission.date().isAfter(from))
            .filter(mission -> mission.date().isBefore(to))
            .collect(Collectors.groupingBy(Mission::company, Collectors.counting()))
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue() > 1)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
            .entrySet()
            .stream()
            .max(Comparator.comparing(Map.Entry::getValue))
            .map(Map.Entry::getKey)
            .orElse("");
    }

    private String getLocationCountry(String location) {
        String[] tokens = location.split(",");
        int lastIdx = tokens.length - 1;
        return tokens[lastIdx].trim();
    }

    @Override
    public Map<String, Collection<Mission>> getMissionsPerCountry() {
        List<String> countries = allMissions.stream()
            .map(Mission::location)
            .map(this::getLocationCountry)
            .distinct()
            .toList();
        Map<String, Collection<Mission>> missionsPerCountry = new HashMap<>();
        for (String country : countries) {
            List<Mission> missionsInCountry = allMissions.stream()
                .filter(mission -> getLocationCountry(mission.location()).equals(country))
                .toList();
            missionsPerCountry.put(country, missionsInCountry);
        }
        return missionsPerCountry;
    }

    @Override
    public List<Mission> getTopNLeastExpensiveMissions(int n, MissionStatus missionStatus, RocketStatus rocketStatus) {
        if (n <= 0) {
            throw new IllegalArgumentException("Desired list length must be greater than 0.");
        }
        if (missionStatus == null) {
            throw new IllegalArgumentException("Mission status was null.");
        }
        if (rocketStatus == null) {
            throw new IllegalArgumentException("Rocket status was null.");
        }

        return allMissions.stream()
            .filter(mission -> mission.missionStatus().equals(missionStatus))
            .filter(mission -> mission.rocketStatus().equals(rocketStatus))
            .filter(mission -> mission.cost().isPresent())
            .sorted(Comparator.comparingDouble(mission -> mission.cost().get()))
            .limit(n)
            .toList();
    }

    @Override
    public Map<String, String> getMostDesiredLocationForMissionsPerCompany() {
        List<String> companies = allMissions.stream()
            .map(Mission::company)
            .distinct()
            .toList();

        Map<String, String> locationsPerCompany = new HashMap<>();
        for (String company : companies) {
            String locationForCompany = allMissions.stream()
                .filter(mission -> mission.company().equals(company))
                .collect(Collectors.groupingBy(Mission::location, Collectors.counting()))
                .entrySet()
                .stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .get();

            locationsPerCompany.put(company, locationForCompany);
        }
        return locationsPerCompany;
    }

    @Override
    public Map<String, String> getLocationWithMostSuccessfulMissionsPerCompany(LocalDate from, LocalDate to) {
        validStartEndDate(from, to);

        List<String> companies = allMissions.stream()
            .map(Mission::company)
            .distinct()
            .toList();

        Map<String, String> locationsPerCompany = new HashMap<>();
        for (String company : companies) {
            String locationForCompany = allMissions.stream()
                .filter(mission -> mission.company().equals(company))
                .filter(mission -> mission.missionStatus().equals(MissionStatus.SUCCESS))
                .filter(mission -> mission.date().isAfter(from))
                .filter(mission -> mission.date().isBefore(to))
                .collect(Collectors.groupingBy(Mission::location, Collectors.counting()))
                .entrySet()
                .stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .get();

            locationsPerCompany.put(company, locationForCompany);
        }
        return locationsPerCompany;
    }

    @Override
    public Collection<Rocket> getAllRockets() {
        return List.copyOf(allRockets);
    }

    @Override
    public List<Rocket> getTopNTallestRockets(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Length limit must be greater than 0.");
        }

        List<Rocket> sorted = allRockets.stream()
            .filter(rocket -> rocket.height().isPresent())
            .sorted(Comparator.comparingDouble(rocket -> rocket.height().get()))
            .toList();
        return sorted.reversed().stream()
            .limit(n)
            .toList();
    }

    @Override
    public Map<String, Optional<String>> getWikiPageForRocket() {
        return allRockets.stream()
            .filter(rocket -> !rocket.wiki().get().isEmpty())
            .collect(Collectors.toMap(Rocket::name, Rocket::wiki));
    }

    @Override
    public List<String> getWikiPagesForRocketsUsedInMostExpensiveMissions(int n, MissionStatus missionStatus,
                                                                          RocketStatus rocketStatus) {
        if (n <= 0) {
            throw new IllegalArgumentException("Length limit must be greater than 0.");
        }
        if (missionStatus == null) {
            throw new IllegalArgumentException("Mission status was null.");
        }
        if (rocketStatus == null) {
            throw new IllegalArgumentException("Rocket status was null.");
        }

        Map<String, Optional<String>> rocketsWikis = getWikiPageForRocket();
        return allMissions.stream()
            .filter(mission -> mission.cost().isPresent())
            .sorted(Comparator.comparing(mission -> mission.cost().get()))
            .toList()
            .reversed()
            .stream()
            .limit(n)
            .map(mission -> rocketsWikis.get(mission.detail().rocketName()).get())
            .toList();
    }

    private int getRocketSuccessfulMissionsCount(Rocket rocket, LocalDate from, LocalDate to) {
        return allMissions.stream()
            .filter(mission -> mission.date().isAfter(from))
            .filter(mission -> mission.date().isBefore(to))
            .filter(mission -> mission.detail().rocketName().equals(rocket.name()))
            .filter(mission -> mission.missionStatus().equals(MissionStatus.SUCCESS))
            .toList()
            .size();
    }

    private int getRocketFailedMissionsCount(Rocket rocket, LocalDate from, LocalDate to) {
        return allMissions.stream()
            .filter(mission -> mission.date().isAfter(from))
            .filter(mission -> mission.date().isBefore(to))
            .filter(mission -> mission.detail().rocketName().equals(rocket.name()))
            .filter(mission -> mission.missionStatus().equals(MissionStatus.FAILURE))
            .toList()
            .size();
    }

    private double calculateReliability(Rocket rocket, LocalDate from, LocalDate to) {
        double successfulCount = getRocketSuccessfulMissionsCount(rocket, from, to);
        double failedCount = getRocketFailedMissionsCount(rocket, from, to);
        double allRocketMissionsCount = successfulCount + failedCount;
        return (2 * successfulCount + failedCount) / (2 * allRocketMissionsCount);
    }

    private Rocket getMostReliableRocket(LocalDate from, LocalDate to) {
        return allRockets.stream()
            .max(Comparator.comparingDouble(rocket -> calculateReliability(rocket, from, to)))
            .get();
    }

    @Override
    public void saveMostReliableRocket(OutputStream outputStream, LocalDate from, LocalDate to) throws CipherException {
        validStartEndDate(from, to);
        if (outputStream == null) {
            throw new IllegalArgumentException("Output stream was null.");
        }

        Rocket rocket = getMostReliableRocket(from, to);
        Path rocketPath = Path.of(ROCKET_DATA_FILE);

        try {
            Files.writeString(rocketPath, rocket.toString(),
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("An error occurred when writing data to file.");
        }

        try (InputStream decryptedInputStream = new FileInputStream(rocketPath.toFile())) {
            Rijndael encryptor = new Rijndael(secretKey);
            encryptor.encrypt(decryptedInputStream, outputStream);
        } catch (IOException e) {
            throw new RuntimeException("An error occurred when writing data to file.");
        }
        rocketPath.toFile().delete();
    }
}
