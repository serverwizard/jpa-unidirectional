package persistence.ddl;

public class ColumnType {

    public static String of(final Class<?> javaType) {
        if (javaType == Long.class || javaType == long.class) {
            return "BIGINT";
        }
        if (javaType == Integer.class || javaType == int.class) {
            return "INT";
        }
        if (javaType == String.class) {
            return "VARCHAR(255)";
        }
        throw new IllegalArgumentException();
    }
}
