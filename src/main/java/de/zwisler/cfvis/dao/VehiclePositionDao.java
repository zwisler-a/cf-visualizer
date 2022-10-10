package de.zwisler.cfvis.dao;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import de.zwisler.cfvis.dto.VehiclePositionDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VehiclePositionDao {
    private static final String VEHICLES_BEFORE_QUERY = """
            SELECT *
            FROM vehicle s1
            INNER JOIN (
                SELECT vehicleUID, max(timestamp) as mts
                FROM vehicle
                WHERE timestamp <= ?
                GROUP BY vehicleUID
            ) s2 on s2.vehicleUID = s1.vehicleUID AND s2.mts = timestamp
            """;
    private static final String VEHICLES_AFTER_QUERY = """
            SELECT *
            FROM vehicle s1
            INNER JOIN (
                SELECT vehicleUID, min(timestamp) as mts
                FROM vehicle
                WHERE timestamp > ?
                GROUP BY vehicleUID
            ) s2 on s2.vehicleUID = s1.vehicleUID AND s2.mts = timestamp
            """;
    private static final String VEHCILES_QUERY = """
            SELECT * FROM vehicle WHERE timestamp = ?
            """;


    private final Cache<Long, List<VehiclePositionDto>> vehicleCache = Caffeine.newBuilder()
            .maximumWeight(1000)
            .weigher((Long key, List<VehiclePositionDto> value) -> value.size())
            .build();


    List<VehiclePositionDto> getVehiclesRightBefore(long timestamp) {
        try {
            Connection con = DataSource.getConnection();
            PreparedStatement pst = con.prepareStatement(VEHICLES_BEFORE_QUERY);
            pst.setLong(1, timestamp);
            ResultSet rs = pst.executeQuery();
            List<VehiclePositionDto> result = new ArrayList<>();
            while (rs.next()) {
                result.add(toVehiclePosition(rs));
            }
            con.close();
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    List<VehiclePositionDto> getVehiclesRightAfter(long timestamp) {
        try {
            Connection con = DataSource.getConnection();
            PreparedStatement pst = con.prepareStatement(VEHICLES_AFTER_QUERY);
            pst.setLong(1, timestamp);
            ResultSet rs = pst.executeQuery();
            List<VehiclePositionDto> result = new ArrayList<>();
            while (rs.next()) {
                result.add(toVehiclePosition(rs));
            }
            con.close();
            return result;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public List<VehiclePositionDto> getVehiclesAt(long timestamp) {
        List<VehiclePositionDto> cached = vehicleCache.getIfPresent(timestamp);
        if (Objects.nonNull(cached)) return cached;
        try {
            Connection con = DataSource.getConnection();
            PreparedStatement pst = con.prepareStatement(VEHICLES_BEFORE_QUERY);
            pst.setLong(1, timestamp);
            ResultSet rs = pst.executeQuery();
            List<VehiclePositionDto> result = new ArrayList<>();
            while (rs.next()) {
                result.add(toVehiclePosition(rs));
            }
            vehicleCache.put(timestamp, result);
            con.close();
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private VehiclePositionDto toVehiclePosition(ResultSet rs) throws SQLException {
        VehiclePositionDto v = new VehiclePositionDto();
        v.setId(rs.getInt("ID"));
        v.setVehicleUID(rs.getString("vehicleUID"));
        v.setPlate(rs.getString("plate"));
        v.setLon(rs.getDouble("lon"));
        v.setLat(rs.getDouble("lat"));
        v.setTimestamp(rs.getLong("timestamp"));
        return v;
    }


}
