package com.devlawal.car;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CarFileDataAccessService implements CarDao, Serializable {
    private final String PATH = "src/com/devlawal/car/car.dat";
    private final File file = new File(PATH);


    // to seed user.dat with static data
    static {
        File file = new File("src/com/devlawal/car/car.dat");
        if (!file.exists()) {
            List<Car> cars = new ArrayList<>();
            cars = List.of(
                    new Car(UUID.fromString("2ea85178-fada-4279-9d5e-eea627049fa2"), "1234", new BigDecimal("100"), Brand.MERCEDES, true),
                    new Car(UUID.fromString("576590ff-57a1-4df3-8430-79980eb42343"), "5678", new BigDecimal("150"), Brand.TESLA, true),
                    new Car(UUID.fromString("9d818235-ce3b-40e8-b74a-3674985c6bcd"), "9012", new BigDecimal("90"), Brand.TOYOTA, false),
                    new Car(UUID.fromString("87cb62d9-d262-4174-b1b2-957f9e2a1f40"), "3456", new BigDecimal("60"), Brand.AUDI, false)
            );

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(cars);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public List<Car> getAllCars() {
        return getCarsFromFile();
    }

    @Override
    public Car getCarById(UUID id) {
        List<Car> cars = getCarsFromFile();
        return cars.stream().filter(car -> car.getId().equals(id)).findFirst().orElse(null);
    }

    private List<Car> getCarsFromFile() {
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(PATH))) {
            return (List<Car>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
