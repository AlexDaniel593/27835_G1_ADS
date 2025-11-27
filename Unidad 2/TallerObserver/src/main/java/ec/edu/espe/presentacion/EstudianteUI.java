package ec.edu.espe.presentacion;

import ec.edu.espe.datos.model.Estudiante;
import ec.edu.espe.logica_negocio.EstudianteService;
import ec.edu.espe.observer.EstudianteObserver;
import ec.edu.espe.observer.TipoEvento;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Vista (UI) del patrón MVC para gestión de Estudiantes.
 * Interfaz gráfica para el CRUD de estudiantes.
 * Implementa Observer para recibir notificaciones de cambios.
 */
public class EstudianteUI extends JFrame implements EstudianteObserver {
    private final EstudianteService service;
    private JTextField txtId, txtNombres, txtEdad;
    private DefaultTableModel tableModel;
    private JTable table;
    private static int contadorVentanas = 0;
    private int numeroVentana;

    public EstudianteUI() {
        this.service = EstudianteService.getInstance();
        this.numeroVentana = ++contadorVentanas;
        initComponents();
        cargarTabla();
        
        // Registrar esta ventana como observador
        service.agregarObservador(this);
        
        // Remover el observador cuando se cierre la ventana
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                service.removerObservador(EstudianteUI.this);
            }
        });
    }

    private void initComponents() {
        setTitle("CRUD Estudiantes - Ventana " + numeroVentana);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 450);
        
        // Posicionar ventanas en diferentes ubicaciones
        if (numeroVentana == 1) {
            setLocation(100, 100);
        } else if (numeroVentana == 2) {
            setLocation(820, 100);
        } else {
            setLocationRelativeTo(null);
        }

        // Panel principalL
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Formulario
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.anchor = GridBagConstraints.WEST;

        c.gridx = 0; c.gridy = 0;
        form.add(new JLabel("ID:"), c);
        c.gridx = 1;
        txtId = new JTextField(15);
        form.add(txtId, c);

        c.gridx = 0; c.gridy = 1;
        form.add(new JLabel("Nombres:"), c);
        c.gridx = 1;
        txtNombres = new JTextField(20);
        form.add(txtNombres, c);

        c.gridx = 0; c.gridy = 2;
        form.add(new JLabel("Edad:"), c);
        c.gridx = 1;
        txtEdad = new JTextField(5);
        form.add(txtEdad, c);

        // adaptar pantalla pequeña
        JScrollPane formScroll = new JScrollPane(form);
        formScroll.setPreferredSize(new Dimension(600, 120));

        // botones
        JPanel buttons = new JPanel();
        JButton btnGuardar = new JButton("Guardar");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");

        buttons.add(btnGuardar);
        buttons.add(btnEditar);
        buttons.add(btnEliminar);
        buttons.add(btnLimpiar);

        // tabla
        tableModel = new DefaultTableModel(new Object[]{"ID","Nombres","Edad"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        JScrollPane scrollTable = new JScrollPane(table);
        scrollTable.setPreferredSize(new Dimension(600, 250));

        // Agregar todo al panel principal
        mainPanel.add(formScroll);
        mainPanel.add(buttons);
        mainPanel.add(scrollTable);


        add(mainPanel);

        btnGuardar.addActionListener(e -> accionGuardar());
        btnEditar.addActionListener(e -> accionEditar());
        btnEliminar.addActionListener(e -> accionEliminar());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtId.setText(tableModel.getValueAt(row,0).toString());
                    txtNombres.setText(tableModel.getValueAt(row,1).toString());
                    txtEdad.setText(tableModel.getValueAt(row,2).toString());
                    txtId.setEnabled(false);
                }
            }
        });
    }

    private void accionGuardar() {
        if (txtId.getText().trim().isEmpty() ||
                txtNombres.getText().trim().isEmpty() ||
                txtEdad.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Todos los campos deben estar llenos para guardar.",
                    "Campos vacíos",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            String id = txtId.getText().trim();
            String nombres = txtNombres.getText().trim();
            int edad = Integer.parseInt(txtEdad.getText().trim());

            Estudiante e = new Estudiante(id, nombres, edad);
            service.agregarEstudiante(e);

            JOptionPane.showMessageDialog(this, "Estudiante guardado correctamente.");
            cargarTabla();
            limpiarFormulario();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "La edad debe ser un número entero.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    private void accionEditar() {
        try {
            String id = txtId.getText().trim();
            if (id.isBlank()) { JOptionPane.showMessageDialog(this, "Seleccione un estudiante."); return; }
            String nombres = txtNombres.getText().trim();
            int edad = Integer.parseInt(txtEdad.getText().trim());
            Estudiante nuevo = new Estudiante(id, nombres, edad);
            service.editarEstudiante(id, nuevo);
            JOptionPane.showMessageDialog(this, "Estudiante actualizado.");
            cargarTabla();
            limpiarFormulario();
            txtId.setEnabled(true);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Edad debe ser un número entero.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void accionEliminar() {
        String id = txtId.getText().trim();
        if (id.isBlank()) { JOptionPane.showMessageDialog(this, "Ingrese/seleccione ID a eliminar."); return; }
        int confirm = JOptionPane.showConfirmDialog(this, "Eliminar estudiante con ID " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = service.eliminarEstudiante(id);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Eliminado.");
                cargarTabla();
                limpiarFormulario();
                txtId.setEnabled(true);
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró estudiante con ID: " + id);
            }
        }
    }

    private void limpiarFormulario() {
        txtId.setText("");
        txtNombres.setText("");
        txtEdad.setText("");
        txtId.setEnabled(true);
        table.clearSelection();
    }

    private void cargarTabla() {
        tableModel.setRowCount(0);
        for (Estudiante e : service.listarEstudiantes()) {
            tableModel.addRow(new Object[]{e.getId(), e.getNombres(), e.getEdad()});
        }
    }

    /**
     * Implementación del método del Observer.
     * Se ejecuta cuando otro observador realiza cambios en los estudiantes.
     */
    @Override
    public void actualizar(TipoEvento tipoEvento, Estudiante estudiante) {
        // Actualizar la tabla cuando se recibe una notificación
        SwingUtilities.invokeLater(() -> {
            cargarTabla();
            System.out.println("[Observer] Ventana actualizada - " + tipoEvento + ": " + estudiante.getId());
        });
    }
}
