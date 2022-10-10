package de.zwisler.cfvis.dao;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VehicleTimesDao {

    private Cache<String, List<Long>> cache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMillis(1000))
            .build();

    public List<Long> getTimes() {
        List<Long> cachedValue = cache.getIfPresent("");
        if (Objects.nonNull(cachedValue)) {
            return cachedValue;
        }
        String SQL_QUERY = "SELECT DISTINCT timestamp FROM vehicle";
        try (
                Connection con = DataSource.getConnection();
                PreparedStatement pst = con.prepareStatement(SQL_QUERY);
                ResultSet rs = pst.executeQuery();
        ) {
            List<Long> result = new ArrayList<>();
            while (rs.next()) {
                result.add(rs.getLong("timestamp"));
            }
            cache.put("", result);
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
