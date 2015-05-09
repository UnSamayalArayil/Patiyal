package io.github.dnivra26.unsamayalarayil;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


public class Item {
    public final String device_id;
    public final String name;
    public final String current_percentage;
    public Item(String device_id, String name, String currentPercentage){
        this.device_id = device_id;
        this.name = name;
        this.current_percentage = currentPercentage;
    }
}
