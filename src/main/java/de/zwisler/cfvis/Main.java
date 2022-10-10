package de.zwisler.cfvis;

import de.zwisler.cfvis.dao.DataSource;
import de.zwisler.cfvis.dao.InterpolatedVehiclePositionDao;
import de.zwisler.cfvis.dao.VehiclePositionDao;
import de.zwisler.cfvis.dao.VehicleTimesDao;
import de.zwisler.cfvis.window.MapWindow;
import de.zwisler.cfvis.window.controller.MapWindowController;

import java.util.List;


public class Main {
    public static void main(String[] args) {
        MapWindowController controller = new MapWindowController(
                new InterpolatedVehiclePositionDao(),
                new VehicleTimesDao()
        );
        MapWindow window = new MapWindow(controller);


        VehiclePositionDao vehicleDao = new VehiclePositionDao();
        VehicleTimesDao timesDao = new VehicleTimesDao();
        InterpolatedVehiclePositionDao vehiclePositionDao = new InterpolatedVehiclePositionDao();
        List<Long> times = timesDao.getTimes();
        Long from = times.stream().min(Long::compareTo).get();
        Long to = times.stream().max(Long::compareTo).get();


//        List<VehiclePositionDto> vehiclePositionDtoList = dao.getVehiclesAt(1665238800258L);
//        System.out.println("Found " + vehiclePositionDtoList.size() + " vehicles at ");
    }

}
