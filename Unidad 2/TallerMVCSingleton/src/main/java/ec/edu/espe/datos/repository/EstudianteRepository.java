package ec.edu.espe.datos.repository;

import ec.edu.espe.datos.model.Estudiante;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio de Estudiantes implementando el patrón Singleton.
 * Garantiza una única instancia y persistencia en memoria compartida.
 */
public class EstudianteRepository {
    // Instancia única del repositorio (Singleton)
    private static EstudianteRepository instance;
    
    // Lista de estudiantes en memoria
    private final List<Estudiante> estudiantes = new ArrayList<>();

    // Constructor privado para evitar instanciación externa
    private EstudianteRepository() {
        System.out.println("[Singleton] Nueva instancia de EstudianteRepository creada.");
    }

    /**
     * Obtiene la instancia única del repositorio (Singleton)
     * @return la instancia única de EstudianteRepository
     */
    public static EstudianteRepository getInstance() {
        if (instance == null) {
            synchronized (EstudianteRepository.class) {
                if (instance == null) {
                    instance = new EstudianteRepository();
                }
            }
        }
        return instance;
    }

    public void agregar(Estudiante e) {
        estudiantes.add(e);
    }

    public boolean editar(String id, Estudiante nuevo) {
        Optional<Estudiante> opt = estudiantes.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst();
        if (opt.isPresent()) {
            Estudiante existente = opt.get();
            existente.setNombres(nuevo.getNombres());
            existente.setEdad(nuevo.getEdad());
            return true;
        }
        return false;
    }

    public boolean eliminar(String id) {
        return estudiantes.removeIf(s -> s.getId().equals(id));
    }

    public List<Estudiante> listar() {
        return new ArrayList<>(estudiantes); // devolver copia para seguridad
    }

    public Estudiante buscarPorId(String id) {
        return estudiantes.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
