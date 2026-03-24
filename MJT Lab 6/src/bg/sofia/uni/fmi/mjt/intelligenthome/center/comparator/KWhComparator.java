package bg.sofia.uni.fmi.mjt.intelligenthome.center.comparator;

import bg.sofia.uni.fmi.mjt.intelligenthome.device.IoTDevice;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;

public class KWhComparator implements Comparator<IoTDevice> {

    @Override
    public int compare(IoTDevice firstDevice, IoTDevice secondDevice) {
        LocalDateTime now = LocalDateTime.now();
        long firstHours = Duration.between(firstDevice.getInstallationDateTime(), now).toHours();
        long secondHours = Duration.between(secondDevice.getInstallationDateTime(), now).toHours();
        double first = firstDevice.getPowerConsumption() * firstHours;
        double second = secondDevice.getPowerConsumption() * secondHours;

        return Double.compare(second, first);
    }

}
