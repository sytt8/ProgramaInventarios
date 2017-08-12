/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Formularios;

import Conexion.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author hp
 */
public class CompraProductoAdministrar extends javax.swing.JFrame {

   private String usuarioID = "1";
    private String CurrentUsuarioID ="1";
    private String usuarioNombre = "admin";
    private ConectarBD conector = null;
    
    private ArrayList<String> ArrayProveedorIDs =new ArrayList<String>();
    private ArrayList<String> ArrayProductoIDs =new ArrayList<String>();
    
    private ArrayList<String> ArrayProveedor =new ArrayList<String>();
    private ArrayList<String> ArrayProducto =new ArrayList<String>();
    
    //atributo del compra
    private String precioCompra = "",cantidadCompra = "",proveedorID = "",productoID = "";

    private String Buscador = "";
    private CompraProductoAdministrar yo;

    public void setYo(CompraProductoAdministrar yo) {
        this.yo = yo;
    }
    
    public void setUsuarioID(String usuarioID) {
        this.usuarioID = usuarioID;
    }

    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }

    public void setConector(ConectarBD conector) {
        this.conector = conector;
    }
    
    public CompraProductoAdministrar(ConectarBD conector,String usuarioID,
            String usuarioNombre) {
        initComponents();
        this.setConector(conector);
        this.setUsuarioID(usuarioID);
        this.setUsuarioNombre(usuarioNombre);
       
        this.MostrarBotones(true, false, false);
        this.MostrarProductos();
        this.MostrarProveedor();
         this.cargarTabla();
    }

    /*
        Buscador
    */
    public void BuscadorCliente(){
        if(!(this.jTextFieldBuscador1.getText().isEmpty())){
            
            this.Buscador = " and concat(p.nombre,' ',p.apellido,' ',pro.nombre,' ',cp.id,' ',cp.precio_compra,' ',u.nombre) like '%"+this.jTextFieldBuscador1.getText()+"%'";
            this.cargarTabla();
        }else{
            this.Buscador = "";
            this.cargarTabla();
        }
    }
    
    /*
        producto
    */
        public void RecargarProducto(){
            this.MostrarProductos();
            this.cargarTabla();
        }
       public void MostrarProductos(){
        try {
            String table_name = "producto ";
            String campos = " id,nombre, concat('Cod:',id,' ',nombre) as producto ";
            String otros = " where visible = true ";
            java.sql.ResultSet resultSet = this.conector.ObtenerDatosParaTabla(table_name,campos ,otros);
            if( resultSet.first() ){
                this.ArrayProductoIDs.clear();
                this.jComboBoxProducto.setModel(new DefaultComboBoxModel());
                do{
                      this.ArrayProductoIDs.add(resultSet.getString("id"));
                      this.ArrayProducto.add(resultSet.getString("producto"));
                      this.jComboBoxProducto.addItem(resultSet.getString("producto"));
                }while(resultSet.next());
            }else{
               JOptionPane.showMessageDialog(null, "No hay registro producto");
               //System.out.println("");
           }
          } catch (Exception ex) {
            System.out.println(ex.getCause().toString());
          }
        
    }

           /*
    Proveedor
    */
        public void RecargarProveedor(){
            this.MostrarProveedor();
        }
       public void MostrarProveedor(){
        try {
                String table_name = "proveedor as c "
                + "inner join persona as p on c.persona_id = p.id "
                + "inner join usuario as u on p.usuario_id = u.id ";
                
                String campos = "concat('Cod:',c.id,' ',p.nombre,' ',p.apellido) as proveedor , c.rnc,c.id,p.nombre,p.apellido,p.cedula, u.nombre as usuario ";        
                String otros = " where  c.visible = true ";
            java.sql.ResultSet resultSet = this.conector.ObtenerDatosParaTabla(table_name,campos ,otros);
            if( resultSet.first() ){
                this.ArrayProveedorIDs.clear();
                this.jComboBoxProveedor.setModel(new DefaultComboBoxModel());
                do{
                        this.ArrayProveedorIDs.add(resultSet.getString("id"));
                        this.ArrayProveedor.add(resultSet.getString("proveedor"));
                      this.jComboBoxProveedor.addItem(resultSet.getString("proveedor"));
                }while(resultSet.next());
            }else{
               JOptionPane.showMessageDialog(null, "No hay registro proveedor");
               //System.out.println("");
           }
          } catch (Exception ex) {
            System.out.println(ex.getCause().toString());
          }
        
    }
    
    public boolean validarProducto(){
        boolean resp = false; 
        /*String table_name = "producto";
            String campos = " * ";
            String otros = " where nombre = '"+this.producto+"' ";
            java.sql.ResultSet resultSet = 
          this.conector.ObtenerDatosParaTabla(table_name,campos ,otros);
        try {
            if( !(resultSet.first()) ){
                resp = true;
            }else{
                   JOptionPane.showMessageDialog(null, "El "
                           + "producto ya existe, favor ingrese otro");
           }
        } catch (SQLException ex) {
            Logger.getLogger(CompraProductoAdministrar.class.getName()).log(Level.SEVERE, null, ex);
            resp = false;
        }*/
        return resp;
    }
    public void insert(){
       if(this.validarDatos()){
          
           try {

                    String tableI = "compra_producto";
                    String camposI = "producto_id,proveedor_id,cantidad,precio_compra,usuario_id,fecha";
                    String valoresI = " '"+this.productoID+"','"+this.proveedorID+"','"+this.cantidadCompra+"','"+this.precioCompra+"' ,'"+this.usuarioID+"',now() ";
                    this.conector.insertData(tableI,camposI , valoresI);
                    this.CurrentUsuarioID = this.conector.obtenerUltimoID("compra_producto");
                    if(!this.CurrentUsuarioID.isEmpty()){
                        this.ManejarCantidadProducto(2);
                        JOptionPane.showMessageDialog(null,"Se inserto");
                        this.limpiar();
                        this.cargarTabla();
                    }else{
                        JOptionPane.showMessageDialog(null, "No se obtuvo el id");
                    }
               
           } catch (Exception ex) {
               Logger.getLogger(CompraProductoAdministrar.class.getName())
                       .log(Level.SEVERE, null, ex);
               JOptionPane.showMessageDialog(null, "Error agregando producto");
           }
       }
       
       
    }
    public boolean validarDatos(){
       this.cantidadCompra = this.jTextFieldCantidad.getText();
       this.precioCompra = this.jTextFieldPrecioCompraUnidad.getText();
       String proveedor = this.jComboBoxProveedor.getSelectedItem().toString();
       String producto = this.jComboBoxProducto.getSelectedItem().toString();
       boolean resp = false;
       if(cantidadCompra.isEmpty()){
           JOptionPane.showMessageDialog(null,"El campo cantidad producto "
                   + "esta vacío");
       }else if(precioCompra.isEmpty()){
            JOptionPane.showMessageDialog(null,"El campo precio compra por unidad "
                    + "esta vacío");
            
       }
       else if(producto.isEmpty()){
            JOptionPane.showMessageDialog(null,"El campo producto "
                    + "esta vacío");
            
       }else if(proveedor.isEmpty()){
            JOptionPane.showMessageDialog(null,"El campo proveedor "
                    + "esta vacío");
            
       }else{
           this.productoID =  this.ArrayProductoIDs.get(this.jComboBoxProducto.getSelectedIndex());
           this.proveedorID = this.ArrayProveedorIDs.get(this.jComboBoxProveedor.getSelectedIndex());
           resp = true;
       }
       return resp;
    }
    public void editar(){
        if(this.validarDatos()){
           String table = "compra_producto";
           String campos = "producto_id ='"+this.productoID+"',proveedor_id ='"+this.proveedorID+"',cantidad='"+this.cantidadCompra+"',precio_compra='"+this.precioCompra+"' ";
           String where = " id = '"+this.CurrentUsuarioID+"' ";
           this.ManejarCantidadProducto(1);
           this.conector.ActualizarDato(table,campos , where);
           this.ManejarCantidadProducto(2);
           JOptionPane.showMessageDialog(null,"Se edito");
           this.limpiar();
           this.cargarTabla();
       }
    }
    
    public void agregarProducto(){
        ProductoAdministrar producto = new ProductoAdministrar(this.conector,this.usuarioID,this.usuarioNombre);
        producto.setFOrigen(this.yo,"compraproducto");
        
        producto.setVisible(true);
    }
    public void agregarProveedor(){
        ProveedorAdministrar proveedor = new ProveedorAdministrar(this.conector
                    ,this.usuarioID,this.usuarioNombre);
        proveedor.setFOrigen(this.yo,"compraproducto");
        proveedor.setYo(proveedor);
        proveedor.setVisible(true);
    }
    
    public void limpiar(){
         this.jTextFieldCantidad.setText("");
         this.jTextFieldPrecioCompraUnidad.setText("");
         this.jComboBoxProducto.setSelectedIndex(0);
         this.jComboBoxProveedor.setSelectedIndex(0);
    }
    public void ManejarCantidadProducto(int opt){
        String table_name = "producto as p"
                + " inner join compra_producto as cp on p.id = cp.producto_id  ";
            String campos = " p.*,cp.cantidad ";
            String otros = " where cp.id = '"+this.CurrentUsuarioID+"' ";
            java.sql.ResultSet resultSet = 
          this.conector.ObtenerDatosParaTabla(table_name,campos ,otros);
        try {
            if( (resultSet.first()) ){
                    switch(opt){
                        case 1:
                            //sacamos la cantidad
                            String tableES = "producto";
                            String camposES = "cantidad_almacen = cantidad_almacen - "+resultSet.getString("cantidad")+" ";
                            String whereES = " id = '"+resultSet.getString("id")+"' ";
                            this.conector.ActualizarDato(tableES,camposES , whereES);
                           break;
                         case 2:
                            //entrar la cantidad
                            String tableEE = "producto";
                            String camposEE = "cantidad_almacen = cantidad_almacen + "+resultSet.getString("cantidad")+" ";
                            String whereEE = " id = '"+resultSet.getString("id")+"' ";
                            this.conector.ActualizarDato(tableEE,camposEE , whereEE);
                           break;
                    }
            }else{
                   JOptionPane.showMessageDialog(null, "No se pudo obtener el producto");
           }
        } catch (SQLException ex) {
            Logger.getLogger(CompraProductoAdministrar.class.getName()).log(Level.SEVERE, null, ex);
         
        }

    }
    public void eliminar(){
       int index = this.jTable2.getSelectedRow();
        if(index >= 0){
           if(JOptionPane.showConfirmDialog(null, "Esta "
                   + "seguro que desea eliminar la compra?",
                   "Eliminar compra", JOptionPane.YES_NO_OPTION,
                   JOptionPane.QUESTION_MESSAGE) 
                   == JOptionPane.YES_OPTION ){ 
                this.CurrentUsuarioID = this.jTable2.getValueAt(index, 0)
                        .toString();
                //Eliminamos la compra
                String table = "compra_producto";
                String campos = "visible=false ";
                String where = " id = '"+this.CurrentUsuarioID+"' ";
                this.conector.ActualizarDato(table,campos , where);
                this.ManejarCantidadProducto(1);
                JOptionPane.showMessageDialog(null,"Se Elimino");
                this.limpiar();
                this.cargarTabla();
           }
        }else{
            JOptionPane.showMessageDialog(null, "No hay fila"
                    + " seleccionada");

       }
       
       
    }
    
  public void MostrarDato(){
        int index = this.jTable2.getSelectedRow();
        if(index >= 0){
        String id = this.jTable2.getValueAt(index, 0).toString();
        //String nombre= this.jTable2.getValueAt(index, 0).toString();
        //String tipo = this.jTable2.getValueAt(index, 0).toString();
        //JOptionPane.showMessageDialog(null, nombre+" "+id+" "+tipo);
        try {
        
            //String table_name = "compra_producto";
        //String campos = " * ";
            //String otros = " where id = '"+id+"' ";
            String table_name = "compra_producto as cp  " +
            "inner join producto as pro on cp.producto_id = pro.id " +
            "inner join proveedor as prov on prov.id = cp.proveedor_id " +
            "inner join persona as p on p.id = prov.persona_id "
            + "inner join usuario as u on u.id = cp.usuario_id";
            //String campos = "  concat('Cod:',prov.id,' ',p.nombre,' ',p.apellido,' Cedula:',p.cedula,' RNC:',prov.rnc) as proveedor ,cp.*, concat('Cod:',pro.id,' ',pro.nombre) as producto  ";
            String campos = "  concat('Cod:',prov.id,' ',p.nombre,' ',p.apellido) as proveedor ,cp.*, concat('Cod:',pro.id,' ',pro.nombre) as producto  ";
            String otros = " where cp.id = '"+id+"'  ";
            
            java.sql.ResultSet resultSet = this.conector.ObtenerDatosParaTabla(table_name,campos ,otros);
        if( resultSet.first() ){
             this.CurrentUsuarioID = resultSet.getString("id");
             this.jTextFieldCantidad.setText(resultSet.getString("cantidad"));
             this.jTextFieldPrecioCompraUnidad.setText(resultSet.getString("precio_compra"));
             
             this.jComboBoxProducto.setSelectedItem(resultSet.getString("producto"));
             this.jComboBoxProveedor.setSelectedItem(resultSet.getString("proveedor"));
             this.MostrarBotones(false, true,true );
        }else{
            JOptionPane.showMessageDialog(null, "No hay registro");
            //System.out.println("");
        }
        
          } catch (SQLException ex) {
            System.out.println(ex.getCause().toString());
          }
        }else{
            JOptionPane.showMessageDialog(null, "No hay fila "
                    + "seleccionada");
        }
    }
    
  public void MostrarBotones(boolean b1,boolean b2,boolean b3){
      this.jButton1.setVisible(b1);
      this.jButton2.setVisible(b2);
      this.jButton3.setVisible(b3);
  }
   public void cargarTabla(){
        try {
            /*
            SELECT cp.*, pro.nombre as producto, concat(p.nombre,' ',p.apellido)
FROM compra_producto as cp 
inner join producto as pro on cp.producto_id = pro.id
inner join proveedor as prov on prov.id = cp.proveedor_id
inner join persona as p on p.id = prov.persona_id
            */
        String table_name = "compra_producto as cp  " +
"inner join producto as pro on cp.producto_id = pro.id " +
"inner join proveedor as prov on prov.id = cp.proveedor_id " +
"inner join persona as p on p.id = prov.persona_id "
                + "inner join usuario as u on u.id = cp.usuario_id";
        
        String campos = "  cp.*,pro.cantidad_almacen, pro.nombre as producto, concat(p.nombre,' ',p.apellido) as proveedor,u.nombre as usuario ";
        String otros = " where cp.visible = true "+this.Buscador;
        java.sql.ResultSet resultSet = this.conector.
                ObtenerDatosParaTabla(table_name,campos ,otros);
        this.jTable2.setModel(new DefaultTableModel());
        if( resultSet.first() ){
            int total = 0;
        do{
            total++;
        }while(resultSet.next());
        resultSet.first();
        //String[] titulos = {"ID","PRODUCTO","CANTIDAD EXISTENTE","PRECIO","CREADO POR"};
        String[] titulos = {"ID","PROVEEDOR","PRODUCTO","PRECIO COMPRA POR UNIDAD","CANTIDAD COMPRADA","CANTIDAD EXISTENTE","COMPRADO POR"};
        Object[][] fila = new Object[total][10];
            
         int c = 0;
         
          do{
                fila[c][0] = resultSet.getString("id");
                    fila[c][1] = resultSet.getString("proveedor");
                    fila[c][2] = resultSet.getString("producto");
                    fila[c][3] = resultSet.getString("precio_compra");
                    fila[c][4] = resultSet.getString("cantidad");
                    fila[c][5] = resultSet.getString("cantidad_almacen");
                    fila[c][6] = resultSet.getString("usuario");
                    c++;
             } while(resultSet.next());
          DefaultTableModel modelo = new DefaultTableModel(fila,titulos);
          this.jTable2.setModel(modelo);
          this.MostrarBotones(true, false, false);
        }else{
            JOptionPane.showMessageDialog(null, "No hay registro");
            //System.out.println("");
        }
        this.limpiar();
          } catch (SQLException ex) {
            System.out.println(ex.getCause().toString());
          }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        Editar = new javax.swing.JMenuItem();
        Eliminar = new javax.swing.JMenuItem();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jTextFieldPrecioCompraUnidad = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jComboBoxProducto = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jComboBoxProveedor = new javax.swing.JComboBox<>();
        jTextFieldCantidad = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        Vista1 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jTextFieldBuscador1 = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();

        Editar.setText("Editar");
        Editar.setToolTipText("");
        Editar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                EditarMousePressed(evt);
            }
        });
        jPopupMenu1.add(Editar);

        Eliminar.setText("Eliminar");
        Eliminar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                EliminarMousePressed(evt);
            }
        });
        jPopupMenu1.add(Eliminar);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jButton2.setText("Editar");
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton2MousePressed(evt);
            }
        });
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Cancelar");
        jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton3MousePressed(evt);
            }
        });
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel3.setText("Cantidad");

        jComboBoxProducto.setToolTipText("");

        jLabel4.setText("Proveedor");

        jComboBoxProveedor.setToolTipText("");

        jLabel10.setText("+");
        jLabel10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel10MousePressed(evt);
            }
        });

        jLabel1.setText("Producto");

        jLabel11.setText("+");
        jLabel11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel11MousePressed(evt);
            }
        });

        jLabel2.setText("Precio unidad");

        jLabel12.setText("Que deseas buscar?");

        jTextFieldBuscador1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldBuscador1KeyReleased(evt);
            }
        });

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTable2.setToolTipText("Click izquierdo para seleccionar la fila y luego click derecho para desplegar las opciones");
        jTable2.setComponentPopupMenu(jPopupMenu1);
        jScrollPane2.setViewportView(jTable2);

        javax.swing.GroupLayout Vista1Layout = new javax.swing.GroupLayout(Vista1);
        Vista1.setLayout(Vista1Layout);
        Vista1Layout.setHorizontalGroup(
            Vista1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Vista1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(Vista1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 630, Short.MAX_VALUE)
                    .addComponent(jTextFieldBuscador1)
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        Vista1Layout.setVerticalGroup(
            Vista1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Vista1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel12)
                .addGap(6, 6, 6)
                .addComponent(jTextFieldBuscador1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 466, Short.MAX_VALUE)
                .addGap(18, 18, 18))
        );

        jButton1.setText("Agregar");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton1MousePressed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(23, 23, 23)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel1)
                        .addGap(21, 21, 21)
                        .addComponent(jLabel11))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jComboBoxProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel4)
                        .addGap(10, 10, 10)
                        .addComponent(jLabel10))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jComboBoxProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel2))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jTextFieldPrecioCompraUnidad, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel3))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jTextFieldCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Vista1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(3, 3, 3))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Vista1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(68, 68, 68)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton3)
                            .addComponent(jButton1))))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel1))
                    .addComponent(jLabel11))
                .addGap(10, 10, 10)
                .addComponent(jComboBoxProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel10))
                .addGap(6, 6, 6)
                .addComponent(jComboBoxProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(jLabel2)
                .addGap(6, 6, 6)
                .addComponent(jTextFieldPrecioCompraUnidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(jLabel3)
                .addGap(6, 6, 6)
                .addComponent(jTextFieldCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MousePressed
        // TODO add your handling code here:
        this.editar();
    }//GEN-LAST:event_jButton2MousePressed

    private void jButton3MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton3MousePressed
        // TODO add your handling code here:
        this.MostrarBotones(true, false, false);
        this.limpiar();
    }//GEN-LAST:event_jButton3MousePressed

    private void jLabel10MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel10MousePressed
        // TODO add your handling code here:
        this.agregarProveedor();
    }//GEN-LAST:event_jLabel10MousePressed

    private void jLabel11MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel11MousePressed
        // TODO add your handling code here:
        this.agregarProducto();
    }//GEN-LAST:event_jLabel11MousePressed

    private void jTextFieldBuscador1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldBuscador1KeyReleased
        // TODO add your handling code here:
        this.BuscadorCliente();
    }//GEN-LAST:event_jTextFieldBuscador1KeyReleased

    private void jButton1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MousePressed
        // TODO add your handling code here:
        this.insert();
    }//GEN-LAST:event_jButton1MousePressed

    private void EditarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_EditarMousePressed
        // TODO add your handling code here:
        this.MostrarDato();
    }//GEN-LAST:event_EditarMousePressed

    private void EliminarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_EliminarMousePressed
        // TODO add your handling code here:
        this.eliminar();
    }//GEN-LAST:event_EliminarMousePressed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        this.editar();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        this.MostrarBotones(true, false, false);
        this.limpiar();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    /**
     * @param args the command line arguments
     */
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem Editar;
    private javax.swing.JMenuItem Eliminar;
    private javax.swing.JPanel Vista1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JComboBox<String> jComboBoxProducto;
    private javax.swing.JComboBox<String> jComboBoxProveedor;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextFieldBuscador1;
    private javax.swing.JTextField jTextFieldCantidad;
    private javax.swing.JTextField jTextFieldPrecioCompraUnidad;
    // End of variables declaration//GEN-END:variables
}
