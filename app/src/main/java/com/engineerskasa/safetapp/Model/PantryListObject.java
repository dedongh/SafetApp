package com.engineerskasa.safetapp.Model;

public class PantryListObject {
    private String itemName, category, quantity, days_left_to_expire, expiration_date,
    mode_of_preservation, description, quantity_threshold, unit_price, item_image,
    unit, exp_notice;

    public PantryListObject() {
    }

    public PantryListObject(String itemName, String category, String quantity, String days_left_to_expire,
                            String expiration_date, String mode_of_preservation, String description,
                            String quantity_threshold, String unit_price, String item_image, String unit, String exp_notice) {
        this.itemName = itemName;
        this.category = category;
        this.quantity = quantity;
        this.days_left_to_expire = days_left_to_expire;
        this.expiration_date = expiration_date;
        this.mode_of_preservation = mode_of_preservation;
        this.description = description;
        this.quantity_threshold = quantity_threshold;
        this.unit_price = unit_price;
        this.item_image = item_image;
        this.unit = unit;
        this.exp_notice = exp_notice;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDays_left_to_expire() {
        return days_left_to_expire;
    }

    public void setDays_left_to_expire(String days_left_to_expire) {
        this.days_left_to_expire = days_left_to_expire;
    }

    public String getExpiration_date() {
        return expiration_date;
    }

    public void setExpiration_date(String expiration_date) {
        this.expiration_date = expiration_date;
    }

    public String getMode_of_preservation() {
        return mode_of_preservation;
    }

    public void setMode_of_preservation(String mode_of_preservation) {
        this.mode_of_preservation = mode_of_preservation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQuantity_threshold() {
        return quantity_threshold;
    }

    public void setQuantity_threshold(String quantity_threshold) {
        this.quantity_threshold = quantity_threshold;
    }

    public String getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(String unit_price) {
        this.unit_price = unit_price;
    }

    public String getItem_image() {
        return item_image;
    }

    public void setItem_image(String item_image) {
        this.item_image = item_image;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getExp_notice() {
        return exp_notice;
    }

    public void setExp_notice(String exp_notice) {
        this.exp_notice = exp_notice;
    }
}
