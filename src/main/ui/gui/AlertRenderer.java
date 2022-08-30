package ui.gui;

import model.Alert;
import model.AlertStatus;

import javax.swing.*;
import java.awt.*;

// Custom renderer to display a rescue alert with an icon corresponding to its status
// note: this class was modeled after https://www.codejava.net/java-se/swing/jlist-custom-renderer-example
public class AlertRenderer extends JLabel implements ListCellRenderer<Alert> {

    // EFFECTS: creates an AlertRenderer with an opaque rendering
    public AlertRenderer() {
        setOpaque(true);
    }

    // EFFECTS: returns a component that corresponds to preferred rendering of list cell
    @Override
    public Component getListCellRendererComponent(JList<? extends Alert> list, Alert alert, int index,
                                                  boolean isSelected, boolean cellHasFocus) {

        AlertStatus status = alert.getStatus();
        ImageIcon statusIcon = new ImageIcon("images/" + statusToImage(status) + ".png");

        setText(alert.getIdCode() + " | ");
        setIcon(statusIcon);
        setText(formatStatus(alert.getStatus()) + " – " + alert.getLocation());

        if (isSelected) {
            setBackground(new Color(0x8BC7EE));
            setForeground(list.getForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        return this;
    }

    // EFFECTS: returns a string of the color that corresponds to the given alert status
    //          NEEDS_HELP is "red", HELP_OTW is "yellow", and RESCUED is "green"
    private String statusToImage(AlertStatus status) {
        if (status.equals(AlertStatus.NEEDS_HELP)) {
            return "red";
        } else if (status.equals(AlertStatus.HELP_OTW)) {
            return "yellow";
        } else {
            return "green";
        }
    }

    // EFFECTS: formats given alert status to string corresponding to AlertStatus enum name
    public static String formatStatus(AlertStatus status) {
        if (status.equals(AlertStatus.NEEDS_HELP)) {
            return "NEEDS HELP";
        } else if (status.equals(AlertStatus.HELP_OTW)) {
            return "HELP IS ON THE WAY";
        } else {
            return "RESCUED";
        }
    }

}