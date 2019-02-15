package com.mvpjava;

import java.util.Optional;
import java.util.Random;

public class Flight {

    private final String aircraftID;
    private int flightLevel;
    private Conflict conflict;

    public Flight(String flightID) {
        this.aircraftID = flightID;
    }

    public String getAircraftID() {
        return aircraftID;
    }

    public Optional<Conflict> probeForConflictReturningOptional() {
        conflict = this.probeAirSpace();
        return (conflict != null)
                ? Optional.of(conflict)
                : Optional.empty();
    }

    public Conflict probeForConfictPossiblyReturningNull() {
        conflict = this.probeAirSpace();
        return (conflict != null)
                ? (conflict)
                : null;
    }

    @Override
    public String toString() {
        return "Flight{" + "aircraftID=" + aircraftID + '}';
    }

    private Conflict probeAirSpace() {
        int randomInt = new Random().nextInt(1000);
        return (randomInt < 500) ? new Conflict() : null;
    }

    public int getFlightLevel() {
        return flightLevel;
    }

    public void setFlightLevel(int flightLevel) {
        this.flightLevel = flightLevel;
    }
    
    

}
