package interface_adapter.events;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

import entity.Item;
import entity.NPC;
import entity.Player;
import entity.WorldItem;
import use_case.Direction;
import use_case.PlayerMovementUseCase;

/**
 * PlayerInputController translates raw keyboard input into movement commands.
 *
 * Responsibilities:
 * - Listens for key press/release events (implements KeyListener)
 * - Translates keyboard events (WASD) into domain concepts (Direction enum)
 * - Updates the PlayerMovementUseCase movement state
 * - Handles special keys (ESC to pause/show menu)
 * - Acts as a bridge between Swing input events and the application layer
 *
 * This controller follows Clean Architecture by keeping presentation concerns
 * (KeyListener) separate from business logic (PlayerMovementUseCase).
 */
public class PlayerInputController implements KeyListener {
    
    /**
     * Callback interface for pause menu events.
     */
    public interface PauseMenuListener {
        void onPauseRequested();
    }
    
    /**
     * Callback interface for sleep zone checks.
     */
    public interface SleepZoneChecker {
        boolean isInSleepZone();
    }
    
    /**
     * Callback interface for sleep actions.
     */
    public interface SleepActionListener {
        void onSleepRequested();
    }
    
    /**
     * Callback interface for stock trading zone checks.
     */
    public interface StockTradingZoneChecker {
        boolean isInStockTradingZone();
    }
    
    /**
     * Callback interface for stock trading actions.
     */
    public interface StockTradingActionListener {
        void onStockTradingRequested();
    }
    
    /**
     * Callback interface for mailbox zone checks.
     */
    public interface MailboxZoneChecker {
        boolean isInMailboxZone();
    }
    
    /**
     * Callback interface for mailbox actions.
     */
    public interface MailboxActionListener {
        void onMailboxRequested();
    }

    /**
     * Callback interface for NPC proximity checks.
     */
    public interface NPCInteractionChecker {
        NPC getNearbyNPC();
    }

    /**
     * Callback interface for NPC interaction actions.
     */
    public interface NPCInteractionListener {
        void onNPCInteractionRequested(NPC npc);
    }

    /**
     * Callback interface for inventory slot selection.
     */
    public interface InventorySlotSelector {
        void setSelectedInventorySlot(int slot);
        int getSelectedInventorySlot();
    }

    /**
     * Callback interface for world item proximity checks.
     */
    public interface WorldItemChecker {
        WorldItem getNearbyWorldItem();
    }

    /**
     * Callback interface for world item collection/purchase.
     */
    public interface WorldItemActionListener {
        void onWorldItemCollected(WorldItem worldItem);
    }

    /**
     * Callback interface for using inventory items.
     */
    public interface InventoryUseListener {
        void onInventoryItemUsed(int slotIndex);
    }

    /**
     * Callback interface for dropping inventory items.
     */
    public interface InventoryDropListener {
        void onInventoryItemDropped(int slotIndex);
    }

    private final PlayerMovementUseCase playerMovementUseCase;
    private JFrame parentFrame;  // Optional: for frame reference
    private PauseMenuListener pauseMenuListener;
    private SleepZoneChecker sleepZoneChecker;
    private SleepActionListener sleepActionListener;
    private StockTradingZoneChecker stockTradingZoneChecker;
    private StockTradingActionListener stockTradingActionListener;
    private MailboxZoneChecker mailboxZoneChecker;
    private MailboxActionListener mailboxActionListener;
    private NPCInteractionChecker npcInteractionChecker;
    private NPCInteractionListener npcInteractionListener;
    private InventorySlotSelector inventorySlotSelector;
    private WorldItemChecker worldItemChecker;
    private WorldItemActionListener worldItemActionListener;
    private InventoryUseListener inventoryUseListener;
    private InventoryDropListener inventoryDropListener;

    /**
     * Constructs a PlayerInputController with the given use case.
     * 
     * @param playerMovementUseCase the use case to delegate movement commands to
     */
    public PlayerInputController(PlayerMovementUseCase playerMovementUseCase) {
        this.playerMovementUseCase = playerMovementUseCase;
    }
    
    /**
     * Sets the parent frame reference.
     *
     * @param frame the parent JFrame
     */
    public void setParentFrame(JFrame frame) {
        this.parentFrame = frame;
    }
    
    /**
     * Sets the pause menu listener for ESC key handling.
     *
     * @param listener the pause menu listener
     */
    public void setPauseMenuListener(PauseMenuListener listener) {
        this.pauseMenuListener = listener;
    }
    
    /**
     * Sets the sleep zone checker callback.
     *
     * @param checker the sleep zone checker
     */
    public void setSleepZoneChecker(SleepZoneChecker checker) {
        this.sleepZoneChecker = checker;
    }
    
    /**
     * Sets the sleep action listener callback.
     *
     * @param listener the sleep action listener
     */
    public void setSleepActionListener(SleepActionListener listener) {
        this.sleepActionListener = listener;
    }
    
    /**
     * Sets the stock trading zone checker callback.
     *
     * @param checker the stock trading zone checker
     */
    public void setStockTradingZoneChecker(StockTradingZoneChecker checker) {
        this.stockTradingZoneChecker = checker;
    }
    
    /**
     * Sets the stock trading action listener callback.
     *
     * @param listener the stock trading action listener
     */
    public void setStockTradingActionListener(StockTradingActionListener listener) {
        this.stockTradingActionListener = listener;
    }
    
    /**
     * Sets the mailbox zone checker callback.
     *
     * @param checker the mailbox zone checker
     */
    public void setMailboxZoneChecker(MailboxZoneChecker checker) {
        this.mailboxZoneChecker = checker;
    }
    
    /**
     * Sets the mailbox action listener callback.
     *
     * @param listener the mailbox action listener
     */
    public void setMailboxActionListener(MailboxActionListener listener) {
        this.mailboxActionListener = listener;
    }

    /**
     * Sets the NPC interaction checker callback.
     *
     * @param checker the NPC interaction checker
     */
    public void setNPCInteractionChecker(NPCInteractionChecker checker) {
        this.npcInteractionChecker = checker;
    }

    /**
     * Sets the NPC interaction listener callback.
     *
     * @param listener the NPC interaction listener
     */
    public void setNPCInteractionListener(NPCInteractionListener listener) {
        this.npcInteractionListener = listener;
    }

    /**
     * Sets the inventory slot selector callback.
     *
     * @param selector the inventory slot selector
     */
    public void setInventorySlotSelector(InventorySlotSelector selector) {
        this.inventorySlotSelector = selector;
    }

    /**
     * Sets the world item checker callback.
     *
     * @param checker the world item checker
     */
    public void setWorldItemChecker(WorldItemChecker checker) {
        this.worldItemChecker = checker;
    }

    /**
     * Sets the world item action listener callback.
     *
     * @param listener the world item action listener
     */
    public void setWorldItemActionListener(WorldItemActionListener listener) {
        this.worldItemActionListener = listener;
    }

    /**
     * Sets the inventory use listener callback.
     *
     * @param listener the inventory use listener
     */
    public void setInventoryUseListener(InventoryUseListener listener) {
        this.inventoryUseListener = listener;
    }

    /**
     * Sets the inventory drop listener callback.
     *
     * @param listener the inventory drop listener
     */
    public void setInventoryDropListener(InventoryDropListener listener) {
        this.inventoryDropListener = listener;
    }

    /**
     * Called when a key is pressed.
     * Translates WASD keys to Direction commands and notifies the use case.
     * 
     * @param e the KeyEvent generated by the keyboard
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        
        // Handle ESC to show pause menu (or exit if no listener)
        if (keyCode == KeyEvent.VK_ESCAPE) {
            if (pauseMenuListener != null) {
                pauseMenuListener.onPauseRequested();
            } else if (parentFrame != null) {
                // Fallback: exit if no pause listener set
                parentFrame.dispose();
                System.exit(0);
            }
            return;
        }
        
        switch (keyCode) {
            case KeyEvent.VK_W:
                playerMovementUseCase.setMovementState(Direction.UP, true);
                break;
            case KeyEvent.VK_A:
                playerMovementUseCase.setMovementState(Direction.LEFT, true);
                break;
            case KeyEvent.VK_S:
                playerMovementUseCase.setMovementState(Direction.DOWN, true);
                break;
            case KeyEvent.VK_D:
                playerMovementUseCase.setMovementState(Direction.RIGHT, true);
                break;

            // Inventory slot selection (1-5 keys)
            case KeyEvent.VK_1:
                if (inventorySlotSelector != null) {
                    int current = inventorySlotSelector.getSelectedInventorySlot();
                    inventorySlotSelector.setSelectedInventorySlot(current == 0 ? -1 : 0);
                }
                break;
            case KeyEvent.VK_2:
                if (inventorySlotSelector != null) {
                    int current = inventorySlotSelector.getSelectedInventorySlot();
                    inventorySlotSelector.setSelectedInventorySlot(current == 1 ? -1 : 1);
                }
                break;
            case KeyEvent.VK_3:
                if (inventorySlotSelector != null) {
                    int current = inventorySlotSelector.getSelectedInventorySlot();
                    inventorySlotSelector.setSelectedInventorySlot(current == 2 ? -1 : 2);
                }
                break;
            case KeyEvent.VK_4:
                if (inventorySlotSelector != null) {
                    int current = inventorySlotSelector.getSelectedInventorySlot();
                    inventorySlotSelector.setSelectedInventorySlot(current == 3 ? -1 : 3);
                }
                break;
            case KeyEvent.VK_5:
                if (inventorySlotSelector != null) {
                    int current = inventorySlotSelector.getSelectedInventorySlot();
                    inventorySlotSelector.setSelectedInventorySlot(current == 4 ? -1 : 4);
                }
                break;

            case KeyEvent.VK_E:
                // Handle NPC interaction if near an NPC (highest priority)
                if (npcInteractionChecker != null && npcInteractionListener != null) {
                    NPC nearbyNPC = npcInteractionChecker.getNearbyNPC();
                    if (nearbyNPC != null) {
                        npcInteractionListener.onNPCInteractionRequested(nearbyNPC);
                        break;
                    }
                }
                // Handle world item pickup/purchase if near an item
                if (worldItemChecker != null && worldItemActionListener != null) {
                    WorldItem nearbyItem = worldItemChecker.getNearbyWorldItem();
                    if (nearbyItem != null) {
                        worldItemActionListener.onWorldItemCollected(nearbyItem);
                        break;
                    }
                }
                // Handle sleep action if in sleep zone
                if (sleepZoneChecker != null && sleepZoneChecker.isInSleepZone() &&
                    sleepActionListener != null) {
                    sleepActionListener.onSleepRequested();
                }
                // Handle stock trading action if in stock trading zone
                else if (stockTradingZoneChecker != null && stockTradingZoneChecker.isInStockTradingZone() &&
                         stockTradingActionListener != null) {
                    stockTradingActionListener.onStockTradingRequested();
                }
                // Handle mailbox action if in mailbox zone
                else if (mailboxZoneChecker != null && mailboxZoneChecker.isInMailboxZone() &&
                         mailboxActionListener != null) {
                    mailboxActionListener.onMailboxRequested();
                }
                // Handle inventory item use if a slot is selected
                else if (inventorySlotSelector != null && inventoryUseListener != null) {
                    int selectedSlot = inventorySlotSelector.getSelectedInventorySlot();
                    if (selectedSlot >= 0) {
                        inventoryUseListener.onInventoryItemUsed(selectedSlot);
                    }
                }
                break;

            case KeyEvent.VK_Q:
                // Drop the currently selected inventory item
                if (inventorySlotSelector != null && inventoryDropListener != null) {
                    int selectedSlot = inventorySlotSelector.getSelectedInventorySlot();
                    if (selectedSlot >= 0) {
                        inventoryDropListener.onInventoryItemDropped(selectedSlot);
                    }
                }
                break;
        }
    }
    
    /**
     * Called when a key is released.
     * Translates WASD key releases to Direction commands and notifies the use case.
     * 
     * @param e the KeyEvent generated by the keyboard
     */
    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        
        switch (keyCode) {
            case KeyEvent.VK_W:
                playerMovementUseCase.setMovementState(Direction.UP, false);
                break;
            case KeyEvent.VK_A:
                playerMovementUseCase.setMovementState(Direction.LEFT, false);
                break;
            case KeyEvent.VK_S:
                playerMovementUseCase.setMovementState(Direction.DOWN, false);
                break;
            case KeyEvent.VK_D:
                playerMovementUseCase.setMovementState(Direction.RIGHT, false);
                break;
        }
    }
    
    /**
     * Called when a key is typed (pressed and released).
     * Not used for movement. keyPressed and keyReleased are sufficient.
     *
     * @param e the KeyEvent generated by the keyboard
     */
    @Override
    public void keyTyped(KeyEvent e) {
        // Not used for movement. keyPressed and keyReleased are sufficient.
    }

    /**
     * Updates the player movement use case (used when loading a saved game).
     * Note: This doesn't update the use case instance, but rather updates the player
     * within the existing use case would be preferred, but this provides a workaround
     * for the load game feature.
     *
     * @param newPlayerMovementUseCase the new player movement use case
     */
    public void updatePlayerMovementUseCase(PlayerMovementUseCase newPlayerMovementUseCase) {
        // Note: Since playerMovementUseCase is final, we can't replace it.
        // The load game feature should instead update the player within the existing use case.
        // This method is here to satisfy the interface but won't work with the current design.
        // The proper solution would be to add a setPlayer method to PlayerMovementUseCase.
    }
}
