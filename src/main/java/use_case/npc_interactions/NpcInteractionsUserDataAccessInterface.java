package use_case.npc_interactions;

import entity.NPC;
import java.util.Map;

/**
 * Interface for accessing NPC data.
 * Clean Architecture principle: use-case depends only on this interface, not concrete implementation.
 */
public interface NpcInteractionsUserDataAccessInterface {
    Map<String, NPC> getAllNpcs();
}
