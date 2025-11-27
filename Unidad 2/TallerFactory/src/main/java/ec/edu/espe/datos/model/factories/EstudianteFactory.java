package ec.edu.espe.datos.model.factories;

import ec.edu.espe.datos.model.Estudiante;

/**
 * Factory para la creación de estudiantes con validación centralizada de datos.
 * Aplica el patrón Factory para encapsular la lógica de creación y validación.
 */
public class EstudianteFactory {
    
    /**
     * Crea un estudiante con validaciones centralizadas
     * 
     * @param id Identificación del estudiante (formato: ADS1, ADS2, etc.)
     * @param nombres Nombres del estudiante
     * @param edad Edad del estudiante
     * @return Estudiante validado
     * @throws IllegalArgumentException si algún dato no cumple las validaciones
     */
    public static Estudiante crearEstudiante(String id, String nombres, int edad) {
        // Validación de ID
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID no puede estar vacío");
        }
        if (id.trim().length() < 2) {
            throw new IllegalArgumentException("El ID debe tener al menos 2 caracteres");
        }
        
        // Validación de nombres
        if (nombres == null || nombres.trim().isEmpty()) {
            throw new IllegalArgumentException("Los nombres no pueden estar vacíos");
        }
        if (nombres.trim().length() < 3) {
            throw new IllegalArgumentException("Los nombres deben tener al menos 3 caracteres");
        }
        
        // Validación de edad
        if (edad <= 0) {
            throw new IllegalArgumentException("La edad debe ser mayor a 0");
        }
        if (edad > 120) {
            throw new IllegalArgumentException("La edad debe ser menor o igual a 120 años");
        }
        
        return new Estudiante(id.trim(), nombres.trim(), edad);
    }
    
    /**
     * Crea un estudiante desde valores de texto (para procesar input)
     * 
     * @param id ID del estudiante
     * @param nombres Nombres del estudiante
     * @param edadStr Edad como String
     * @return Estudiante validado
     * @throws IllegalArgumentException si los datos son inválidos
     */
    public static Estudiante crearEstudianteDesdeString(String id, String nombres, String edadStr) {
        if (edadStr == null || edadStr.trim().isEmpty()) {
            throw new IllegalArgumentException("La edad no puede estar vacía");
        }
        
        try {
            int edad = Integer.parseInt(edadStr.trim());
            return crearEstudiante(id, nombres, edad);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("La edad debe ser un número entero válido");
        }
    }
    
    /**
     * Crea un estudiante desde una línea de texto con formato CSV
     * 
     * @param linea Línea en formato "id,nombres,edad"
     * @return Estudiante validado
     * @throws IllegalArgumentException si el formato o datos son inválidos
     */
    public static Estudiante crearEstudianteDesdeCsv(String linea) {
        if (linea == null || linea.trim().isEmpty()) {
            throw new IllegalArgumentException("La línea no puede estar vacía");
        }
        
        String[] partes = linea.split(",", 3);
        if (partes.length != 3) {
            throw new IllegalArgumentException("Formato inválido. Esperado: id,nombres,edad");
        }
        
        return crearEstudianteDesdeString(partes[0].trim(), partes[1].trim(), partes[2].trim());
    }
}
