package bg.sofia.uni.fmi.mjt.csvprocessor.table.column;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class BaseColumn implements Column {
    private Set<String> values;

    public BaseColumn() {
        this(new LinkedHashSet<>());
    }

    public BaseColumn(Set<String> values) {
        this.values = values;
    }

    @Override
    public void addData(String data) {
        if (data == null || data.isBlank()) {
            throw new IllegalArgumentException("Data was null or blank.");
        }
        values.add(data);
    }

    @Override
    public Collection<String> getData() {
        Collection<String> valuesCopy = new ArrayList<>();
        List<String> valuesList = new ArrayList<>(values);
        for (int i = 1; i < valuesList.size(); i++) {
            valuesCopy.add(valuesList.get(i));
        }
        return Collections.unmodifiableCollection(valuesCopy);
    }

}
