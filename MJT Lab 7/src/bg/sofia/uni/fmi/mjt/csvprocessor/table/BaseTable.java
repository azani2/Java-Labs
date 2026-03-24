package bg.sofia.uni.fmi.mjt.csvprocessor.table;

import bg.sofia.uni.fmi.mjt.csvprocessor.exceptions.CsvDataNotCorrectException;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.column.BaseColumn;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.column.Column;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class BaseTable implements Table {
    private int columnsCount;
    private int rowsCount;
    private List<Column> columns;
    private List<String> columnNames;

    public BaseTable() {
        columnsCount = 0;
        rowsCount = 0;
        columns = new ArrayList<>();
        columnNames = new ArrayList<>();
    }

    private boolean isEmpty() {
        return columnsCount == 0;
    }

    @Override
    public void addData(String[] data) throws CsvDataNotCorrectException {
        if (data == null) {
            throw new IllegalArgumentException("Data to add was null.");
        }
        if (!isEmpty() && data.length != columnsCount) {
            throw new CsvDataNotCorrectException(
                "Data was in wrong format : parts count was not equal to the table's column count.");
        }
        if (isEmpty()) {
            columnsCount = data.length;
            for (int i = 0; i < columnsCount; i++) {
                Column newColumn = new BaseColumn();
                newColumn.addData(data[i]);
                columns.add(newColumn);
                columnNames.add(data[i]);
            }
            rowsCount = 1;
            return;
        }
        for (int i = 0; i < columnsCount; i++) {
            columns.get(i).addData(data[i]);
        }
        rowsCount++;
    }

    @Override
    public Collection<String> getColumnNames() {
        Collection<String> names = new ArrayList<>(columnNames);
        return Collections.unmodifiableCollection(names);
    }

    @Override
    public Collection<String> getColumnData(String column) {
        if (column == null || column.isBlank()) {
            throw new IllegalArgumentException("Column name was null or blank.");
        }

        int index = columnNames.indexOf(column);
        if (index == -1) {
            throw new IllegalArgumentException("No colum named \"" + column + "\" in this table.");
        }

        return columns.get(index).getData();
    }

    @Override
    public int getRowsCount() {
        return rowsCount;
    }
}
