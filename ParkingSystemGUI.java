import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * The graphical user interface of the parking slot application.
 *
 * The window shows every slot as a button: staff slots in the left column, visitor slots
 * in the middle column and the seven operations in the right column. Slots can be clicked
 * to park or remove a car, delete the slot or see the car's details. All user input and
 * all messages use Swing dialogs (no console). The business rules are in the CarPark,
 * ParkingSlot and Car classes; this class only collects input and shows results.
 *
 * @author Yugma ManojBhai Mangukiya
 * @id 106227407
 * @version 1.0
 * @date 24/10/2026
 */
public class ParkingSystemGUI extends JFrame
{
    // ---- colours -----
    private static final Color WINDOW_BACKGROUND = new Color(33, 37, 41);    // charcoal
    private static final Color STAFF_HEADER = new Color(211, 84, 0);         // burnt orange
    private static final Color VISITOR_HEADER = new Color(30, 132, 73);      // forest green
    private static final Color STAFF_FREE = new Color(252, 228, 205);        // light peach
    private static final Color VISITOR_FREE = new Color(212, 239, 223);      // light green
    private static final Color OCCUPIED = new Color(243,163,255);            // lavender
    private static final Color LIST_BUTTON = new Color(241, 196, 15);        // gold
    private static final Color MENU_BUTTON = new Color(127, 219, 255);       // light blue
    private static final Border HIGHLIGHT_BORDER = BorderFactory.createLineBorder(Color.YELLOW, 6);

    /** Orders slots by slot ID. */
    private static final Comparator<ParkingSlot> SLOT_ORDER = new Comparator<ParkingSlot>()
    {
        public int compare(ParkingSlot a, ParkingSlot b)
        {
            return a.getSlotId().compareTo(b.getSlotId());
        }
    };

    /** Shown when a slot ID is not in the format A111. */
    private static final String SLOT_FORMAT_MESSAGE =
        "Incorrect format. Please enter the correct slot ID format, e.g. A111 (one capital letter followed by 3 digits).";

    /** Shown when a registration number is not in the format T12345. */
    private static final String REG_FORMAT_MESSAGE =
        "Incorrect format. Please enter the correct car registration format, e.g. T12345 (one capital letter followed by 5 digits).";

    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 14);
    private static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 18);

    // ---- model and GUI components ---------
    private CarPark carPark;
    private SlotListPanel staffPanel;      // column holding the staff slot buttons
    private SlotListPanel visitorPanel;    // column holding the visitor slot buttons
    private Map<String, JButton> slotButtons;   // slot ID -> its button
    private JButton highlightedButton;     // button of the slot found by "Find a car"
    private JLabel statusLabel;            // message line at the bottom of the window

    /** Creates and shows the application window with 4 staff slots and 5 visitor slots. */
    public ParkingSystemGUI()
    {
        carPark = new CarPark();
        slotButtons = new HashMap<>();
        createWindow();
        addInitialSlots();
        rebuildSlotButtons();
        setVisible(true);
    }

    //----------------------
    //  Building the window
    //----------------------

    /** Sets up the frame, its layout and the three areas of the window. */
    private void createWindow()
    {
        setTitle("Parking Slot Application");
        setSize(1150, 720);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                exitApplication();
            }
        });

        add(createSlotArea(), BorderLayout.CENTER);
        add(createMenuPanel(), BorderLayout.EAST);
        add(createStatusBar(), BorderLayout.SOUTH);
    }

    /** @return the grey area holding the staff column and the visitor column */
    private JPanel createSlotArea()
    {
        staffPanel = new SlotListPanel();
        visitorPanel = new SlotListPanel();

        JPanel area = new JPanel(new GridLayout(1, 2, 30, 0));
        area.setBackground(WINDOW_BACKGROUND);
        area.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        area.add(createColumn("Staff Slots", STAFF_HEADER, staffPanel));
        area.add(createColumn("Visitor Slots", VISITOR_HEADER, visitorPanel));
        return area;
    }

    /**
     * Creates one column: a coloured heading above a scrollable list of slot buttons.
     *
     * @param title heading text
     * @param headerColor background colour of the heading
     * @param slotPanel the panel that will hold the slot buttons
     * @return the finished column
     */
    private JPanel createColumn(String title, Color headerColor, SlotListPanel slotPanel)
    {
        JLabel header = new JLabel(title, SwingConstants.CENTER);
        header.setFont(HEADER_FONT);
        header.setForeground(Color.WHITE);
        header.setOpaque(true);
        header.setBackground(headerColor);
        header.setPreferredSize(new Dimension(100, 80));

        JScrollPane scroll = new JScrollPane(slotPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(20);

        JPanel column = new JPanel(new BorderLayout());
        column.add(header, BorderLayout.NORTH);
        column.add(scroll, BorderLayout.CENTER);
        return column;
    }

    /** @return the right-hand panel containing the seven operation buttons */
    private JPanel createMenuPanel()
    {
        JPanel menu = new JPanel(new GridLayout(7, 1, 0, 2));
        menu.setPreferredSize(new Dimension(190, 0));

        menu.add(createMenuButton("List all car slots", LIST_BUTTON, new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                listAllSlots();
            }
        }));

        menu.add(createMenuButton("Park a car", MENU_BUTTON, new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                parkCar(null);
            }
        }));

        menu.add(createMenuButton("Find a car", MENU_BUTTON, new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                findCar();
            }
        }));

        menu.add(createMenuButton("Remove a car", MENU_BUTTON, new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                removeCar(null);
            }
        }));

        menu.add(createMenuButton("Add a car slot", MENU_BUTTON, new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                addSlot();
            }
        }));

        menu.add(createMenuButton("Delete a car slot", MENU_BUTTON, new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                deleteSlot(null);
            }
        }));

        menu.add(createMenuButton("Exit", MENU_BUTTON, new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                exitApplication();
            }
        }));

        return menu;
    }

    /**
     * Creates one flat coloured operation button.
     *
     * @param text     the button label
     * @param colour   background colour
     * @param listener the code run when the button is clicked
     * @return the button
     */
    private JButton createMenuButton(String text, Color colour, ActionListener listener)
    {
        JButton button = createFlatButton(text, colour);
        button.addActionListener(listener);
        return button;
    }

    /**
     * Creates a button that shows its background colour on every look and feel.
     *
     * @param text   the button label
     * @param colour background colour
     * @return the button
     */
    private JButton createFlatButton(String text, Color colour)
    {
        JButton button = new JButton(text);
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI());   // setBackground
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.BLACK);
        button.setBackground(colour);
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        return button;
    }

    /** @return the bottom line explaining the colours and the click shortcuts */
    private JPanel createStatusBar()
    {
        statusLabel = new JLabel();
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        showHint();

        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Color.DARK_GRAY);
        bar.add(statusLabel, BorderLayout.CENTER);
        return bar;
    }

    /** Shows the default hint text in the status bar. */
    private void showHint()
    {
        statusLabel.setText("light peach = free staff slot   |   light green = free visitor slot   |   "
            + "lavender = occupied   |   Click a slot to park, remove, view or delete.");
    }

    //--------------
    //  Slot buttons
    //--------------

    /** Adds the starting slots S001-S004 (staff) and V001-V005 (visitor). */
    private void addInitialSlots()
    {
        for (int i = 1; i <= 4; i++)
        {
            carPark.addSlot(String.format("S%03d", i), ParkingSlot.STAFF);
        }

        for (int i = 1; i <= 5; i++)
        {
            carPark.addSlot(String.format("V%03d", i), ParkingSlot.VISITOR);
        }
    }

    /** Clears both columns and creates one button for every slot (used after add/delete). */
    private void rebuildSlotButtons()
    {
        staffPanel.removeAll();
        visitorPanel.removeAll();
        slotButtons.clear();
        highlightedButton = null;

        ArrayList<ParkingSlot> sorted = carPark.getAllSlots();
        Collections.sort(sorted, SLOT_ORDER);

        for (ParkingSlot slot : sorted)
        {
            JButton button = createSlotButton(slot);
            slotButtons.put(slot.getSlotId(), button);
            (slot.isStaffSlot() ? staffPanel : visitorPanel).add(button);
        }

        staffPanel.revalidate();
        staffPanel.repaint();
        visitorPanel.revalidate();
        visitorPanel.repaint();
    }

    /**
     * Creates the button that represents a slot and connects its click handler.
     *
     * @param slot the slot to represent
     * @return the button
     */
    private JButton createSlotButton(final ParkingSlot slot)
    {
        JButton button = createFlatButton("", Color.WHITE);
        button.setPreferredSize(new Dimension(300, 80));
        button.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                slotClicked(slot);
            }
        });
        updateSlotButton(slot, button);
        return button;
    }

    /**
     * Refreshes the text and colour of one slot button so it matches its slot
     * (only this button is changed, the others are left alone).
     *
     * @param slot the slot whose button should be refreshed
     */
    private void updateSlotButton(ParkingSlot slot)
    {
        updateSlotButton(slot, slotButtons.get(slot.getSlotId()));
    }

    /**
     * Sets the text and colour of a slot button from the state of its slot.
     *
     * @param slot   the slot the button represents
     * @param button the button to update
     */
    private void updateSlotButton(ParkingSlot slot, JButton button)
    {
        button.setText(slot.getButtonText());
        if (slot.isOccupied())
        {
            button.setBackground(OCCUPIED);
        }
        else
        {
            button.setBackground(slot.isStaffSlot() ? STAFF_FREE : VISITOR_FREE);
        }
        button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        button.repaint();
    }

    /**
     * Marks a slot button with a thick yellow border (used by "Find a car").
     *
     * @param slot the slot to highlight
     */
    private void highlightSlot(ParkingSlot slot)
    {
        clearHighlight();
        highlightedButton = slotButtons.get(slot.getSlotId());
        highlightedButton.setBorder(HIGHLIGHT_BORDER);
        highlightedButton.scrollRectToVisible(
            new Rectangle(0, 0, highlightedButton.getWidth(), highlightedButton.getHeight()));
    }

    /** Removes the yellow border from the previously highlighted slot button. */
    private void clearHighlight()
    {
        if (highlightedButton != null)
        {
            highlightedButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
            highlightedButton = null;
        }
    }

    //---------------------------------------
    //  Click on a slot (advanced interaction)
    //---------------------------------------

    /**
     * Handles a click on a slot button: offers the actions that make sense for the slot.
     *
     * @param slot the slot that was clicked
     */
    private void slotClicked(ParkingSlot slot)
    {
        clearHighlight();
        if (slot.isOccupied())
        {
            String[] options = {"Show car details", "Remove car", "Cancel"};
            int choice = askOption("Slot " + slot.getSlotId() + " (" + slot.getSlotType()
                + ") is occupied by " + slot.getParkedCar().getRegNumber() + ".\nWhat would you like to do?",
                "Slot " + slot.getSlotId(), options);
            if (choice == 0)
            {
                showCarDetails(slot);
            }
            else if (choice == 1)
            {
                removeCar(slot.getParkedCar().getRegNumber());
            }
        }
        else
        {
            String[] options = {"Park a car here", "Delete this slot", "Cancel"};
            int choice = askOption("Slot " + slot.getSlotId() + " (" + slot.getSlotType()
                + ") is not occupied.\nWhat would you like to do?", "Slot " + slot.getSlotId(), options);
            if (choice == 0)
            {
                parkCar(slot.getSlotId());
            }
            else if (choice == 1)
            {
                deleteSlot(slot.getSlotId());
            }
        }
    }

    /**
     * Shows a dialog with several choice buttons.
     *
     * @param message the question
     * @param title   dialog title
     * @param options the button labels
     * @return the index of the chosen button, or -1 if the dialog was closed
     */
    private int askOption(String message, String title, String[] options)
    {
        return JOptionPane.showOptionDialog(this, message, title, JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE, null, options, options[options.length - 1]);
    }

    //---------------------------------
    //  Operation 1: List all car slots
    //---------------------------------
    /** Shows every slot in a table: ID, type, status and, for parked cars, the car details. */
    private void listAllSlots()
    {
        clearHighlight();
        ArrayList<ParkingSlot> sorted = carPark.getAllSlots();
        if (sorted.isEmpty())
        {
            showInfo("There are no slots in the car park.", "All car slots");
            return;
        }
        Collections.sort(sorted, SLOT_ORDER);

        String[] columns = {"Slot ID", "Type", "Status", "Reg", "Make", "Model", "Owner",
                            "Parked at", "Parked for", "Fee"};

        DefaultTableModel model = new DefaultTableModel(columns, 0)
        {
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        };
        
        for (ParkingSlot slot : sorted)
        {
            model.addRow(createTableRow(slot));
        }

        JTable table = new JTable(model);
        table.setRowHeight(24);
        table.getColumnModel().getColumn(7).setPreferredWidth(140);
        table.getColumnModel().getColumn(8).setPreferredWidth(230);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(1000, Math.min(420, 70 + 24 * sorted.size())));
        JOptionPane.showMessageDialog(this, scroll, "All car slots", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Builds one row of the slot table.
     *
     * @param slot the slot to describe
     * @return the cell values of the row
     */
    private Object[] createTableRow(ParkingSlot slot)
    {
        if (!slot.isOccupied())
        {
            return new Object[] {slot.getSlotId(), slot.getSlotType(), "Not occupied",
                                 "", "", "", "", "", "", ""};
        }
        Car car = slot.getParkedCar();
        return new Object[] {slot.getSlotId(), slot.getSlotType(), "Occupied", car.getRegNumber(),
                             car.getMake(), car.getModel(), car.getOwner(), car.getParkedTimeText(),
                             car.getParkedDurationText(), "$" + car.getParkingFee()};
    }

    //--------------------------
    //  Operation 2: Park a car
    //--------------------------
    /**
     * Lets the user park a car using a form. The form is shown again with the same
     * values after a mistake, so nothing has to be typed twice.
     *
     * @param slotId the slot ID to fill in on the form, or null if the user types it
     */
    private void parkCar(String slotId)
    {
           clearHighlight();
        
           if (carPark.getAllSlots().isEmpty())
        {
            showError("There are no slots in the car park. Add a slot first.");
            return;
        }

        JTextField slotField = new JTextField(slotId == null ? "" : slotId, 12);
        JTextField regField = new JTextField(12);
        JTextField ownerField = new JTextField(12);
        JTextField makeField = new JTextField(12);
        JTextField modelField = new JTextField(12);
        JCheckBox staffBox = new JCheckBox("Owner is a staff member");
        
        if (slotId != null)
        {
            staffBox.setSelected(carPark.findSlotById(slotId).isStaffSlot());
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Slot ID (e.g. A111 - capital letter + 3 digits):"));
        form.add(slotField);
        form.add(new JLabel("Registration (e.g. T12345 - capital letter + 5 digits):"));
        form.add(regField);
        form.add(new JLabel("Owner name:"));
        form.add(ownerField);
        form.add(new JLabel("Make:"));
        form.add(makeField);
        form.add(new JLabel("Model:"));
        form.add(modelField);
        form.add(new JLabel("Car type:"));
        form.add(staffBox);

        while (JOptionPane.showConfirmDialog(this, form, "Park a car", JOptionPane.OK_CANCEL_OPTION,
                   JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION)
        {
            String slot = slotField.getText().trim();
            String reg = regField.getText().trim();
            String problem = checkParkInput(slot, reg, ownerField.getText(),
                                            makeField.getText(), modelField.getText());
            if (problem != null)
            {
                showError(problem);
            }
            else
            {
                Car car = new Car(reg, ownerField.getText(), makeField.getText(),
                                  modelField.getText(), staffBox.isSelected());
                if (tryParkCar(slot, car))
                {
                    return;
                }
            }
        }
    }

    /**
     * Checks the park form. A wrongly formatted entry and a correctly formatted
     * entry that does not exist give different messages.
     *
     * @param slotId the slot ID typed by the user
     * @param reg   the registration number typed by the user
     * @param owner  the owner name typed by the user
     * @param make   the make typed by the user
     * @param model  the model typed by the user
     * @return the message to show, or null if the input is acceptable
     */
    private String checkParkInput(String slotId, String reg, String owner, String make, String model)
    {
        if (!ParkingSlot.isValidSlotId(slotId))
        {
            return SLOT_FORMAT_MESSAGE;
        }
        if (carPark.findSlotById(slotId) == null)
        {
            return "Slot " + slotId + " does not exist. Please enter the ID of an existing slot.";
        }
        if (!Car.isValidReg(reg))
        {
            return REG_FORMAT_MESSAGE;
        }
        if (owner.trim().isEmpty() || make.trim().isEmpty() || model.trim().isEmpty())
        {
            return "Owner name, make and model must not be empty.";
        }
        return null;
    }

    /**
     * Parks the car and reports the result.
     *
     * @param slotId the slot to use (already checked to exist)
     * @param car    the car to park
     * @return true if the car was parked, false if a rule was broken (message shown)
     */
    private boolean tryParkCar(String slotId, Car car)
    {
        try
        {
            carPark.parkCar(slotId, car);
            updateSlotButton(carPark.findSlotById(slotId));
            showInfo("Car " + car.getRegNumber() + " parked in slot " + slotId + ".\n"
                + "Parking time: " + car.getParkedTimeText(), "Car parked");
            return true;
        }
        catch (IllegalArgumentException | IllegalStateException e)
        {
            showError(e.getMessage());
            return false;
        }
    }

    //-------------------------
    //  Operation 3: Find a car
    //-------------------------
    /** Asks for a registration number and shows where that car is parked. */
    private void findCar()
    {
        clearHighlight();
        String reg = askRegistration("Find a car");
        if (reg == null)
        {
            return;
        }

        ParkingSlot slot = carPark.findSlotByReg(reg);
        if (slot == null)
        {
            showError("Car " + reg + " is not parked in this car park.");
            return;
        }

        highlightSlot(slot);
        showCarDetails(slot);
    }

    /**
     * Shows the slot, car, owner, parking time, parked length and fee of a parked car.
     *
     * @param slot the occupied slot
     */
    private void showCarDetails(ParkingSlot slot)
    {
        Car car = slot.getParkedCar();
        showInfo("Slot ID: " + slot.getSlotId() + " (" + slot.getSlotType() + ")\n" + car, "Car found");
    }


    //---------------------------
    //  Operation 4: Remove a car
    //---------------------------

    /**
     * Removes a car after asking for confirmation, then shows its parking length and fee.
     *
     * @param reg the registration number to remove, or null to ask the user for it
     */
    private void removeCar(String reg)
    {
        clearHighlight();
        if (reg == null)
        {
            reg = askRegistration("Remove a car");
            if (reg == null)
            {
                return;
            }
        }

        ParkingSlot slot = carPark.findSlotByReg(reg);
        if (slot == null)
        {
            showError("Car " + reg + " is not parked in this car park.");
            return;
        }

        if (!confirm("Remove car " + reg + " from slot " + slot.getSlotId() + "?", "Remove a car"))
        {
            return;
        }

        try
        {
            Car car = carPark.removeCar(reg);
            updateSlotButton(slot);
            showInfo("Car " + reg + " was removed from slot " + slot.getSlotId() + ".\n"
                + "Parked at: " + car.getParkedTimeText() + "\n"
                + "Parked for: " + car.getParkedDurationText() + "\n"
                + "Parking fee: $" + car.getParkingFee(), "Car removed");
        }
        catch (IllegalArgumentException | IllegalStateException e)
        {
            showError(e.getMessage());
        }
    }

    //-----------------------------
    //  Operation 5: Add a car slot
    //-----------------------------

    /** Lets the user add a slot by entering its ID and choosing its type. */
    private void addSlot()
    {
        clearHighlight();
        JTextField idField = new JTextField(8);
        JComboBox<String> typeBox = new JComboBox<>(new String[] {ParkingSlot.STAFF, ParkingSlot.VISITOR});

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Slot ID (e.g. A111 - capital letter + 3 digits):"));
        form.add(idField);
        form.add(new JLabel("Slot type:"));
        form.add(typeBox);

        while (JOptionPane.showConfirmDialog(this, form, "Add a car slot", JOptionPane.OK_CANCEL_OPTION,
                   JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION)
        {
            if (!ParkingSlot.isValidSlotId(idField.getText().trim()))
            {
                showError(SLOT_FORMAT_MESSAGE);
                continue;
            }
            try
            {
                ParkingSlot slot = carPark.addSlot(idField.getText().trim(), (String) typeBox.getSelectedItem());
                rebuildSlotButtons();
                showInfo("Slot " + slot.getSlotId() + " (" + slot.getSlotType() + ") was added.", "Slot added");
                return;
            }
            catch (IllegalArgumentException e)
            {
                showError(e.getMessage());
            }
        }
    }

    //--------------------------------
    //  Operation 6: Delete a car slot
    //--------------------------------

    /**
     * Deletes an unoccupied slot after asking for confirmation.
     *
     * @param slotId the slot to delete, or null to ask the user for the ID
     */
    private void deleteSlot(String slotId)
    {
        clearHighlight();
        if (slotId == null)
        {
            slotId = askText("Enter the ID of the slot to delete\n(capital letter + 3 digits, e.g. A111):",
                             "Delete a car slot", ParkingSlot.SLOT_ID_PATTERN, SLOT_FORMAT_MESSAGE);
            if (slotId == null)
            {
                return;
            }
        }

        ParkingSlot slot = carPark.findSlotById(slotId);
        if (slot == null)
        {
            showError("Slot " + slotId + " does not exist.");
            return;
        }
        
        if (slot.isOccupied())
        {
            showError("Slot " + slotId + " cannot be deleted because a car is parked in it.");
            return;
        }
        
        if (!confirm("Delete slot " + slotId + "?", "Delete a car slot"))
        {
            return;
        }

        carPark.deleteSlot(slotId);
        rebuildSlotButtons();
        showInfo("Slot " + slotId + " was deleted.", "Slot deleted");
    }

    //-------------------
    //  Operation 7: Exit
    //-------------------

    /** Asks for confirmation and closes the application. */
    private void exitApplication()
    {
        if (confirm("Do you want to exit the Parking Slot Application?", "Exit"))
        {
            dispose();
            System.exit(0);
        }
    }

    //----------------------
    //  Small dialog helpers
    //----------------------

    /**
     * Asks for a car registration number, repeating until it is valid or cancelled.
     *
     * @param title dialog title
     * @return the registration number, or null if the user cancelled
     */
    private String askRegistration(String title)
    {
        return askText("Enter the car registration number\n(capital letter + 5 digits, e.g. T12345):", title, Car.REG_PATTERN,REG_FORMAT_MESSAGE);
    }

    /**
     * Asks for a text that must match a pattern, repeating until it does or the user cancels.
     *
     * @param message      the prompt, which also explains the expected format
     * @param title        dialog title
     * @param pattern      regular expression the text must match
     * @param errorMessage message shown when the text does not match
     * @return the text entered, or null if the user cancelled
     */
    private String askText(String message, String title, String pattern, String errorMessage)
    {
        while (true)
        {
            String text = JOptionPane.showInputDialog(this, message, title, JOptionPane.QUESTION_MESSAGE);
            if (text == null)
            {
                return null;
            }

            text = text.trim();

            if (text.matches(pattern))
            {
                return text;
            }

            showError(errorMessage);
        }
    }

    /**
     * Asks a yes/no question.
     *
     * @param message the question
     * @param title   dialog title
     * @return true if the user chose Yes
     */
    private boolean confirm(String message, String title)
    {
        return JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    /**
     * Shows an information message.
     *
     * @param message the text to show
     * @param title   dialog title
     */
    private void showInfo(String message, String title)
    {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Shows an error message.
     *
     * @param message the text to show
     */
    private void showError(String message)
    {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    //-----------------------
    //  Helper panel and main
    //-----------------------

    /**
     * A vertical list of equally tall slot buttons. When the window is tall enough the buttons
     * share the height; with many slots the list scrolls.
     */
    private static class SlotListPanel extends JPanel implements Scrollable
    {
        /** Creates an empty list with one column of buttons. */
        
        SlotListPanel()
        {
            super(new GridLayout(0, 1, 0, 2));
        }

        public Dimension getPreferredScrollableViewportSize()
        {
            return getPreferredSize();
        }

        public int getScrollableUnitIncrement(Rectangle visible, int orientation, int direction)
        {
            return 20;
        }

        public int getScrollableBlockIncrement(Rectangle visible, int orientation, int direction)
        {
            return visible.height;
        }

        public boolean getScrollableTracksViewportWidth()
        {
            return true;
        }

        public boolean getScrollableTracksViewportHeight()
        {
            return getParent() instanceof JViewport && getParent().getHeight() > getPreferredSize().height;
        }
    }

    /**
     * Starts the application.
     *
     * @param args not used
     */
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                new ParkingSystemGUI();
            }
        });
    }
}