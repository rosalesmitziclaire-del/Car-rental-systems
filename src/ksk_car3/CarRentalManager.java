package ksk_car3;

import java.time.LocalDate;
import java.util.ArrayList;

// Our "database": cars, rentals (linked list) and staff accounts.
public class CarRentalManager {

    private ArrayList<Vehicle> vehicles = new ArrayList<>();      // PILLAR: Polymorphism - Sedan and SUV in one list
    private TransactionList transactions = new TransactionList(); // DATA STRUCTURE: linked list
    private ArrayList<User> staff = new ArrayList<>();            // PILLAR: Polymorphism - Admin and FrontDesk in one list

    public CarRentalManager() {
        vehicles.add(new Sedan("NBC 1234", "Toyota", "Vios", CarColor.SILVER, 2000));
        vehicles.add(new Sedan("ABC 5678", "Honda", "Civic", CarColor.BLACK, 2200));
        vehicles.add(new SUV("XYZ 4321", "Toyota", "Fortuner", CarColor.RED, 3500));
        vehicles.add(new SUV("DEF 8765", "Ford", "Everest", CarColor.BLACK, 3300));

        staff.add(new Admin("Kai Rosales", "Admin", "admin123"));
        staff.add(new FrontDesk("Sepllarian Cruz", "POS", "POS123"));
    }

    // ---- Staff / login ----

    public User authenticate(String username, String password) {
        for (User u : staff) {
            if (u.getUsername().equalsIgnoreCase(username) && u.checkPassword(password)) return u;
        }
        return null;
    }

    public ArrayList<User> getStaff() { return staff; }

    public int countAdmins() {
        int n = 0;
        for (User u : staff) if (u instanceof Admin) n++;
        return n;
    }

    public void addStaff(User u) {
        for (User x : staff) {
            if (x.getUsername().equalsIgnoreCase(u.getUsername())) {
                throw new IllegalArgumentException("Username already exists.");
            }
        }
        staff.add(u);
    }

    // Edit name / username / role / password. Returns the account (a new object if the role changed).
    public User updateStaff(User target, String name, String username, boolean makeAdmin, String newPassword) {
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Full name cannot be empty.");
        if (username == null || username.trim().isEmpty()) throw new IllegalArgumentException("Username cannot be empty.");
        for (User x : staff) {
            if (x != target && x.getUsername().equalsIgnoreCase(username.trim())) {
                throw new IllegalArgumentException("Username already exists.");
            }
        }
        boolean isAdmin = target instanceof Admin;
        if (isAdmin && !makeAdmin && countAdmins() <= 1) {
            throw new IllegalStateException("There must be at least one Admin.");
        }
        if (newPassword != null && !newPassword.isEmpty()) target.setPassword(newPassword);
        target.setFullName(name);
        target.setUsername(username);
        if (isAdmin != makeAdmin) {
            User replacement = target.changeRole(makeAdmin);
            staff.set(staff.indexOf(target), replacement);
            return replacement;
        }
        return target;
    }

    public void deleteStaff(User target, User current) {
        if (target == current) throw new IllegalStateException("You can't delete your own account.");
        if (target instanceof Admin && countAdmins() <= 1) throw new IllegalStateException("You can't delete the last Admin.");
        staff.remove(target);
    }

    // ---- Inventory side (Admin) ----

    public ArrayList<Vehicle> getAllVehicles() { return vehicles; }

    public void addVehicle(Vehicle v) {
        for (Vehicle x : vehicles) {
            if (x.getPlateNumber().equalsIgnoreCase(v.getPlateNumber())) {
                throw new IllegalArgumentException("Plate number already exists.");
            }
        }
        vehicles.add(v);
    }

    public void updateVehicle(Vehicle v, String brand, String model, CarColor color, double rate, boolean maintenance) {
        v.setBrand(brand);
        v.setModel(model);
        v.setColor(color);
        v.setDailyRate(rate);
        if (maintenance && v.getStatus() == VehicleStatus.AVAILABLE) v.markMaintenance();
        else if (!maintenance && v.getStatus() == VehicleStatus.MAINTENANCE) v.markAvailable();
    }

    public void deleteVehicle(Vehicle v) {
        if (v.getStatus() == VehicleStatus.RENTED) {
            throw new IllegalStateException(v.getFullName() + " is currently rented out and can't be deleted.");
        }
        vehicles.remove(v);
    }

    public ArrayList<Vehicle> getAvailableVehicles() {
        ArrayList<Vehicle> list = new ArrayList<>();
        for (Vehicle v : vehicles) if (v.isAvailable()) list.add(v);
        return list;
    }

    public int countAvailable() { return getAvailableVehicles().size(); }

    public int countRented() {
        int n = 0;
        for (Vehicle v : vehicles) if (v.getStatus() == VehicleStatus.RENTED) n++;
        return n;
    }

    public int countMaintenance() {
        int n = 0;
        for (Vehicle v : vehicles) if (v.getStatus() == VehicleStatus.MAINTENANCE) n++;
        return n;
    }

    // ALGORITHM: linear search by plate (case-insensitive, partial match).
    public ArrayList<Vehicle> searchByPlate(String query) {
        String q = query == null ? "" : query.trim().toLowerCase();
        ArrayList<Vehicle> result = new ArrayList<>();
        for (Vehicle v : vehicles) {
            if (v.getPlateNumber().toLowerCase().contains(q)) result.add(v);
        }
        return result;
    }

    // ALGORITHM: insertion sort by daily rate, written by hand (no Collections.sort).
    public ArrayList<Vehicle> sortByRate(ArrayList<Vehicle> list, boolean ascending) {
        ArrayList<Vehicle> r = new ArrayList<>(list);
        for (int i = 1; i < r.size(); i++) {
            Vehicle key = r.get(i);
            int j = i - 1;
            while (j >= 0 && (ascending ? r.get(j).getDailyRate() > key.getDailyRate()
                                        : r.get(j).getDailyRate() < key.getDailyRate())) {
                r.set(j + 1, r.get(j));
                j--;
            }
            r.set(j + 1, key);
        }
        return r;
    }

    // ---- Rental side (POS) ----

    public RentalTransaction processRental(Customer customer, Vehicle vehicle,
                                            LocalDate start, LocalDate end, String by) {
        if (vehicle == null) throw new IllegalArgumentException("Please select a vehicle.");
        if (!vehicle.isAvailable()) throw new IllegalStateException(vehicle.getFullName() + " is no longer available.");
        RentalTransaction t = new RentalTransaction(customer, vehicle, start, end, by);
        vehicle.markRented();
        transactions.addFirst(t);
        return t;
    }

    // Customer brought the car back: transaction Completed, car Available again.
    public void returnCar(RentalTransaction t) {
        t.markCompleted();
        t.getVehicle().markAvailable();
    }

    // Rental cancelled: transaction Cancelled, car Available again.
    public void cancelRental(RentalTransaction t) {
        t.markCancelled();
        t.getVehicle().markAvailable();
    }

    // Newest first, straight from the linked list.
    public ArrayList<RentalTransaction> getAllTransactions() { return transactions.toArrayList(); }
    public ArrayList<RentalTransaction> getRecentTransactions(int n) { return transactions.recent(n); }
    public int countTransactions() { return transactions.size(); }
    public int countTransactionsToday() { return transactions.countProcessedOn(LocalDate.now()); }
    public int countByStatus(TransactionStatus s) { return transactions.countByStatus(s); }
    public int countOverdue() { return transactions.countOverdue(); }
    public double getTotalRevenue() { return transactions.revenue(); }
    public double getRevenueToday() { return transactions.revenueOn(LocalDate.now()); }
}
