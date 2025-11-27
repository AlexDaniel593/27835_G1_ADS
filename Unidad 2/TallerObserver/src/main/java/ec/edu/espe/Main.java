package ec.edu.espe;

import ec.edu.espe.controlador.EstudianteControlador;
import ec.edu.espe.datos.model.Estudiante;
import ec.edu.espe.datos.repository.EstudianteRepository;
import ec.edu.espe.logica_negocio.EstudianteService;
import ec.edu.espe.presentacion.EstudianteUI;

import javax.swing.*;

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
        // Cargar datos de ejemplo usando el servicio
        cargarDatosEjemplo();
        
        // Lanzar la interfaz gráfica
        SwingUtilities.invokeLater(() -> {
            EstudianteUI ui = new EstudianteUI();
            ui.setVisible(true);
        });
        
        // Lanzar la interfaz gráfica
        SwingUtilities.invokeLater(() -> {
            EstudianteUI ui = new EstudianteUI();
            ui.setVisible(true);
        });
    }
    
    
    /**
     * Carga datos de ejemplo en el repositorio
     */
    private static void cargarDatosEjemplo() {
        System.out.println("--- Cargando datos de ejemplo ---");
        EstudianteService service = EstudianteService.getInstance();
        
        try {
            service.agregarEstudiante(new Estudiante("ADS1", "Kevin Amaguaña", 23));
            service.agregarEstudiante(new Estudiante("ADS2", "Jairo Bonilla", 22));
            service.agregarEstudiante(new Estudiante("ADS3", "Daniel Guaman", 22));
            service.agregarEstudiante(new Estudiante("ADS4", "Reishel Tipan", 22));
            System.out.println("✓ 4 estudiantes cargados exitosamente");
        } catch (Exception e) {
            System.err.println("Error al cargar datos: " + e.getMessage());
        }
        
        System.out.println("--- Datos cargados ---\n");
    }

}
