package de.zwisler.cfvis.window;

import de.zwisler.cfvis.window.controller.MapWindowController;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;

public class InfoPanel extends JPanel {
    private final JLabel lblDate;
    private final JButton btnPlayStop;
    private final JTextField txtStartTimestamp;
    private final JButton btnApplyStartTime;
    private final JCheckBox chkRenderPlate;
    private final JCheckBox chkRenderTravelInfo;


    public InfoPanel(MapWindowController controller) {
        lblDate = new JLabel("Date: ?");
        txtStartTimestamp = new JTextField("1665328736000");

        btnApplyStartTime = new JButton("Set Time");
        btnApplyStartTime.addActionListener(e ->
                controller.setStartTimestamp(Long.parseLong(txtStartTimestamp.getText()))
        );
        btnPlayStop = new JButton("Play");
        btnPlayStop.addActionListener(e -> {
            controller.togglePlay();
        });

        JSlider framesPerSecond = new JSlider(JSlider.HORIZONTAL,
                -30000, 30000, 15000);
        framesPerSecond.addChangeListener(e -> {
            controller.setSpeed(framesPerSecond.getValue());
        });

        chkRenderPlate = new JCheckBox("Plate");
        chkRenderPlate.addChangeListener(e -> controller.setRenderPlate(chkRenderPlate.isSelected()));

        chkRenderTravelInfo = new JCheckBox("Travel Info");
        chkRenderTravelInfo.addChangeListener(e -> controller.setRenderTravelInfo(chkRenderTravelInfo.isSelected()));

        framesPerSecond.setMajorTickSpacing(10000);
        framesPerSecond.setMinorTickSpacing(1000);
        framesPerSecond.setPaintTicks(true);

        add(lblDate);
        add(txtStartTimestamp);
        add(btnApplyStartTime);
        add(framesPerSecond);
        add(btnPlayStop);
        add(chkRenderPlate);
        add(chkRenderTravelInfo);
        setLayout(new GridLayout());
    }

    public void update(WindowState.WindowStateSnapshot state) {
        String date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(state.getTime());
        lblDate.setText("Date: " + date);
        btnPlayStop.setText(!state.isPlaying() ? "Play" : "Stop");
    }
}
