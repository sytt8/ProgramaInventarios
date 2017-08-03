/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Formularios;

import Conexion.ConectarBD;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author hp
 */
public class TipoTelefonoAdministrar extends javax.swing.JFrame {
    
    private String usuarioID = "1";
    private String CurrentUsuarioID ="1";
    private String usuarioNombre = "admin";
    private ConectarBD conector = null;
    private String Origen= "tipotelefono";
    private ClientesAdministrar clienteAdministrar;
    private ProveedorAdministrar ProveedorAdministrar;
    
    private String Buscador = "";
        
    public void setUsuarioID(String usuarioID) {
        this.usuarioID = usuarioID;
    }

    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }

    public void setConector(ConectarBD conector) {
        this.conector = conector;
    }
    
    
    public void setFOrigen(ClientesAdministrar fOrigen,String origen){
        this.clienteAdministrar = fOrigen;
        this.Origen = origen;
    }
    public void setFOrigen(ProveedorAdministrar fOrigen,String origen){
        this.ProveedorAdministrar = fOrigen;
        this.Origen = origen;
    }
    
    public void validarOrigen(){
            if(this.Origen.equals("tipotelefono")){
                JOptionPane.showMessageDialog(null,"Se inserto");
                this.jTextField1.setText("");
                this.cargarTabla();
            }else if(this.Origen.equals("cliente")){
                this.clienteAdministrar.RecargarTipoTelefono();
                this.setVisible(false);
            }else if(this.Origen.equals("proveedor")){
                this.ProveedorAdministrar.RecargarTipoTelefono();
                this.setVisible(false);
            }else if(this.Origen.equals("compraproducto")){
                this.ProveedorAdministrar.RecargarTipoTelefono();
                this.setVisible(false);
            }
    }
    
    public TipoTelefonoAdministrar(ConectarBD conector,String usuarioID,
            String usuarioNombre) {
        initComponents();
        this.setConector(conector);
        this.setUsuarioID(usuarioID);
        this.setUsuarioNombre(usuarioNombre);
        this.cargarTabla();
    }
    
    /*
        Buscador
    */
    public void BuscadorCliente(){
        if(!(this.jTextFieldBuscador.getText().isEmpty())){
            this.Buscador = " and concat(nombre,' ',id) like '%"+this.jTextFieldBuscador.getText()+"%'";
            this.cargarTabla();
        }else{
            this.Buscador = "";
            this.cargarTabla();
        }
    }
    

    public void insert(){
       String usuario = this.jTextField1.getText();
       
       if(usuario.isEmpty()){
           JOptionPane.showMessageDialog(null,"El campo tipo teléfono esta vacío");
       }else{
           String table_name = "tipo_telefono";
            String campos = " * ";
            String otros = " where nombre = '"+usuario+"' ";
            java.sql.ResultSet resultSet = 
          this.conector.ObtenerDatosParaTabla(table_name,campos ,otros);
           try {
               if( !(resultSet.first()) ){
                   String tableI = "tipo_telefono";
                   String camposI = "nombre,usuario_id,fecha";
                   String valoresI = " '"+usuario+"','"+this.usuarioID+"',now() ";
                   this.conector.insertData(tableI,camposI , valoresI);
                   this.validarOrigen();
                   
               }else{
                   JOptionPane.showMessageDialog(null, "El tipo teléfono ya existe, favor ingrese otro");
               }
           } catch (SQLException ex) {
               Logger.getLogger(TipoTelefonoAdministrar.class.getName())
                       .log(Level.SEVERE, null, ex);
           }
       }
       
       
    }
    public void editar(){
       String usuario = this.jTextField1.getText();

       if(usuario.isEmpty()){
           JOptionPane.showMessageDialog(null,"El campo tipo teléfono esta vacío");
       }else{
           String table = "tipo_telefono";
           String campos = "nombre='"+usuario+"' ";
           String where = " id = '"+this.CurrentUsuarioID+"' ";
           this.conector.ActualizarDato(table,campos , where);
           JOptionPane.showMessageDialog(null,"Se edito");
           this.jTextField1.setText("");
           this.cargarTabla();
       }
       
       
    }
    public void limpiar(){
         this.jTextField1.setText("");
    }
    public void eliminar(){
       int index = this.jTable1.getSelectedRow();
        if(index >= 0){
           if(JOptionPane.showConfirmDialog(null, "Esta seguro que desea eliminar el tipo teléfono?",
                   "Eliminar tipo teléfono", JOptionPane.YES_NO_OPTION,
                   JOptionPane.QUESTION_MESSAGE) 
                   == JOptionPane.YES_OPTION ){ 
                this.CurrentUsuarioID = this.jTable1.getValueAt(index, 0)
                        .toString();
                String table = "tipo_telefono";
                String campos = "visible=false ";
                String where = " id = '"+this.CurrentUsuarioID+"' ";
                this.conector.ActualizarDato(table,campos , where);
                JOptionPane.showMessageDialog(null,"Se Elimino");
                this.limpiar();
                this.cargarTabla();
           }
        }else{
            JOptionPane.showMessageDialog(null, "No hay fila seleccionada");

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
        String table_name = "tipo_telefono";
        String campos = " * ";
        String otros = " where id = '"+id+"' ";
        java.sql.ResultSet resultSet = this.conector.ObtenerDatosParaTabla(table_name,campos ,otros);
        if( resultSet.first() ){
             this.CurrentUsuarioID = resultSet.getString("id");
             this.jTextField1.setText(resultSet.getString("nombre"));
             this.MostrarBotones(false, true,true );
          
        }else{
            JOptionPane.showMessageDialog(null, "No hay registro");
            //System.out.println("");
        }
          } catch (SQLException ex) {
            System.out.println(ex.getCause().toString());
          }
        }else{
            JOptionPane.showMessageDialog(null, "No hay fila seleccionada");
        }
    }
    
  public void MostrarBotones(boolean b1,boolean b2,boolean b3){
      this.jButton1.setVisible(b1);
      this.jButton2.setVisible(b2);
      this.jButton3.setVisible(b3);
  }
   public void cargarTabla(){
        try {
        String table_name = "tipo_telefono";
        String campos = " * ";
        String otros = " where visible = true "+this.Buscador;
        java.sql.ResultSet resultSet = this.conector.ObtenerDatosParaTabla(table_name,campos ,otros);
        if( resultSet.first() ){
            int total = 0;
        do{
            total++;
        }while(resultSet.next());
        resultSet.first();
        String[] titulos = {"ID","NOMBRE"};
        Object[][] fila = new Object[total][10];
            
         int c = 0;
         
          do{
                fila[c][0] = resultSet.getString("id");
                    fila[c][1] = resultSet.getString("nombre");
                    c++;
             } while(resultSet.next());
          DefaultTableModel modelo = new DefaultTableModel(fila,titulos);
          this.jTable1.setModel(modelo);
          this.MostrarBotones(true, false, false);
        }else{
            JOptionPane.showMessageDialog(null, "No hay registro");
            //System.out.println("");
        }
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
        jButton1 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jTextFieldBuscador = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jTextField1 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();

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

        jButton1.setText("Agregar");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton1MousePressed(evt);
            }
        });

        jLabel9.setText("Que deseas buscar?");

        jTextFieldBuscador.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldBuscadorKeyReleased(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTable1.setToolTipText("Click izquierdo para seleccionar la fila y luego click derecho para desplegar las opciones");
        jTable1.setComponentPopupMenu(jPopupMenu1);
        jScrollPane1.setViewportView(jTable1);

        jLabel1.setText("Tipo de teléfono");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jButton2)
                        .addGap(9, 9, 9)
                        .addComponent(jButton3)
                        .addGap(35, 35, 35)
                        .addComponent(jButton1)
                        .addGap(19, 19, 19)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(jTextFieldBuscador, javax.swing.GroupLayout.PREFERRED_SIZE, 530, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 546, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2)
                    .addComponent(jButton3)
                    .addComponent(jButton1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(6, 6, 6)
                        .addComponent(jTextFieldBuscador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(jLabel1)
                        .addGap(6, 6, 6)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 377, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    private void jButton1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MousePressed
        // TODO add your handling code here:
        this.insert();
    }//GEN-LAST:event_jButton1MousePressed

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
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextFieldBuscador;
    // End of variables declaration//GEN-END:variables
}
