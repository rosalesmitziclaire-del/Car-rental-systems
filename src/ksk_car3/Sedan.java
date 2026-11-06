package ksk_car3;

// No extra surcharge, just the daily rate times the days.
// PILLAR: Inheritance (extends Vehicle) and Polymorphism (own calculateRentalFee).
public class Sedan extends Vehicle {

    public Sedan(String plateNumber, String brand, String model,
                 CarColor color, double dailyRate) {
        super(plateNumber, brand, model, color, dailyRate);
    }

    @Override
    public String getVehicleType() {
        return "Sedan";
    }

    @Override
    public double calculateRentalFee(int totalDays) {
        return computeBaseFee(totalDays);
    }
}
