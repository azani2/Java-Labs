import bg.sofia.uni.fmi.mjt.intelligenthome.center.comparator.RegistrationComparator;
import  static org.junit.jupiter.api.Assertions.assertEquals;

import bg.sofia.uni.fmi.mjt.intelligenthome.device.IoTDevice;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.RgbBulb;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class RegistrationComparatorTest {
    private Map<String, LocalDateTime> idRegistrations;

    private RegistrationComparator comparator;

    @Test
    void testCompareSmallerToBiggerDate() {
        idRegistrations = new HashMap<>();
        LocalDateTime earlier =LocalDateTime.now().minusHours(10);
        LocalDateTime later =LocalDateTime.now().plusHours(10);
        IoTDevice deviceEarlier = new RgbBulb("B1", 1, LocalDateTime.now());
        IoTDevice deviceLater = new RgbBulb("B2", 1, LocalDateTime.now());
        idRegistrations.put(deviceEarlier.getId(), earlier);
        idRegistrations.put(deviceLater.getId(), later);
        comparator = new RegistrationComparator(idRegistrations);

        assertEquals(-1, comparator.compare(deviceEarlier, deviceLater),
            ("-1 expected but was" + comparator.compare(deviceEarlier, deviceLater)));
    }

    @Test
    void testCompareBiggerToSmallerDate() {
        idRegistrations = new HashMap<>();
        LocalDateTime earlier =LocalDateTime.now().minusHours(10);
        LocalDateTime later =LocalDateTime.now().plusHours(10);
        IoTDevice deviceEarlier = new RgbBulb("B1", 1, LocalDateTime.now());
        IoTDevice deviceLater = new RgbBulb("B2", 1, LocalDateTime.now());
        idRegistrations.put(deviceEarlier.getId(), earlier);
        idRegistrations.put(deviceLater.getId(), later);
        comparator = new RegistrationComparator(idRegistrations);

        assertEquals(1, comparator.compare(deviceLater, deviceEarlier),
            ("1 expected but was" + comparator.compare(deviceEarlier, deviceLater)));
    }

    @Test
    void testCompareEqualDates() {
        idRegistrations = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        IoTDevice device1 = new RgbBulb("B1", 1, LocalDateTime.now());
        IoTDevice device2 = new RgbBulb("B2", 1, LocalDateTime.now());
        idRegistrations.put(device1.getId(), now);
        idRegistrations.put(device2.getId(), now);
        comparator = new RegistrationComparator(idRegistrations);

        assertEquals(0, comparator.compare(device1, device2),
            ("0 expected but was" + comparator.compare(device1, device2)));
    }
}
