import java.util.ArrayList;

/**
 * The car park. It keeps the list of parking slots and carries out all business rules:
 * unique slot IDs, parking only in free slots of the right type, a car in only one slot,
 * and no deleting of occupied slots. It contains no user-interface code; problems are
 * reported by throwing exceptions whose messages can be shown to the user.

 */
public class CarPark
{
    private ArrayList<ParkingSlot> slots;   // all slots of the car park, in the order added

    /** Creates an empty car park. */
    public CarPark()
    {
        slots = new ArrayList<>();
    }

    /**
     * Adds a new empty slot.
     *
     * @param slotId   identifier, one capital letter followed by 3 digits
     * @param slotType "Staff" or "Visitor"
     * @return the slot that was created
     * @throws IllegalArgumentException if the ID or type is invalid or the ID is already used
     */
    public ParkingSlot addSlot(String slotId, String slotType)
    {
        ParkingSlot slot = new ParkingSlot(slotId, slotType);   // validates the arguments
        
        if (findSlotById(slotId) != null)
        {
            throw new IllegalArgumentException("Slot " + slotId + " already exists. Slot IDs must be unique.");
        }
        slots.add(slot);
        return slot;
    }

    /**
     * Finds a slot by its identifier.
     *
     * @param slotId the slot ID to look for
     * @return the slot, or null if there is no such slot
     */
    public ParkingSlot findSlotById(String slotId)
    {
        for (ParkingSlot slot : slots)
        {
            if (slot.getSlotId().equals(slotId))
            {
                return slot;
            }
        }
        return null;
    }

    /**
     * Finds the slot a car is parked in.
     *
     * @param regNumber the car's registration number
     * @return the slot holding that car, or null if the car is not parked
     */
    public ParkingSlot findSlotByReg(String regNumber)
    {
        for (ParkingSlot slot : slots)
        {
            if (slot.isOccupied() && slot.getParkedCar().getRegNumber().equals(regNumber))
            {
                return slot;
            }
        }
        return null;
    }

    /**
     * Deletes an unoccupied slot.
     *
     * @param slotId the ID of the slot to delete
     * @throws IllegalArgumentException if the slot does not exist
     * @throws IllegalStateException if a car is parked in the slot
     */
    public void deleteSlot(String slotId)
    {
        ParkingSlot slot = findSlotById(slotId);
        if (slot == null)
        {
            throw new IllegalArgumentException("Slot " + slotId + " does not exist.");
        }
        
        if (slot.isOccupied())
        {
            throw new IllegalStateException("Slot " + slotId + " cannot be deleted because a car is parked in it.");
        }
        
        slots.remove(slot);
    }

    /**
     * Parks a car in a slot. The slot must exist, be empty and be of the right type,
     * and the car must not already be parked somewhere else.
     *
     * @param slotId the slot to use
     * @param car the car to park
     * @throws IllegalArgumentException if the car is null or the slot does not exist
     * @throws IllegalStateException if a rule is broken (occupied slot, wrong type, car already parked)
     */
    public void parkCar(String slotId, Car car)
    {
        if (car == null)
        {
            throw new IllegalArgumentException("No car was given.");
        }
        ParkingSlot slot = findSlotById(slotId);
        if (slot == null)
        {
            throw new IllegalArgumentException("Slot " + slotId + " does not exist.");
        }
        ParkingSlot current = findSlotByReg(car.getRegNumber());
        if (current != null)
        {
            throw new IllegalStateException("Car " + car.getRegNumber()
                + " is already parked in slot " + current.getSlotId() + ".");
        }
        slot.parkCar(car);
    }

    /**
     * Removes a car from the car park.
     *
     * @param regNumber the registration number of the car to remove
     * @return the car that was removed (with its parking time still recorded)
     * @throws IllegalArgumentException if no parked car has that registration number
     */
    public Car removeCar(String regNumber)
    {
        ParkingSlot slot = findSlotByReg(regNumber);
        if (slot == null)
        {
            throw new IllegalArgumentException("Car " + regNumber + " is not parked in this car park.");
        }
        return slot.removeCar();
    }

    /**
     * Gives all slots in the order they were added.
     *
     * @return a copy of the list of slots (changing it does not change the car park)
     */
    public ArrayList<ParkingSlot> getAllSlots()
    {
        return new ArrayList<>(slots);
    }
}
