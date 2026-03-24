package bg.sofia.uni.fmi.mjt.csvprocessor.table.column;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ColumnTest {
    BaseColumn baseColumn;

    @Test
    void testAddDataNullData() {
        baseColumn = new BaseColumn();
        assertThrows(IllegalArgumentException.class, () -> baseColumn.addData(null),
            "IllegalArgumentException was expected, but nothing was thrown.");
    }

    @Test
    void tstAddDataBlank() {
        baseColumn = new BaseColumn();
        assertThrows(IllegalArgumentException.class, () -> baseColumn.addData("  "),
            "IllegalArgumentException was expected, but nothing was thrown.");
    }

    @Test
    void testAddDataValid() {
        baseColumn = new BaseColumn();
        baseColumn.addData("testNameValue");
        baseColumn.addData("testValue1");
        baseColumn.addData("testValue2");
        assertIterableEquals(baseColumn.getData(), List.of("testValue1", "testValue2"),
            "Expected collection doesn't match result collection.");
    }
}
