package bg.sofia.uni.fmi.mjt.csvprocessor.table;

import bg.sofia.uni.fmi.mjt.csvprocessor.exceptions.CsvDataNotCorrectException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TableTest {
    Table baseTable;

    @Test
    void testAddDataNull() {
        baseTable = new BaseTable();
        assertThrows(IllegalArgumentException.class, () -> baseTable.addData(null),
            "IllegalArgumentException was expected, but nothing was thrown.");
    }

    @Test
    void testAddDataWrongFormat() {
        baseTable = new BaseTable();
        String[] data1 = new String[2];
        data1[0] = "d11";
        data1[1] = "d12";

        String[] data2 = new String[3];
        data2[0] = "d21";
        data2[1] = "d22";
        data2[2] = "d23";

        assertDoesNotThrow(() -> baseTable.addData(data1),
            "Unexpected exception was thrown when adding valid data to empty table.");
        assertThrows(CsvDataNotCorrectException.class, () -> baseTable.addData(data2),
            "CsvDataNotCorrectException was expected, but nothing was thrown.");
    }

    @Test
    void addDataEmpty() {
        baseTable = new BaseTable();
        String[] data1 = new String[3];
        data1[0] = "colName1";
        data1[1] = "colName2";
        data1[2] = "colName3";

        assertDoesNotThrow(() -> baseTable.addData(data1),
            "Unexpected exception thrown when adding data to empty table");
        List<String> names = List.of("colName1", "colName2", "colName3");
        assertIterableEquals(baseTable.getColumnNames(), names,
            "Column names were expected to be the first added values to an empty table.");
    }

    @Test
    void getColumnDataEmptyTable() {
        baseTable = new BaseTable();
        assertThrows(IllegalArgumentException.class, () -> baseTable.getColumnData("test"),
            "IllegalArgumentException was expected, but nothing was thrown.");
    }

    @Test
    void getColumnDataNullColumnName() {
        baseTable = new BaseTable();
        assertThrows(IllegalArgumentException.class, () -> baseTable.getColumnData(null),
            "IllegalArgumentException was expected, but nothing was thrown.");
    }

    @Test
    void getColumnDataBlankColumnName() {
        baseTable = new BaseTable();
        assertThrows(IllegalArgumentException.class, () -> baseTable.getColumnData("  "),
            "IllegalArgumentException was expected, but nothing was thrown.");
    }

    @Test
    void testGetColumnDataUnknownNonemptyTable() {
        baseTable = new BaseTable();
        String[] data1 = new String[2];
        data1[0] = "colName1";
        data1[1] = "colName2";
        assertDoesNotThrow(() -> baseTable.addData(data1),
            "Unexpected exception thrown when adding data to empty table");
        assertThrows(IllegalArgumentException.class, () -> baseTable.getColumnData("colName3"),
            "IllegalArgumentException was expected, but nothing was thrown.");
    }

    @Test
    void testAddDataNotEmpty() {
        baseTable = new BaseTable();
        String[] names = new String[2];
        names[0] = "colName1";
        names[1] = "colName2";

        assertDoesNotThrow(() -> baseTable.addData(names),
            "Unexpected exception thrown when adding data to empty table");

        String[] datas = new String[2];
        datas[0] = "value11";
        datas[1] = "value21";

        assertDoesNotThrow(() -> baseTable.addData(datas),
            "Unexpected exception was thrown when adding valid data to table.");
    }

    @Test
    void testGetRowsCountNoRows() {
        baseTable = new BaseTable();
        assertEquals(baseTable.getRowsCount(), 0,
            "Unexpected rows count of empty table.");
    }

    @Test
    void testGetRowsCountManyRows() {
        baseTable = new BaseTable();
        for (int i = 0; i < 5; i++) {
            String[] data = new String[2];
            data[0] = "value0" + i;
            data[1] = "value1" + i;

            assertDoesNotThrow(() -> baseTable.addData(data),
                "Unexpected exception thrown when adding data to table");
        }
        assertEquals(baseTable.getRowsCount(), 5,
            "Unexpected rows count of empty table.");
    }

    @Test
    void testGetColumnDataNone() {
        baseTable = new BaseTable();
        String[] names = new String[2];
        names[0] = "colName1";
        names[1] = "colName2";

        assertDoesNotThrow(() -> baseTable.addData(names),
            "Unexpected exception thrown when adding data to empty table");
        assertEquals(baseTable.getColumnData("colName1").size(), 0,
            "Unexpected column size when getting data from empty column.");
    }

    @Test
    void testGetColumnDataVali() {
        baseTable = new BaseTable();
        String[] names = new String[2];
        names[0] = "colName1";
        names[1] = "colName2";

        assertDoesNotThrow(() -> baseTable.addData(names),
            "Unexpected exception thrown when adding data to empty table");

        for (int i = 0; i < 5; i++) {
            String[] data = new String[2];
            data[0] = "value0" + i;
            data[1] = "value1" + i;

            assertDoesNotThrow(() -> baseTable.addData(data),
                "Unexpected exception thrown when adding data to table");
        }
        assertEquals(baseTable.getColumnData("colName1").size(), 5,
            "Unexpected column size when getting data from empty column.");
    }
}
