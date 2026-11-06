package ksk_car3;

// Abstract base class for every car (abstraction + encapsulation).
// PILLAR: Abstraction (abstract class + abstract methods), Encapsulation (private fields, no plate setter).
public abstract class Vehicle {

    private String plateNumber;
    private String brand;
    private String model;
    private CarColor color;
    private double dailyRate;
    private VehicleStatus status;

    public Vehicle(String plateNumber, String brand, String model,
                   CarColor color, double dailyRate) {
        this.plateNumber = plateNumber;
        this.brand = brand;
        this.model = model;
        this.color = color;
        this.dailyRate = dailyRate;
        this.status = VehicleStatus.AVAILABLE;
    }

    // Each subclass writes its own version (inheritance + polymorphism).
    public abstract String getVehicleType();
    public abstract double calculateRentalFee(int totalDays);

    protected double computeBaseFee(int totalDays) {
        return dailyRate * totalDays;
    }

    public String getPlateNumber() { return plateNumber; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public String getFullName() { return brand + " " + model; }
    public CarColor getColor() { return color; }
    public double getDailyRate() { return dailyRate; }
    public VehicleStatus getStatus() { return status; }

    // PILLAR: Encapsulation - validated setters (the plate number has no setter on purpose).
    public void setBrand(String brand) {
        if (brand == null || brand.trim().isEmpty()) throw new IllegalArgumentException("Brand cannot be empty.");
        this.brand = brand.trim();
    }

    public void setModel(String model) {
        if (model == null || model.trim().isEmpty()) throw new IllegalArgumentException("Model cannot be empty.");
        this.model = model.trim();
    }

    public void setColor(CarColor color) {
        if (color == null) throw new IllegalArgumentException("Please choose a color.");
        this.color = color;
    }

    public void setDailyRate(double dailyRate) {
        if (dailyRate <= 0) throw new IllegalArgumentException("Daily rate must be more than 0.");
        this.dailyRate = dailyRate;
    }

    public boolean isAvailable() {
        return status == VehicleStatus.AVAILABLE;
    }

    public void markRented() {
        if (status != VehicleStatus.AVAILABLE) {
            throw new IllegalStateException(plateNumber + " is not available for rent.");
        }
        status = VehicleStatus.RENTED;
    }

    public void markMaintenance() {
        if (status == VehicleStatus.RENTED) {
            throw new IllegalStateException(plateNumber + " is rented out right now.");
        }
        status = VehicleStatus.MAINTENANCE;
    }

    public void markAvailable() {
        status = VehicleStatus.AVAILABLE;
    }

    @Override
    public String toString() {
        return getFullName() + " (" + plateNumber + ")";
    }
}
