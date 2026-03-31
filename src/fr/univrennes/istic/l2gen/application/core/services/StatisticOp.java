package fr.univrennes.istic.l2gen.application.core.services;

public enum StatisticOp {
    MIN,
    MAX,
    AVG,
    SUM,
    COUNT;

    public String getDisplayName() {
        return switch (this) {
            case MIN -> "Min";
            case MAX -> "Max";
            case AVG -> "Average";
            case SUM -> "Sum";
            case COUNT -> "Count";
        };
    }
}
