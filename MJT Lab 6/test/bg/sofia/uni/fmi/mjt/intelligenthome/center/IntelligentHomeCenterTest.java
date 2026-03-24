package bg.sofia.uni.fmi.mjt.intelligenthome.center;

import bg.sofia.uni.fmi.mjt.intelligenthome.center.exceptions.DeviceAlreadyRegisteredException;
import bg.sofia.uni.fmi.mjt.intelligenthome.center.exceptions.DeviceNotFoundException;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.AmazonAlexa;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.DeviceType;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.IoTDevice;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.RgbBulb;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.WiFiThermostat;
import bg.sofia.uni.fmi.mjt.intelligenthome.storage.DeviceStorage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class IntelligentHomeCenterTest {

    @Mock
    private DeviceStorage storageMock;
    Map<String, LocalDateTime> registrations;

    @InjectMocks
    private IntelligentHomeCenter center;

    @Test
    void testRegisterWithNullDeviceThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> center.register(null),
            "Registering a null object should throw IllegalArgumentException, nut nothing was thrown.");
    }

    @Test
    void testRegisterWithExistingDeviceThrowsException() {
        IoTDevice exampleDevice = new RgbBulb("bulb", 1, LocalDateTime.now());

        when(storageMock.exists(exampleDevice.getId()))
            .thenReturn(true);

        Assertions.assertThrows(DeviceAlreadyRegisteredException.class, () -> center.register(exampleDevice),
            "DeviceAlreadyRegisteredException was expected, but nothing was thrown.");
    }

    @Test
    void testUnregisterWithNullDeviceThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> center.unregister(null),
            "Unregistering a null object should throw IllegalArgumentException, nut nothing was thrown.");
    }

    @Test
    void testUnregisterUnregisteredDeviceThrowsException() {
        IoTDevice exampleDevice = new RgbBulb("bulb", 1, LocalDateTime.now());

        when(storageMock.exists(exampleDevice.getId()))
            .thenReturn(false);

        Assertions.assertThrows(DeviceNotFoundException.class, () -> center.unregister(exampleDevice),
            "DeviceNotFoundException was expected, but nothing was thrown.");
    }

    @Test
    void testRegisterValidDeviceExists() {
        IoTDevice exampleDevice = new RgbBulb("bulb", 1, LocalDateTime.now());
        when(storageMock.exists(exampleDevice.getId()))
            .thenReturn(false);

        Assertions.assertDoesNotThrow(() -> center.register(exampleDevice),
            "Registered device must exist in storage but didn't.");
    }

    @Test
    void testUnregisterValidDevice() {
        IoTDevice exampleDevice = new RgbBulb("bulb", 1, LocalDateTime.now());
        when(storageMock.exists(exampleDevice.getId()))
            .thenReturn(true);

        Assertions.assertDoesNotThrow(() -> center.unregister(exampleDevice),
            "Registered device must exist in storage but didn't.");
    }

    @Test
    void testGetDeviceByIdWithNullId() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> center.getDeviceById(null),
            "Passing null id must result in IllegalArgumentException, but nothing was thrown.");
    }

    @Test
    void testGetDeviceByIdWithBlankId() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> center.getDeviceById("  "),
            "Passing blank id must result in IllegalArgumentException, but nothing was thrown.");
    }

    @Test
    void testGetDeviceByIdWithBlankIdTab() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> center.getDeviceById("\t"),
            "Passing blank id must result in IllegalArgumentException, but nothing was thrown.");
    }

    @Test
    void testGetDeviceByIdWithEmptyId() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> center.getDeviceById(""),
            "Passing blank id must result in IllegalArgumentException, but nothing was thrown.");
    }

    @Test
    void testGetDeviceByIdUnregisteredDeviceId() {
        IoTDevice deviceInstance = new RgbBulb("bulb", 1, LocalDateTime.now());
        when(storageMock.exists(deviceInstance.getId()))
            .thenReturn(false);

        Assertions.assertThrows(DeviceNotFoundException.class, () -> center.getDeviceById(deviceInstance.getId()),
            "Passing unregistered device id must result in IllegalArgumentException, but nothing was thrown.");
    }

    @Test
    void testGetDeviceByIdValidDeviceId() throws DeviceNotFoundException {
        IoTDevice deviceInstance = new RgbBulb("bulb", 1, LocalDateTime.now());
        String id = deviceInstance.getId();

        when(storageMock.exists(id))
            .thenReturn(true);
        when(storageMock.get(id))
            .thenReturn(deviceInstance);

        Assertions.assertDoesNotThrow(() -> center.getDeviceById(id),
            "Passing valid unregistered id should return corresponding device.");

        Assertions.assertTrue(center.getDeviceById(id).equals(deviceInstance),
            ("Passing valid unregistered id should return correct corresponding device but was "
                + deviceInstance.toString()));
    }

    @Test
    void testGetDeviceQuantityPerTypeWithNullType() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> center.getDeviceQuantityPerType(null),
            "Passing null DeviceType should have resulted in IllegalArgumentException but nothing was thrown.");
    }

    @Test
    void testGetDeviceQuantityPerTypeNone() {
        List<IoTDevice> emptyDeviceList = new ArrayList<>();

        when(storageMock.listAll())
            .thenReturn(emptyDeviceList);

        Assertions.assertEquals(0, center.getDeviceQuantityPerType(DeviceType.BULB),
            ("No (0) amount of devices of type BULB expected but was " +
                center.getDeviceQuantityPerType(DeviceType.BULB)));

        Assertions.assertEquals(0, center.getDeviceQuantityPerType(DeviceType.SMART_SPEAKER),
            ("No (0) amount of devices of type SMART_SPEAKER expected but was " +
                center.getDeviceQuantityPerType(DeviceType.SMART_SPEAKER)));

        Assertions.assertEquals(0, center.getDeviceQuantityPerType(DeviceType.SMART_SPEAKER),
            ("No (0) amount of devices of type BULB expected but was " +
                center.getDeviceQuantityPerType(DeviceType.SMART_SPEAKER)));
    }

    @Test
    void testGetDeviceQuantityPerTypeManyOf1Type() {
        List<IoTDevice> deviceList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            deviceList.add(new RgbBulb("StarrySky", 1, LocalDateTime.now()));
        }

        when(storageMock.listAll())
            .thenReturn(deviceList);

        Assertions.assertEquals(10, center.getDeviceQuantityPerType(DeviceType.BULB),
            ("10 devices of type BULB expected but was " +
                center.getDeviceQuantityPerType(DeviceType.BULB)));

        Assertions.assertEquals(0, center.getDeviceQuantityPerType(DeviceType.SMART_SPEAKER),
            ("No (0) amount of devices of type SMART_SPEAKER expected but was " +
                center.getDeviceQuantityPerType(DeviceType.SMART_SPEAKER)));

        Assertions.assertEquals(0, center.getDeviceQuantityPerType(DeviceType.THERMOSTAT),
            ("No (0) amount of devices of type BULB expected but was " +
                center.getDeviceQuantityPerType(DeviceType.THERMOSTAT)));
    }

    @Test
    void testGetDeviceQuantityPerTypeManyOfDifferentTypes() {
        List<IoTDevice> deviceList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            deviceList.add(new RgbBulb("StarrySky", 1, LocalDateTime.now()));
        }
        for (int i = 0; i < 4; i++) {
            deviceList.add(new AmazonAlexa("Alexa", 2, LocalDateTime.now()));
        }
        for (int i = 0; i < 5; i++) {
            deviceList.add(new WiFiThermostat("Thermostat", 3, LocalDateTime.now()));
        }
        for (int i = 0; i < 4; i++) {
            deviceList.add(new RgbBulb("StarrySky", 1, LocalDateTime.now()));
        }

        when(storageMock.listAll())
            .thenReturn(deviceList);

        Assertions.assertEquals(7, center.getDeviceQuantityPerType(DeviceType.BULB),
            ("7 devices of type BULB expected but was " +
                center.getDeviceQuantityPerType(DeviceType.BULB)));

        Assertions.assertEquals(4, center.getDeviceQuantityPerType(DeviceType.SMART_SPEAKER),
            ("4 devices of type SMART_SPEAKER expected but was " +
                center.getDeviceQuantityPerType(DeviceType.SMART_SPEAKER)));

        Assertions.assertEquals(5, center.getDeviceQuantityPerType(DeviceType.THERMOSTAT),
            ("5 devices of type BULB expected but was " +
                center.getDeviceQuantityPerType(DeviceType.THERMOSTAT)));
    }

    @Test
    void testGetTopNDevicesByPowerConsumptionNegativeN() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> center.getTopNDevicesByPowerConsumption(-5),
            "Passing negative number should have resulted in IllegalArgumentException but nothing was thrown.");
    }

    @Test
    void testGetTopNDevicesByPowerConsumptionNone() {
        Collection<IoTDevice> emptyList = new ArrayList<>();

        when(storageMock.listAll())
            .thenReturn(emptyList);

        Assertions.assertEquals(0, center.getTopNDevicesByPowerConsumption(15).size(),
            ("Size of returned collection was expected to be 0, but was " +
                center.getTopNDevicesByPowerConsumption(15).size()));
    }

    @Test
    void testGetTopNDevicesByPowerConsumptionLessThanN() {
        Collection<IoTDevice> smallerList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            IoTDevice bulb = new RgbBulb("StarrySky", 1, LocalDateTime.now());
            smallerList.add(bulb);
        }

        when(storageMock.listAll())
            .thenReturn(smallerList);

        Assertions.assertEquals(10, center.getTopNDevicesByPowerConsumption(15).size(),
            ("Size of returned collection was expected to be 10, but was " +
                center.getTopNDevicesByPowerConsumption(15).size()));
    }

    @Test
    void testGetTopNDevicesByPowerConsumptionMoreThanN() {
        Collection<IoTDevice> biggerList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            IoTDevice bulb = new RgbBulb("StarrySky", 1, LocalDateTime.now());
            biggerList.add(bulb);
        }

        when(storageMock.listAll())
            .thenReturn(biggerList);

        Assertions.assertEquals(15, center.getTopNDevicesByPowerConsumption(15).size(),
            ("Size of returned collection was expected to be 15, but was " +
                center.getTopNDevicesByPowerConsumption(15).size()));
    }

    @Test
    void testGetFirstNDevicesByRegistrationNegativeN() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> center.getFirstNDevicesByRegistration(-5),
            "Passing negative number should have resulted in IllegalArgumentException but nothing was thrown.");
    }

    @Test
    void testGetFirstNDevicesByRegistrationNone() {
        Collection<IoTDevice> emptyList = new ArrayList<>();

        when(storageMock.listAll())
            .thenReturn(emptyList);

        Assertions.assertEquals(0, center.getFirstNDevicesByRegistration(15).size(),
            ("Size of returned collection was expected to be 0, but was " +
                center.getFirstNDevicesByRegistration(15).size()));
    }

    @Test
    void testGetFirstNDevicesByRegistrationLessThanN() {
        Collection<IoTDevice> smallerList = new ArrayList<>();
        registrations = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            IoTDevice bulb = new RgbBulb("StarrySky", 1, LocalDateTime.now());
            smallerList.add(bulb);
            registrations.put(bulb.getId(), LocalDateTime.now());
        }

        when(storageMock.listAll())
            .thenReturn(smallerList);

        Assertions.assertEquals(10, center.getFirstNDevicesByRegistration(15).size(),
            ("Size of returned collection was expected to be 10, but was " +
                center.getFirstNDevicesByRegistration(15).size()));
    }

    @Test
    void testGetFirstNDevicesByRegistrationMoreThanN() {
        Collection<IoTDevice> biggerList = new ArrayList<>();
        registrations = new HashMap<>();
        for (int i = 0; i < 20; i++) {
            IoTDevice bulb = new RgbBulb("StarrySky", 1, LocalDateTime.now());
            biggerList.add(bulb);
            registrations.put(bulb.getId(), LocalDateTime.now());
        }

        when(storageMock.listAll())
            .thenReturn(biggerList);

        Assertions.assertEquals(15, center.getFirstNDevicesByRegistration(15).size(),
            ("Size of returned collection was expected to be 15, but was " +
                center.getFirstNDevicesByRegistration(15).size()));
    }

    @Test
    void testGetTopNDevicesByPowerConsumptionSorted() {
        Collection<IoTDevice> sortedList = new ArrayList<>();
        registrations = new HashMap<>();
        LocalDateTime someTimeAgo = LocalDateTime.now().minusHours(10);

        for (int i = 0; i < 10; i++) {
            IoTDevice bulb = new RgbBulb("StarrySky", i, LocalDateTime.now());
            sortedList.add(bulb);
            registrations.put(bulb.getId(), someTimeAgo);
        }

        List<IoTDevice> shuffledList = new ArrayList<>(sortedList);
        Collections.shuffle(shuffledList);

        when(storageMock.listAll())
            .thenReturn(shuffledList);

        List<String> sortedIds = new ArrayList<>();
        for (IoTDevice device : shuffledList) {
            sortedIds.add(device.getId());
        }

        assertIterableEquals(sortedIds, center.getTopNDevicesByPowerConsumption(10),
            "Returned collection was supposed to be sorted by power consumption but was not.");
    }
}
