package data_access;

import com.google.gson.JsonObject;
import entity.NPC;
import entity.Player;

import java.util.Map;

public class ProgressFileUserDataObject {

    public void JsonFileWriter(Player player){

        JsonObject playerData = new JsonObject();
        playerData.addProperty("name", player.getName());
        playerData.addProperty("balance", player.getBalance());

    }
}
