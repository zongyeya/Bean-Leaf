package com.syp;

public class ItemModel {
    String name;
    String image_path;
    Double price;

    public ItemModel(String name, String image_path, Double price) {
        this.name = name;
        this.image_path = image_path;
        this.price = price;
    }

    public String get_name() {
        return name;
    }

    public String get_image_path() {
        return image_path;
    }

    public String get_price() {
        return Double.toString(price);
    }
}
