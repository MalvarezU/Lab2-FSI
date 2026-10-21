package com.udea.droolrules.airline.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Hecho de salida: decisión calculada por las reglas de la aerolínea.
 * Cada regla activa setea su bandera correspondiente y registra su nombre.
 */
public class AirlineDecision {

    private boolean upgradedToBusiness;
    private boolean priorityCheckIn;
    private double discountPercent;
    private boolean upgradeEligible = true;
    private boolean emergencyExitSeatAssigned;
    private double compensationAmount;
    private int loyaltyPoints;
    private boolean luggageAllowed = true;
    private boolean vipLoungeAccess;
    private boolean preferentialFamilySeat;

    private List<String> appliedRules = new ArrayList<>();
    private String message;

    public AirlineDecision() {
    }

    public boolean isUpgradedToBusiness() {
        return upgradedToBusiness;
    }

    public void setUpgradedToBusiness(boolean upgradedToBusiness) {
        this.upgradedToBusiness = upgradedToBusiness;
    }

    public boolean isPriorityCheckIn() {
        return priorityCheckIn;
    }

    public void setPriorityCheckIn(boolean priorityCheckIn) {
        this.priorityCheckIn = priorityCheckIn;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }

    public boolean isUpgradeEligible() {
        return upgradeEligible;
    }

    public void setUpgradeEligible(boolean upgradeEligible) {
        this.upgradeEligible = upgradeEligible;
    }

    public boolean isEmergencyExitSeatAssigned() {
        return emergencyExitSeatAssigned;
    }

    public void setEmergencyExitSeatAssigned(boolean emergencyExitSeatAssigned) {
        this.emergencyExitSeatAssigned = emergencyExitSeatAssigned;
    }

    public double getCompensationAmount() {
        return compensationAmount;
    }

    public void setCompensationAmount(double compensationAmount) {
        this.compensationAmount = compensationAmount;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public boolean isLuggageAllowed() {
        return luggageAllowed;
    }

    public void setLuggageAllowed(boolean luggageAllowed) {
        this.luggageAllowed = luggageAllowed;
    }

    public boolean isVipLoungeAccess() {
        return vipLoungeAccess;
    }

    public void setVipLoungeAccess(boolean vipLoungeAccess) {
        this.vipLoungeAccess = vipLoungeAccess;
    }

    public boolean isPreferentialFamilySeat() {
        return preferentialFamilySeat;
    }

    public void setPreferentialFamilySeat(boolean preferentialFamilySeat) {
        this.preferentialFamilySeat = preferentialFamilySeat;
    }

    public List<String> getAppliedRules() {
        return appliedRules;
    }

    public void setAppliedRules(List<String> appliedRules) {
        this.appliedRules = appliedRules;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "AirlineDecision{" +
                "upgradedToBusiness=" + upgradedToBusiness +
                ", priorityCheckIn=" + priorityCheckIn +
                ", discountPercent=" + discountPercent +
                ", upgradeEligible=" + upgradeEligible +
                ", emergencyExitSeatAssigned=" + emergencyExitSeatAssigned +
                ", compensationAmount=" + compensationAmount +
                ", loyaltyPoints=" + loyaltyPoints +
                ", luggageAllowed=" + luggageAllowed +
                ", vipLoungeAccess=" + vipLoungeAccess +
                ", preferentialFamilySeat=" + preferentialFamilySeat +
                ", appliedRules=" + appliedRules +
                ", message='" + message + '\'' +
                '}';
    }
}
