package ksk_car3;

// Adds a flat per-day surcharge, so the fee math differs from Sedan (polymorphism).
// PILLAR: Inheritance (extends Vehicle) and Polymorphism (own calculateRentalFee).
public class SUV extends Vehicle {

    private static final double SURCHARGE_PER_DAY = 300.0;

    public SUV(String plateNumber, String brand, String model,
               CarColor color, double dailyRate) {
        super(plateNumber, brand, model, color, dailyRate);
    }

    @Override
    public String getVehicleType() {
        return "SUV";
    }

    @Override
    public double calculateRentalFee(int totalDays) {
        return computeBaseFee(totalDays) + (SURCHARGE_PER_DAY * totalDays);
    }
}
