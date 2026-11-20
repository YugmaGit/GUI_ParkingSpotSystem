import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A car that can be parked in a parking slot.
 * A car is identified by its registration number (one uppercase letter followed by
 * five digits, e.g. "T12345"). It knows its owner, make, model, whether the owner is a
 * staff member and the time at which it was parked.
 * The class also calculates how long the car has been parked and the parking fee.
 *
 * @author Yugma ManojBhai Mangukiya
 * @id 106227407
 * @version 1.0
 * @date 24/10/2026
 */
public class Car
{
    /** Pattern a registration number must match: one capital letter followed by 5 digits. */
    public static final String REG_PATTERN = "[A-Z][0-9]{5}";

    /** Parking fee charged for each hour (or part of an hour), in dollars. */
    public static final int FEE_PER_HOUR = 10;

    /** Format used whenever a date and time is shown to the user. */
    private static final DateTimeFormatter TIME_FORMAT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String regNumber;           // capital letter + 5 digits, e.g. T12345
    private String owner;               // name of the car's owner
    private String make;                // make of the car, e.g. Toyota
    private String model;               // model of the car, e.g. Camry
    private boolean staffMember;        // true if the owner is a staff member
    private LocalDateTime parkedTime;   // null while the car is not parked

    /**
     * Creates a car.
     *
     * @param regNumber  registration number, one capital letter followed by 5 digits
     * @param owner       name of the owner (not empty)
     * @param make        make of the car (not empty)
     * @param model       model of the car (not empty)
     * @param staffMember true if the owner is a staff member
     * @throws IllegalArgumentException if any argument breaks the rules above
     */
    public Car(String regNumber, String owner, String make, String model, boolean staffMember)
    {
        if (!isValidReg(regNumber))
        {
            throw new IllegalArgumentException("Incorrect format. Please enter the correct car registration format, e.g. T12345 (one capital letter followed by 5 digits).");
        }

        if (isBlank(owner) || isBlank(make) || isBlank(model))
        {
            throw new IllegalArgumentException("Owner, make and model must not be empty.");
        }
        this.regNumber = regNumber;
        this.owner = owner.trim();
        this.make = make.trim();
        this.model = model.trim();
        this.staffMember = staffMember;
    }

    /**
     * Checks whether a text is a correctly formatted registration number.
     *
     * @param reg the text to check (may be null)
     * @return true if it is one capital letter followed by 5 digits
     */
    public static boolean isValidReg(String reg)
    {
        return reg != null && reg.matches(REG_PATTERN);
    }

    /**
     * Checks whether a text is missing or contains only spaces.
     *
     * @param text the text to check (may be null)
     * @return true if the text is null or empty after trimming
     */
    private static boolean isBlank(String text)
    {
        return text == null || text.trim().isEmpty();
    }

    /** @return the getRegNumber registration number */
    public String getRegNumber()
    {
        return regNumber;
    }

    /** @return the owner's name */
    public String getOwner()
    {
        return owner;
    }

    /** @return the make of the car */
    public String getMake()
    {
        return make;
    }

    /** @return the model of the car */
    public String getModel()
    {
        return model;
    }

    /** @return true if the owner is a staff member */
    public boolean isStaffMember()
    {
        return staffMember;
    }

    /**
     * Records the time at which the car was parked.
     *
     * @param parkedTime the parking time, or null when the car leaves
     */
    public void setParkedTime(LocalDateTime parkedTime)
    {
        this.parkedTime = parkedTime;
    }

    /** @return the time the car was parked, or null if it is not parked */
    public LocalDateTime getParkedTime()
    {
        return parkedTime;
    }

    /** @return the parking time as text, e.g. "2026-08-10 16:48:05" ("-" if not parked) */
    public String getParkedTimeText()
    {
        return parkedTime == null ? "-" : parkedTime.format(TIME_FORMAT);
    }

    /**
     * Works out how long the car has been parked.
     *
     * @return the time between the parking time and now (zero if not parked)
     */
    public Duration getParkedDuration()
    {
        if (parkedTime == null)
        {
            return Duration.ZERO;
        }
        return Duration.between(parkedTime, LocalDateTime.now());
    }

    /** @return the parked length as text, e.g. "0 hours 25 minutes and 20 seconds" */
    public String getParkedDurationText()
    {
        Duration d = getParkedDuration();
        return d.toHours() + " hours " + d.toMinutesPart() + " minutes and "
               + d.toSecondsPart() + " seconds";
    }

    /**
     * Calculates the parking fee: $10 per hour, and anything under one hour
     * (or any part of an hour) is charged as a full hour.
     *
     * @return the fee in dollars, or 0 if the car is not parked
     */
    public int getParkingFee()
    {
        if (parkedTime == null)
        {
            return 0;
        }
        long seconds = Math.max(0, getParkedDuration().getSeconds());
        long hours = (seconds + 3599) / 3600;   // round up to whole hours
        return (int) Math.max(1, hours) * FEE_PER_HOUR;
    }

    /** @return a multi-line description of the car and its parking time, length and fee */
    @Override
    public String toString()
    {
        return "Registration: " + regNumber
             + "\nOwner: " + owner
             + "\nMake: " + make
             + "\nModel: " + model
             + "\nType: " + (staffMember ? "Staff" : "Visitor")
             + "\nParked at: " + getParkedTimeText()
             + "\nParked for: " + getParkedDurationText()
             + "\nParking fee: $" + getParkingFee();
    }
}