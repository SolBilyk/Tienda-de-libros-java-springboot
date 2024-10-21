package utn.tienda_libros.vista;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import utn.tienda_libros.modelo.Libro;
import utn.tienda_libros.servicio.LibroServicio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@Component
public class LibroFrom extends JFrame {
    LibroServicio libroServicio;
    private JPanel panel;
    private JTable tablaLibros;
    private JTextField idTexto;
    private JTextField libroTexto;
    private JTextField autorTexto;
    private JTextField precioTexto;
    private JTextField existenciasTexto;
    private JButton agregarButton;
    private JButton modificarButton;
    private JButton eliminarButton;
    private DefaultTableModel tablaModeloLibros;

    @Autowired
    public LibroFrom(LibroServicio libroServicio){
        this.libroServicio = libroServicio;
        //Creamos un metodo para tomar la informacion de la base de datos y que se visualice
        iniciarForma();
        agregarButton.addActionListener(e -> agregarLibro()); //cambiamos a lambda

        tablaLibros.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                //creamos un metodo para cargar libros
                cargarLibroSeleccionado();
            }
        });
        modificarButton.addActionListener(e -> modificarLibro());
        eliminarButton.addActionListener(e -> eliminarLibro());
    }

    private void iniciarForma(){
        setContentPane(panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setSize(900, 700);
        //Para obtener las dimensiones de la ventana
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension tamanioPantalla = toolkit.getScreenSize();
        int x = (tamanioPantalla.width - getWidth()/2);
        int y = (tamanioPantalla.height - getHeight()/2);
        setLocation(x, y);
    }

    private void agregarLibro() {
        //Leer los valores del formulario
        if(libroTexto.getText().equals("")){
            mostrarMensaje("Ingresa el nombre del libro");
            libroTexto.requestFocusInWindow();
            return; //mueve el cursor para que se vuelva a ingresar el dato, nombre del libro
        }
        //si la caja de librode texto ya tiene algo entonces comenzamos a ingresar variables
        var nombreLibro = libroTexto.getText();
        var autor = autorTexto.getText();
        var precio = Double.parseDouble(precioTexto.getText());
        var existencias = Integer.parseInt(existenciasTexto.getText());
        //Creamos el objeto libro
        var libro = new Libro(null, nombreLibro, autor, precio, existencias);
        //libro.setNombreLibro(nombreLibro);
        //libro.setAutor(autor);
        //libro.setPrecio(precio);
        //libro.setExistencias(existencias);
        this.libroServicio.guardarLibro(libro); //si el id del libro es nulo, hace un insert y sino hace un update
        //Si funciona todobien ponemos un mensaje
        mostrarMensaje("Se agrego el libro...");
        //Cuando se ingresaron libros, se deben limpiar las cajas de texto
        limpiarFormulario();
        listarLibros(); //actualiza la tabla de libros
    }

    //Metodo para cargar libros
    private void cargarLibroSeleccionado(){
        //Los indices de las columnas inician en 0: id, 1:libro, 2:autor, 3:precio, 4:existencias
        var renglon = tablaLibros.getSelectedRow(); //esto es para saber cual es el renglon que se presiono dentro del formulario
        //hacemos una comprobacion para ver si renglon es diferente a -1 entonces no se selecciono ningun registro
        if (renglon != -1) {
            String idLibro = tablaLibros.getModel().getValueAt(renglon, 0).toString();
            //de la tabla libro pedimos el modelo, una vez que lo tenemos le indicamos la columna y el id y lo convertimos en una cadena
            idTexto.setText(idLibro); //obtenemos el id del libro en el jtextfield
            String nombreLibro =
                    tablaLibros.getModel().getValueAt(renglon, 1). toString();
            libroTexto.setText(nombreLibro); //caja
            String autor =
                    tablaLibros.getModel().getValueAt(renglon, 2).toString();
            autorTexto.setText(autor);
            String precio =
                    tablaLibros.getModel().getValueAt(renglon, 3).toString();
            precioTexto.setText(precio);
            String existencias =
                    tablaLibros.getModel().getValueAt(renglon, 4).toString();
            existenciasTexto.setText(existencias);
        }
    }

    //Metodo modificar libro
    private void modificarLibro(){
        if (this.idTexto.equals("")) {
            mostrarMensaje("Debes seleccionar un registro en la tabla ");
        }
        else {
            //Verificamos que el nombre del libro no sea nulo
            if (libroTexto.getText().equals("")){
                mostrarMensaje("Ingrese el nombre del libro...");
                libroTexto.requestFocusInWindow();
                return;
            }
            //Si esta la caja de libro texto vacio le pedimos que ingrese el nombre del libro
            //y retornamos y volvemos a marcar el cursor para que se ingrese el dato
            //Llenamos el objeto libro a actualizar
            int idLibro = Integer.parseInt(idTexto.getText());
            var nombreLibro = libroTexto.getText();
            var autor = autorTexto.getText();
            var precio = Double.parseDouble(precioTexto.getText());
            var existencias = Integer.parseInt(existenciasTexto.getText());
            var libro = new Libro(idLibro, nombreLibro, autor, precio, existencias);
            libroServicio.guardarLibro(libro);
            mostrarMensaje("Se modifico el libro...");
            limpiarFormulario();
            listarLibros(); //se refresca la tabla y nos muestra las modificaciones nuevas
        }
    }

    //Metodo eliminar libro
    private void eliminarLibro(){
        var renglon = tablaLibros.getSelectedRow();
        if (renglon != -1){  //si el usuario selecciono una fila quiere decir que es verdadero, porque renglo -1 seria vacio
            String idLibro =
                    tablaLibros.getModel().getValueAt(renglon, 0).toString();
            var libro = new Libro(); //creamos el objeto para eliminar
            libro.setIdLibro(Integer.parseInt(idLibro));
            libroServicio.eliminarLibro(libro);
            mostrarMensaje("Libro " + idLibro + " Eliminado");
            limpiarFormulario();
            listarLibros();
        }
        else {   //para cuando no se haya seleccionado nada, ningun renglon
             mostrarMensaje("No se ha seleccionado ningun libro de la tabla a eliminar");
        }

    }

    //Metodo para limpiar las cajas
    private void limpiarFormulario(){
        libroTexto.setText("");
        autorTexto.setText("");
        precioTexto.setText("");
        existenciasTexto.setText("");
    }

    private void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }

    private void createUIComponents() {
        idTexto = new JTextField("");
        idTexto.setVisible(false); //No es visible en el formulario
        this.tablaModeloLibros = new DefaultTableModel(0, 5){
            @Override //esto es para que no se puedan modificar las celdas al hacerles doble click
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        //Array para las 5 columbas
        String[] cabecera = {"Id", "Libro", "Autor", "Precio", "Existencias"};
        this.tablaModeloLibros.setColumnIdentifiers(cabecera);
        //Instanciamos el objeto de JTable
        this.tablaLibros = new JTable(tablaModeloLibros);
        tablaLibros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); //esto es para evitar que se seleccionen varios registros
        listarLibros();
    }

    private void listarLibros(){
        //Limpiar la tabla
        tablaModeloLibros.setRowCount(0);
        //Obtener los libros de la base de datos
        var libros = libroServicio.listarLibros();
        //Iteramoss cada libro
        libros.forEach((libro) -> { //Funcion lambda
            //Creamos cada registro para agregarlos ala tabla
            Object [] renglonLibro = {
                    //representa cada columna o renglon de la tabla
                    libro.getIdLibro(),
                    libro.getNombreLibro(),
                    libro.getAutor(),
                    libro.getPrecio(),
                    libro.getExistencias()
            };
            this.tablaModeloLibros.addRow(renglonLibro);
        });
    }
}
