package com.projects.appointment_booking_system.entity.enums;

public enum AppointmentStatus {
    BOOKED,
    CANCELLED,
    COMPLETED;

    public boolean canTransitionTo(AppointmentStatus target) {
        if (this == BOOKED) {
            return target == CANCELLED || target == COMPLETED;
        }
        return false;
    }

    public void validateTransitionTo(AppointmentStatus target) {
        if (!canTransitionTo(target)) {
            throw new IllegalStateException(
                "Invalid status transition: Cannot change status from " + this + " to " + target
            );
        }
    }
}
