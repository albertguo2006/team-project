import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StockGameSwing {

    private JFrame frame;
    private JLabel priceLabel;
    private JLabel arrowLabel;

    private double lastPrice;

    public StockGameSwing(List<Double> realPrices) {

        PriceSimulator simulator = new PriceSimulator(realPrices);
        lastPrice = realPrices.get(0);

        // --- UI Setup ---
        frame = new JFrame("Stock Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);

        priceLabel = new JLabel("Price: " + lastPrice, SwingConstants.CENTER);
        priceLabel.setFont(new Font("Arial", Font.BOLD, 24));

        arrowLabel = new JLabel("—", SwingConstants.CENTER);
        arrowLabel.setFont(new Font("Arial", Font.BOLD, 40));

        frame.setLayout(new BorderLayout());
        frame.add(priceLabel, BorderLayout.CENTER);
        frame.add(arrowLabel, BorderLayout.SOUTH);

        frame.setVisible(true);

        // --- Swing Timer: update every 1 second ---
        new Timer(1000, e -> {
            double price = simulator.nextPrice();

            priceLabel.setText(String.format("Price: %.2f", price));

            if (price > lastPrice) {
                arrowLabel.setText("▲");
                arrowLabel.setForeground(Color.GREEN);
            } else if (price < lastPrice) {
                arrowLabel.setText("▼");
                arrowLabel.setForeground(Color.RED);
            } else {
                arrowLabel.setText("—");
                arrowLabel.setForeground(Color.BLACK);
            }

            lastPrice = price;
        }).start();
    }


    public static void main(String[] args) throws Exception {

        // Load your real prices from the JSON file
        List<Double> opens =
                AlphaStockDataBase.getIntradayOpensForDayIndex("VOO.json", 1);

        SwingUtilities.invokeLater(() -> new StockGameSwing(opens));
    }
}
