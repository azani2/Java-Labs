package bg.sofia.uni.fmi.mjt.csvprocessor.table.printer;

import bg.sofia.uni.fmi.mjt.csvprocessor.exceptions.CsvDataNotCorrectException;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.BaseTable;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.Table;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.Mockito.when;

public class TablePrinterTest {
    Table table;
    TablePrinter markdownPrinter;

    @Test
    void testPrintTableEmpty() {
        markdownPrinter = new MarkdownTablePrinter();
        table = new BaseTable();
        ColumnAlignment[] alignents = new ColumnAlignment[0];

        List<String> emptyList = List.of();
        assertIterableEquals(markdownPrinter.printTable(table, alignents), emptyList,
            "Empty collection was expected but was not returned.");
    }

    @Test
    void testPrintTableDefaultAlignments() throws CsvDataNotCorrectException {
        markdownPrinter = new MarkdownTablePrinter();
        ColumnAlignment[] alignments = new ColumnAlignment[0];
        table = new BaseTable();
        for (int i = 0; i < 2; i++) {
            String[] data = new String[2];
            data[0] = "value0" + i;
            data[1] = "value1" + i;

            table.addData(data);
        }

        List<String> list = List.of("| value00 | value10 |",
            "| ------- | ------- |",
            "| value01 | value11 |");
        assertIterableEquals(markdownPrinter.printTable(table, alignments), list,
            "Empty collection was expected but was not returned.");
    }

    @Test
    void testPrintTableDefinedAlignments() throws CsvDataNotCorrectException {
        markdownPrinter = new MarkdownTablePrinter();
        ColumnAlignment[] alignments = new ColumnAlignment[3];
        alignments[0] = ColumnAlignment.LEFT;
        alignments[1] = ColumnAlignment.CENTER;
        alignments[2] = ColumnAlignment.RIGHT;
        table = new BaseTable();
        for (int i = 0; i < 2; i++) {
            String[] data = new String[3];
            data[0] = "value0" + i;
            data[1] = "value1" + i;
            data[2] = "value2" + i;

            table.addData(data);
        }

        List<String> list = List.of("| value00 | value10 | value20 |",
            "| :------ | :-----: | ------: |",
            "| value01 | value11 | value21 |");
        assertIterableEquals(markdownPrinter.printTable(table, alignments), list,
            "Empty collection was expected but was not returned.");
    }

    @Test
    void testPrintTableExtraAlignments() throws CsvDataNotCorrectException {
        markdownPrinter = new MarkdownTablePrinter();
        ColumnAlignment[] alignments = new ColumnAlignment[3];
        alignments[0] = ColumnAlignment.LEFT;
        alignments[1] = ColumnAlignment.CENTER;
        alignments[2] = ColumnAlignment.RIGHT;
        table = new BaseTable();
        for (int i = 0; i < 2; i++) {
            String[] data = new String[2];
            data[0] = "value0" + i;
            data[1] = "value1" + i;

            table.addData(data);
        }

        List<String> list = List.of("| value00 | value10 |",
            "| :------ | :-----: |",
            "| value01 | value11 |");
        assertIterableEquals(markdownPrinter.printTable(table, alignments), list,
            "Empty collection was expected but was not returned.");
    }

    @Test
    void testPrintTableMaxLength() throws CsvDataNotCorrectException {
        markdownPrinter = new MarkdownTablePrinter();
        ColumnAlignment[] alignments = new ColumnAlignment[0];
        table = new BaseTable();

        String[] data = new String[2];
        data[0] = "value00";
        data[1] = "value10";
        table.addData(data);

        data = new String[2];
        data[0] = "val01";
        data[1] = "value11";
        table.addData(data);

        List<String> list = List.of("| value00 | value10 |",
            "| ------- | ------- |",
            "| val01   | value11 |");
        assertIterableEquals(markdownPrinter.printTable(table, alignments), list,
            "Empty collection was expected but was not returned.");
    }

    @Test
    void testPrintTableMinLength() throws CsvDataNotCorrectException {
        markdownPrinter = new MarkdownTablePrinter();
        ColumnAlignment[] alignments = new ColumnAlignment[0];
        table = new BaseTable();

        String[] data = new String[2];
        data[0] = "v0";
        data[1] = "value10";
        table.addData(data);

        data = new String[2];
        data[0] = "v1";
        data[1] = "value11";
        table.addData(data);

        List<String> list = List.of("| v0  | value10 |",
            "| --- | ------- |",
            "| v1  | value11 |");
        assertIterableEquals(markdownPrinter.printTable(table, alignments), list,
            "Empty collection was expected but was not returned.");
    }
}
