package de.zwisler.cfvis.window.controller;

import de.zwisler.cfvis.dao.InterpolatedVehiclePositionDao;
import de.zwisler.cfvis.dao.VehicleTimesDao;
import de.zwisler.cfvis.mapper.WaypointMapper;
import de.zwisler.cfvis.util.RepeatingCall;
import de.zwisler.cfvis.window.MapWindow;
import de.zwisler.cfvis.window.WindowState;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class MapWindowController {
    private final InterpolatedVehiclePositionDao vehiclePositionDao;
    private final VehicleTimesDao vehicleTimesDao;
    private WindowState state = new WindowState();
    private MapWindow mapWindow;

    public void register(MapWindow mapWindow) {
        this.mapWindow = mapWindow;
    }

    public void togglePlay() {
        state.setPlaying(!state.isPlaying());
        if (state.isPlaying()) {
            List<Long> times = vehicleTimesDao.getTimes();
            RepeatingCall.execute(stopper -> {
                state.setTime(state.getTime() + state.getSpeed());

                if (times.get(times.size() - 1) < state.getTime()) {
                    stopper.accept(true);
                }
                stopper.accept(state.isPlaying());
                state.setWaypoints(vehiclePositionDao.getVehiclesAt(state.getTime()).stream().map(WaypointMapper::map).collect(Collectors.toSet()));
                mapWindow.update(state.snapshot());
            });
        }
    }

    public void setStartTimestamp(long startTimestamp) {
        state.setTime(startTimestamp);
        state.setWaypoints(vehiclePositionDao.getVehiclesAt(state.getTime()).stream().map(WaypointMapper::map).collect(Collectors.toSet()));
        mapWindow.update(state.snapshot());
    }

    public void setSpeed(int value) {
        this.state.setSpeed(value);
    }

    public void setRenderPlate(boolean selected) {
        state.setRenderPlate(selected);
        mapWindow.update(state.snapshot());
    }

    public void setRenderTravelInfo(boolean selected) {
        state.setRenderTravelInfo(selected);
        mapWindow.update(state.snapshot());
    }
}
