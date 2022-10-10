package de.zwisler.cfvis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class VehiclePositionDto {
    private int id;
    private String plate;
    private String vehicleUID;
    private double lon;
    private double lat;
    private long timestamp;
    private Map<String, String> additionalInfo;
}
