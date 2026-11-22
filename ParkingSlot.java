import java.time.LocalDateTime;

/**
 * One parking slot of the car park. A slot has an identifier (one capital letter followed
 * by three digits, e.g. "S001"), a type (staff or visitor) and may hold one parked car.
 *

 */
public class ParkingSlot
{
    /** Slot type for staff members. */
    public static final String STAFF = "Staff";

    /** Slot type for visitors. */
    public static final String VISITOR = "Visitor";

    /** Pattern a slot identifier must match: one capital letter followed by 3 digits. */
    public static final String SLOT_ID_PATTERN = "[A-Z][0-9]{3}";

    private String slotId;      // capital letter + 3 digits, e.g. S001
    private String slotType;    // "Staff" or "Visitor"
    private Car parkedCar;      // the parked car, null when the slot is empty

    /**
     * Creates an empty parking slot.
     *
     * @param slotId   identifier, one capital letter followed by 3 digits
     * @param slotType either {@link #STAFF} or {@link #VISITOR} (case is ignored)
     * @throws IllegalArgumentException if the identifier or type is invalid
     */
    public ParkingSlot(String slotId, String slotType)
    {
        if (!isValidSlotId(slotId))
        {
            throw new IllegalArgumentException("Incorrect format. Please enter the correct slot ID format, e.g. A111 (one capital letter followed by 3 digits).");
        }

        if (slotType == null || !(slotType.equalsIgnoreCase(STAFF) || slotType.equalsIgnoreCase(VISITOR)))
        {
            throw new IllegalArgumentException("Slot type must be Staff or Visitor.");
        }

        this.slotId = slotId;
        this.slotType = slotType.equalsIgnoreCase(STAFF) ? STAFF : VISITOR;
        this.parkedCar = null;
    }

    /**
     * Checks whether a text is a correctly formatted slot identifier.
     *
     * @param slotId the text to check (may be null)
     * @return true if it is one capital letter followed by 3 digits
     */
    public static boolean isValidSlotId(String slotId)
    {
        return slotId != null && slotId.matches(SLOT_ID_PATTERN);
    }

    /** @return the slot identifier */
    public String getSlotId()
    {
        return slotId;
    }

    /** @return the slot type, "Staff" or "Visitor" */
    public String getSlotType()
    {
        return slotType;
    }

    /** @return true if this is a staff slot */
    public boolean isStaffSlot()
    {
        return slotType.equals(STAFF);
    }

    /** @return true if a car is parked in this slot */
    public boolean isOccupied()
    {
        return parkedCar != null;
    }

    /**
     * Parks a car in this slot and records the current time in the car.
     *
     * @param car the car to park (not null)
     * @throws IllegalArgumentException if the car is null
     * @throws IllegalStateException    if the slot is occupied, or the car's type
     *                                  (staff/visitor) does not match the slot type
     */
    public void parkCar(Car car)
    {
        if (car == null)
        {
            throw new IllegalArgumentException("No car was given.");
        }

        if (isOccupied())
        {
            throw new IllegalStateException("Slot " + slotId + " is already occupied.");
        }

        if (car.isStaffMember() != isStaffSlot())
        {
            throw new IllegalStateException(car.isStaffMember()
                ? "A staff car can only be parked in a Staff slot."
                : "A visitor car can only be parked in a Visitor slot.");
        }

        car.setParkedTime(LocalDateTime.now());
        parkedCar = car;
    }

    /**
     * Removes the car from this slot.
     *
     * @return the car that was removed
     * @throws IllegalStateException if the slot is empty
     */
    public Car removeCar()
    {
        if (!isOccupied())
        {
            throw new IllegalStateException("Slot " + slotId + " has no car to remove.");
        }

        Car leaving = parkedCar;
        parkedCar = null;
        return leaving;
    }

    /** @return the parked car, or null if the slot is empty */
    public Car getParkedCar()
    {
        return parkedCar;
    }

    /** @return a one-line text for the slot button, e.g. "S001 is not occupied" */
    public String getButtonText()
    {
        if (isOccupied())
        {
            return slotId + " is occupied by " + parkedCar.getRegNumber()
                   + " (" + parkedCar.getMake() + " " + parkedCar.getModel() + ")";
        }

        return slotId + " is not occupied";
    }

    /** @return a multi-line description of the slot and the car in it */
    @Override
    public String toString()
    {
        String text = "Slot " + slotId + " (" + slotType + ") - ";
        if (isOccupied())
        {
            return text + "occupied\n" + parkedCar;
        }

        return text + "not occupied";
    }
}
