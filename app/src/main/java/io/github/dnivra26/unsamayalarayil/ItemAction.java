package io.github.dnivra26.unsamayalarayil;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


public class ItemAction {
    String itemName;
    String action;
    String phoneNumber;

    public ItemAction(String itemName, String action, String phoneNumber) {
        this.itemName = itemName;
        this.action = action;
        this.phoneNumber = phoneNumber;
    }

    public String getItemName() {
        return itemName;
    }

    public String getAction() {
        return action;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
