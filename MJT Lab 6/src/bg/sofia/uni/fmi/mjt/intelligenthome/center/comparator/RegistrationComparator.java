package bg.sofia.uni.fmi.mjt.intelligenthome.center.comparator;

import bg.sofia.uni.fmi.mjt.intelligenthome.device.IoTDevice;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Map;

public class RegistrationComparator implements Comparator<IoTDevice> {

    private Map<String, LocalDateTime> idRegistrations;

    public RegistrationComparator(Map<String, LocalDateTime> registrations) {
        this.idRegistrations = registrations;
    }

    @Override
    public int compare(IoTDevice firstDevice, IoTDevice secondDevice) {
        LocalDateTime first = idRegistrations.get(firstDevice.getId());
        LocalDateTime second = idRegistrations.get(secondDevice.getId());

        if (first == second) {
            return 0;
        } else if (first.isBefore(second)) {
            return -1;
        }
        return 1;
    }

}
