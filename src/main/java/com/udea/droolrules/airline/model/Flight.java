package com.udea.droolrules.airline.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Hecho de negocio: vuelo de la aerolínea.
 */
public class Flight {

    @NotBlank(message = "El número de vuelo es obligatorio")
    private String flightNumber;

    /** Retraso del vuelo en minutos. */
    @Min(value = 0, message = "El retraso no puede ser negativo")
    private int delayMinutes;

    /** Duración del vuelo en horas. */
    @Min(value = 0, message = "La duración no puede ser negativa")
    private double durationHours;

    /** Asientos de salida de emergencia disponibles. */
    @Min(value = 0, message = "Los asientos de emergencia no pueden ser negativos")
    private int emergencyExitSeatsAvailable;

    public Flight() {
    }

    public Flight(String flightNumber, int delayMinutes, double durationHours, int emergencyExitSeatsAvailable) {
        this.flightNumber = flightNumber;
        this.delayMinutes = delayMinutes;
        this.durationHours = durationHours;
        this.emergencyExitSeatsAvailable = emergencyExitSeatsAvailable;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public int getDelayMinutes() {
        return delayMinutes;
    }

    public void setDelayMinutes(int delayMinutes) {
        this.delayMinutes = delayMinutes;
    }

    public double getDurationHours() {
        return durationHours;
    }

    public void setDurationHours(double durationHours) {
        this.durationHours = durationHours;
    }

    public int getEmergencyExitSeatsAvailable() {
        return emergencyExitSeatsAvailable;
    }

    public void setEmergencyExitSeatsAvailable(int emergencyExitSeatsAvailable) {
        this.emergencyExitSeatsAvailable = emergencyExitSeatsAvailable;
    }
}
