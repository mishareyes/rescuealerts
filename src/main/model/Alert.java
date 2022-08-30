package model;

import exceptions.AlreadyRescuedException;
import exceptions.InvalidNumPeopleException;
import org.json.JSONObject;
import persistence.Writable;

import static model.AlertStatus.*;

// Represents an alert for a rescue call with a name, contact number, location,
// number of people at location, urgency level, status, and details about rescuer (if any).
// Each alert has a unique id code and is initialized once alert is added to RescueAlerts.
public class Alert implements Writable {
    private int idCode;
    private String name;
    private String contactNum;
    private String location;
    private int numPeople;
    private boolean isUrgent;
    private AlertStatus status;

    private String rescuerName;
    private String rescuerContactNum;

    // constructor
    // EFFECTS: creates a new alert with a NEED_HELP status, given name, location,
    //          number of people, and urgency level
    //          if given numPeople is < 1, throws an InvalidNumPeopleException
    public Alert(String name, String contactNum, String location, int numPeople,
                 boolean hasBabyElderlyDisabledInjured) throws InvalidNumPeopleException {
        // idCode is intialized once this is added to RescueAlerts
        this.name = name;
        this.contactNum = contactNum;
        this.location = location;
        this.isUrgent = hasBabyElderlyDisabledInjured;
        status = NEEDS_HELP;

        if (numPeople > 0) {
            this.numPeople = numPeople;
        } else {
            throw new InvalidNumPeopleException();
        }
    }

    // getters
    public int getIdCode() {
        return this.idCode;
    }

    public String getName() {
        return this.name;
    }

    public String getContactNum() {
        return this.contactNum;
    }

    public String getLocation() {
        return this.location;
    }

    public int getNumPeople() {
        return this.numPeople;
    }

    public boolean isUrgent() {
        return this.isUrgent;
    }

    public AlertStatus getStatus() {
        return this.status;
    }

    public String getRescuerName() {
        return this.rescuerName;
    }

    public String getRescuerContactNum() {
        return this.rescuerContactNum;
    }

    // setters
    public void setIdCode(int idCode) {
        this.idCode = idCode;
    }

    public void setStatus(AlertStatus status) {
        this.status = status;
    }

    public void setRescuerName(String name) {
        this.rescuerName = name;
    }

    public void setRescuerContactNum(String num) {
        this.rescuerContactNum = num;
    }

    // EFFECTS: returns the next status of this in order of NEED_HELP --> HELP_OTW --> RESCUED
    //          if status is already RESCUED, throws AlreadyRescuedException
    public AlertStatus getNextStatus() throws AlreadyRescuedException {
        if (getStatus() == NEEDS_HELP) {
            return HELP_OTW;
        } else if (getStatus() == HELP_OTW) {
            return RESCUED;
        } else {
            throw new AlreadyRescuedException();
        }
    }

    // EFFECTS: returns true if this has a rescuer (with a rescuer name and appropriate status) and false otherwise
    public boolean hasRescuer() {
        return rescuerName != null && status != NEEDS_HELP;
    }

    // note: the implementation of this method was modeled after the JsonSerializationDemo sample project
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("id code", idCode);
        json.put("status", status);
        json.put("name", name);
        json.put("contact number", contactNum);
        json.put("location", location);
        json.put("number of people", numPeople);
        json.put("is urgent?", isUrgent);
        json.put("name of rescuer", rescuerName);
        json.put("contact number of rescuer", rescuerContactNum);
        return json;
    }

    @Override
    public String toString() {
        return formatIdCode(idCode) + " | " + formatStatus(status) + " – " + location;
    }

    // REQUIRES: idCode >= 0
    // EFFECTS: formats given id code to string "XXX" ex. 12 -> "012"
    public static String formatIdCode(int idCode) {
        if (idCode < 10) {
            return "00" + idCode;
        } else if (idCode < 100) {
            return "0" + idCode;
        } else if (idCode < 1000) {
            return String.valueOf(idCode);
        } else {
            return null;
        }
    }

    // EFFECTS: formats given alert status to string corresponding to AlertStatus enum name
    public static String formatStatus(AlertStatus status) {
        if (status.equals(AlertStatus.NEEDS_HELP)) {
            return "🔴️ NEEDS HELP";
        } else if (status.equals(AlertStatus.HELP_OTW)) {
            return "🟡 HELP IS ON THE WAY";
        } else {
            return "🟢 RESCUED";
        }
    }

    // EFFECTS: if given name String is "anon", returns "Anonymous".
    //          otherwise does nothing to string and returns it
    public static String formatAnonymous(String name) {
        if (name.equals("anon")) {
            name = "Anonymous";
        }
        return name;
    }
}
