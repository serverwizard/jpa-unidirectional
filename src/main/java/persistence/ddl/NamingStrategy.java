package persistence.ddl;

public class NamingStrategy {

    public static String toSnakeCase(final String name) {
        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            final char ch = name.charAt(i);
            if (Character.isUpperCase(ch)) {
                if (i > 0) {
                    result.append('_');
                }
                result.append(Character.toLowerCase(ch));
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }
}
