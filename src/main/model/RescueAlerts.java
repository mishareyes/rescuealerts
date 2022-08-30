package model;

import exceptions.InvalidIdCodeException;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Writable;

import java.util.ArrayList;
import java.util.List;

// Represents a list of the rescue Alerts that have been posted by users so far.
// The given name refers to the name of the disaster where rescue alerts are needed.
// Every Alert added to this is given a unique id code, starting with INITIAL_ID_CODE
public class RescueAlerts implements Writable {
    public static final int INITIAL_ID_CODE = 000;

    private String name;
    List<Alert> rescueAlerts;
    private int idCodeCurrent = INITIAL_ID_CODE;

    // constructor
    // EFFECTS: creates a new RescueAlerts list with the given name
    public RescueAlerts(String name) {
        rescueAlerts = new ArrayList<>();
        this.name = name;
    }

    // EFFECTS: returns name of this
    public String getName() {
        return this.name;
    }

    // EFFECTS: returns the current number of posted rescue alerts
    public int getNumAlerts() {
        return rescueAlerts.size();
    }

    // EFFECTS: returns the full list of posted rescue alerts
    public List<Alert> getAllAlerts() {
        return rescueAlerts;
    }

    // MODIFIES: this
    // EFFECTS: if a is not null and is not already an item in the list, adds a to list
    // and gives it a unique idCode. if a is null or is already in the list, does nothing.
    public void addAlert(Alert a) {
        if (a != null && !rescueAlerts.contains(a)) {
            rescueAlerts.add(a);
            a.setIdCode(idCodeCurrent);
            idCodeCurrent++;
        }
    }

    // EFFECTS: looks for Alert that matches given idCode and returns it.
    //          if no Alert matching idCode is found, throws an InvalidIdCodeException.
    public Alert getAlertById(int idCode) throws InvalidIdCodeException {
        for (Alert a : rescueAlerts) {
            if (a.getIdCode() == idCode) {
                return a;
            }
        }
        throw new InvalidIdCodeException();
    }

    // note: the implementation of this method was modeled after the JsonSerializationDemo sample project
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("alerts", alertsToJson());
        return json;
    }

    // EFFECTS: returns posted alerts in this RescueAlerts as a JSON array
    // note: the implementation of this method was modeled after the JsonSerializationDemo sample project
    private JSONArray alertsToJson() {
        JSONArray jsonArray = new JSONArray();

        for (Alert a : rescueAlerts) {
            jsonArray.put(a.toJson());
        }

        return jsonArray;
    }
}
