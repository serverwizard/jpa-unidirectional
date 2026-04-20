package persistence.ddl;

import annotation.JoinColumn;
import annotation.ManyToOne;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DdlGenerator {

    private static final String INDENT = "    ";

    public String generateCreateTable(final Class<?> entityClass) {
        if (!entityClass.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException();
        }

        final String tableName = resolveTableName(entityClass);
        final List<String> definitions = new ArrayList<>();

        for (final Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(ManyToOne.class)) {
                definitions.add(joinColumnDefinition(field));
                continue;
            }
            definitions.add(columnDefinition(field));
        }

        foreignKeyConstraint(entityClass).ifPresent(definitions::add);

        final String body = definitions.stream()
                .map(line -> INDENT + line)
                .collect(Collectors.joining(",\n"));

        return "CREATE TABLE " + tableName + " (\n" + body + "\n);";
    }

    private String resolveTableName(final Class<?> entityClass) {
        if (entityClass.isAnnotationPresent(Table.class)) {
            final String name = entityClass.getAnnotation(Table.class).name();
            if (!name.isEmpty()) {
                return name;
            }
        }
        return entityClass.getSimpleName().toLowerCase();
    }

    private String columnDefinition(final Field field) {
        final String columnName = NamingStrategy.toSnakeCase(field.getName());
        final String sqlType = ColumnType.of(field.getType());
        if (field.isAnnotationPresent(Id.class)) {
            return columnName + " " + sqlType + " PRIMARY KEY";
        }
        return columnName + " " + sqlType;
    }

    private String joinColumnDefinition(final Field field) {
        final String columnName = resolveJoinColumnName(field);
        final Class<?> referencedIdType = findIdField(field.getType()).getType();
        return columnName + " " + ColumnType.of(referencedIdType);
    }

    private java.util.Optional<String> foreignKeyConstraint(final Class<?> entityClass) {
        for (final Field field : entityClass.getDeclaredFields()) {
            if (!field.isAnnotationPresent(ManyToOne.class)) {
                continue;
            }
            final String columnName = resolveJoinColumnName(field);
            final Class<?> referencedType = field.getType();
            final String referencedTable = resolveTableName(referencedType);
            final String referencedColumn = NamingStrategy.toSnakeCase(
                    findIdField(referencedType).getName());
            return java.util.Optional.of(
                    "FOREIGN KEY (" + columnName + ") REFERENCES "
                            + referencedTable + "(" + referencedColumn + ")");
        }
        return java.util.Optional.empty();
    }

    private String resolveJoinColumnName(final Field field) {
        if (field.isAnnotationPresent(JoinColumn.class)) {
            return field.getAnnotation(JoinColumn.class).name();
        }
        return NamingStrategy.toSnakeCase(field.getName()) + "_id";
    }

    private Field findIdField(final Class<?> entityClass) {
        for (final Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                return field;
            }
        }
        throw new IllegalArgumentException();
    }
}
