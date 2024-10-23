import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleJsonFormatter {

    // Expresión regular básica para pares clave-valor en JSON
    private static final String JSON_REGEX = "\\s*\\{\\s*(\"[^\"]*\"\\s*:\\s*\"[^\"]*\"\\s*,?\\s*)*}\\s*";

    public static void main(String[] args) {
        String jsonStr = "{\"name\":\"John\", \"age\":30, \"city\":\"New York\", \"hobbies\": [\"reading\", \"coding\"]}";
        System.out.println(jsonStr);

        // Validar JSON con expresiones regulares
        if (isValidJson(jsonStr)) {
            System.out.println("El JSON está correctamente formateado.");
            // Formatear JSON
            String formattedJson = formatJson(jsonStr);
            System.out.println("JSON formateado:");
            System.out.println(formattedJson);
        } else {
            System.out.println("El JSON no está bien formado.");
        }
    }

    public static boolean isValidJson(String json) {
        Pattern pattern = Pattern.compile(JSON_REGEX);
        Matcher matcher = pattern.matcher(json);
        return matcher.matches();
    }

    public static String formatJson(String jsonStr) {
        StringBuilder formattedJson = new StringBuilder();
        int indentLevel = 0;
        boolean inQuotes = false;

        for (int i = 0; i < jsonStr.length(); i++) {
            char currentChar = jsonStr.charAt(i);

            switch (currentChar) {
                case '{':
                case '[':
                    if (!inQuotes) {
                        formattedJson.append(currentChar);
                        formattedJson.append("\n");
                        indentLevel++;
                        addIndentation(formattedJson, indentLevel);
                    } else {
                        formattedJson.append(currentChar);
                    }
                    break;

                case '}':
                case ']':
                    if (!inQuotes) {
                        formattedJson.append("\n");
                        indentLevel--;
                        addIndentation(formattedJson, indentLevel);
                        formattedJson.append(currentChar);
                    } else {
                        formattedJson.append(currentChar);
                    }
                    break;

                case ',':
                    formattedJson.append(currentChar);
                    if (!inQuotes) {
                        formattedJson.append("\n");
                        addIndentation(formattedJson, indentLevel);
                    }
                    break;

                case ':':
                    formattedJson.append(currentChar);
                    if (!inQuotes) {
                        formattedJson.append(" ");
                    }
                    break;

                case '"':
                    formattedJson.append(currentChar);
                    // Alternar estado de inQuotes
                    inQuotes = !inQuotes;
                    break;

                default:
                    formattedJson.append(currentChar);
                    break;
            }
        }

        return formattedJson.toString();
    }

    private static void addIndentation(StringBuilder sb, int indentLevel) {
        for (int i = 0; i < indentLevel; i++) {
            sb.append("    "); // Agregar 4 espacios por nivel de indentación
        }
    }
}
