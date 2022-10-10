package de.zwisler.cfvis.mapper;

import de.zwisler.cfvis.dto.VehiclePositionDto;
import de.zwisler.cfvis.window.CarWaypoint;

public class WaypointMapper {

    public static CarWaypoint map(VehiclePositionDto vehiclePositionDto) {
        return new CarWaypoint(vehiclePositionDto);
    }

}
