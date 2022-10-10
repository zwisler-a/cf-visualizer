package de.zwisler.cfvis.window.map;

import de.zwisler.cfvis.window.CarWaypoint;
import de.zwisler.cfvis.window.WindowState;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.WaypointRenderer;

import java.awt.*;
import java.awt.geom.Point2D;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Objects;

public class CarWaypointRenderer implements WaypointRenderer<CarWaypoint> {

    private boolean renderPlate = false;
    private boolean renderTravelInfo = false;

    @Override
    public void paintWaypoint(Graphics2D g, JXMapViewer viewer, CarWaypoint w) {
        g = (Graphics2D) g.create();

        Point2D point = viewer.getTileFactory().geoToPixel(w.getPosition(), viewer.getZoom());
        int x = (int) point.getX();
        int y = (int) point.getY();

        if (!Objects.isNull(w.getVehiclePositionDto().getAdditionalInfo())) {
            GeoPosition targetPos = new GeoPosition(
                    Double.parseDouble(w.getVehiclePositionDto().getAdditionalInfo().get("target_lat")),
                    Double.parseDouble(w.getVehiclePositionDto().getAdditionalInfo().get("target_lon"))
            );
            Point2D target = viewer.getTileFactory().geoToPixel(targetPos, viewer.getZoom());
            g.setColor(Color.RED);
            g.drawLine(x + 5, y + 5, (int) target.getX(), (int) target.getY());
        }

        g.setColor(Color.BLACK);
        g.fillArc(x, y, 10, 10, 0, 360);

        if (renderPlate) {
            g.drawString(w.getVehiclePositionDto().getPlate(), x + 10, y + 10);
        }

        if (renderTravelInfo && Objects.nonNull(w.getVehiclePositionDto().getAdditionalInfo())) {
            Map<String, String> a = w.getVehiclePositionDto().getAdditionalInfo();
            String dStart = new SimpleDateFormat("HH:mm:ss").format(
                    Long.parseLong(a.get("start_time"))
            );
            String dEnd = new SimpleDateFormat("HH:mm:ss").format(
                    Long.parseLong(a.get("end_time"))
            );
            g.drawString("Start: " + dStart, x + 10, y + 25);
            g.drawString("End  : " + dEnd, x + 10, y + 35);
        }


        g.dispose();
    }

    public void update(WindowState.WindowStateSnapshot state) {
        this.renderPlate = state.isRenderPlate();
        this.renderTravelInfo = state.isRenderTravelInfo();
    }
}
