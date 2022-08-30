package persistence;

import model.Alert;
import model.AlertStatus;
import model.RescueAlerts;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

// unit tests for the JsonReader class
// note: this class was modeled after the JsonSerializationDemo sample project
public class JsonReaderTest extends JsonTest {

    @Test
    void testReaderNonExistentFile() {
        JsonReader reader = new JsonReader("./data/noSuchFile.json");
        try {
            RescueAlerts ra = reader.read();
            fail("IOException expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testReaderInvalidNumPeopleExceptionExpected() {
        JsonReader reader = new JsonReader("./data/testReaderRescueAlertInvalidNumPeopleException.json");
        try {
            RescueAlerts ra = reader.read();
            assertEquals(new ArrayList<>(), ra.getAllAlerts());
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }

    @Test
    void testReaderEmptyRescueAlerts() {
        JsonReader reader = new JsonReader("./data/testReaderEmptyRescueAlerts.json");
        try {
            RescueAlerts ra = reader.read();
            assertEquals("My rescue alerts", ra.getName());
            assertEquals(0, ra.getNumAlerts());
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }

    @Test
    void testReaderManyRescueAlerts() {
        JsonReader reader = new JsonReader("./data/testReaderManyRescueAlerts.json");
        try {
            RescueAlerts ra = reader.read();
            assertEquals("Typhoon Ulysses", ra.getName());
            List<Alert> alerts = ra.getAllAlerts();
            assertEquals(3, alerts.size());
            checkAlert(0, AlertStatus.HELP_OTW, "Jericho Rosales", "09172473475",
                    "1432 Kaimito, Barangay Don Juan", 5, true,
                    "Kim Jones", "09159876543", alerts.get(0));
            checkAlert(1, AlertStatus.NEEDS_HELP, "Vice Ganda", "09228459012",
                    "ABS-CBN Broadcasting Center", 2, false,
                    null, null, alerts.get(1));
            checkAlert(2, AlertStatus.NEEDS_HELP, "Willie Revillame", "09151234567",
                    "82 White Plains Avenue", 9, false,
                    null, null, alerts.get(2));
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }
}
