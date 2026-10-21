package com.udea.droolrules.airline.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Hecho de entrada: solicitud de evaluación para un pasajero y un vuelo.
 */
public class AirlineRequest {

    @NotNull(message = "El pasajero es obligatorio")
    @Valid
    private Passenger passenger;

    @NotNull(message = "El vuelo es obligatorio")
    @Valid
    private Flight flight;

    /** Peso del equipaje en kilogramos. */
    @NotNull(message = "El peso del equipaje es obligatorio")
    @Min(value = 0, message = "El peso del equipaje no puede ser negativo")
    private double luggageWeightKg;

    /** Preferencia de asiento: "Any", "Window", "Aisle", etc. */
    @NotBlank(message = "La preferencia de asiento es obligatoria")
    private String seatPreference;

    /** Precio base del pasaje, usado para calcular descuentos. */
    @Min(value = 0, message = "El precio base no puede ser negativo")
    private double basePrice;

    public AirlineRequest() {
    }

    public AirlineRequest(Passenger passenger, Flight flight, double luggageWeightKg, String seatPreference, double basePrice) {
        this.passenger = passenger;
        this.flight = flight;
        this.luggageWeightKg = luggageWeightKg;
        this.seatPreference = seatPreference;
        this.basePrice = basePrice;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public double getLuggageWeightKg() {
        return luggageWeightKg;
    }

    public void setLuggageWeightKg(double luggageWeightKg) {
        this.luggageWeightKg = luggageWeightKg;
    }

    public String getSeatPreference() {
        return seatPreference;
    }

    public void setSeatPreference(String seatPreference) {
        this.seatPreference = seatPreference;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }
}
