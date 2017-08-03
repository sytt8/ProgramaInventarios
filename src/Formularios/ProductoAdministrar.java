/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Formularios;

import Conexion.*;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author hp
 */
public class ProductoAdministrar extends javax.swing.JFrame {

    /**
     * Creates new form ProductoAdministrar
     */
    private String usuarioID = "1";
    private String CurrentUsuarioID ="1";
    private String usuarioNombre = "admin";
    private ConectarBD conector = null;
    
    //atributo del producto
    private String producto = "",precio = "";

    private String Buscador = "";
    private String Origen= "producto";
    private CompraProductoAdministrar compraProductoAdministrar;
    
    public void setFOrigen(CompraProductoAdministrar compraProductoAdministrar, String origen){
        this.compraProductoAdministrar = compraProductoAdministrar;
        this.Origen = origen;
    }
    
        public void validarOrigen(){
            if(this.Origen.equals("compraproducto")){
                this.compraProductoAdministrar.RecargarProducto();
                this.setVisible(false);
            }else{
                JOptionPane.showMessageDialog(null,"Se inserto");
                this.limpiar();
                this.cargarTabla();
            }
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
    
    public ProductoAdministrar(ConectarBD conector,String usuarioID,
            String usuarioNombre) {
        initComponents();
        this.setConector(conector);
        this.setUsuarioID(usuarioID);
        this.setUsuarioNombre(usuarioNombre);
        this.cargarTabla();
        this.MostrarBotones(true, false, false);
    }

    /*
        Buscador
    */
    public void BuscadorCliente(){
        if(!(this.jTextFieldBuscador.getText().isEmpty())){
            this.Buscador = " and concat(nombre,' ',precio,' ',cantidad_almacen,' ',id) like '%"+this.jTextFieldBuscador.getText()+"%'";
            this.cargarTabla();
        }else{
            this.Buscador = "";
            this.cargarTabla();
        }
    }
    
    public boolean validarProducto(){
        boolean resp = false; 
        String table_name = "producto";
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
            Logger.getLogger(ProductoAdministrar.class.getName()).log(Level.SEVERE, null, ex);
            resp = false;
        }
        return resp;
    }
    public void insert(){
       if(this.validarDatos()){
          
           try {
                if(this.validarProducto()){
                    String tableI = "producto";
                    String camposI = "nombre,precio,usuario_id,fecha";
                    String valoresI = " '"+this.producto+"','"+this.precio+"' "+
                            ",'"+this.usuarioID+"',now() ";
                    this.conector.insertData(tableI,camposI , valoresI);
                    this.validarOrigen();
                 }
               
           } catch (Exception ex) {
               Logger.getLogger(ProductoAdministrar.class.getName())
                       .log(Level.SEVERE, null, ex);
               JOptionPane.showMessageDialog(null, "Error agregando producto");
           }
       }
       
       
    }
    public boolean validarDatos(){
       this.producto = this.jTextField1.getText();
       this.precio = this.jTextField2.getText();
       boolean resp = false;
       if(producto.isEmpty()){
           JOptionPane.showMessageDialog(null,"El campo producto "
                   + "esta vacío");
       }else if(precio.isEmpty()){
            JOptionPane.showMessageDialog(null,"El campo precio "
                    + "esta vacío");
            
       }else{
           resp = true;
       }
       return resp;
    }
    public void editar(){
       
        if(this.validarDatos()){
           String table = "producto";
           String campos = "nombre='"+this.producto+"',precio='"+this.precio+"' ";
           String where = " id = '"+this.CurrentUsuarioID+"' ";
           this.conector.ActualizarDato(table,campos , where);
           JOptionPane.showMessageDialog(null,"Se edito");
           this.limpiar();
           this.cargarTabla();
       }
       
       
    }
    public void limpiar(){
         this.jTextField1.setText("");
         this.jTextField2.setText("");
    }
    public void eliminar(){
       int index = this.jTable1.getSelectedRow();
        if(index >= 0){
           if(JOptionPane.showConfirmDialog(null, "Esta "
                   + "seguro que desea eliminar el producto?",
                   "Eliminar producto", JOptionPane.YES_NO_OPTION,
                   JOptionPane.QUESTION_MESSAGE) 
                   == JOptionPane.YES_OPTION ){ 
                this.CurrentUsuarioID = this.jTable1.getValueAt(index, 0)
                        .toString();
                String table = "producto";
                String campos = "visible=false ";
                String where = " id = '"+this.CurrentUsuarioID+"' ";
                this.conector.ActualizarDato(table,campos , where);
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
        int index = this.jTable1.getSelectedRow();
        if(index >= 0){
        String id = this.jTable1.getValueAt(index, 0).toString();
        //String nombre= this.jTable1.getValueAt(index, 0).toString();
        //String tipo = this.jTable1.getValueAt(index, 0).toString();
        //JOptionPane.showMessageDialog(null, nombre+" "+id+" "+tipo);
        try {
        String table_name = "producto";
        String campos = " * ";
        String otros = " where id = '"+id+"' ";
        java.sql.ResultSet resultSet = this.conector.ObtenerDatosParaTabla(table_name,campos ,otros);
        if( resultSet.first() ){
             this.CurrentUsuarioID = resultSet.getString("id");
             this.jTextField1.setText(resultSet.getString("nombre"));
             this.jTextField2.setText(resultSet.getString("precio"));
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
        String table_name = "producto";
        String campos = " * ";
        String otros = " where visible = true "+this.Buscador;
        java.sql.ResultSet resultSet = this.conector.
                ObtenerDatosParaTabla(table_name,campos ,otros);
        this.jTable1.setModel(new DefaultTableModel());
        if( resultSet.first() ){
            int total = 0;
        do{
            total++;
        }while(resultSet.next());
        resultSet.first();
        String[] titulos = {"ID","PRODUCTO","CANTIDAD EXISTENTE","PRECIO","CREADO POR"};
        Object[][] fila = new Object[total][10];
            
         int c = 0;
         
          do{
                fila[c][0] = resultSet.getString("id");
                    fila[c][1] = resultSet.getString("nombre");
                    fila[c][2] = resultSet.getString("cantidad_almacen");
                    fila[c][3] = resultSet.getString("precio");
                    fila[c][4] = resultSet.getString("usuario_id");
                    c++;
             } while(resultSet.next());
          DefaultTableModel modelo = new DefaultTableModel(fila,titulos);
          this.jTable1.setModel(modelo);
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
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jTextField1 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jTextField2 = new javax.swing.JTextField();
        jTextFieldBuscador = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();

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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTable1.setToolTipText("Click izquierdo para seleccionar la fila y luego click derecho para desplegar las opciones");
        jTable1.setComponentPopupMenu(jPopupMenu1);
        jScrollPane1.setViewportView(jTable1);

        jLabel1.setText("Producto");

        jLabel2.setText("Precio");

        jButton1.setText("Agregar");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton1MousePressed(evt);
            }
        });

        jButton2.setText("Editar");
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton2MousePressed(evt);
            }
        });

        jButton3.setText("Cancelar");
        jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton3MousePressed(evt);
            }
        });

        jTextFieldBuscador.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldBuscadorKeyReleased(evt);
            }
        });

        jLabel9.setText("Que deseas buscar?");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(23, 23, 23)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(jTextFieldBuscador, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addComponent(jLabel2))
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(36, 36, 36)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 510, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(79, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(6, 6, 6)
                        .addComponent(jTextFieldBuscador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton3)
                            .addComponent(jButton1))))
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(11, 11, 11)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addGap(6, 6, 6)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 372, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(65, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MousePressed
        // TODO add your handling code here:
        this.insert();
    }//GEN-LAST:event_jButton1MousePressed

    private void jButton2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MousePressed
        // TODO add your handling code here:
        this.editar();
    }//GEN-LAST:event_jButton2MousePressed

    private void jButton3MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton3MousePressed
        // TODO add your handling code here:
        this.MostrarBotones(true, false, false);
        this.limpiar();
    }//GEN-LAST:event_jButton3MousePressed

    private void jTextFieldBuscadorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldBuscadorKeyReleased
        // TODO add your handling code here:
        this.BuscadorCliente();
    }//GEN-LAST:event_jTextFieldBuscadorKeyReleased

    private void EditarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_EditarMousePressed
        // TODO add your handling code here:
        this.MostrarDato();
    }//GEN-LAST:event_EditarMousePressed

    private void EliminarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_EliminarMousePressed
        // TODO add your handling code here:
        this.eliminar();
    }//GEN-LAST:event_EliminarMousePressed

    /**
     * @param args the command line arguments
     */
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem Editar;
    private javax.swing.JMenuItem Eliminar;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextFieldBuscador;
    // End of variables declaration//GEN-END:variables
}
