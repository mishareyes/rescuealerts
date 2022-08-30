package persistence;

import model.Alert;
import model.AlertStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonTest {
    protected void checkAlert(int idCode, AlertStatus status, String name, String contactNum,
                              String location, int numPeople, boolean isUrgent,
                              String rescuerName, String rescuerContactNum, Alert alert) {
        assertEquals(idCode, alert.getIdCode());
        assertEquals(status, alert.getStatus());
        assertEquals(name, alert.getName());
        assertEquals(contactNum, alert.getContactNum());
        assertEquals(location, alert.getLocation());
        assertEquals(numPeople, alert.getNumPeople());
        assertEquals(isUrgent, alert.isUrgent());
        assertEquals(rescuerName, alert.getRescuerName());
        assertEquals(rescuerContactNum, alert.getRescuerContactNum());
    }
}
