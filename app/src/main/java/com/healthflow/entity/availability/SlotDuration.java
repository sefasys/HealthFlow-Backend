package com.healthflow.entity.availability;

public enum SlotDuration {
    MIN_10(10), MIN_15(15), MIN_20(20), MIN_30(30), MIN_45(45);

    private final int minutes;
    SlotDuration(int minutes) { this.minutes = minutes; }
    public int getMinutes() { return minutes; }
}
