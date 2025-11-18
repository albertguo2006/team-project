package use_case.npc_interactions;

import entity.NPC;

import java.util.HashMap;

public interface NpcInteractionsUserDataAccessInterface {
    HashMap<String, NPC> getNPCMap();
}
