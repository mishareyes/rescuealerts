package persistence;

import exceptions.InvalidNumPeopleException;
import model.Alert;
import model.AlertStatus;
import model.RescueAlerts;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

// unit tests for the JsonWriter class
// note: this class was modeled after the JsonSerializationDemo sample project
public class JsonWriterTest extends JsonTest {

    @Test
    void testWriterInvalidFile() {
        try {
            RescueAlerts ra = new RescueAlerts("My rescue alerts");
            JsonWriter writer = new JsonWriter("./data/my\0illegal:fileName.json");
            writer.open();
            fail("IOException was expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testWriterEmptyRescueAlerts() {
        try {
            RescueAlerts ra = new RescueAlerts("My rescue alerts");
            JsonWriter writer = new JsonWriter("./data/testWriterEmptyRescueAlerts.json");
            writer.open();
            writer.write(ra);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterEmptyRescueAlerts.json");
            ra = reader.read();
            assertEquals("My rescue alerts", ra.getName());
            assertEquals(0, ra.getNumAlerts());
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

    @Test
    void testWriterManyRescueAlerts() {
        try {
            RescueAlerts ra = new RescueAlerts("Typhoon Ulysses");

            Alert first = new Alert("Jericho Rosales", "09172473475", "1432 Kaimito, Barangay Don Juan", 5, true);
            Alert second = new Alert("Vice Ganda", "09228459012", "ABS-CBN Broadcasting Center", 2, false);
            Alert third = new Alert("Willie Revillame", "09151234567", "82 White Plains Avenue", 9, false);
            first.setStatus(AlertStatus.HELP_OTW);
            first.setRescuerName("Kim Jones");
            first.setRescuerContactNum("09159876543");

            ra.addAlert(first);
            ra.addAlert(second);
            ra.addAlert(third);
            JsonWriter writer = new JsonWriter("./data/testWriterManyRescueAlerts.json");
            writer.open();
            writer.write(ra);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterManyRescueAlerts.json");
            ra = reader.read();
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

        } catch (IOException | InvalidNumPeopleException e) {
            fail("Exception should not have been thrown");
        }
    }


}
