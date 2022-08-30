package model;

import exceptions.InvalidIdCodeException;
import exceptions.InvalidNumPeopleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static model.RescueAlerts.INITIAL_ID_CODE;
import static org.junit.jupiter.api.Assertions.*;

// unit tests for the RescueAlerts class
class RescueAlertsTest {
    RescueAlerts testRescueAlerts;
    Alert a1;
    Alert a2;
    Alert a3;

    @BeforeEach
    public void setup() {
        testRescueAlerts = new RescueAlerts("Typhoon Ulysses");
        try {
            a1 = new Alert("Jericho Rosales", "09172473475", "1432 Kaimito, Barangay Don Juan", 5, true);
            a2 = new Alert("Vice Ganda", "09228459012", "ABS-CBN Broadcasting Center", 2, false);
            a3 = new Alert("Willie Revillame", "09151234567", "82 White Plains Avenue", 9, false);
        } catch (InvalidNumPeopleException e) {
            fail("Unexpected InvalidNumPeopleException");
        }
        testRescueAlerts.addAlert(a1);
        testRescueAlerts.addAlert(a2);
        testRescueAlerts.addAlert(a3);
    }

    @Test
    public void testGetters() {
        List<Alert> alerts = new ArrayList<>();
        alerts.add(a1);
        alerts.add(a2);
        alerts.add(a3);
        assertEquals(alerts,testRescueAlerts.getAllAlerts());
        assertEquals(3,testRescueAlerts.getNumAlerts());
        assertEquals("Typhoon Ulysses", testRescueAlerts.getName());
    }

    @Test
    public void testNoDuplicates() {
        testRescueAlerts.addAlert(a1);
        testRescueAlerts.addAlert(a1);
        assertEquals(3,testRescueAlerts.getNumAlerts());
        try {
            assertEquals(a1,testRescueAlerts.getAlertById(INITIAL_ID_CODE));
            assertEquals(a2,testRescueAlerts.getAlertById(INITIAL_ID_CODE+1));
            assertEquals(a3,testRescueAlerts.getAlertById(INITIAL_ID_CODE+2));
        } catch (InvalidIdCodeException e) {
            fail("Unexpected InvalidIdCodeException");
        }
    }

    @Test
    public void testAddNull() {
        testRescueAlerts.addAlert(null);
        assertEquals(3,testRescueAlerts.getNumAlerts());

        List<Alert> alerts = new ArrayList<>();
        alerts.add(a1);
        alerts.add(a2);
        alerts.add(a3);
        assertEquals(alerts,testRescueAlerts.getAllAlerts());
    }

    @Test
    public void testGetAlertByIdExpectException() {
        assertEquals(3,testRescueAlerts.getNumAlerts());
        try {
            assertNotEquals(a1,testRescueAlerts.getAlertById(INITIAL_ID_CODE+testRescueAlerts.getNumAlerts()));
            fail("InvalidIdCodeException was not thrown");
        } catch (InvalidIdCodeException e) {
            // do nothing
        }
    }
}