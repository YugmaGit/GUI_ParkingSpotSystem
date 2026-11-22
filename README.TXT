PROJECT TITLE: Parking Slot Application (GUI Parking Spot System)

AUTHORS: Yugma

PURPOSE OF PROJECT:
A small Java Swing application that manages the staff and visitor parking slots of a
company car park. Every slot is shown as a coloured button. Users can add and delete
slots, park a car, find a car, remove a car and list all slots. The time a car is parked,
how long it has been parked and the parking fee ($10 per hour, any part of an hour is
charged as a full hour) are recorded and shown. All input and output uses GUI dialogs -
there is no console interface.

CLASSES:
  ParkingSystemGUI - the window and all user interaction (the only user-interface class)
  CarPark          - keeps the list of slots and applies the rules
  ParkingSlot      - one slot (ID, type, and the car parked in it)
  Car              - a car (rego, owner, make, model, staff or visitor, parking time, fee)

HOW TO START THIS PROJECT:
1. Open the folder as a project (Project > Open Project).
2. Click Compile.
3. Right-click the class ParkingSystemGUI.

USER INSTRUCTIONS:

Window layout
  - Left column   (burnt orange heading) : Staff slots.
  - Middle column (forest green heading) : Visitor slots.
  - Right column  : the seven operation buttons.
  - Bottom bar    : colour key and a hint.
  - Colours: light peach = free staff slot
             light green = free visitor slot
             lavender    = occupied slot
  - The program starts with staff slots S001-S004 and visitor slots V001-V005.

Formats
  - Slot ID: one capital letter + 3 digits, e.g. S001, V005, A111.
  - Car registration: one capital letter + 5 digits, e.g. T12345.

Operations (buttons on the right)
  List all car slots : a table of every slot with its type, status and, for parked cars,
                       the rego, make, model, owner, parking time, parked length and fee.

  Park a car         : type the slot ID, the car registration, owner name, make and model,
                       and tick "Owner is a staff member" if it is a staff car. A staff car
                       can only use a Staff slot and a visitor car only a Visitor slot.
                       The parking time is shown when the car is parked.

  Find a car         : enter the registration. The slot ID, make, model, registration,
                       owner, parking time, parked length and fee are shown, and the
                       car's slot is outlined in yellow.

  Remove a car       : enter the registration and confirm. The parked length and the
                       fee are shown.

  Add a car slot     : enter a new unique slot ID and choose Staff or Visitor.

  Delete a car slot  : enter the slot ID. A slot with a car in it cannot be deleted.

  Exit               : asks for confirmation, then closes the program.
                       (Closing the window with the X button asks the same question.)

Shortcut - click a slot
  Click a FREE slot      -> choose "Park a car here" or "Delete this slot".
  Click an OCCUPIED slot -> choose "Show car details" or "Remove car".

Error messages
  - Wrong format: "Incorrect format. Please enter the correct slot ID format, e.g. A111"
    (or the car registration format, e.g. T12345).
  - Correct format but not found: "Slot A111 does not exist" or
    "Car T12345 is not parked in this car park."
  - Other problems (duplicate slot ID, slot already occupied, wrong slot type for the car,
    car already parked in another slot, deleting an occupied slot, empty owner/make/model)
    are also explained in a pop-up message and nothing is changed.
  - Press Cancel in any dialog to give up the operation without changing anything.
