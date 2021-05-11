package fr.notavone;

import javax.swing.*;
import java.awt.*;

public class ReferencedIcon extends ImageIcon {
    private final String reference;

    public ReferencedIcon(Image image, String reference) {
        super(image.getScaledInstance(120, 120, Image.SCALE_SMOOTH));
        this.reference = reference;
    }

    public String getReference() {
        return reference;
    }
}
