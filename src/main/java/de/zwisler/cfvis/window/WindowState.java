package de.zwisler.cfvis.window;

import lombok.Data;
import lombok.Value;

import java.util.Collections;
import java.util.Set;

@Data
public class WindowState{
    Set<CarWaypoint> waypoints = Collections.emptySet();
    long time;
    long speed = 15000;
    boolean playing = false;
    boolean renderPlate = false;
    boolean renderTravelInfo = false;

    public WindowStateSnapshot snapshot(){
        return new WindowStateSnapshot(
                Collections.unmodifiableSet(getWaypoints()),
                getTime(),
                getSpeed(),
                isPlaying(),
                isRenderPlate(),
                isRenderTravelInfo()
        );
    }

    @Value
    public static class WindowStateSnapshot {
        Set<CarWaypoint> waypoints;
        long time;
        long speed;
        boolean playing;
        boolean renderPlate;
        boolean renderTravelInfo;
    }

}
