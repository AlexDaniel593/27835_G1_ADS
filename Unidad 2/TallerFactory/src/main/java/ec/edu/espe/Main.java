package ec.edu.espe;

import ec.edu.espe.datos.model.Estudiante;
import ec.edu.espe.datos.model.factories.EstudianteFactory;
import ec.edu.espe.logica_negocio.EstudianteService;
import ec.edu.espe.presentacion.EstudianteUI;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Clase principal que demuestra la implementación del patrón MVC con Singleton.
 * - Modelo: Estudiante
 * - Vista: EstudianteUI
 * - Controlador: EstudianteControlador
 * - Servicio: EstudianteService
 * - Repositorio: EstudianteRepository (Singleton)
 */
public class Main {
    public static void main(String[] args) {
        
        cargarDatosEjemplo();
        // Lanzar la interfaz gráfica
        SwingUtilities.invokeLater(() -> {
            EstudianteUI ui = new EstudianteUI();
            ui.setVisible(true);
        });
    }
    
    
    /**
     * Carga datos de ejemplo desde un archivo de texto
     */
    private static void cargarDatosEjemplo() {
        System.out.println("--- Cargando datos de ejemplo desde archivo ---");
        EstudianteService service = new EstudianteService();
        
        try (InputStream is = Main.class.getResourceAsStream("/estudiantes.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            
            String linea;
            int contador = 0;
            
            while ((linea = reader.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    try {
                        Estudiante estudiante = EstudianteFactory.crearEstudianteDesdeCsv(linea);
                        service.agregarEstudiante(estudiante);
                        contador++;
                        System.out.println("  ✓ Cargado: " + estudiante);
                    } catch (IllegalArgumentException e) {
                        System.err.println("  ✗ Error en línea: " + linea + " - " + e.getMessage());
                    }
                }
            }
            
            System.out.println("✓ " + contador + " estudiantes cargados exitosamente");
            
        } catch (Exception e) {
            System.err.println("Error al cargar archivo: " + e.getMessage());
        }
        
        System.out.println("--- Datos cargados ---\n");
    }
    
}
