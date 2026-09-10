package com.fixit.enums;

public enum Priority {
    LOW("Low", 1),
    MEDIUM("Medium", 2),
    HIGH("High", 3),
    CRITICAL("Critical", 4);

    private final String displayName;
    private final int severity;

    Priority(String displayName, int severity) {
        this.displayName = displayName;
        this.severity = severity;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getSeverity() {
        return severity;
    }
}
