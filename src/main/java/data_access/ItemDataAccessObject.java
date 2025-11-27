package data_access;

import entity.Item;
import org.json.JSONArray;
import org.json.JSONObject;
import use_case.inventory.ItemDataAccessInterface;
import use_case.load_progress.LoadProgressDataAccessInterface;

import java.io.IOException;
import java.util.HashMap;

import static data_access.LoadFileUserDataAccessObject.JSONFileReader;

public class ItemDataAccessObject implements ItemDataAccessInterface {
    public static final String ITEM_FILE = "src/main/resources/items.json";

    @Override
    public HashMap<String, Item> getItemMap() {
        try{
            JSONArray data = JSONFileReader(ITEM_FILE);
            HashMap<String, Item> items = new HashMap<>();
            for (int i = 0; i < data.length(); i++) {
                JSONObject itemData = data.getJSONObject(i);
                String name = itemData.getString("name");
                String description = itemData.getString("description");
                String type = itemData.getString("type");
                int score = itemData.getInt("score");
                int price = itemData.optInt("price", 0);
                boolean isConsumable = itemData.optBoolean("isConsumable", true);
                String buffType = itemData.isNull("buffType") ? null : itemData.optString("buffType", null);

                Item item = new Item(name, description, type, score, price, isConsumable, buffType);
                items.put(item.getName(), item);
            }
            return items;
        }
        catch (IOException e){
            throw new RuntimeException();
        }
    }

    public static void main(String[] args) {
        ItemDataAccessObject itemDataAccessObject = new ItemDataAccessObject();
        HashMap<String, Item> ItemMap = itemDataAccessObject.getItemMap();
    }
}


