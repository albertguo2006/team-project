//package app;
//
//import javax.swing.SwingUtilities;
//
//import view.MainGameWindow;
//
///**
// * Main is the entry point for the California Prop. 65 application.
// *
// * Responsibilities:
// * - Configure system properties for display support
// * - Initialize and display the main window with menu system
// *
// * This follows Clean Architecture by creating the top-level window
// * which then orchestrates the creation of all layers as needed.
// */
//public class Main {
//
//    public static void main(String[] args) {
//        // Enable HiDPI/scaling support for modern displays
//        System.setProperty("sun.java2d.uiScale.enabled", "true");
//
//        // Enable Wayland support on Linux (Java 17+)
//        // This will automatically use Wayland when available, fallback to X11 otherwise
//        String sessionType = System.getenv("XDG_SESSION_TYPE");
//        if ("wayland".equalsIgnoreCase(sessionType)) {
//            System.out.println("Detected Wayland session - native support enabled");
//            // Wayland is automatically detected in Java 17+, but we can force it if needed:
//            // System.setProperty("awt.toolkit.name", "WLToolkit");
//        } else if ("x11".equalsIgnoreCase(sessionType)) {
//            System.out.println("Detected X11 session");
//        }
//
//        // Run on the Event Dispatch Thread (Swing requirement)
//        SwingUtilities.invokeLater(() -> {
//            // Create the main window with integrated menu system
//            MainGameWindow mainWindow = new MainGameWindow();
//
//            // Display the window (starts at main menu)
//            mainWindow.setVisible(true);
//        });
//    }
//}

package app;

import javax.swing.SwingUtilities;

import data_access.NPCDataAccessObject;
import entity.Player;
import interface_adapter.events.PlayerInputController;
import use_case.PlayerMovementUseCase;
import use_case.npc_interactions.*;
import view.MainGameWindow;
import javax.swing.*;
import java.awt.*;
import interface_adapter.events.npc_interactions.*;
import view.NpcInteractionsButtonView;
import view.NpcInteractionsView;

public class Main {

    public static void main(String[] args) {
        JFrame mainFrame = new JFrame("Game Main Window");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(600, 400);
        mainFrame.setLayout(null);
        new NpcInteractionsButtonView(mainFrame,0,0);
    }
}

