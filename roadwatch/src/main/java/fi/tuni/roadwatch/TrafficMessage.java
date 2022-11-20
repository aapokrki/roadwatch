package fi.tuni.roadwatch;

import com.sothawo.mapjfx.Coordinate;

import java.util.ArrayList;

public class TrafficMessage {
    String trafficAnnouncementType;
    String title;
    String description;
    ArrayList<Coordinate> coordinates = new ArrayList<>();
    String situation; //features{name:}
    String comment;
    String date;

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setTrafficAnnouncementType(String trafficAnnouncementType) {
        this.trafficAnnouncementType = trafficAnnouncementType;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCoordinates(ArrayList<Coordinate> coordinates) {
        this.coordinates = coordinates;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public void setDate(String dataUpdatedTime) {
        this.date = dataUpdatedTime;
    }
}
