package ec.edu.espe.logica_negocio;


import ec.edu.espe.datos.model.Estudiante;
import ec.edu.espe.datos.repository.EstudianteRepository;

import java.util.List;

/**
 * Servicio de lógica de negocio para Estudiantes.
 * Capa de negocio en el patrón MVC que utiliza el repositorio Singleton.
 */
public class EstudianteService {
    private final EstudianteRepository repo;

    public EstudianteService() {
        this.repo = EstudianteRepository.getInstance();
    }

    public boolean agregarEstudiante(Estudiante e) throws IllegalArgumentException {
        validarEstudiante(e, true);
        repo.agregar(e);
        return true;
    }

    public boolean editarEstudiante(String id, Estudiante nuevo) throws IllegalArgumentException {
        validarEstudiante(nuevo, false);
        boolean ok = repo.editar(id, nuevo);
        if (!ok) throw new IllegalArgumentException("No existe estudiante con ID: " + id);
        return ok;
    }

    public boolean eliminarEstudiante(String id) {
        return repo.eliminar(id);
    }

    public List<Estudiante> listarEstudiantes() {
        return repo.listar();
    }

    public Estudiante buscarPorId(String id) {
        return repo.buscarPorId(id);
    }

    private void validarEstudiante(Estudiante e, boolean esNuevo) {
        if (e == null) throw new IllegalArgumentException("Estudiante nulo");
        if (e.getId() == null || e.getId().isBlank()) throw new IllegalArgumentException("ID requerido");
        if (e.getNombres() == null || e.getNombres().isBlank()) throw new IllegalArgumentException("Nombres requeridos");
        if (e.getEdad() <= 0) throw new IllegalArgumentException("Edad debe ser mayor que 0");
        if (esNuevo) {
            Estudiante existente = repo.buscarPorId(e.getId());
            if (existente != null) throw new IllegalArgumentException("ID ya existe: " + e.getId());
        }
    }
}
