package com.udea.droolrules.airline.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Hecho de negocio: pasajero de la aerolínea.
 */
public class Passenger {

    @NotBlank(message = "El nombre del pasajero es obligatorio")
    private String name;

    @NotNull(message = "La edad del pasajero es obligatoria")
    private int age;

    /** Membresía: Basic, Silver, Gold o Platinum. */
    @NotBlank(message = "La membresía del pasajero es obligatoria")
    private String membershipTier;

    /** Indica si el pasajero viaja con niños. */
    private boolean travellingWithChildren;

    public Passenger() {
    }

    public Passenger(String name, int age, String membershipTier, boolean travellingWithChildren) {
        this.name = name;
        this.age = age;
        this.membershipTier = membershipTier;
        this.travellingWithChildren = travellingWithChildren;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getMembershipTier() {
        return membershipTier;
    }

    public void setMembershipTier(String membershipTier) {
        this.membershipTier = membershipTier;
    }

    public boolean isTravellingWithChildren() {
        return travellingWithChildren;
    }

    public void setTravellingWithChildren(boolean travellingWithChildren) {
        this.travellingWithChildren = travellingWithChildren;
    }
}
