package de.zwisler.cfvis.window;

import de.zwisler.cfvis.dto.VehiclePositionDto;
import lombok.Getter;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;

@Getter
public class CarWaypoint extends DefaultWaypoint {

    private final VehiclePositionDto vehiclePositionDto;

    public CarWaypoint(VehiclePositionDto vehiclePositionDto) {
        super(new GeoPosition(vehiclePositionDto.getLat(), vehiclePositionDto.getLon()));
        this.vehiclePositionDto = vehiclePositionDto;
    }

}
