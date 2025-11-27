package ec.edu.espe.logica_negocio;


import ec.edu.espe.datos.model.Estudiante;
import ec.edu.espe.datos.repository.EstudianteRepository;
import ec.edu.espe.observer.EstudianteObserver;
import ec.edu.espe.observer.TipoEvento;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de lógica de negocio para Estudiantes.
 * Capa de negocio en el patrón MVC que utiliza el repositorio Singleton.
 * Implementa el patrón Observer para notificar cambios a las interfaces.
 * Implementa Singleton para compartir la misma instancia entre todas las ventanas.
 */
public class EstudianteService {
    private static EstudianteService instance;
    private final EstudianteRepository repo;
    private final List<EstudianteObserver> observadores;

    private EstudianteService() {
        this.repo = EstudianteRepository.getInstance();
        this.observadores = new ArrayList<>();
        System.out.println("[Singleton] Nueva instancia de EstudianteService creada.");
    }

    /**
     * Obtiene la instancia única del servicio (Singleton)
     * @return la instancia única de EstudianteService
     */
    public static EstudianteService getInstance() {
        if (instance == null) {
            synchronized (EstudianteService.class) {
                if (instance == null) {
                    instance = new EstudianteService();
                }
            }
        }
        return instance;
    }

    /**
     * Registra un observador para recibir notificaciones de cambios
     * @param observador El observador a registrar
     */
    public void agregarObservador(EstudianteObserver observador) {
        if (!observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    /**
     * Remueve un observador de la lista de notificaciones
     * @param observador El observador a remover
     */
    public void removerObservador(EstudianteObserver observador) {
        observadores.remove(observador);
    }

    /**
     * Notifica a todos los observadores sobre un cambio
     * @param tipoEvento Tipo de evento ocurrido
     * @param estudiante Estudiante afectado
     */
    private void notificarObservadores(TipoEvento tipoEvento, Estudiante estudiante) {
        for (EstudianteObserver observador : observadores) {
            observador.actualizar(tipoEvento, estudiante);
        }
    }

    public boolean agregarEstudiante(Estudiante e) throws IllegalArgumentException {
        validarEstudiante(e, true);
        repo.agregar(e);
        notificarObservadores(TipoEvento.CREAR, e);
        return true;
    }

    public boolean editarEstudiante(String id, Estudiante nuevo) throws IllegalArgumentException {
        validarEstudiante(nuevo, false);
        boolean ok = repo.editar(id, nuevo);
        if (!ok) throw new IllegalArgumentException("No existe estudiante con ID: " + id);
        notificarObservadores(TipoEvento.ACTUALIZAR, nuevo);
        return ok;
    }

    public boolean eliminarEstudiante(String id) {
        Estudiante estudiante = repo.buscarPorId(id);
        boolean ok = repo.eliminar(id);
        if (ok && estudiante != null) {
            notificarObservadores(TipoEvento.ELIMINAR, estudiante);
        }
        return ok;
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
