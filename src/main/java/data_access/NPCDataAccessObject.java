// still needs to fix (temp commented out)


package data_access;

import entity.NPC;
import org.json.JSONArray;
import org.json.JSONObject;
import use_case.npc_interactions.NpcInteractionsUserDataAccessInterface;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


import static data_access.LoadFileUserDataAccessObject.JSONFileReader;


public class NPCDataAccessObject implements NpcInteractionsUserDataAccessInterface {
    private static final String NPC_FILE = "src/main/resources/npc.json";
    private Map<String, NPC> allNpcs;

    public NPCDataAccessObject() {
        this.allNpcs = loadNpcsFromJson("src/main/resources/npc_prompts.json");
    }

    @Override
    public Map<String, NPC> getAllNpcs() {
        return allNpcs;
    }

    public NPC getRandomNpc() {
        if (allNpcs.isEmpty()) return null;
        Object[] values = allNpcs.values().toArray();
        return (NPC) values[new Random().nextInt(values.length)];
    }

    public java.util.List<NPC> getNPCsInZone(String zoneName) {
        java.util.List<NPC> npcsInZone = new java.util.ArrayList<>();
        for (NPC npc : allNpcs.values()) {
            if (npc.getLocation().equals(zoneName)) {
                npcsInZone.add(npc);
            }
        }
        return npcsInZone;
    }

    private Map<String, NPC> loadNpcsFromJson(String filePath) {
        Map<String, NPC> npcMap = new HashMap<>();
        try {
            String content = Files.readString(Paths.get(filePath));
            JSONArray npcArray = new JSONArray(content);

            for (int i = 0; i < npcArray.length(); i++) {
                JSONObject obj = npcArray.getJSONObject(i);
                String name = obj.keys().next(); // get the first (and only) key
                String dialogue = obj.getString(name);

                // Assign location based on NPC personality
                String location = assignLocationByNpc(name);

                // Create NPC object
                NPC npc = new NPC(
                        name,
                        dialogue,
                        location,
                        0.0,                 // starting cash
                        0                    // default relationship score
                ); // NPC cash is not implemented and default relationship score is also not implmented (its too complex)

                // Assign position based on NPC name (arbitrary positions)
                assignNpcPosition(npc, name);

                npcMap.put(name, npc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return npcMap;
    }

    /**
     * Assigns a location to an NPC based on their personality and name.
     * @param npcName The name of the NPC
     * @return The zone name where the NPC should be located
     */
    private String assignLocationByNpc(String npcName) {
        switch (npcName) {
            case "Bob":
                return "Office Lobby"; // Bureaucrat in formal office setting
            case "Danny":
                return "Office (Your Cubicle)"; // TA helping with work/code
            case "Sebestian":
                return "Subway Station 1"; // Eccentric character in public transit
            case "Sir Maximilian Alexander Percival Ignatius Thaddeus Montgomery-Worthington III, Esquire of the Grand Order of the Silver Falcon":
                return "Grocery Store"; // Aristocrat inspecting goods with disdain
            case "Sophia":
                return "Street 2"; // Social learner in a meeting area
            default:
                return "Home"; // Default fallback location
        }
    }

    /**
     * Assigns arbitrary x,y positions to NPCs based on their name.
     * These positions can be adjusted later as needed.
     * @param npc The NPC to position
     * @param npcName The name of the NPC
     */
    private void assignNpcPosition(NPC npc, String npcName) {
        switch (npcName) {
            case "Bob":
                npc.setX(960);  // Center of lobby area
                npc.setY(600);
                break;
            case "Danny":
                npc.setX(1100);  // Near desk, avoiding stock trading zone
                npc.setY(400);
                break;
            case "Sebestian":
                npc.setX(960);  // Center platform
                npc.setY(700);
                break;
            case "Sir Maximilian Alexander Percival Ignatius Thaddeus Montgomery-Worthington III, Esquire of the Grand Order of the Silver Falcon":
                npc.setX(960);  // Center aisle
                npc.setY(1100);
                break;
            case "Sophia":
                npc.setX(700);  // Street corner
                npc.setY(600);
                break;
            default:
                npc.setX(800);  // Default position
                npc.setY(500);
                break;
        }
    }


    // -- ignore (to be changed)
    private final Map<String, NPC> npcs = new HashMap<>();
    //

    // temporary
//    @Override
//    public NPC getNpcByName(String name) {
//        return npcs.get(name);
//    }
//
//    @Override
//    public void saveNpc(NPC npc) {
//        npcs.put(npc.getName(), npc);
//    }
}
