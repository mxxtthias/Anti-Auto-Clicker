package de.luzifer.core.api.enums;

public enum ViolationType {

    EASY(1), NORMAL(2), HARD(3);

    private int i;

    ViolationType(int i) {
        this.i = i;
    }

    public int getViolations() {
        return i;
    }

}
