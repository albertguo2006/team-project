package view;

import interface_adapter.save_progress.SaveProgressViewModel;

import javax.swing.*;
import java.awt.*;

public class SaveGameView extends JPanel {
    private static final Color BACKGROUND_COLOR = Color.BLACK;
    private static final Color TITLE_COLOR = new Color(255, 215, 0);
    private static final Color SUBTITLE_COLOR = Color.WHITE;

    SaveProgressViewModel saveProgressViewModel;

    SaveGameView(SaveProgressViewModel saveProgressViewModel) {
        this.saveProgressViewModel = saveProgressViewModel;

        setBackground(BACKGROUND_COLOR);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(Box.createVerticalGlue());

        // Add Game Title
        JLabel title = new JLabel("Sims Knockoff (Recession Edition)", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 64));
        title.setForeground(TITLE_COLOR);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(title);

        // Add space between title and Pixel Text
        add(Box.createVerticalStrut(40));

        // Add Saving Game Pixel Text
        ImageIcon savingImage = new ImageIcon(getClass().getClassLoader().getResource("view_images/saving_game_text.png"));
        int imageWidth = savingImage.getIconWidth() / 2;
        int imageHeight = savingImage.getIconHeight() / 2;
        Image scaledSavingTextImage = savingImage.getImage().getScaledInstance(imageWidth, imageHeight, Image.SCALE_DEFAULT);
        ImageIcon scaledImageIcon = new ImageIcon(scaledSavingTextImage);
        JLabel savingText = new JLabel(scaledImageIcon);
        savingText.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(savingText);

        // Add space between Pixel Text and GIF
        add(Box.createVerticalStrut(20));

        // Loading in Saving GIF from resources
        ImageIcon gifIcon = new ImageIcon(getClass().getClassLoader().getResource("view_images/saving.gif"));
        int gifWidth = 200;
        int gifHeight = 200;
        Image scaledImage = gifIcon.getImage().getScaledInstance(gifWidth, gifHeight, Image.SCALE_DEFAULT);
        ImageIcon scaledGifIcon = new ImageIcon(scaledImage);

        JLabel savingGIF = new JLabel(scaledGifIcon);
        savingGIF.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(savingGIF);

        // Add gap between GIF and saving message
        add(Box.createVerticalStrut(40));

        // Add randomized Loading Messages from ViewModel
        JLabel loadingMessage = new JLabel(saveProgressViewModel.getSavingMessage(), SwingConstants.CENTER);
        loadingMessage.setFont(new Font("Arial", Font.PLAIN, 26));
        loadingMessage.setForeground(SUBTITLE_COLOR);
        loadingMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(loadingMessage);

        add(Box.createVerticalGlue());
    }
}

