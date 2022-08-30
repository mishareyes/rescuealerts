package persistence;

import exceptions.InvalidNumPeopleException;
import model.Alert;
import model.AlertStatus;
import model.RescueAlerts;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

// Represents a reader that reads rescue alerts from JSON data stored in file
// note: this class was modeled after the JsonSerializationDemo sample project
public class JsonReader {
    private String source;

    // EFFECTS: constructs reader to read from source file
    public JsonReader(String source) {
        this.source = source;
    }

    // EFFECTS: reads rescue alerts from file and returns it;
    // throws IOException if an error occurs reading data from file
    public RescueAlerts read() throws IOException {
        String jsonData = readFile(source);
        JSONObject jsonObject = new JSONObject(jsonData);
        return parseRescueAlerts(jsonObject);
    }

    // EFFECTS: reads source file as string and returns it
    private String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s));
        }

        return contentBuilder.toString();
    }

    // EFFECTS: parses rescue alerts from JSON object and returns it
    private RescueAlerts parseRescueAlerts(JSONObject jsonObject) {
        String name = jsonObject.getString("name");
        RescueAlerts ra = new RescueAlerts(name);
        addAlerts(ra, jsonObject);
        return ra;
    }

    // MODIFIES: ra
    // EFFECTS: parses alerts from JSON object and adds them to rescue alerts
    private void addAlerts(RescueAlerts ra, JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("alerts");
        for (Object json : jsonArray) {
            JSONObject nextAlert = (JSONObject) json;
            addAlert(ra, nextAlert);
        }
    }

    // MODIFIES: ra
    // EFFECTS: parses alert from JSON object and adds it to rescue alerts
    private void addAlert(RescueAlerts ra, JSONObject jsonObject) {
        int idCode = jsonObject.getInt("id code");
        AlertStatus status = AlertStatus.valueOf(jsonObject.getString("status"));
        String name = jsonObject.getString("name");
        String contactNum = jsonObject.getString("contact number");
        String location = jsonObject.getString("location");
        int numPeople = jsonObject.getInt("number of people");
        boolean isUrgent = jsonObject.getBoolean("is urgent?");
        String rescuerName = getNull(jsonObject, "name of rescuer");
        String rescuerContactNum = getNull(jsonObject, "contact number of rescuer");
        Alert alert = null;
        try {
            alert = new Alert(name, contactNum, location, numPeople, isUrgent);
            alert.setIdCode(idCode);
            alert.setStatus(status);
            alert.setRescuerName(rescuerName);
            alert.setRescuerContactNum(rescuerContactNum);
        } catch (InvalidNumPeopleException e) {
            System.out.println("This is okay");
        }
        ra.addAlert(alert);
    }

    // EFFECTS: if jsonObject is null, returns null. otherwise, returns the jsonObject string that corresponds to key s
    private String getNull(JSONObject jsonObject, String s) {
        if (jsonObject.isNull(s)) {
            return null;
        } else {
            return jsonObject.getString(s);
        }
    }

}
