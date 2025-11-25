package ec.edu.espe.controlador;

import ec.edu.espe.datos.model.Estudiante;
import ec.edu.espe.logica_negocio.EstudianteService;

import java.util.List;

/**
 * Controlador que recibe acciones de la Vista y las delega a la capa de Negocio.
 * Este controlador actúa como intermediario entre la Vista (UI) y el Servicio.
 * Múltiples controladores comparten el mismo repositorio gracias al patrón Singleton.
 */
public class EstudianteControlador {
    private final EstudianteService service;
    private final String identificador;

    public EstudianteControlador(String identificador) {
        this.service = new EstudianteService();
        this.identificador = identificador;
        System.out.println("[Controlador " + identificador + "] Creado y conectado al servicio.");
    }

    /**
     * Acción: Crear un nuevo estudiante
     */
    public boolean accionCrear(String id, String nombres, int edad) {
        try {
            Estudiante estudiante = new Estudiante(id, nombres, edad);
            boolean resultado = service.agregarEstudiante(estudiante);
            System.out.println("[Controlador " + identificador + "] CREAR: " + estudiante + " - Éxito");
            return resultado;
        } catch (IllegalArgumentException e) {
            System.err.println("[Controlador " + identificador + "] CREAR: Error - " + e.getMessage());
            throw e;
        }
    }

    /**
     * Acción: Listar todos los estudiantes
     */
    public List<Estudiante> accionListar() {
        List<Estudiante> estudiantes = service.listarEstudiantes();
        System.out.println("[Controlador " + identificador + "] LISTAR: " + estudiantes.size() + " estudiantes encontrados");
        return estudiantes;
    }

    /**
     * Acción: Buscar un estudiante por ID
     */
    public Estudiante accionBuscar(String id) {
        Estudiante estudiante = service.buscarPorId(id);
        if (estudiante != null) {
            System.out.println("[Controlador " + identificador + "] BUSCAR: Encontrado - " + estudiante);
        } else {
            System.out.println("[Controlador " + identificador + "] BUSCAR: No encontrado ID=" + id);
        }
        return estudiante;
    }

    /**
     * Acción: Actualizar un estudiante existente
     */
    public boolean accionActualizar(String id, String nombres, int edad) {
        try {
            Estudiante estudiante = new Estudiante(id, nombres, edad);
            boolean resultado = service.editarEstudiante(id, estudiante);
            System.out.println("[Controlador " + identificador + "] ACTUALIZAR: " + estudiante + " - Éxito");
            return resultado;
        } catch (IllegalArgumentException e) {
            System.err.println("[Controlador " + identificador + "] ACTUALIZAR: Error - " + e.getMessage());
            throw e;
        }
    }

    /**
     * Acción: Eliminar un estudiante
     */
    public boolean accionEliminar(String id) {
        boolean resultado = service.eliminarEstudiante(id);
        if (resultado) {
            System.out.println("[Controlador " + identificador + "] ELIMINAR: ID=" + id + " - Éxito");
        } else {
            System.out.println("[Controlador " + identificador + "] ELIMINAR: ID=" + id + " - No encontrado");
        }
        return resultado;
    }

    /**
     * Acción: Obtener estadísticas
     */
    public void accionMostrarEstadisticas() {
        List<Estudiante> estudiantes = service.listarEstudiantes();
        int total = estudiantes.size();
        double edadPromedio = estudiantes.stream()
                .mapToInt(Estudiante::getEdad)
                .average()
                .orElse(0.0);
        
        System.out.println("[Controlador " + identificador + "] ESTADÍSTICAS:");
        System.out.println("  - Total estudiantes: " + total);
        System.out.println("  - Edad promedio: " + String.format("%.2f", edadPromedio) + " años");
    }

    /**
     * Obtiene el identificador del controlador
     */
    public String getIdentificador() {
        return identificador;
    }
}
