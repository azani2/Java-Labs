package bg.sofia.uni.fmi.mjt.intelligenthome.device;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class IoTDeviceTest {
    private IoTDevice device;

    @Test
    void testGetName() {
        device = new AmazonAlexa("Light", 1, LocalDateTime.now());

        assertTrue(device.getName().equals("Light"), "Name should have been equal to last passed name but wasn't.");
    }
}
