/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Formularios;

import Conexion.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author hp
 */
public class UsuariosAdministrar extends javax.swing.JFrame {

   private String usuarioID="1";
   private String CurrentUsuarioID="1";
   private String usuarioNombre="admin";
   private ConectarBD conector = null;
   private String Buscador = "";
   private Statement st;
   private ResultSet rs;
   
   
   public void setUsuarioID(String usuarioID){
         this.usuarioID = usuarioID;
         }
     public void setUsuarioNombre(String usuarioNombre){
         this.usuarioNombre = usuarioNombre;
         }
     
     public void setConector(ConectarBD conector){
         this.conector = conector;
   }
     
   public void MostrarBotones (boolean b1,boolean b2,boolean b3 ){
       this.jButtonEditar.setVisible(b1);
       this.jButtonAdd.setVisible(b2);
       this.jButtonCancel.setVisible(b3);
   }
   
   public void cargarTabla(){
       try {
           String table_name = "usuario";
           String campos = " * ";
           String otros = " where visible=true";
           
            ResultSet rs=this.conector.ObtenerDatosParaTabla(table_name, campos, otros);
            
            if(rs.first()){
                int total=0;
                do {
                    total++;
                }while(rs.next());
                rs.first();
                
                String[] titulos = {"ID", "TITULOS", "TIPO"};
                
                Object [][] fila = new Object[total][10]; 
                
                int c=0;
                
                do{
                    fila[c][0]=rs.getString("id");
                    fila[c][1]=rs.getString("nombre");
                    fila[c][2]=rs.getString("tipo");
                  c++;
                }while (rs.next());
            DefaultTableModel modelo = new DefaultTableModel(fila, titulos);
            this.jTable1.setModel(modelo);
            this.MostrarBotones(true, false,false);
            }else{
                JOptionPane.showMessageDialog(null, "No hay registro");
                }  }    
        catch (SQLException e){
            JOptionPane.showMessageDialog(null, "Contraseña incorrecta: " +e.getCause().toString());
        }
   }
      
   
    
    public void insert(){
    String usuario=this.jTextFieldUser.getText();
    String clave = new String (this.jPasswordField1.getPassword());
    String tipo = this.jComboBox1.getSelectedItem().toString();
    if(usuario.isEmpty()){
        JOptionPane.showMessageDialog(null, "El campo usuario esta vacio");
    } else if (clave.isEmpty()) {
        JOptionPane.showMessageDialog(null, "El campo clave esta vacio");
    } else if (clave.length()<= 6) {
        JOptionPane.showMessageDialog(null, "La clave debe contener mas de 6 caracteres");
    }else{
            String table_name = "usuario";
            String campos = "*";
            String otros = " where nombre = '"+usuario+"' ";
            rs= this.conector.ObtenerDatosParaTabla(table_name,campos ,otros);
            try {
               if( !(rs.first()) ){
                   String tableI = "usuario";
                   String camposI = "nombre,clave,tipo,usuario_id,fecha";
                   String valoresI = " '"+usuario+"','"+clave+"','"+tipo+"','"+this.usuarioID+"',now() ";
                   this.conector.insertData(tableI,camposI , valoresI);
                   JOptionPane.showMessageDialog(null,"Se inserto");
                   this.jTextFieldUser.setText("");
                   this.jPasswordField1.setText("");
                   this.jComboBox1.setSelectedIndex(0);
                   this.cargarTabla();
               }else{
                   JOptionPane.showMessageDialog(null, "El "
                           + "usuario ya existe, favor ingrese otro");
               }
           } catch (SQLException ex) {
               Logger.getLogger(UsuariosAdministrar.class.getName())
                       .log(Level.SEVERE, null, ex);
           }
    }
    }
       public void editar(){
       String usuario = this.jTextFieldUser.getText();
       String clave = new String(this.jPasswordField1.getPassword());
       String tipo = this.jComboBox1.getSelectedItem().toString();
            
       if(usuario.isEmpty()){
           JOptionPane.showMessageDialog(null,"El campo usuario esta vacío");
       }else if(clave.isEmpty()){
            JOptionPane.showMessageDialog(null,"El campo clave esta vacío");
       }else if(clave.length() <= 6){
            JOptionPane.showMessageDialog(null,"La clve tiene que tener mas de 6 caracteres");
       }else{
           String table = "usuario";
           String campos = "nombre='"+usuario+"',clave='"+clave+"',tipo='"+tipo+"' ";
           String where = " id = '"+this.CurrentUsuarioID+"' ";
           this.conector.ActualizarDato(table,campos , where);
           JOptionPane.showMessageDialog(null,"Se edito");
           this.jTextFieldUser.setText("");
           this.jPasswordField1.setText("");
           this.jComboBox1.setSelectedIndex(0);
           this.cargarTabla();
       }
       }
    public void limpiar(){
         this.jTextFieldUser.setText("");
         this.jPasswordField1.setText("");
         this.jComboBox1.setSelectedIndex(0);
                
    }
    public void eliminar(){
       int index = this.jTable1.getSelectedRow();
        if(index >= 0){
           if(JOptionPane.showConfirmDialog(null, "Esta seguro que desea eliminar el usuario?", "Eliminar usuario", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION ){this.CurrentUsuarioID = this.jTable1.getValueAt(index, 0).toString();
                String table = "usuario";
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
    
    public void BuscadorCliente(){
         this.Buscador = "";
        if(!(this.jTextFieldBuscador.getText().isEmpty())){
            this.Buscador = " and concat(nombre,' ',tipo,' ',c.id) like '%"+this.jTextFieldBuscador.getText()+"%'";
            //this.cargarTabla();
        }
            this.cargarTabla();
        
    }

       
       public void MostrarDato(){
        int index = this.jTable1.getSelectedRow();
        if(index >= 0){
        String id = this.jTable1.getValueAt(index, 0).toString();
        
        try {
        String table_name = "usuario";
        String campos = " * ";
        String otros = " where id = '"+id+"' ";
        rs = this.conector.ObtenerDatosParaTabla(table_name,campos ,otros);
        if( rs.first() ){
             this.CurrentUsuarioID = rs.getString("id");
             this.jTextFieldUser.setText(rs.getString("nombre"));
             this.jPasswordField1.setText(rs.getString("clave"));
             this.jComboBox1.setSelectedItem(rs.getString("tipo"));
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
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButtonAdd = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jTextFieldUser = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jPasswordField1 = new javax.swing.JPasswordField();
        jComboBox1 = new javax.swing.JComboBox<>();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jTextField7 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTextField9 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jButtonCancel = new javax.swing.JButton();
        jButtonEditar = new javax.swing.JButton();
        jTextFieldBuscador = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButtonAdd.setText("Añadir");
        jButtonAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddActionPerformed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jLabel10.setText("Usuario");

        jLabel11.setText("Clave");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "cajero", "supervisor", "cajero" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(49, 49, 49)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel11)
                        .addComponent(jLabel10)
                        .addComponent(jPasswordField1, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                        .addComponent(jTextFieldUser)))
                .addContainerGap(145, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextFieldUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(141, Short.MAX_VALUE))
        );

        jComboBox1.getAccessibleContext().setAccessibleName("");

        jTabbedPane1.addTab("Usuario", jPanel5);

        jLabel1.setText("Nombre");

        jTextField1.setPreferredSize(new java.awt.Dimension(250, 20));

        jLabel2.setText("Apellido");

        jTextField2.setPreferredSize(new java.awt.Dimension(250, 20));

        jLabel5.setText("Cedula");
        jLabel5.setToolTipText("");

        jTextField3.setPreferredSize(new java.awt.Dimension(250, 20));

        jLabel6.setText("RNC");

        jTextField4.setPreferredSize(new java.awt.Dimension(250, 20));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addContainerGap(45, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(75, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Datos personales", jPanel1);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 314, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Telefono", jPanel4);

        jLabel4.setText("Calle");

        jTextField7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField7ActionPerformed(evt);
            }
        });

        jLabel7.setText("Residencial (Opcional):");

        jTextField8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField8ActionPerformed(evt);
            }
        });

        jLabel8.setText("Número:");

        jTextField9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField9ActionPerformed(evt);
            }
        });

        jLabel9.setText("Sector:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel7)
                        .addComponent(jLabel4)
                        .addComponent(jTextField6, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
                        .addComponent(jTextField7)))
                .addContainerGap(136, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(33, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Dirección", jPanel3);

        jLabel3.setText("Correo");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(73, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(220, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Email", jPanel2);

        jButtonCancel.setText("Cancelar");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        jButtonEditar.setText("Editar");
        jButtonEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEditarActionPerformed(evt);
            }
        });

        jTextFieldBuscador.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldBuscadorKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 81, Short.MAX_VALUE)
                        .addComponent(jButtonEditar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonAdd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonCancel)
                        .addGap(44, 44, 44)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 394, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jTextFieldBuscador, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(23, Short.MAX_VALUE)
                .addComponent(jTextFieldBuscador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 422, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 328, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonAdd)
                    .addComponent(jButtonCancel)
                    .addComponent(jButtonEditar))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

     public UsuariosAdministrar(ConectarBD conector, String usuarioID, String usuarioNombre) {
        initComponents();
         this.setConector(conector);
         this.setUsuarioID(usuarioID);
         this.setUsuarioNombre(usuarioNombre);
         this.cargarTabla();
    }
    
    private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddActionPerformed
this.insert();        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonAddActionPerformed

    private void jTextField7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField7ActionPerformed

    private void jTextField8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField8ActionPerformed

    private void jTextField9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField9ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField9ActionPerformed

    private void jButtonEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEditarActionPerformed
this.editar();        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonEditarActionPerformed

    private void jTextFieldBuscadorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldBuscadorKeyReleased
     this.BuscadorCliente();   // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldBuscadorKeyReleased

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
this.MostrarBotones(true, false, false);
this.limpiar();        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonCancelActionPerformed

    /**
     * @param args the command line arguments
     */
   

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAdd;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonEditar;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    private javax.swing.JTextField jTextFieldBuscador;
    private javax.swing.JTextField jTextFieldUser;
    // End of variables declaration//GEN-END:variables

   
}
