package view;

import data_access.NPCDataAccessObject;
import entity.NPC;
import interface_adapter.events.npc_interactions.*;
import use_case.npc_interactions.NpcInteractionsUserDataAccessInterface;

import javax.swing.*;
import java.awt.*;

public class NpcInteractionsView {

    public static void show(NpcInteractionsController controller,
                            NpcInteractionsViewModel viewModel,
                            NpcInteractionsUserDataAccessInterface dataAccess) {
        NPC npc = ((NPCDataAccessObject) dataAccess).getRandomNpc();
        if (npc == null) {
            JOptionPane.showMessageDialog(null, "No NPCs found!");
            return;
        }
        String npcName = npc.getName();

        JFrame frame = new JFrame("Chat with " + npcName);
        frame.setSize(400, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        JTextField inputField = new JTextField(25);
        JButton sendBtn = new JButton("Send");

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(inputField, BorderLayout.CENTER);
        panel.add(sendBtn, BorderLayout.EAST);

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(panel, BorderLayout.SOUTH);

        viewModel.setListener(() -> {
            simulateTyping(chatArea, viewModel.getNpcName(), viewModel.getAiResponse());
        });

        sendBtn.addActionListener(e -> {
            String message = inputField.getText();
            inputField.setText("");
            controller.handleUserMessage(npcName, message);
        });

        frame.setVisible(true);
    }

    // simulates the typing by letter (small detail that can/cannot be added)
    private static void simulateTyping(JTextArea chatArea, String speaker, String message) {
        final String fullMessage = speaker + ": " + message + "\n";
        Timer timer = new Timer(30, null); // 30 ms per character
        final int[] index = {0};

        timer.addActionListener(e -> {
            if (index[0] < fullMessage.length()) {
                chatArea.append(String.valueOf(fullMessage.charAt(index[0])));
                chatArea.setCaretPosition(chatArea.getDocument().getLength());
                index[0]++;
            } else {
                timer.stop();
            }
        });

        timer.start();
    }
}