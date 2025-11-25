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
        System.out.println("╔══════════════════════════════════════════════════════════════════╗");
        System.out.println("║         DEMOSTRACIÓN PATRÓN SINGLETON + MVC + CONTROLADOR        ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Demostrar que el Singleton funciona correctamente
        demostrarSingleton();
        
        // Demostrar múltiples controladores compartiendo datos
        demostrarControladores();
        
        // Cargar datos de ejemplo usando el servicio
        cargarDatosEjemplo();
        
        // Lanzar la interfaz gráfica
        SwingUtilities.invokeLater(() -> {
            EstudianteUI ui = new EstudianteUI();
            ui.setVisible(true);
        });
    }
    
    /**
     * Demuestra que el patrón Singleton funciona correctamente:
     * - Solo se crea una instancia
     * - Múltiples referencias apuntan a la misma instancia
     * - Los datos persisten en memoria compartida
     */
    private static void demostrarSingleton() {
        System.out.println("--- Prueba de Singleton ---");
        
        // Obtener dos "instancias" del repositorio
        EstudianteRepository repo1 = EstudianteRepository.getInstance();
        EstudianteRepository repo2 = EstudianteRepository.getInstance();
        
        // Verificar que son la misma instancia
        System.out.println("repo1 == repo2: " + (repo1 == repo2));
        System.out.println("HashCode repo1: " + System.identityHashCode(repo1));
        System.out.println("HashCode repo2: " + System.identityHashCode(repo2));
        
        // Agregar estudiante a través de repo1
        Estudiante testStudent = new Estudiante("TEST1", "Estudiante Prueba", 20);
        repo1.agregar(testStudent);
        System.out.println("\nEstudiante agregado vía repo1: " + testStudent);
        
        // Verificar que está disponible en repo2
        Estudiante encontrado = repo2.buscarPorId("TEST1");
        System.out.println("Estudiante obtenido vía repo2: " + encontrado);
        System.out.println("¿Se encuentra el mismo estudiante? " + (encontrado != null));
        
        // Limpiar el estudiante de prueba
        repo1.eliminar("TEST1");
        System.out.println("\n--- Singleton verificado correctamente ---\n");
    }
    
    /**
     * Carga datos de ejemplo en el repositorio
     */
    private static void cargarDatosEjemplo() {
        System.out.println("--- Cargando datos de ejemplo ---");
        EstudianteService service = new EstudianteService();
        
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
    
    /**
     * Demuestra que múltiples controladores comparten los mismos datos
     */
    private static void demostrarControladores() {
        System.out.println("--- Prueba de Múltiples Controladores ---");
        
        // Crear dos controladores
        EstudianteControlador ctrl1 = new EstudianteControlador("1");
        EstudianteControlador ctrl2 = new EstudianteControlador("2");
        
        // Controlador 1 agrega un estudiante
        ctrl1.accionCrear("DEMO1", "Estudiante Demo", 25);
        
        // Controlador 2 puede ver el estudiante agregado por Controlador 1
        Estudiante encontrado = ctrl2.accionBuscar("DEMO1");
        System.out.println("¿Controlador 2 ve estudiante de Controlador 1? " + (encontrado != null ? "✓ SÍ" : "✗ NO"));
        
        // Limpiar
        ctrl1.accionEliminar("DEMO1");
        
        System.out.println("--- Controladores verificados correctamente ---\n");
    }
}
