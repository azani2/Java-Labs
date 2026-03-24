package bg.sofia.uni.fmi.mjt.csvprocessor;

import bg.sofia.uni.fmi.mjt.csvprocessor.exceptions.CsvDataNotCorrectException;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.BaseTable;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.Table;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.printer.ColumnAlignment;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.printer.MarkdownTablePrinter;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.printer.TablePrinter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class CsvProcessor implements CsvProcessorAPI {
    private Table table;
    int columnsCount;

    public CsvProcessor() {
        this(new BaseTable());
    }

    public CsvProcessor(Table table) {
        this.table = table;
    }

    @Override
    public void readCsv(Reader reader, String delimiter) throws CsvDataNotCorrectException {
        if (reader == null) {
            throw new IllegalArgumentException("Reader was null.");
        }
        if (delimiter == null || delimiter.isBlank()) {
            throw new IllegalArgumentException("Delimiter was null or blank.");
        }

        columnsCount = 0;
        delimiter = "\\Q" + delimiter + "\\E";
        try {
            var bufferedReader = new BufferedReader(reader);
            String line;
            if ((line = bufferedReader.readLine()) != null) {
                List<String> row = new ArrayList<>(List.of(line.split(delimiter)));
                columnsCount = row.size();
                table.addData(line.split(delimiter));
                while ((line = bufferedReader.readLine()) != null) {
                    row = new ArrayList<>(List.of(line.split(delimiter)));
                    if (columnsCount != row.size()) {
                        throw new CsvDataNotCorrectException("Csv data was not in correct format.");
                    }
                    table.addData(line.split(delimiter));
                }
                bufferedReader.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writeTable(Writer writer, ColumnAlignment... alignments) {
        if (writer == null) {
            throw new IllegalArgumentException("Writer was null.");
        }
        if (alignments == null) {
            throw new IllegalArgumentException("Alignments was null.");
        }

        TablePrinter markdownPrinter = new MarkdownTablePrinter();
        List<String> formattedTableRows = new ArrayList<>(markdownPrinter.printTable(table, alignments));

        try {
            var bufferedWriter = new BufferedWriter(writer);
            for (String row : formattedTableRows) {
                if (formattedTableRows.indexOf(row) != 0 ) {
                    bufferedWriter.write(System.lineSeparator());
                }
                bufferedWriter.write(row);
            }
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
