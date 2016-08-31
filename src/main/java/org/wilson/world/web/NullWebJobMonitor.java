package org.wilson.world.web;

public class NullWebJobMonitor implements WebJobMonitor {

    @Override
    public void start(int totalSteps) {
    }

    @Override
    public void adjust(int adjustment) {
    }

    @Override
    public void progress(int steps) {
    }

    @Override
    public void succeed() {
    }

    @Override
    public void fail() {
    }

    @Override
    public boolean isStopRequired() {
        return false;
    }

    @Override
    public void stop() {
    }

}
