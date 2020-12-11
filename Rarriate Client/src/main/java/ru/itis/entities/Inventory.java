package ru.itis.entities;

import ru.itis.entities.items.AbstractItem;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private List<AbstractItem> items;

    public Inventory() {
        items = new ArrayList<>();
    }

    public void addItem(AbstractItem item) {
        items.add(item);
    }

    public List<AbstractItem> getItems() {
        return items;
    }
}
