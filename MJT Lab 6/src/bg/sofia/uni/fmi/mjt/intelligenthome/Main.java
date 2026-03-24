package bg.sofia.uni.fmi.mjt.intelligenthome;

import bg.sofia.uni.fmi.mjt.intelligenthome.center.IntelligentHomeCenter;
import bg.sofia.uni.fmi.mjt.intelligenthome.center.exceptions.DeviceAlreadyRegisteredException;
import bg.sofia.uni.fmi.mjt.intelligenthome.center.exceptions.DeviceNotFoundException;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.AmazonAlexa;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.IoTDevice;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.RgbBulb;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.WiFiThermostat;
import bg.sofia.uni.fmi.mjt.intelligenthome.storage.DeviceStorage;
import bg.sofia.uni.fmi.mjt.intelligenthome.storage.MapDeviceStorage;

import java.time.LocalDateTime;
import java.util.Collection;

public class Main {
    public static void main(String[] args) throws DeviceAlreadyRegisteredException, DeviceNotFoundException {
        final int cons1 = 50;
        final int cons2 = 500;
        final int hours = 10;
        DeviceStorage st1 = new MapDeviceStorage();
        IoTDevice spkr1 = new AmazonAlexa("alexa", 1, LocalDateTime.now().minusHours(hours));
        IoTDevice therm = new WiFiThermostat("thrm", cons1, LocalDateTime.now().minusHours(hours));
        IoTDevice bulb = new RgbBulb("bulb", cons2, LocalDateTime.now().minusHours(hours));

        st1.store(spkr1.getId(), spkr1);

        IntelligentHomeCenter c1 = new IntelligentHomeCenter(st1);
        c1.register(therm);
        c1.register(bulb);
        System.out.println(c1.getDeviceById(spkr1.getId()).toString());
        System.out.println(c1.getDeviceById(therm.getId()).toString());
        System.out.println(c1.getDeviceById(bulb.getId()).toString());

        Collection<String> sortedByCons = c1.getTopNDevicesByPowerConsumption(2);
        System.out.println(sortedByCons.toString());
    }
}