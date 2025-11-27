package ec.edu.espe.observer;

import ec.edu.espe.datos.model.Estudiante;

/**
 * Interfaz Observer para el patrón Observer.
 * Define el contrato para los observadores que serán notificados
 * cuando ocurran cambios en los estudiantes.
 */
public interface EstudianteObserver {
    /**
     * Método llamado cuando se produce un cambio en un estudiante
     * @param tipoEvento Tipo de operación realizada (CREAR, ACTUALIZAR, ELIMINAR)
     * @param estudiante Estudiante afectado por el cambio
     */
    void actualizar(TipoEvento tipoEvento, Estudiante estudiante);
}
