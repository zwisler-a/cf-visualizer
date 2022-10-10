package de.zwisler.cfvis.window.map;

import de.zwisler.cfvis.window.CarWaypoint;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.AbstractPainter;
import org.jxmapviewer.viewer.DefaultWaypointRenderer;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointRenderer;

import java.awt.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CarWaypointPainter extends AbstractPainter<JXMapViewer> {
    private WaypointRenderer<? super CarWaypoint> renderer = new DefaultWaypointRenderer();
    private Set<CarWaypoint> waypoints = new HashSet<>();

    public CarWaypointPainter() {
        setAntialiasing(true);
        setCacheable(false);
    }

    /**
     * Sets the waypoint renderer to use when painting waypoints
     *
     * @param r the new WaypointRenderer to use
     */
    public void setRenderer(WaypointRenderer<CarWaypoint> r) {
        this.renderer = r;
    }

    /**
     * Gets the current set of waypoints to paint
     *
     * @return a typed Set of Waypoints
     */
    public Set<CarWaypoint> getWaypoints() {
        return Collections.unmodifiableSet(waypoints);
    }

    /**
     * Sets the current set of waypoints to paint
     *
     * @param waypoints the new Set of Waypoints to use
     */
    public void setWaypoints(Set<CarWaypoint> waypoints) {
        this.waypoints = waypoints;
    }

    @Override
    protected void doPaint(Graphics2D g, JXMapViewer map, int width, int height) {
        if (renderer == null) {
            return;
        }

        Rectangle viewportBounds = map.getViewportBounds();

        g.translate(-viewportBounds.getX(), -viewportBounds.getY());

        Set<CarWaypoint> waypointSet = getWaypoints();
        for (CarWaypoint w : waypointSet) {
            renderer.paintWaypoint(g, map, w);
        }

        g.translate(viewportBounds.getX(), viewportBounds.getY());

    }

}