package io.github.dnivra26.unsamayalarayil;

public class NewDevice {
    public final String user_id;
    public final String device_id;
    public final String item_name;
    public final int alert_percentage;
    public NewDevice(String user_id, String deviceName, String itemName, int alertPercentage){
        this.user_id = user_id;
        this.device_id = deviceName;
        this.item_name = itemName;
        this.alert_percentage = alertPercentage;
    }
}
