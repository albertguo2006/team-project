package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import entity.GameSettings;

/**
 * SettingsPanel provides a UI for adjusting game settings.
 * 
 * Responsibilities:
 * - Display volume slider control
 * - Display resolution dropdown selector
 * - Allow saving or canceling changes
 * - Maintain reference to game settings
 * 
 * The panel uses the GameSettings entity to read and write configuration.
 */
public class SettingsPanel extends JPanel {
    
    private static final Color BACKGROUND_COLOR = new Color(20, 20, 30);
    private static final Color TITLE_COLOR = new Color(255, 215, 0);  // Gold
    private static final Color LABEL_COLOR = Color.WHITE;
    private static final Color BUTTON_COLOR = new Color(60, 60, 80);
    private static final Color BUTTON_HOVER_COLOR = new Color(80, 80, 120);
    private static final Color BUTTON_TEXT_COLOR = Color.WHITE;
    
    private final GameSettings settings;
    private JSlider volumeSlider;
    private JComboBox<String> resolutionComboBox;
    private JButton saveButton;
    private JButton cancelButton;
    
    // Store original values for cancel operation
    private float originalVolume;
    private int originalResolutionIndex;
    
    /**
     * Constructs the settings panel with the given game settings.
     * @param settings the game settings to display and modify
     */
    public SettingsPanel(GameSettings settings) {
        this.settings = settings;
        this.originalVolume = settings.getMasterVolume();
        this.originalResolutionIndex = settings.getResolutionPresetIndex();
        if (this.originalResolutionIndex < 0) {
            this.originalResolutionIndex = 2;  // Default to Large if custom
        }
        
        setBackground(BACKGROUND_COLOR);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(50, 100, 50, 100));
        
        // Add vertical glue to center content
        add(Box.createVerticalGlue());
        
        // Title
        JLabel titleLabel = createTitleLabel();
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(titleLabel);
        
        add(Box.createRigidArea(new Dimension(0, 60)));
        
        // Volume control section - creates and stores volumeSlider
        JPanel volumePanel = createVolumePanel();
        volumePanel.setAlignmentX(CENTER_ALIGNMENT);
        add(volumePanel);
        
        add(Box.createRigidArea(new Dimension(0, 40)));
        
        // Resolution control section - creates and stores resolutionComboBox
        JPanel resolutionPanel = createResolutionPanel();
        resolutionPanel.setAlignmentX(CENTER_ALIGNMENT);
        add(resolutionPanel);
        
        add(Box.createRigidArea(new Dimension(0, 60)));
        
        // Buttons - creates and stores saveButton and cancelButton
        JPanel buttonPanel = createButtonPanel();
        buttonPanel.setAlignmentX(CENTER_ALIGNMENT);
        add(buttonPanel);
        
        // Add vertical glue at bottom
        add(Box.createVerticalGlue());
        
        // Update controls with current settings
        updateControlsFromSettings();
    }
    
    /**
     * Creates the title label.
     * @return configured title JLabel
     */
    private JLabel createTitleLabel() {
        JLabel label = new JLabel("Settings");
        label.setFont(new Font("Arial", Font.BOLD, 56));
        label.setForeground(TITLE_COLOR);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }
    
    /**
     * Creates the volume control panel with label and slider.
     * Stores the slider in the volumeSlider field.
     * @return panel containing volume controls
     */
    private JPanel createVolumePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setMaximumSize(new Dimension(600, 120));
        
        // Volume label
        JLabel volumeLabel = new JLabel("Master Volume");
        volumeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        volumeLabel.setForeground(LABEL_COLOR);
        volumeLabel.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(volumeLabel);
        
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Volume slider - store reference
        volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
        volumeSlider.setBackground(BACKGROUND_COLOR);
        volumeSlider.setForeground(LABEL_COLOR);
        volumeSlider.setMajorTickSpacing(25);
        volumeSlider.setMinorTickSpacing(5);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);
        volumeSlider.setAlignmentX(LEFT_ALIGNMENT);
        
        // Customize slider labels
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put(0, createSliderLabel("0%"));
        labelTable.put(25, createSliderLabel("25%"));
        labelTable.put(50, createSliderLabel("50%"));
        labelTable.put(75, createSliderLabel("75%"));
        labelTable.put(100, createSliderLabel("100%"));
        volumeSlider.setLabelTable(labelTable);
        
        panel.add(volumeSlider);
        
        return panel;
    }
    
    /**
     * Creates a label for the slider.
     * @param text the label text
     * @return configured JLabel
     */
    private JLabel createSliderLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(LABEL_COLOR);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        return label;
    }
    
    /**
     * Creates the resolution control panel with label and dropdown.
     * Stores the combo box in the resolutionComboBox field.
     * @return panel containing resolution controls
     */
    private JPanel createResolutionPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setMaximumSize(new Dimension(600, 80));
        
        // Resolution label
        JLabel resolutionLabel = new JLabel("Display Resolution");
        resolutionLabel.setFont(new Font("Arial", Font.BOLD, 24));
        resolutionLabel.setForeground(LABEL_COLOR);
        resolutionLabel.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(resolutionLabel);
        
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Resolution combo box - store reference
        String[] resolutionOptions = {
            "Small (1280x800)",
            "Medium (1600x1000)",
            "Large (1920x1200)",
            "Extra Large (2560x1600)"
        };
        
        resolutionComboBox = new JComboBox<>(resolutionOptions);
        resolutionComboBox.setFont(new Font("Arial", Font.PLAIN, 18));
        resolutionComboBox.setMaximumSize(new Dimension(400, 40));
        resolutionComboBox.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(resolutionComboBox);
        
        return panel;
    }
    
    /**
     * Creates the button panel with Save and Cancel buttons.
     * Stores the buttons in saveButton and cancelButton fields.
     * @return panel containing action buttons
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBackground(BACKGROUND_COLOR);
        
        saveButton = createActionButton("Save");
        cancelButton = createActionButton("Cancel");
        
        panel.add(saveButton);
        panel.add(Box.createRigidArea(new Dimension(40, 0)));
        panel.add(cancelButton);
        
        return panel;
    }
    
    /**
     * Creates a styled action button.
     * @param text the button text
     * @return configured JButton
     */
    private JButton createActionButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setForeground(BUTTON_TEXT_COLOR);
        button.setBackground(BUTTON_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        
        Dimension buttonSize = new Dimension(200, 50);
        button.setPreferredSize(buttonSize);
        button.setMinimumSize(buttonSize);
        button.setMaximumSize(buttonSize);
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(BUTTON_HOVER_COLOR);
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(BUTTON_COLOR);
            }
        });
        
        return button;
    }
    
    /**
     * Updates the UI controls to reflect current settings.
     */
    private void updateControlsFromSettings() {
        // Update volume slider
        int volumePercent = (int) (settings.getMasterVolume() * 100);
        volumeSlider.setValue(volumePercent);
        
        // Update resolution combo box
        int presetIndex = settings.getResolutionPresetIndex();
        if (presetIndex >= 0 && presetIndex < resolutionComboBox.getItemCount()) {
            resolutionComboBox.setSelectedIndex(presetIndex);
        } else {
            resolutionComboBox.setSelectedIndex(2);  // Default to Large
        }
    }
    
    /**
     * Applies the current control values to the settings.
     */
    public void applySettings() {
        // Apply volume
        float volume = volumeSlider.getValue() / 100.0f;
        settings.setMasterVolume(volume);
        
        // Apply resolution
        int selectedIndex = resolutionComboBox.getSelectedIndex();
        settings.setResolutionPreset(selectedIndex);
        
        // Update original values
        originalVolume = volume;
        originalResolutionIndex = selectedIndex;
    }
    
    /**
     * Reverts controls to original values (cancels changes).
     */
    public void revertSettings() {
        // Revert settings to original
        settings.setMasterVolume(originalVolume);
        settings.setResolutionPreset(originalResolutionIndex);
        
        // Update controls
        updateControlsFromSettings();
    }
    
    /**
     * Gets the current settings.
     * @return the game settings
     */
    public GameSettings getSettings() {
        return settings;
    }
    
    /**
     * Adds an action listener to the Save button.
     * @param listener the action listener to add
     */
    public void addSaveListener(ActionListener listener) {
        saveButton.addActionListener(listener);
    }
    
    /**
     * Adds an action listener to the Cancel button.
     * @param listener the action listener to add
     */
    public void addCancelListener(ActionListener listener) {
        cancelButton.addActionListener(listener);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Enable anti-aliasing
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }
}