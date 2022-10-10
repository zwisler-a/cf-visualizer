package de.zwisler.cfvis.dao;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import de.zwisler.cfvis.dto.VehiclePositionDto;
import de.zwisler.cfvis.util.Tuple;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class InterpolatedVehiclePositionDao {
    private final VehicleTimesDao vehicleTimesDao;
    private final VehiclePositionDao vehiclePositionDao;
    private final Cache<Tuple<Long, Integer>, Tuple<List<VehiclePositionDto>, List<VehiclePositionDto>>>
            cache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMillis(10000))
            .build();

    private final Set<Tuple<Long, Integer>> openPrefetches = ConcurrentHashMap.newKeySet();

    private final ThreadPoolExecutor executor =
            (ThreadPoolExecutor) Executors.newFixedThreadPool(4);

    public InterpolatedVehiclePositionDao() {
        this.vehicleTimesDao = new VehicleTimesDao();
        this.vehiclePositionDao = new VehiclePositionDao();
    }


    public List<VehiclePositionDto> getVehiclesAt(long timestamp) {
        var start = System.currentTimeMillis();
        Tuple<Long, Integer> cacheKey = getCacheKey(timestamp);
        var cacheValue = cache.getIfPresent(cacheKey);

        List<VehiclePositionDto> vStart;
        List<VehiclePositionDto> vEnd;
        if (Objects.isNull(cacheValue)) {
            System.out.println("Cache miss on " + cacheKey);
            vStart = vehiclePositionDao.getVehiclesRightBefore(timestamp);
            vEnd = vehiclePositionDao.getVehiclesRightAfter(timestamp);
            System.out.println("Fetching took " + (System.currentTimeMillis() - start) + "ms.");
            cache.put(cacheKey, new Tuple<>(vStart, vEnd));
        } else {
            vStart = cacheValue.getLeft();
            vEnd = cacheValue.getRight();
            preemptiveNextVehiclesCaching(cacheKey);
        }


        Map<String, Tuple<VehiclePositionDto, VehiclePositionDto>> vehicles = new HashMap<>();
        vStart.forEach(v -> vehicles.put(v.getVehicleUID(), new Tuple<>(v, null)));
        vEnd.forEach(v -> {
            Tuple<VehiclePositionDto, VehiclePositionDto> p = vehicles.getOrDefault(v.getVehicleUID(), new Tuple<>(null, null));
            p.setRight(v);
            vehicles.put(v.getVehicleUID(), p);
        });

        List<VehiclePositionDto> result = new ArrayList<>();
        vehicles.forEach((s, t) -> result.add(interpolateVehiclePos(t.getLeft(), t.getRight(), timestamp)));
        return result;
    }

    private void preemptiveNextVehiclesCaching(Tuple<Long, Integer> cacheKey) {
        var start = System.currentTimeMillis();
        if (openPrefetches.contains(cacheKey)) {
            return;
        }
        ;
        openPrefetches.add(cacheKey);
        executor.submit(() -> {
            Long nextTime = vehicleTimesDao.getTimes().get(cacheKey.getRight() + 5);
            Tuple<Long, Integer> nextCacheKey = getCacheKey(nextTime);
            if (Objects.isNull(cache.getIfPresent(nextCacheKey))) {
                var vStart = vehiclePositionDao.getVehiclesRightBefore(nextTime);
                var vEnd = vehiclePositionDao.getVehiclesRightAfter(nextTime);
                System.out.println("Preemtive fetching for " + nextCacheKey + " took " + (System.currentTimeMillis() - start) + "ms");
                cache.put(nextCacheKey, new Tuple<>(vStart, vEnd));

            }
            openPrefetches.remove(cacheKey);
        });
    }


    private Tuple<Long, Integer> getCacheKey(long timestamp) {
        List<Long> times = vehicleTimesDao.getTimes();
        Iterator<Long> timesIt = times.iterator();
        int idx = 0;
        Long current = timesIt.next();
        while (current <= timestamp && timesIt.hasNext()) {
            current = timesIt.next();
            idx++;
        }
        return new Tuple<>(current, idx);
    }

    private VehiclePositionDto interpolateVehiclePos(VehiclePositionDto v1, VehiclePositionDto v2, long timestamp) {
        if (Objects.isNull(v1)) return v2;
        if (Objects.isNull(v2)) return v1;
        double per = (timestamp - v1.getTimestamp()) / (double) (v2.getTimestamp() - v1.getTimestamp());
        double lon = (v2.getLon() - v1.getLon()) * per + v1.getLon();
        double lat = (v2.getLat() - v1.getLat()) * per + v1.getLat();

        VehiclePositionDto interpolated = new VehiclePositionDto();
        interpolated.setVehicleUID(v1.getVehicleUID());
        interpolated.setId(-1);
        interpolated.setPlate(v1.getPlate());
        interpolated.setTimestamp(timestamp);
        interpolated.setLon(lon);
        interpolated.setLat(lat);
        interpolated.setAdditionalInfo(Map.ofEntries(
                Map.entry("target_lat", v2.getLat() + ""),
                Map.entry("target_lon", v2.getLon() + ""),
                Map.entry("start_time", v1.getTimestamp() + ""),
                Map.entry("end_time", v2.getTimestamp() + "")
        ));
        return interpolated;
    }
}
