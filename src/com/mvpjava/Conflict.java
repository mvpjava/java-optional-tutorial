package com.mvpjava;

import java.util.Random;

public class Conflict {

    private final int conflictId;

    public Conflict() {
        conflictId = new Random().nextInt(101);
    }

    public Conflict(int conflictId) {
        this.conflictId = conflictId;
    }
    public int getConflictId() {
        return conflictId;
    }

    @Override
    public String toString() {
        return "Conflict{" + "conflictId=" + conflictId + '}';
    }

}
