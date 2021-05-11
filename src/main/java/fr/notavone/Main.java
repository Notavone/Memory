package fr.notavone;

import javax.swing.*;
import java.net.URL;
import java.security.CodeSource;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Main {
    public static void main(String[] args) {
        Locale.setDefault(Locale.ENGLISH);
        SwingUtilities.invokeLater(() -> new Controller(new Model()));
    }
}