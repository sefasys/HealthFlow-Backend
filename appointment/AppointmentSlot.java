package appointment;

public record AppointmentSlot(
                  TimeRange timeRange,
                  SlotStatus status
) {
    public boolean isAvailable() {
        return status == SlotStatus.AVAILABLE;
    }
}
