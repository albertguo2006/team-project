package use_case.inventory;

import entity.Item;

import java.util.HashMap;

public interface ItemDataAccessInterface {
    HashMap<String, Item> getItemMap();
}


