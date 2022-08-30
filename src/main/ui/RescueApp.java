package ui;

import exceptions.AlreadyRescuedException;
import exceptions.InvalidIdCodeException;
import exceptions.InvalidNumPeopleException;
import model.Alert;
import model.AlertStatus;
import model.RescueAlerts;
import persistence.JsonReader;
import persistence.JsonWriter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

// A Disaster Rescue Alerts Application
public class RescueApp {
    private static final String JSON_STORE = "./data/rescueAlerts.json";
    private Scanner input;
    private RescueAlerts rescueAlerts;
    private Alert first;
    private Alert second;
    private Alert third;
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;

    // EFFECTS: runs the rescue alerts application, with initialized Alerts
    public RescueApp() throws FileNotFoundException {
        input = new Scanner(System.in);
        rescueAlerts = new RescueAlerts("Typhoon Ulysses");
        jsonWriter = new JsonWriter(JSON_STORE);
        jsonReader = new JsonReader(JSON_STORE);
        runRescueAlerts();
        // init();
    }

    // MODIFIES: this
    // EFFECTS: processes user input
    // note: the implementation of this method was based on the provided Teller application
    private void runRescueAlerts() {
        boolean keepGoing = true;
        String command = null;

        while (keepGoing) {
            displayAlerts();
            displayMenu();
            command = input.next();
            command = command.toLowerCase();

            if (command.equals("q")) {
                remindToSave();
                keepGoing = false;
            } else {
                processCommand(command);
            }
        }

        System.out.println("\nThank you for keeping your kapwa kababayan safe! 🇵🇭✨");
    }

    // MODIFIES: this
    // EFFECTS: initializes the already posted alerts
    private void init() {
        rescueAlerts = new RescueAlerts("Typhoon Ulysses");
        input = new Scanner(System.in);
        try {
            first = new Alert("Jericho Rosales", "09172473475", "1432 Kaimito, Barangay Don Juan", 5, true);
            second = new Alert("Vice Ganda", "09228459012", "ABS-CBN Broadcasting Center", 2, false);
            third = new Alert("Willie Revillame", "09151234567", "82 White Plains Avenue", 9, false);
        } catch (InvalidNumPeopleException e) {
            // do nothing, initialized alerts in this method will always have a valid number of people
        }
        rescueAlerts.addAlert(first);
        rescueAlerts.addAlert(second);
        rescueAlerts.addAlert(third);
        first.setStatus(AlertStatus.HELP_OTW);
        first.setRescuerName("Kim Jones");
        first.setRescuerContactNum("09159876543");
    }

    // EFFECTS: displays a list of alerts posted so far with corresponding id codes, statuses, & locations
    private void displayAlerts() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
        LocalDateTime now = LocalDateTime.now();

        System.out.println("Mabuhay! \nHere are the rescue alerts for disaster "
                + rescueAlerts.getName() + " as of " + dtf.format(now));
        System.out.println(" ID | ⚪️ Status – Location ");
        for (Alert a : rescueAlerts.getAllAlerts()) {
            System.out.println(a.toString());
        }
    }

    // EFFECTS: display menu of options to user
    // note: the implementation of this method was based on the provided Teller application
    private void displayMenu() {
        System.out.println("\nSelect from:");
        System.out.println("\tl -> load rescue alerts");
        System.out.println("\ta -> add an alert");
        System.out.println("\tv -> view an alert in detail");
        System.out.println("\tr -> respond to an alert");
        System.out.println("\ts -> save rescue alerts");
        System.out.println("\tq -> quit");
    }

    // MODIFIES: this
    // EFFECTS: processes user command
    //          if an Exception is caught, prints an error message
    private void processCommand(String command) {
        if ("a".equals(command)) {
            try {
                addAnAlert();
            } catch (InvalidNumPeopleException e) {
                System.err.println("You have provided an invalid number of people in your alert...\n"
                        + "Please retry adding a new alert.\n");
            }
        } else if ("v".equals(command)) {
            viewAnAlert();
        } else if ("r".equals(command)) {
            try {
                respondToAlert();
            } catch (InvalidIdCodeException e) {
                System.err.println("You have chosen a nonexistent Alert ID...\n");
            }
        } else if ("l".equals(command)) {
            loadRescueAlerts();
        } else if ("s".equals(command)) {
            saveRescueAlerts();
        } else {
            System.out.println("Please enter a valid selection...");
        }
    }

    // MODIFIES: this
    // EFFECTS: reminds user to save rescue alerts. if user chooses yes,
    //          saves rescue alerts to file and informs user of save, otherwise prints a message
    private void remindToSave() {
        System.out.println("You have chosen to quit the application.");
        System.out.println("Would you like to save before you exit?");
        System.out.println("Type 'y' for yes and 'n' for no.");
        boolean willSave = formatYesOrNo(input.next());
        if (willSave) {
            saveRescueAlerts();
        } else {
            System.out.println("You have chosen not to save your changes to rescue alerts.");
        }
    }

    // MODIFIES: this
    // EFFECTS: adds a new alert to the list of posted rescue alerts
    //          if InvalidNumPeopleException is caught, throws it back up
    private void addAnAlert() throws InvalidNumPeopleException {
        input.nextLine(); // placeholder for proper user input
        System.out.println("What is your name? Type 'anon' if you would like to remain anonymous.");
        String name = input.nextLine();
        System.out.println("What is your contact number?");
        String number = input.nextLine();
        System.out.println("What is the exact location where rescuers may find you?");
        String location = input.nextLine();
        System.out.println("How many people are at the location with you?");
        int numPeople = input.nextInt();
        System.out.println("Do you have any babies/ elderly/ injured/ disabled persons with you?"
                + " Type 'y' for yes and 'n' for no.");
        String hasBabyElderlyDisabledInjured = input.next();

        Alert newAlert = new Alert(Alert.formatAnonymous(name), number, location, numPeople,
                formatYesOrNo(hasBabyElderlyDisabledInjured));
        rescueAlerts.addAlert(newAlert);
        System.out.println("You have successfully added a new alert:\n" + Alert.formatIdCode(newAlert.getIdCode())
                + " | " + newAlert.getName() + " – " + newAlert.getLocation() + "\n");
    }

    // EFFECTS: displays selected alert in detail for user
    //          if selected has a rescuer, displays their details. otherwise no details about rescuer displayed
    //          if an InvalidIdCodeException is caught, prints an error message
    private void viewAnAlert() {
        System.out.println("Which alert would you like to view?");
        try {
            Alert selected = selectAlert();
            if (selected.hasRescuer()) {
                System.out.println("--------------------------------------------------");
                System.out.println("Rescuer Name: " + selected.getRescuerName());
                System.out.println("Rescuer Contact Number: " + selected.getRescuerContactNum() + "\n");
            }
        } catch (InvalidIdCodeException e) {
            System.err.println("You have chosen a nonexistent Alert ID...\n");
        }
    }

    // MODIFIES: this
    // EFFECTS: allows user to change status of selected alert
    //          if InvalidIdCodeException is caught, throws it back up
    private void respondToAlert() throws InvalidIdCodeException {
        System.out.println("Which alert would you like to respond to?");
        Alert selected = selectAlert();

        System.out.println("Is this the right alert? Type 'y' for yes and 'n' for no.");
        boolean isCorrect = formatYesOrNo(input.next());

        while (!isCorrect) {
            selected = selectAlert();
            System.out.println("Is this the right alert? Type 'y' for yes and 'n' for no.");
            isCorrect = formatYesOrNo(input.next());
        }

        System.out.println("The status of Alert ID " + Alert.formatIdCode(selected.getIdCode())
                + " is currently set to: " + Alert.formatStatus(selected.getStatus()));
        try {
            System.out.println("Would you like to set this alert to " + Alert.formatStatus(selected.getNextStatus())
                    + "?"
                    + " Type 'y' for yes and 'n' for no.");
            boolean isValidated = formatYesOrNo(input.next());
            changeStatus(selected, isValidated);
        } catch (AlreadyRescuedException e) {
            System.out.println("This Alert has already been rescued. Thank you for your support. 🌈✨\n");
        }
    }

    // MODIFIES: this
    // EFFECTS: if isValidated is true, changes status of selected. if selected has no rescuer,
    //          gives selected a rescuerName and rescuerContactNum and prints a message.
    //          if selected has no rescuer, prints a message.
    //          if isValidated is false, prints a message and does not modify selected.
    private void changeStatus(Alert selected, boolean isValidated) throws AlreadyRescuedException {
        if (isValidated) {
            selected.setStatus(selected.getNextStatus());
            input.nextLine(); // placeholder for proper user input
            if (!selected.hasRescuer()) {
                System.out.println("What is your name?");
                selected.setRescuerName(input.nextLine());
                System.out.println("What is your contact number?");
                selected.setRescuerContactNum(input.nextLine());
            }
            System.out.println("You have successfully changed the status of Alert ID "
                    + Alert.formatIdCode(selected.getIdCode()) + " to "
                    + Alert.formatStatus(selected.getStatus()) + ".");
            System.out.println("Thank you for your generous service, " + selected.getRescuerName() + " 🌈✨\n");
        } else {
            System.out.println("You have opted not to respond to this alert.\n");
        }
    }

    // EFFECTS: returns an alert that matches the idCode from user input and displays its details
    //          if InvalidIdCodeException is caught, throws it back up
    private Alert selectAlert() throws InvalidIdCodeException {
        System.out.println("Please type in the ID of the alert you wish to select.");
        int idCode = input.nextInt();

        Alert selected = rescueAlerts.getAlertById(idCode);
        System.out.println("You have chosen Alert ID " + Alert.formatIdCode(selected.getIdCode()));
        System.out.println("--------------------------------------------------");
        System.out.println("Status: " + Alert.formatStatus(selected.getStatus()));
        System.out.println("Name: " + selected.getName());
        System.out.println("Contact Number: " + selected.getContactNum());
        System.out.println("Location: " + selected.getLocation());
        System.out.println("Number of People: " + selected.getNumPeople());
        System.out.println("Has baby/elderly/disabled/injured? " + selected.isUrgent() + "\n");
        return selected;
    }

    // EFFECTS: saves the rescue alerts to file
    // note: the implementation of this method was modeled after the sonSerializationDemo sample project
    private void saveRescueAlerts() {
        try {
            jsonWriter.open();
            jsonWriter.write(rescueAlerts);
            jsonWriter.close();
            System.out.println("Saved " + rescueAlerts.getName() + " to " + JSON_STORE);
        } catch (FileNotFoundException e) {
            System.out.println("Unable to write to file: " + JSON_STORE);
        }
    }

    // MODIFIES: this
    // EFFECTS: loads rescue alerts from file
    // note: the implementation of this method was modeled after the sonSerializationDemo sample project
    private void loadRescueAlerts() {
        try {
            rescueAlerts = jsonReader.read();
            System.out.println("Loaded " + rescueAlerts.getName() + " from " + JSON_STORE);
        } catch (IOException e) {
            System.out.println("Unable to read from file: " + JSON_STORE);
        }
    }

    // EFFECTS: formats given String argument into boolean
    //          returns true for "y" = yes, false for "n" = no,
    //          and prints a message + returns false for an invalid selection
    private boolean formatYesOrNo(String yesOrNo) {
        if (yesOrNo.equals("y")) {
            return true;
        } else if (yesOrNo.equals("n")) {
            return false;
        } else {
            System.out.println("Please enter a valid selection...\n");
            return false;
        }
    }
}
