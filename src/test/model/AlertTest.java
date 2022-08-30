package model;

import exceptions.AlreadyRescuedException;
import exceptions.InvalidNumPeopleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static model.AlertStatus.*;
import static org.junit.jupiter.api.Assertions.*;

// unit tests for the Alert class
class AlertTest {
    Alert a1;

    @BeforeEach
    public void setup() {
        try {
            a1 = new Alert("Jericho Rosales","09172473475","1432 Kaimito, Barangay Don Juan",5, true);
        } catch (InvalidNumPeopleException e) {
            fail("Unexpected InvalidNumPeopleException");
        }
    }

    @Test
    public void testGetters() {
        assertEquals("Jericho Rosales", a1.getName());
        assertEquals("09172473475",a1.getContactNum());
        assertEquals("1432 Kaimito, Barangay Don Juan", a1.getLocation());
        assertEquals(5, a1.getNumPeople());
        assertTrue(a1.isUrgent());
        assertEquals(NEEDS_HELP, a1.getStatus());

        a1.setIdCode(RescueAlerts.INITIAL_ID_CODE);
        assertEquals(RescueAlerts.INITIAL_ID_CODE,a1.getIdCode());
    }

    @Test
    public void testRescuerHelpOtw() {
        a1.setStatus(HELP_OTW);
        a1.setRescuerName("Kim Jones");
        a1.setRescuerContactNum("09159876543");
        assertEquals("Kim Jones", a1.getRescuerName());
        assertEquals("09159876543",a1.getRescuerContactNum());
        assertTrue(a1.hasRescuer());
    }

    @Test
    public void testRescuerRescued() {
        a1.setStatus(RESCUED);
        a1.setRescuerName("Kim Jones");
        a1.setRescuerContactNum("09159876543");
        assertEquals("Kim Jones", a1.getRescuerName());
        assertEquals("09159876543",a1.getRescuerContactNum());
        assertTrue(a1.hasRescuer());
    }

    @Test
    public void testHasNoRescuerNeedsHelpAndNull() {
        assertEquals(NEEDS_HELP, a1.getStatus());
        assertNull(a1.getRescuerName());
        assertFalse(a1.hasRescuer());
    }

    @Test
    public void testHasNoRescuerNeedsHelpOnly() {
        assertEquals(NEEDS_HELP, a1.getStatus());
        a1.setRescuerName("Kim Jones");
        a1.setRescuerContactNum("09159876543");
        assertEquals("Kim Jones", a1.getRescuerName());
        assertEquals("09159876543",a1.getRescuerContactNum());
        assertFalse(a1.hasRescuer());
    }

    @Test
    public void testHasNoRescuerNullOnly() {
        a1.setStatus(HELP_OTW);
        assertEquals(HELP_OTW, a1.getStatus());
        assertNull(a1.getRescuerName());
        assertFalse(a1.hasRescuer());

        a1.setStatus(RESCUED);
        assertEquals(RESCUED, a1.getStatus());
        assertNull(a1.getRescuerName());
        assertFalse(a1.hasRescuer());
    }


    @Test
    public void testSetNextStatusNeedsHelpToHelpOtw() {
        assertEquals(NEEDS_HELP,a1.getStatus());
        try {
            assertEquals(HELP_OTW,a1.getNextStatus());
            a1.setStatus(a1.getNextStatus());
        } catch (AlreadyRescuedException e) {
            fail("Unexpected AlreadyRescuedException");
        }
        assertNotEquals(NEEDS_HELP,a1.getStatus());
        assertEquals(HELP_OTW,a1.getStatus());
    }

    @Test
    public void testSetNextStatusHelpOtwToRescued() {
        a1.setStatus(HELP_OTW);
        assertEquals(HELP_OTW,a1.getStatus());
        try {
            assertEquals(RESCUED,a1.getNextStatus());
            a1.setStatus(a1.getNextStatus());
        } catch (AlreadyRescuedException e) {
            fail("Unexpected AlreadyRescuedException");
        }
        assertNotEquals(HELP_OTW,a1.getStatus());
        assertEquals(RESCUED,a1.getStatus());
    }

    @Test
    public void testSetNextStatusRescuedExpectException() {
        a1.setStatus(RESCUED);
        assertEquals(RESCUED,a1.getStatus());
        try {
            assertEquals(RESCUED,a1.getNextStatus());
            a1.setStatus(a1.getNextStatus());
            fail("AlreadyRescuedException was not caught");
        } catch (AlreadyRescuedException e) {
            // do nothing
        }
        assertEquals(RESCUED,a1.getStatus());
    }

    @Test
    public void testZeroNumPeopleExpectException() {
        try {
            new Alert("Anonymous","09288881234","SM Megamall",0,false);
            fail("InvalidNumPeopleException was not thrown");
        } catch (InvalidNumPeopleException e) {
            // do nothing
        }
    }

    @Test
    public void testNegativeNumPeopleExpectException() {
        try {
            new Alert("Anonymous","09288881234","SM Megamall",-2,false);
            fail("InvalidNumPeopleException was not thrown");
        } catch (InvalidNumPeopleException e) {
            // do nothing
        }
    }

    @Test
    public void testToString() {
        a1.setIdCode(43);
        assertEquals("043 | 🔴️ NEEDS HELP – 1432 Kaimito, Barangay Don Juan", a1.toString());
    }

    @Test
    public void testFormatIdCode() {
        assertEquals("000", Alert.formatIdCode(0));
        assertEquals("001", Alert.formatIdCode(1));
        assertEquals("009", Alert.formatIdCode(9));
        assertEquals("010", Alert.formatIdCode(10));
        assertEquals("099", Alert.formatIdCode(99));
        assertEquals("100", Alert.formatIdCode(100));
        assertEquals("999", Alert.formatIdCode(999));
        assertNull(Alert.formatIdCode(1000));
    }

    @Test
    public void testFormatStatus() {
        assertEquals("🔴️ NEEDS HELP", Alert.formatStatus(a1.getStatus()));
        assertEquals("🔴️ NEEDS HELP", Alert.formatStatus(NEEDS_HELP));
        assertEquals("🟡 HELP IS ON THE WAY", Alert.formatStatus(HELP_OTW));
        assertEquals("🟢 RESCUED", Alert.formatStatus(RESCUED));
    }

    @Test
    public void testFormatAnonymous() {
        assertEquals("Anonymous", Alert.formatAnonymous("anon"));
        assertEquals("anony", Alert.formatAnonymous("anony"));
        assertEquals("ANONYMOUS", Alert.formatAnonymous("ANONYMOUS"));
        assertEquals("ANON", Alert.formatAnonymous("ANON"));
        assertEquals("pizza", Alert.formatAnonymous("pizza"));
    }
}