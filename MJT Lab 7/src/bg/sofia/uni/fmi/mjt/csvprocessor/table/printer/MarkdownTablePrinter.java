package bg.sofia.uni.fmi.mjt.csvprocessor.table.printer;

import bg.sofia.uni.fmi.mjt.csvprocessor.table.Table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarkdownTablePrinter implements TablePrinter {
    static final int MIN_MAX_LENGTH = 3;
    private Map<String, Integer> columnsMaxLengths;
    private Table toFormat;
    private List<ColumnAlignment> alignments;
    int rowsCount;

    public MarkdownTablePrinter() {
        columnsMaxLengths = null;
        toFormat = null;
        alignments = null;
    }

    private void calculateColumnMaxLengths() {
        columnsMaxLengths = new HashMap<>();

        for (String columnName : toFormat.getColumnNames()) {
            List<String> columnData = new ArrayList<>(toFormat.getColumnData(columnName));

            int maxLength = MIN_MAX_LENGTH;
            for (String data : columnData) {
                if (data.length() > maxLength) {
                    maxLength = data.length();
                }
            }
            if (columnName.length() > maxLength) {
                maxLength = columnName.length();
            }
            columnsMaxLengths.put(columnName, maxLength);
        }
    }

    private String columnNamesRow() {
        StringBuilder row = new StringBuilder("|");
        for (String columnName : toFormat.getColumnNames()) {
            int maxLength = columnsMaxLengths.get(columnName);

            row.append(" ");
            row.append(columnName);
            row.append(" ".repeat(maxLength - columnName.length()));
            row.append(" |");
        }

        return row.toString();
    }

    private String columnFiller(int currentIndex, int maxLength) {
        int alignmentsCount = alignments.size();

        ColumnAlignment currentAlignment;
        if (currentIndex < alignmentsCount) {
            currentAlignment = alignments.get(currentIndex);
        } else {
            currentAlignment = ColumnAlignment.NOALIGNMENT;
        }

        int alignmentCharCount = currentAlignment.getAlignmentCharactersCount();
        String dashes = "-".repeat(maxLength - alignmentCharCount);
        return switch (currentAlignment) {
            case LEFT -> ":" + dashes;
            case CENTER -> ":" + dashes + ":";
            case RIGHT -> dashes + ":";
            case NOALIGNMENT -> dashes;
        };
    }

    private String alignmentsRow() {
        StringBuilder row = new StringBuilder("|");
        int currentIndex = 0;

        for (String columnName : toFormat.getColumnNames()) {
            int maxLength = columnsMaxLengths.get(columnName);
            String columnFiller = columnFiller(currentIndex, maxLength);
            row.append(" ");
            row.append(columnFiller);
            row.append(" |");
            currentIndex++;
        }

        return row.toString();
    }

    private String dataRow(int index) {
        StringBuilder row = new StringBuilder("|");

        for (String columnName : toFormat.getColumnNames()) {
            List<String> columnData = new ArrayList<>(toFormat.getColumnData(columnName));
            String currentData = columnData.get(index);
            int maxLength = columnsMaxLengths.get(columnName);

            row.append(" ");
            row.append(currentData);
            row.append(" ".repeat(maxLength - currentData.length()));
            row.append(" |");
        }

        return row.toString();
    }

    @Override
    public Collection<String> printTable(Table table, ColumnAlignment... alignments) {
        this.toFormat = table;
        calculateColumnMaxLengths();
        this.alignments = new ArrayList<>();
        this.alignments.addAll(Arrays.asList(alignments));
        rowsCount = table.getRowsCount();

        List<String> rows = new ArrayList<>();
        if (rowsCount >= 1) {
            rows.add(columnNamesRow());
        }
        if (rowsCount >= 2) {
            rows.add(alignmentsRow());
        }

        for (int i = 0; i < rowsCount - 1; i++) {
            rows.add(dataRow(i));
        }

        return Collections.unmodifiableCollection(rows);
    }
}
