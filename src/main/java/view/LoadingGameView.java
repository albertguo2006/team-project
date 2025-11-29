package view;

import interface_adapter.load_progress.LoadProgressViewModel;

import javax.swing.*;
import java.awt.*;

public class LoadingGameView extends JPanel {
    private static final Color BACKGROUND_COLOR = Color.BLACK;
    private static final Color TITLE_COLOR = new Color(255, 215, 0);
    private static final Color SUBTITLE_COLOR = Color.WHITE;

    private LoadProgressViewModel loadProgressViewModel;

    public LoadingGameView(LoadProgressViewModel loadProgressViewModel) {
        this.loadProgressViewModel = loadProgressViewModel;

        setBackground(BACKGROUND_COLOR);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(Box.createVerticalGlue());

        // Add Game Title
        JLabel title = new JLabel("Sims Knockoff (Recession Edition)", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 48));
        title.setForeground(TITLE_COLOR);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(title);


        // Loading in GIF from resources
        ImageIcon gifIcon = new ImageIcon(getClass().getClassLoader().getResource("gifs/loading.gif"));
        int gifWidth = 300;
        int gifHeight = 300;
        Image scaledImage = gifIcon.getImage().getScaledInstance(gifWidth, gifHeight, Image.SCALE_DEFAULT);
        ImageIcon scaledGifIcon = new ImageIcon(scaledImage);

        JLabel loadingGIF = new JLabel(scaledGifIcon);
        loadingGIF.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(loadingGIF);

        // Add randomized Loading Messages from ViewModel
        JLabel loadingMessage = new JLabel(loadProgressViewModel.getloadingMessage(), SwingConstants.CENTER);
        loadingMessage.setFont(new Font("Arial", Font.PLAIN, 26));
        loadingMessage.setForeground(SUBTITLE_COLOR);
        loadingMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(loadingMessage);

        add(Box.createVerticalGlue());
    }

    public static void main(String[] args) {

        // Testing Splash Screen
        JFrame frame = new JFrame("Loading Splash Screen Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 800);
        frame.setLocationRelativeTo(null);

        LoadProgressViewModel loadProgressViewModel = new LoadProgressViewModel();

        frame.add(new LoadingGameView(loadProgressViewModel));
        frame.setVisible(true);
    }
}
