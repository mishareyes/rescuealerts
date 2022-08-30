package ui.gui;

import exceptions.InvalidNumPeopleException;
import model.Alert;
import model.RescueAlerts;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Renders the Disaster Rescue Alerts application on a graphic interface
public class MainGUI extends JPanel implements ActionListener, ListSelectionListener {

    private static final String JSON_STORE = "./data/rescueAlerts.json";

    protected static JLabel statusLabel;
    private JSplitPane splitPane;
    private JTextArea selectedAlertLabel;
    private JList<Alert> list;
    private RescueAlerts rescueAlerts;
    private DefaultListModel<Alert> raListModel;
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;

    // EFFECTS: renders visual components of application on split pane screen
    // note: this method was based off SplitPaneDemoProject and SplitPaneDividerDemoProject from Oracle Java docs
    public MainGUI() {
        super(new BorderLayout());
        statusLabel = new JLabel("Sample Rescue Alerts file currently loaded.");
        init();
        updateRaListModel();

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        list.addListSelectionListener(this);
        list.setCellRenderer(new AlertRenderer());
        JScrollPane listRescueAlertsScrollPane = new JScrollPane(list);

        selectedAlertLabel = new JTextArea();
        JScrollPane alertsScrollPane = new JScrollPane(selectedAlertLabel);

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                listRescueAlertsScrollPane, alertsScrollPane);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(350);

        Dimension minimumSize = new Dimension(100, 50);
        listRescueAlertsScrollPane.setMinimumSize(minimumSize);
        alertsScrollPane.setMinimumSize(minimumSize);

        updateDisplayedAlert(raListModel.get(list.getSelectedIndex()));
        add(createTitle(), BorderLayout.PAGE_START);
        add(splitPane, BorderLayout.CENTER);
        add(createControlPanel(), BorderLayout.PAGE_END);
    }

    // MODIFIES: this
    // EFFECTS: initializes jsonWriter, jsonReader, and a list of sample rescue alerts
    private void init() {
        rescueAlerts = new RescueAlerts("Typhoon Ulysses");
        raListModel = new DefaultListModel<>();
        jsonWriter = new JsonWriter(JSON_STORE);
        jsonReader = new JsonReader(JSON_STORE);

        Alert first = null;
        Alert second = null;
        try {
            first = new Alert("Sample Rescue 1", "0917XXXXXXX", "01 Street", 1, false);
            second = new Alert("Sample Rescue 2", "0929XXXXXXX", "The Mall", 2, true);
        } catch (InvalidNumPeopleException e) {
            // do nothing, initialized alerts in this method will always have a valid number of people
        }
        rescueAlerts.addAlert(first);
        rescueAlerts.addAlert(second);
    }

    // EFFECTS: returns a JComponent that contains logo + title of application and its status
    private JComponent createTitle() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
        LocalDateTime now = LocalDateTime.now();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        ImageIcon logo = new ImageIcon("images/rescueAlerts logo.png");
        JLabel logoLabel = new JLabel(logo);
        JLabel label1 = new JLabel("RESCUE ALERTS FOR DISASTER " + rescueAlerts.getName().toUpperCase());
        JLabel label2 = new JLabel("as of " + dtf.format(now));

        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        label1.setAlignmentX(Component.CENTER_ALIGNMENT);
        label2.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        Font bold = new Font("Lucida Grande",Font.BOLD,16);
        label1.setFont(bold);
        Font italicized = new Font("Lucida Grande",Font.ITALIC,13);
        statusLabel.setFont(italicized);

        panel.add(logoLabel);
        panel.add(label1);
        panel.add(label2);
        panel.add(statusLabel);
        return panel;
    }

    // EFFECTS: returns a JComponent that contains functional buttons for adding new alerts, loading, and saving
    private JComponent createControlPanel() {
        JPanel panel = new JPanel();

        JButton addNewAlert = new JButton("Add a new alert");
        addNewAlert.setActionCommand("add");
        addNewAlert.addActionListener(this);
        panel.add(addNewAlert);

        JButton loadAlerts = new JButton("Load alerts");
        loadAlerts.setActionCommand("load");
        loadAlerts.addActionListener(this);
        panel.add(loadAlerts);

        JButton saveAlerts = new JButton("Save alerts");
        saveAlerts.setActionCommand("save");
        saveAlerts.addActionListener(this);
        panel.add(saveAlerts);

        return panel;
    }

    // MODIFIES: this
    // EFFECTS: processes command when button is pressed
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "add":
                addAlert();
                break;
            case "load":
                loadAlerts();
                break;
            case "save":
                saveAlerts();
                break;
        }
    }

    // MODIFIES: this
    // EFFECTS: adds new alert by prompting user for information with dialog boxes
    //          if user decides to press cancel or exit, does not add the new alert
    private void addAlert() {
        JFrame parent = new JFrame();
        JComponent[] components = getNumPeopleComponents();

        String name = JOptionPane.showInputDialog(parent, "What is your name? Type 'anon' if"
                + " you would like to remain anonymous.", "Name", JOptionPane.PLAIN_MESSAGE);
        if (name == null) {
            return;
        }
        String contactNum = JOptionPane.showInputDialog(parent,"What is your contact number?",
                "Contact Number",  JOptionPane.PLAIN_MESSAGE);
        if (contactNum == null) {
            return;
        }
        String location = JOptionPane.showInputDialog(parent, "What is the exact location where rescuers may find you?",
                "Location",  JOptionPane.PLAIN_MESSAGE);
        if (location == null) {
            return;
        }
        JOptionPane.showOptionDialog(parent, components, "Number of People",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
        int numPeople = (int) ((JSpinner)components[1]).getValue();
        int isUrgent = JOptionPane.showConfirmDialog(parent, "Do you have any babies/ elderly/ injured/ disabled "
                + "persons with you?", "Urgency", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);

        addNewAlert(name, contactNum, location, numPeople, isUrgent);
    }

    // MODIFIES: this
    // EFFECTS: adds new alert to rescueAlerts and raListModel using given arguments,
    //          updates statusLabel to give update on added alert
    private void addNewAlert(String name, String contactNum, String location, int numPeople, int isUrgent) {
        try {
            Alert newAlert = new Alert(Alert.formatAnonymous(name), contactNum, location, numPeople,
                    formatYesOrNo(isUrgent));
            rescueAlerts.addAlert(newAlert);
            raListModel.addElement(newAlert);
            statusLabel.setText("You have successfully added a new alert:\n" + Alert.formatIdCode(newAlert.getIdCode())
                    + " | " + newAlert.getName() + " – " + newAlert.getLocation());
        } catch (InvalidNumPeopleException e) {
            statusLabel.setText("Invalid number of people input");
        }
    }

    // EFFECTS: returns a JComponent array of a spinner and label for asking user the number of people at location
    private JComponent[] getNumPeopleComponents() {
        JLabel numPeopleLabel = new JLabel("How many people are at the location with you?");
        SpinnerModel numberModel = new SpinnerNumberModel(1, 1, 100, 1);
        JSpinner spinner = new JSpinner(numberModel);
        return new JComponent[]{numPeopleLabel, spinner};
    }

    // MODIFIES: this
    // EFFECTS: loads rescue alerts from file and updates statusLabel to give update on load
    // note: the implementation of this method was modeled after the JsonSerializationDemo sample project
    private void loadAlerts() {
        try {
            rescueAlerts = jsonReader.read();
            raListModel.clear();
            updateRaListModel();
            statusLabel.setText("Loaded " + rescueAlerts.getName() + " from " + JSON_STORE);
        } catch (IOException e) {
            statusLabel.setText("Unable to read from file: " + JSON_STORE);
        }
    }

    // MODIFIES: this
    // EFFECTS: saves the rescue alerts to file and updates statusLabel to give update on save
    // note: the implementation of this method was modeled after the JsonSerializationDemo sample project
    private void saveAlerts() {
        try {
            jsonWriter.open();
            jsonWriter.write(rescueAlerts);
            jsonWriter.close();
            statusLabel.setText("Saved " + rescueAlerts.getName() + " to " + JSON_STORE);
        } catch (FileNotFoundException e) {
            statusLabel.setText("Unable to write to file: " + JSON_STORE);
        }
    }

    /**
     * Create the GUI and show it.  For thread safety, this method
     * should be invoked from the event-dispatching thread.
     */
    // MODIFIES: this
    // EFFECTS: creates the GUI and shows it
    // note: this method was based off the SplitPaneDemoProject on Oracle Java docs
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Rescue Alerts Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        MainGUI newContentPane = new MainGUI();
        newContentPane.setOpaque(true);
        frame.setContentPane(newContentPane);

        frame.pack();
        frame.setSize(700,400);
        frame.setVisible(true);
    }

    // MODIFIES: this
    // EFFECTS: runs the GUI of the application
    // note: this method was based off the SplitPaneDemoProject on Oracle Java docs
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(MainGUI::createAndShowGUI);
    }

    // MODIFIES: this
    // EFFECTS: responds to the changed selection of alert in displayed rescueAlerts list
    //          if list.getSelectedIndex() == -1 (or when becomes empty), does not update the displayed alert
    @Override
    public void valueChanged(ListSelectionEvent e) {
        JList list = (JList)e.getSource();
        if (list.getSelectedIndex() == -1) {
            return;
        }
        updateDisplayedAlert(raListModel.get(list.getSelectedIndex()));
    }

    // MODIFIES: this
    // EFFECTS: renders the specific details of selected alert
    private void updateDisplayedAlert(Alert selectedAlert) {
        selectedAlertLabel.setText("Alert ID " + Alert.formatIdCode(selectedAlert.getIdCode())
                + "\nStatus: " + AlertRenderer.formatStatus(selectedAlert.getStatus())
                + "\nName: " + selectedAlert.getName()
                + "\nContact Number: " + selectedAlert.getContactNum()
                + "\nLocation: " + selectedAlert.getLocation()
                + "\nNumber of People: " + selectedAlert.getNumPeople()
                + "\nHas baby/elderly/disabled/injured? " + selectedAlert.isUrgent()
                + hasRescuerText(selectedAlert));
    }

    // EFFECTS: if selectedAlert has a rescuer, returns details of rescuer
    //          otherwise returns an empty string
    private String hasRescuerText(Alert selectedAlert) {
        if (selectedAlert.hasRescuer()) {
            return "\n\nName of Rescuer: " + selectedAlert.getRescuerName()
                    + "\nContact Number of Rescuer: " + selectedAlert.getRescuerContactNum();
        }
        return "";
    }

    // MODIFIES: this
    // EFFECTS: when rescueAlerts is modified, updates raListModel to add all the elements
    //          contained in rescueAlerts
    private void updateRaListModel() {
        for (Alert a : rescueAlerts.getAllAlerts()) {
            raListModel.addElement(a);
        }
        list = new JList<>(raListModel);
    }

    // EFFECTS: if yesOrNo == 0 (chose Yes), returns true. otherwise (chose No) returns false
    private boolean formatYesOrNo(int yesOrNo) {
        return yesOrNo == 0;
    }

}
