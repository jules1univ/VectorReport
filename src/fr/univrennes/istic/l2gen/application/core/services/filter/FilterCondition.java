package fr.univrennes.istic.l2gen.application.core.services.filter;

public final class FilterCondition {

    public final String logicalOperator;
    public final String columnName;
    public final String conditionType;
    public final String value;

    public FilterCondition(String logicalOperator, String columnName, String conditionType, String value) {
        this.logicalOperator = logicalOperator;
        this.columnName = columnName;
        this.conditionType = conditionType;
        this.value = value;
    }
}