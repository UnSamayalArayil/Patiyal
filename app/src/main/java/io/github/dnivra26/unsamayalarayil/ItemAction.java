package io.github.dnivra26.unsamayalarayil;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


public class ItemAction {
    String itemName;
    String action;
    String phoneNumber;
    String lattitude;
    String longitude;

    public ItemAction(String itemName, String action, String phoneNumber, String lattitude, String longitude) {
        this.itemName = itemName;
        this.action = action;
        this.phoneNumber = phoneNumber;
        this.lattitude = lattitude;
        this.longitude = longitude;
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

    public String getLattitude() {
        return lattitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLattitude(String lattitude) {
        this.lattitude = lattitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
