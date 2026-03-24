package bg.sofia.uni.fmi.mjt.csvprocessor;

import bg.sofia.uni.fmi.mjt.csvprocessor.exceptions.CsvDataNotCorrectException;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.BaseTable;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.Table;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.printer.ColumnAlignment;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

import static org.junit.jupiter.api.Assertions.*;

public class CsvProcessorTest {
    Table table;
    @InjectMocks
    CsvProcessorAPI csvProcessor;

    @Test
    void testReadCsvNullReader() {
        csvProcessor = new CsvProcessor();
        assertThrows(IllegalArgumentException.class, () -> csvProcessor.readCsv(null, ","),
            "IllegalArgumentException was expected, but nothing was thrown.");
    }

    @Test
    void testReadCsvNullDelimiter() {
        csvProcessor = new CsvProcessor();
        assertThrows(FileNotFoundException.class, () -> csvProcessor.readCsv(new FileReader("exampledata.csv"), null),
            "IllegalArgumentException was expected, but nothing was thrown.");
    }

    @Test
    void testReadCsvBlankDelimiter() {
        csvProcessor = new CsvProcessor();
        assertThrows(FileNotFoundException.class, () -> csvProcessor.readCsv(new FileReader("exampledata.csv"), "  "),
            "IllegalArgumentException was expected, but nothing was thrown.");
    }

    @Test
    void testWriteTable() throws CsvDataNotCorrectException {
        table = new BaseTable();
        String[] data = new String[2];
        data[0] = "colName1";
        data[1] = "colNaame2";
        data = new String[2];
        data[0] = "val1";
        data[1] = "val2";
        table.addData(data);

        csvProcessor = new CsvProcessor();
        assertDoesNotThrow(() ->csvProcessor.writeTable(new FileWriter("exampleResult.txt"), new ColumnAlignment[0]),
            "Unexpected exception when writing to file.");
    }
}
