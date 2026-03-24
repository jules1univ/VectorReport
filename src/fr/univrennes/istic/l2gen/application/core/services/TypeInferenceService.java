package fr.univrennes.istic.l2gen.application.core.services;

import fr.univrennes.istic.l2gen.application.core.table.DataType;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public final class TypeInferenceService {

    private TypeInferenceService() {
    }

    public static List<DataType> inferColumnTypes(
            Statement statement,
            String parquetPath,
            List<String> columnNames) throws Exception {

        List<DataType> inferredTypes = new ArrayList<>();

        for (String column : columnNames) {
            inferredTypes.add(resolveColumnType(statement, parquetPath, column));
        }

        return inferredTypes;
    }

    private static DataType resolveColumnType(
            Statement statement,
            String tableName,
            String column) throws Exception {

        ResultSet emptyCheck = statement.executeQuery(
                String.format("SELECT 1 FROM '%s' WHERE \"%s\" IS NOT NULL", tableName, column));
        if (!emptyCheck.next()) {
            return DataType.EMPTY;
        }

        for (DataType candidate : DataType.INFERENCE_ORDER) {
            if (allRowsMatchType(statement, tableName, column, candidate)) {
                return candidate;
            }
        }
        return DataType.STRING;
    }

    private static boolean allRowsMatchType(
            Statement statement,
            String tableName,
            String column,
            DataType type) throws Exception {

        String query = String.format(
                "SELECT 1 FROM '%s' WHERE NOT (%s)",
                tableName,
                type.toCastSQL(column));

        try (ResultSet rs = statement.executeQuery(query)) {
            return !rs.next();
        }
    }

}