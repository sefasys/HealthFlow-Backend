package user.patient;

import appointment.Appointment;
import user.User;

import java.util.List;

public class Patient {
    private User user;
    private BloodType bloodType;
    private List<Appointment> appointments;

    public Patient(User user,
                   BloodType bloodType,
                   List<Appointment> appointments){

        if(user == null){
            throw new IllegalArgumentException(
                    "User must be set."
            );
        }
        if(bloodType == null){
            throw new IllegalArgumentException(
                    "Blood type must be set."
            );
        }
        if(appointments == null){
            throw new IllegalArgumentException(
                    "Appointments must be set."
            );
        }

        this.appointments = appointments;
        this.user = user;
        this.bloodType = bloodType;

    }

    public User getUser() {
        return user;
    }

    public BloodType getBloodType() {
        return bloodType;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }
}
