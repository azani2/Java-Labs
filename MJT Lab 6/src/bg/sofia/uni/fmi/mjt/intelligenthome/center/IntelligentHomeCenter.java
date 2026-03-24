package bg.sofia.uni.fmi.mjt.intelligenthome.center;

import bg.sofia.uni.fmi.mjt.intelligenthome.device.DeviceType;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.IoTDevice;
import bg.sofia.uni.fmi.mjt.intelligenthome.center.comparator.KWhComparator;
import bg.sofia.uni.fmi.mjt.intelligenthome.center.comparator.RegistrationComparator;
import bg.sofia.uni.fmi.mjt.intelligenthome.center.exceptions.DeviceAlreadyRegisteredException;
import bg.sofia.uni.fmi.mjt.intelligenthome.center.exceptions.DeviceNotFoundException;
import bg.sofia.uni.fmi.mjt.intelligenthome.storage.DeviceStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class IntelligentHomeCenter {
    DeviceStorage storage;
    Map<String, LocalDateTime> registrations;

    public IntelligentHomeCenter(DeviceStorage storage) {
        this.storage = storage;

        registrations = new HashMap<>();
    }

    /**
     * Adds a @device to the IntelligentHomeCenter.
     *
     * @throws IllegalArgumentException         in case @device is null.
     * @throws DeviceAlreadyRegisteredException in case the @device is already
     *                                          registered.
     */
    public void register(IoTDevice device) throws DeviceAlreadyRegisteredException {
        if (device == null) {
            throw new IllegalArgumentException("device cannot be null");
        }
        if (storage.exists(device.getId())) {
            throw new DeviceAlreadyRegisteredException("Device was already registered.");
        }

        storage.store(device.getId(), device);
        registrations.put(device.getId(), LocalDateTime.now());
    }

    /**
     * Removes the @device from the IntelligentHomeCenter.
     *
     * @throws IllegalArgumentException in case null is passed.
     * @throws DeviceNotFoundException  in case the @device is not found.
     */
    public void unregister(IoTDevice device) throws DeviceNotFoundException {
        if (device == null) {
            throw new IllegalArgumentException("device cannot be null");
        }
        if (!storage.exists(device.getId())) {
            throw new DeviceNotFoundException("Device not registered.");
        }

        storage.delete(device.getId());
    }

    /**
     * Returns a IoTDevice with an ID @id if found.
     *
     * @throws IllegalArgumentException in case @id is null or blank.
     * @throws DeviceNotFoundException  in case device with ID @id is not found.
     */
    public IoTDevice getDeviceById(String id) throws DeviceNotFoundException {
        if (id == null || id.isBlank() || id.isEmpty()) {
            throw new IllegalArgumentException("Device was null.");
        }
        if (!storage.exists(id)) {
            throw new DeviceNotFoundException("Device not registered.");
        }

        return storage.get(id);
    }

    /**
     * Returns the total number of devices with type @type registered in
     * SmartCityHub.
     *
     * @throws IllegalArgumentException in case @type is null.
     */
    public int getDeviceQuantityPerType(DeviceType type) {
        if (type == null) {
            throw new IllegalArgumentException("Type was null.");
        }

        int quantity = 0;

        for (IoTDevice device : storage.listAll()) {
            if (device.getType().equals(type)) {
                quantity++;
            }
        }

        return quantity;
    }

    public Collection<String> getTopNDevicesByPowerConsumption(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("N must be non-negative number.");
        }

        List<IoTDevice> sortedDevices = new LinkedList<>(storage.listAll());
        Collections.sort(sortedDevices, new KWhComparator());

        if (n > sortedDevices.size()) {
            n = sortedDevices.size();
        }

        List<String> sortedIds = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            sortedIds.add(sortedDevices.get(i).getId());
        }
        return sortedIds;
    }

    public Collection<IoTDevice> getFirstNDevicesByRegistration(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("N must be non-negative number.");
        }

        List<IoTDevice> sortedDevices = new LinkedList<>(storage.listAll());
        Collections.sort(sortedDevices, new RegistrationComparator(registrations));

        if (n > sortedDevices.size()) {
            n = sortedDevices.size();
        }

        List<IoTDevice> topNDevices = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            topNDevices.add(sortedDevices.get(i));
        }
        return topNDevices;
    }
}
