package bg.sofia.uni.fmi.mjt.football;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class FootballPlayerAnalyzerTest {
    private FootballPlayerAnalyzer analyzer;
    private Reader fileReader;

    @Test
    void testGetAllPlayers() {
        try {
            fileReader = new FileReader("fifa_players_test1.csv");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        analyzer = new FootballPlayerAnalyzer(fileReader);

        List<Player> expectedList = new ArrayList<>();
        expectedList.add(Player.of(
            "L. Messi;Lionel Andrés Messi Cuccittini;6/24/1987;31;170.18;72.1;CF,RW,ST;Argentina;94;94;110500000;565000;Left"));
        expectedList.add(Player.of(
            "C. Eriksen;Christian  Dannemann Eriksen;2/14/1992;27;154.94;76.2;CAM,RM,CM;Denmark;88;89;69500000;205000;Right"));

        assertIterableEquals(expectedList, analyzer.getAllPlayers(),
            "Unexpected results getting players list.");
    }

    @Test
    void testGetAllNationalities() {
        try {
            fileReader = new FileReader("fifa_players_test1.csv");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        analyzer = new FootballPlayerAnalyzer(fileReader);

        List<String> expectedList = List.of("Argentina", "Denmark");

        assertIterableEquals(expectedList, analyzer.getAllNationalities(),
            "Unexpected results getting nationalities list.");
    }

    @Test
    void testGetHighestPaidPlayerByNationality() {
        try {
            fileReader = new FileReader("fifa_players_test2.csv");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        analyzer = new FootballPlayerAnalyzer(fileReader);

        Player expectedPlayer = Player.of(
            "L. Messi;Lionel Andrés Messi Cuccittini;6/24/1987;31;170.18;72.1;CF,RW,ST;Argentina;94;94;110500000;565000;Left");
        assertEquals(expectedPlayer, analyzer.getHighestPaidPlayerByNationality("Argentina"),
            "Unexpected results getting highest paid player from nationality.");
    }

    @Test
    void testGetHighestPaidPlayerByNationalityNull() {
        try {
            fileReader = new FileReader("fifa_players_test1.csv");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        analyzer = new FootballPlayerAnalyzer(fileReader);

        assertThrows(IllegalArgumentException.class, () -> analyzer.getHighestPaidPlayerByNationality(null),
            "Unexpected results getting nationalities list.");
    }

    @Test
    void testFootballPlayerAnalyzer() {
        assertThrows(FileNotFoundException.class, () -> fileReader = new FileReader("unknownFile.csv"),
            "UncheckedIOException (FileNotFoundException) was expected, but was not thrown.");
    }

    @Test
    void testGroupByPosition() {
        try {
            fileReader = new FileReader("fifa_players_test3.csv");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        analyzer = new FootballPlayerAnalyzer(fileReader);

        Player pl1 = Player.of("M;LAMC;6/24/1987;31;170.18;72.1;ST;Denmark;94;94;110500000;565000;Left");
        Player pl2 = Player.of("E;CDE;2/14/1992;27;154.94;76.2;CAM;Denmark;88;89;69500000;205000;Right");
        Player pl3 = Player.of("P;PP;3/15/1993;25;190.5;83.9;ST,CAM;France;88;91;73000000;255000;Right");
        Set<Player> posST = Set.of(pl1, pl3);
        Set<Player> posCAM = Set.of(pl2, pl3);

        assertTrue(posST.size() == analyzer.groupByPosition().get(Position.CAM).size()
            && posST.containsAll(analyzer.groupByPosition().get(Position.ST)),
            "Expected player set does not match result.");

        assertTrue(posCAM.size() == analyzer.groupByPosition().get(Position.CAM).size()
            && posCAM.containsAll(analyzer.groupByPosition().get(Position.CAM)),
            "Expected player set does not match result.");
    }
    
    @Test
    void testGetTopProspectPlayerForPositionInBudget() {
        try {
            fileReader = new FileReader("fifa_players_test4.csv");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        analyzer = new FootballPlayerAnalyzer(fileReader);
        Player expectedPlayer = Player.of("M;LAMC;6/24/1987;10;170.18;72.1;ST;Denmark;94;94;100;565000;Left");
        Optional<Player> optExpected = Optional.of(expectedPlayer);

        assertEquals(optExpected, analyzer.getTopProspectPlayerForPositionInBudget(Position.ST, 150),
            "Expected player does not match result.");
    }

    @Test
    void testGetSimilarPlayersNull() {
        try {
            fileReader = new FileReader("fifa_players_test4.csv");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        analyzer = new FootballPlayerAnalyzer(fileReader);
        assertThrows(IllegalArgumentException.class, () -> analyzer.getSimilarPlayers(null),
            "IllegalArgumentException was expected when passing null as player but was not thrown.");
    }

    @Test
    void testGetPlayersByFullNameKeyword() {
        try {
            fileReader = new FileReader("fifa_players_test4.csv");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        analyzer = new FootballPlayerAnalyzer(fileReader);
        assertThrows(IllegalArgumentException.class, () -> analyzer.getPlayersByFullNameKeyword(null),
            "IllegalArgumentException was expected when passing null as keyword but was not thrown.");
    }

    @Test
    void testGetTopProspectPlayerForPositionInBudgetNullPosition() {
        try {
            fileReader = new FileReader("fifa_players_test4.csv");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        analyzer = new FootballPlayerAnalyzer(fileReader);
        assertThrows(IllegalArgumentException.class, () -> analyzer.getTopProspectPlayerForPositionInBudget(null, 150),
            "IllegalArgumentException was expected when passing null as position but was not thrown.");
    }

    @Test
    void testGetTopProspectPlayerForPositionInBudgetNegativeBudget() {
        try {
            fileReader = new FileReader("fifa_players_test4.csv");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        analyzer = new FootballPlayerAnalyzer(fileReader);
        assertThrows(IllegalArgumentException.class, () -> analyzer.getTopProspectPlayerForPositionInBudget(Position.ST, -150),
            "IllegalArgumentException was expected when passing negative number as budget but was not thrown.");
    }

    @Test
    void testGetSimilarPlayers() {
        try {
            fileReader = new FileReader("fifa_players_test5.csv");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        analyzer = new FootballPlayerAnalyzer(fileReader);

        Player pl1 = Player.of("L. Messi;Lionel Andrés Messi Cuccittini;6/24/1987;31;170.18;72.1;CF,RW,ST;Argentina;94;94;110500000;565000;Left");
        Player pl2 = Player.of("L. Sessi;Lionel Andrés Messi Succittini;6/24/1987;31;170.18;72.1;CF,RW,ST;Argentina;94;94;110500000;565000;Left");
        Player toBeSimilarTo = Player.of("L. Kessi;Lionel Andrés Messi Cuccittini;6/24/1987;31;170.18;72.1;CF,RW,ST;Argentina;94;94;110500000;565000;Left");

        Set<Player> expectedPlayers = Set.of(pl1, pl2);
        Set<Player> actual = analyzer.getSimilarPlayers(toBeSimilarTo);
        assertTrue(expectedPlayers.size() == actual.size()
        && expectedPlayers.containsAll(actual),
            "Result set of similar players does not match expected.");
    }
}
