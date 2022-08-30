package ui;

import exceptions.InvalidNumPeopleException;
import model.Alert;

import java.io.FileNotFoundException;

// Runs the Disaster Rescue Alerts Application
// note: this class was modeled after the JsonSerializationDemo sample project
public class Main {
    public static void main(String[] args) {
        try {
            new RescueApp();
        } catch (FileNotFoundException e) {
            System.out.println("Unable to run application: file not found");
        }
    }
}
