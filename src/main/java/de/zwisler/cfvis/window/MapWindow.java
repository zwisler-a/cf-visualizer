package de.zwisler.cfvis.window;

import de.zwisler.cfvis.window.controller.MapWindowController;
import de.zwisler.cfvis.window.map.CarWaypointPainter;
import de.zwisler.cfvis.window.map.CarWaypointRenderer;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter;
import org.jxmapviewer.painter.AbstractPainter;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.WaypointPainter;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MapWindow extends JFrame {
    private final CarWaypointPainter waypointPainter;
    private final JXMapViewer mapViewer;
    private final InfoPanel infoPanel;
    private final CarWaypointRenderer waypointRenderer;

    public MapWindow(MapWindowController controller) {
        controller.register(this);
        infoPanel = new InfoPanel(controller);
        mapViewer = new JXMapViewer();

        // Create a TileFactoryInfo for OpenStreetMap
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);

        // Use 8 threads in parallel to load the tiles
        tileFactory.setThreadPoolSize(2);

        // Add interactions
        MouseInputListener mia = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mia);
        mapViewer.addMouseMotionListener(mia);
        mapViewer.addMouseListener(new CenterMapListener(mapViewer));
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCenter(mapViewer));
        mapViewer.addKeyListener(new PanKeyListener(mapViewer));

        waypointPainter = new CarWaypointPainter();
        waypointRenderer = new CarWaypointRenderer();
        waypointPainter.setRenderer(waypointRenderer);
        // Create a compound painter that uses both the route-painter and the waypoint-painter
        List<AbstractPainter<JXMapViewer>> painters = new ArrayList<>();
        painters.add(waypointPainter);
        CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(painters);

        mapViewer.setOverlayPainter(painter);

        // Set the focus
        GeoPosition leipzig = new GeoPosition(51.3401639636345, 12.374707119035026);

        mapViewer.setZoom(7);
        mapViewer.setAddressLocation(leipzig);

        setLayout(new BorderLayout());
        getContentPane().add(mapViewer, BorderLayout.CENTER);
        getContentPane().add(infoPanel, BorderLayout.PAGE_START);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void update(WindowState.WindowStateSnapshot state) {
        waypointPainter.setWaypoints(state.getWaypoints());
        waypointRenderer.update(state);
        infoPanel.update(state);
        mapViewer.repaint();
    }
}
