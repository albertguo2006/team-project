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
                Item item = new Item(itemData.getString("name"), itemData.getString("description"),
                        itemData.getString("type"), itemData.getInt("score"));
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


