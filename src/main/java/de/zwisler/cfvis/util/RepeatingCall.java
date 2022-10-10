package de.zwisler.cfvis.util;

import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class RepeatingCall extends Thread {
    protected boolean isRunning = true;
    private int TARGET_FPS = 60;

    private final Consumer<Consumer<Boolean>> function;

    public static void execute(Consumer<Consumer<Boolean>> function) {
        new RepeatingCall(function).start();
    }

    @Override
    public void run() {
        long now;
        long updateTime;
        long wait;

        final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;

        while (isRunning) {
            now = System.nanoTime();

            function.accept((running) -> this.isRunning = running);

            updateTime = System.nanoTime() - now;
            wait = (OPTIMAL_TIME - updateTime) / 1000000;

            try {
                Thread.sleep(Math.max(1,wait));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
