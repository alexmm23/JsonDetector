package org.example;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class JsonFileValidator {

    public static void main(String[] args) {
        String filePath = "ruta/al/archivo.json";
        if (isValidJsonFile(filePath)) {
            System.out.println("El archivo contiene JSON bien formado.");
        } else {
            System.out.println("El archivo no contiene JSON bien formado.");
        }
    }

    public static boolean isValidJsonFile(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();

        // Leer el archivo y construir una cadena de texto
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                contentBuilder.append(line);
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
            return false;
        }

        String fileContent = contentBuilder.toString().trim();

        // Intentar parsear el contenido como JSON
        try {
            if (fileContent.startsWith("{")) {
                new JSONObject(fileContent);  // Parsear como objeto JSON
            } else if (fileContent.startsWith("[")) {
                new JSONArray(fileContent);  // Parsear como arreglo JSON
            } else {
                return false;
            }
            return true;  // El archivo es JSON bien formado
        } catch (JSONException e) {
            return false;  // No es un JSON bien formado
        }
    }
}
