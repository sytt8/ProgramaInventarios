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
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author hp
 */
public class ProveedorAdministrar extends javax.swing.JFrame {

    /**
     * Creates new form ProveedorAdministrar
     */
   private String usuarioID = "1";
    private String CurrentClienteID ="1";
    private String CurrentPersonaID ="1";
    private String usuarioNombre = "admin";
    private ConectarBD conector = null;
    
    private ArrayList<String> PersonaEmails = new ArrayList<String>();
    private ArrayList<String> PersonaEmailIDs = new ArrayList<String>();
    
    private ArrayList<String> PersonaDireccion = new ArrayList<String>();
    private ArrayList<String> PersonaDireccionIDs = new ArrayList<String>();
    
    private ArrayList<String> PersonaTelefono = new ArrayList<String>();
    private ArrayList<String> PersonaTelefonoTipoNombre = new ArrayList<String>();
    private ArrayList<String> PersonaTelefonoTipoID = new ArrayList<String>();
    private ArrayList<String> PersonaTelefonoIDs = new ArrayList<String>();
 
    private ArrayList<String> ArrayTipoTelefonoIDs =new ArrayList<String>();
       
    private String CurrentEmailID = "1",CurrentDireccionID = "1",CurrentTelefonoID = "1";
    private boolean EditarClienteActivado = false;
    
    private String Buscador = "";
    private ProveedorAdministrar yo;
    private String Origen= "proveedor";
    private CompraProductoAdministrar compraProductoAdministrar;
    private String nombre,apellido,cedula,rnc;
    
    public void setFOrigen(CompraProductoAdministrar compraProductoAdministrar, String origen){
        this.compraProductoAdministrar = compraProductoAdministrar;
        this.Origen = origen;
    }
    
        public void validarOrigen(){
            if(this.Origen.equals("compraproducto")){
                this.compraProductoAdministrar.RecargarProveedor();
                this.setVisible(false);
            }else{
                JOptionPane.showMessageDialog(null,"Se inserto");
                this.limpiar();
                this.cargarTabla();
            }
    }
        
    public void setYo(ProveedorAdministrar yo) {
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
    
    
    public ProveedorAdministrar(ConectarBD conector,String usuarioID,String usuarioNombre) {
        initComponents();
        this.setConector(conector);
        this.setUsuarioID(usuarioID);
        this.setUsuarioNombre(usuarioNombre);
        this.cargarTabla();
        this.MostrarBotonesEmail(true, false, false);
        this.MostrarBotonesDireccion(true, false, false);
        this.MostrarBotonesTelefono(true, false, false);
        this.setLocationRelativeTo(null);
        this.MostrarTipoTelefono();
        this.MostrarBotones(true, false, false);
    }
 
    /*
        Buscador
    */
    public void BuscadorCliente(){
        if(!(this.jTextFieldBuscador.getText().isEmpty())){
            this.Buscador = " and concat(p.nombre,' ',p.apellido,' ',p.cedula,' ',c.rnc,' ',c.id) like '%"+this.jTextFieldBuscador.getText()+"%'";
            this.cargarTabla();
        }else{
            this.Buscador = "";
            this.cargarTabla();
        }
    }
    
    public void agregarTipoTelefono(){
        if(this.Origen.equals("proveedor")){
        TipoTelefonoAdministrar tipoTelefono = new TipoTelefonoAdministrar(this.conector,this.usuarioID,this.usuarioNombre);
        tipoTelefono.setFOrigen(this.yo,"proveedor");
        tipoTelefono.setVisible(true);
        }else if(this.Origen.equals("compraproducto")){
            TipoTelefonoAdministrar tipoTelefono = new TipoTelefonoAdministrar(this.conector,this.usuarioID,this.usuarioNombre);
            tipoTelefono.setFOrigen(this.yo,"compraproducto");
            tipoTelefono.setVisible(true);
        }
    }
    
    
    /*
    Tipo telefono
    */
        public void RecargarTipoTelefono(){
            this.MostrarTipoTelefono();
        }
       public void MostrarTipoTelefono(){
       
        //String tipo = this.jTable1.getValueAt(index, 0).toString();
        //JOptionPane.showMessageDialog(null, nombre+" "+id+" "+tipo);
        try {
            String table_name = "tipo_telefono ";
            String campos = " * ";
            String otros = " where visible = true ";
            java.sql.ResultSet resultSet = this.conector.ObtenerDatosParaTabla(table_name,campos ,otros);
            if( resultSet.first() ){
                this.ArrayTipoTelefonoIDs.clear();
                this.jComboBoxTipoTelefono.setModel(new DefaultComboBoxModel());
                do{
                        this.ArrayTipoTelefonoIDs.add(resultSet.getString("id"));
                       this.jComboBoxTipoTelefono.addItem(resultSet.getString("nombre"));
                }while(resultSet.next());
            }else{
               JOptionPane.showMessageDialog(null, "No hay registro");
               //System.out.println("");
           }
          } catch (Exception ex) {
            System.out.println(ex.getCause().toString());
          }
        
    }

    /*
   ADMINISTRAR CLIENTE / PERSONA
   */
    public void insert(){
       //valida si podemos continuar
        if(this.validarVacioContinuo()){
           //queremos evitar que un cliente se ingrese dos veces por esto validamos si existe la cedula o el rnc
           String table = "proveedor as c "
                + "inner join persona as p on c.persona_id = p.id";
            String campos = " c.id ";
            String otros = " where (c.rnc = '"+this.rnc+"' )or(p.cedula ='"+this.cedula+"' ) ";
            java.sql.ResultSet resultSet = this.conector.ObtenerDatosParaTabla(table,campos ,otros);
           try {
               //validamos de que el resultado de la db sea 0 si lo es podemos crear el cliente
               if( !(resultSet.first()) ){
                   //insertamos primero a persona porque es una entidad fuerte
                   String tableI = "persona";
                   String camposI = "nombre,apellido,cedula,usuario_id,fecha";
                   String valoresI = " '"+this.nombre+"','"+this.apellido+"','"+this.cedula+"','"+this.usuarioID+"',now() ";
                   this.conector.insertData(tableI,camposI , valoresI);
                   
                   //obtenemos el ultimo id de la entidad persona
                   String idPersona = this.conector.obtenerUltimoID(tableI);
                   
                   //insertamos cliente
                   String tableIC = "proveedor";
                   String camposIC = "persona_id,rnc,usuario_id,fecha";
                   String valoresIC = "'"+idPersona+"', '"+this.rnc+"','"+this.usuarioID+"',now() ";
                   this.conector.insertData(tableIC,camposIC , valoresIC);
                  
                   
                    //validamos que hayan email para agregar
                    if(this.PersonaEmails.size() > 0){
                        for (int i = 0; i < this.PersonaEmails.size(); i++) {
                        String tableIPE = "persona_email";
                        String camposIPE = "persona_id,correo,usuario_id,fecha";
                        String valoresIPE = "'"+idPersona+"', '"+this.PersonaEmails.get(i)+"','"+this.usuarioID+"',now() ";
                        this.conector.insertData(tableIPE,camposIPE , valoresIPE);
                     }
                    }
                    //Dirección
                    if(this.PersonaDireccion.size() > 0){
                        for (int i = 0; i < this.PersonaDireccion.size(); i++) {
                        String tableIPE = "persona_direccion";
                        String camposIPE = "persona_id,direccion,usuario_id,fecha";
                        String valoresIPE = "'"+idPersona+"', '"+this.PersonaDireccion.get(i)+"','"+this.usuarioID+"',now() ";
                        this.conector.insertData(tableIPE,camposIPE , valoresIPE);
                     }
                    }
                    //Teléfono
                    if(this.PersonaTelefono.size() > 0){
                        for (int i = 0; i < this.PersonaTelefono.size(); i++) {
                        String tableIPE = "persona_telefono";
                        String camposIPE = "persona_id,telefono,tipo_telefono_id,usuario_id,fecha";
                        String valoresIPE = "'"+idPersona+"', '"+this.PersonaTelefono.get(i)+"', '"+this.PersonaTelefonoTipoID.get(i)+"','"+this.usuarioID+"',now() ";
                        this.conector.insertData(tableIPE,camposIPE , valoresIPE);
                     }
                    }
                  /* //validamos que haya teléfono para agregar
                   String tableIC = "cliente";
                   String camposIC = "persona_id,rnc,usuario_id,fecha";
                   String valoresIC = "'"+idPersona+"', '"+this.rnc+"','"+this.usuarioID+"',now() ";
                   this.conector.insertData(tableIC,camposIC , valoresIC);
                   */
                   this.validarOrigen();

               }else{
                   JOptionPane.showMessageDialog(null, "El provedor ya existe, favor ingrese otro");
               }
           } catch (SQLException ex) {
               Logger.getLogger(ProveedorAdministrar.class.getName()).log(Level.SEVERE, null, ex);
           }
       }
       
       
    }
    public boolean validarVacioContinuo(){
       this.nombre =  this.jTFNombre.getText();
       this.apellido =  this.jTFApellido.getText();
       this.rnc =  this.jTFRNC.getText();
       this.cedula =  this.jTFCedula.getText();
       //significa no puede continuar
       boolean respuesta = false;
       //validamos que el dato no este vacío
       if(nombre.isEmpty()){
           JOptionPane.showMessageDialog(null,"El campo nombre esta vacío");
       }else if(apellido.isEmpty()){
            JOptionPane.showMessageDialog(null,"El campo apellido esta vacío");
       }else if(rnc.isEmpty() && cedula.isEmpty()){
            JOptionPane.showMessageDialog(null,"debe agregar la cedula o el rnc");
       }else{
           //como los campos necesario no estan vacío le decimos que puede continuar
           respuesta = true;
       }
       return respuesta;
    }
    public void editar(){
      

       if(this.validarVacioContinuo()){
           String table = "proveedor as c "
                + "inner join persona as p on c.persona_id = p.id";
           String campos = "p.nombre='"+this.nombre+"',p.apellido='"+this.apellido+"',p.cedula='"+this.cedula+"',c.rnc='"+this.rnc+"' ";
           String where = " c.id = '"+this.CurrentClienteID+"' ";
           this.conector.ActualizarDato(table,campos , where);
           JOptionPane.showMessageDialog(null,"Se edito");
           this.limpiar();
           this.cargarTabla();
           this.EditarClienteActivado = false;
        }
       
       
    }
    public void limpiar(){
        this.jTFApellido.setText("");
        this.jTFNombre.setText("");
        this.jTFRNC.setText("");
        this.jTFCedula.setText("");
        this.jTableEmail.setModel(new DefaultTableModel());
        this.jTabbedPane1.setSelectedIndex(0);
        this.PersonaEmailIDs.clear();
        this.PersonaEmails.clear();
        
        this.jTableDireccion.setModel(new DefaultTableModel());
        this.PersonaDireccionIDs.clear();
        this.PersonaDireccion.clear();
        
        this.jTableTelefono.setModel(new DefaultTableModel());
        this.PersonaTelefonoIDs.clear();
        this.PersonaTelefono.clear();
        this.PersonaTelefonoTipoNombre.clear();
        this.PersonaTelefonoTipoID.clear();
    }
    public void eliminar(){
       int index = this.jTableCliente.getSelectedRow();
        if(index >= 0){
           if(JOptionPane.showConfirmDialog(null, "Esta seguro que desea eliminar el proveedor?", "Eliminar proveedor", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION ){ 
                this.CurrentClienteID = this.jTableCliente.getValueAt(index, 0).toString();
                String table = "cliente";
                String campos = "visible=false ";
                String where = " id = '"+this.CurrentClienteID+"' ";
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
        int index = this.jTableCliente.getSelectedRow();
        if(index >= 0){
        String id = this.jTableCliente.getValueAt(index, 0).toString();
        //String nombre= this.jTable1.getValueAt(index, 0).toString();
        //String tipo = this.jTable1.getValueAt(index, 0).toString();
        //JOptionPane.showMessageDialog(null, nombre+" "+id+" "+tipo);
        try {
        String table_name = "proveedor as c "
                + "inner join persona as p on c.persona_id = p.id "
                + "inner join usuario as u on p.usuario_id = u.id ";
        String campos = " c.rnc,c.id,p.nombre,p.apellido,p.cedula, u.nombre as usuario, p.id as personaid ";
        String otros = " where c.id = '"+id+"' and c.visible = true ";
        java.sql.ResultSet resultSet = this.conector.ObtenerDatosParaTabla(table_name,campos ,otros);
        if( resultSet.first() ){
                   //Mostrar datos personales y del cliente
                   this.CurrentClienteID = resultSet.getString("id");
                   this.jTFApellido.setText(resultSet.getString("apellido"));
                   this.jTFNombre.setText(resultSet.getString("nombre"));
                   this.jTFCedula.setText(resultSet.getString("cedula"));
                   this.jTFRNC.setText(resultSet.getString("rnc"));
                   this.CurrentPersonaID = resultSet.getString("personaid");
                   this.MostrarBotones(false, true,true );
                   
                   this.EditarClienteActivado = true;
                   //email
                   this.MostrarEmails();
                   this.MostrarBotonesEmail(true,false, false);
                   
                   //direccion
                   this.MostrarDirecciones();
                   this.MostrarBotonesDireccion(true,false, false);
                   
                   //telefono
                   this.MostrarTelefonos();
                   this.MostrarBotonesTelefono(true,false, false);
        
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
      this.jButtonAgregarCliente.setVisible(b1);
      this.jButtonEditarCliente.setVisible(b2);
      this.jButtonCancelarCliente.setVisible(b3);
      
  }
   public void cargarTabla(){
        try {
        String table_name = "proveedor as c inner join persona as p on c.persona_id = p.id "
                + "inner join usuario as u on p.usuario_id = u.id ";
        String campos = " c.id, c.rnc, p.nombre,p.apellido,p.cedula, u.nombre as usuario ";
        String otros = " where  c.visible = true "+this.Buscador+" ";
        java.sql.ResultSet resultSet = this.conector.ObtenerDatosParaTabla(table_name,campos ,otros);
        if( resultSet.first() ){
            int total = 0;
        do{
            total++;
        }while(resultSet.next());
        resultSet.first();
        String[] titulos = {"ID","NOMBRE","APELLIDO","CEDULA","RNC","CREADO POR"};
        
        Object[][] fila = new Object[total][10];
            
         int c = 0;
         
          do{
                fila[c][0] = resultSet.getString("id");
                    fila[c][1] = resultSet.getString("nombre");
                    fila[c][2] = resultSet.getString("apellido");
                    fila[c][3] = resultSet.getString("cedula"); 
                    fila[c][4] = resultSet.getString("rnc");
                    fila[c][5] = resultSet.getString("usuario");
                    c++;
             } while(resultSet.next());
          DefaultTableModel modelo = new DefaultTableModel(fila,titulos);
          this.jTableCliente.setModel(modelo);
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

   /*
   ADMINISTRAR EMAIL
   */
   public void agregarEmail(){
       
        String email =this.jTextFieldEmail.getText(); 
        if(email.isEmpty()){
            JOptionPane.showMessageDialog(null, "El campo email esta vacío");
        }else{
            
            //Validamos que se este actualizando un cliente para poder saber que hacer
            if(this.EditarClienteActivado){
                //validamos de que ese email no exista
                String table = " persona_email ";
                String campos = " id ";
                String otros = " where correo like '%"+email+"%'  and persona_id = '"+this.CurrentPersonaID+"'  ";
                java.sql.ResultSet resultSet = this.conector.ObtenerDatosParaTabla(table,campos ,otros);
           try {
               //validamos de que el resultado de la db sea 0 si lo es podemos crear el cliente
               if( !(resultSet.first()) ){
                   //insertamos primero a persona porque es una entidad fuerte
                   String tableI = "persona_email";
                   String camposI = "correo,persona_id,usuario_id,fecha";
                   String valoresI = " '"+email+"','"+this.CurrentPersonaID+"','"+this.usuarioID+"',now() ";
                   this.conector.insertData(tableI,camposI , valoresI);
                  this.MostrarEmails();
               }else{
                   JOptionPane.showMessageDialog(null, "El proveedor ya existe, favor ingrese otro");
               }
            }   catch (SQLException ex) {
                    Logger.getLogger(ProveedorAdministrar.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{
                this.PersonaEmails.add(this.jTextFieldEmail.getText());
                this.PersonaEmailIDs.add("0");
                this.cargarTablaEmail();
            }
        this.MostrarBotonesEmail(true, false, false);
         this.limpiarEmail();
        }
   }
   public void cargarTablaEmail(){
        int total = this.PersonaEmails.size();
        String[] titulos = {"ID","EMAIL"};
        Object[][] fila = new Object[total][3];
        int c = 0; 
          while(c < total){
                fila[c][0] = this.PersonaEmailIDs.get(c);
                fila[c][1] = this.PersonaEmails.get(c);
                c++;
             }
          DefaultTableModel modelo = new DefaultTableModel(fila,titulos);
          this.jTableEmail.setModel(modelo);
        
   }
   public void MostrarBotonesEmail(boolean agregar,boolean editar,boolean cancelar){
      this.jButtonEmailAgregar.setVisible(agregar);
      this.jButtonEmailEditar.setVisible(editar);
      this.jButtonEmailCancelar.setVisible(cancelar);   
  }
   public void limpiarEmail(){
       this.jTextFieldEmail.setText("");
   }
   public void eliminarEmail(){
       int index = this.jTableEmail.getSelectedRow();
        if(index >= 0){
           if(JOptionPane.showConfirmDialog(null, "Esta seguro que desea eliminar el email?", "Eliminar email", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION ){ 
              
               //Validamos que se este actualizando un cliente para poder saber que hacer
            if(this.EditarClienteActivado){
               try {
                   String id = this.jTableEmail.getValueAt( index,0 ).toString();
                   String tableE = "persona_email";
                   String camposE = "visible = false ";
                   String otrosE = " id = '"+id+"'  ";
                   this.conector.ActualizarDato(tableE,camposE , otrosE);
                  this.MostrarEmails();
               
               } catch (Exception ex) {
                    Logger.getLogger(ProveedorAdministrar.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{
            
               this.PersonaEmailIDs.remove(index);
               this.PersonaEmails.remove(index);
                this.cargarTablaEmail();
               }
               
               
               
               
               
               /*this.CurrentClienteID = this.jTableCliente.getValueAt(index, 0).toString();
                String table = "cliente";
                String campos = "visible=false ";
                String where = " id = '"+this.CurrentClienteID+"' ";
                this.conector.ActualizarDato(table,campos , where);
                JOptionPane.showMessageDialog(null,"Se Elimino");
                */
                
                this.limpiarEmail();
                this.MostrarBotonesEmail(true, false, false);
           }
        }else{
            JOptionPane.showMessageDialog(null, "No hay fila seleccionada");

       }
       
       
    }
   public void MostrarEmails(){
       
        //String tipo = this.jTable1.getValueAt(index, 0).toString();
        //JOptionPane.showMessageDialog(null, nombre+" "+id+" "+tipo);
        try {
            String table_name = "persona_email ";
            String campos = " * ";
            String otros = " where persona_id = '"+this.CurrentPersonaID+"' and visible = true ";
            java.sql.ResultSet resultSet = this.conector.ObtenerDatosParaTabla(table_name,campos ,otros);
            this.PersonaEmailIDs.clear();
            this.PersonaEmails.clear();
            if( resultSet.first() ){
                do{
                       this.PersonaEmailIDs.add(resultSet.getString("id"));
                       this.PersonaEmails.add(resultSet.getString("correo"));
                }while(resultSet.next());
               
                this.MostrarBotones(false, true,true );
                this.cargarTablaEmail();
          }else{
            JOptionPane.showMessageDialog(null, "No hay registro");
            //System.out.println("");
        }
        this.MostrarBotonesEmail(false, true, true);
          } catch (Exception ex) {
            System.out.println(ex.getCause().toString());
          }
        
    }
    public void MostrarDatoEmail(){
        int index = this.jTableEmail.getSelectedRow();
        if(index >= 0){
        
        //String tipo = this.jTable1.getValueAt(index, 0).toString();
        //JOptionPane.showMessageDialog(null, nombre+" "+id+" "+tipo);
        try {
            if(this.EditarClienteActivado){
                this.CurrentEmailID = this.jTableEmail.getValueAt(index, 0).toString();
            }else{
                this.CurrentEmailID = index+"";
            }
            String email= this.jTableEmail.getValueAt(index, 1).toString();
            this.jTextFieldEmail.setText(email);
                 /*
        String table_name = "cliente as c "
                + "inner join persona as p on c.persona_id = p.id "
                + "inner join usuario as u on p.usuario_id = u.id ";
        String campos = " c.rnc,c.id,p.nombre,p.apellido,p.cedula, u.nombre as usuario ";
        String otros = " where c.id = '"+id+"' and c.visible = true ";
        java.sql.ResultSet resultSet = this.conector.ObtenerDatosParaTabla(table_name,campos ,otros);
        if( resultSet.first() ){
                   this.CurrentClienteID = resultSet.getString("id");
                   this.jTFApellido.setText(resultSet.getString("apellido"));
                   this.jTFNombre.setText(resultSet.getString("nombre"));
                   this.jTFCedula.setText(resultSet.getString("cedula"));
                   this.jTFRNC.setText(resultSet.getString("rnc"));
                   this.MostrarBotones(false, true,true );
          }else{
            JOptionPane.showMessageDialog(null, "No hay registro");
            //System.out.println("");
        }*/
        this.MostrarBotonesEmail(false, true, true);
          } catch (Exception ex) {
            System.out.println(ex.getCause().toString());
          }
        }else{
            JOptionPane.showMessageDialog(null, "No hay fila seleccionada");
        }
    }
    public void editarEmail(){
        String email =this.jTextFieldEmail.getText();
        if(email.isEmpty()){
            JOptionPane.showMessageDialog(null, "El campo email esta vacío");
        }else{
            //Validamos que se este actualizando un cliente para poder saber que hacer
            if(this.EditarClienteActivado){
                //validamos de que ese email no exista
                String table = " persona_email ";
                String campos = " id ";
                String otros = " where correo like '%"+email+"%'  and persona_id = '"+this.CurrentPersonaID+"'  ";
                java.sql.ResultSet resultSet = this.conector.ObtenerDatosParaTabla(table,campos ,otros);
           try {
               //validamos de que el resultado de la db sea 0 si lo es podemos crear el cliente
               if( !(resultSet.first()) ){
                   //insertamos primero a persona porque es una entidad fuerte
                   String tableE = "persona_email";
                   String camposE = "correo = '"+email+"' ";
                   String otrosE = " id = '"+this.CurrentEmailID+"'  ";
                   this.conector.ActualizarDato(tableE,camposE , otrosE);
                  this.MostrarEmails();
               }else{
                   JOptionPane.showMessageDialog(null, "El email ya existe, favor ingrese otro");
               }
            }   catch (SQLException ex) {
                    Logger.getLogger(ProveedorAdministrar.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{
            
                 this.PersonaEmails.set(Integer.parseInt(this.CurrentEmailID),this.jTextFieldEmail.getText() );
                 this.cargarTablaEmail();
            }
           /* String table = "cliente as c "
                + "inner join persona as p on c.persona_id = p.id";
           String campos = "p.nombre='"+this.nombre+"',p.apellido='"+this.apellido+"',p.cedula='"+this.cedula+"',c.rnc='"+this.rnc+"' ";
           String where = " c.id = '"+this.CurrentClienteID+"' ";
           this.conector.ActualizarDatos(table,campos , where);*/
           JOptionPane.showMessageDialog(null,"Se edito");
           this.limpiarEmail();
           this.MostrarBotonesEmail(true, false, false);
      
       }
    }
   /*
   ADMINISTRAR DIRECCION
   */
   
     public void agregarDireccion(){
       
        String direccion =this.jTextAreaDireccion.getText(); 
        if(direccion.isEmpty()){
            JOptionPane.showMessageDialog(null, "El campo dirección esta vacío");
        }else{
            
            //Validamos que se este actualizando un cliente para poder saber que hacer
            if(this.EditarClienteActivado){
                //validamos de que ese email no exista
                String table = " persona_direccion ";
                String campos = " id ";
                String otros = " where direccion like '%"+direccion+"%'  and persona_id = '"+this.CurrentPersonaID+"'  ";
                java.sql.ResultSet resultSet = this.conector.ObtenerDatosParaTabla(table,campos ,otros);
           try {
               //validamos de que el resultado de la db sea 0 si lo es podemos crear el cliente
               if( !(resultSet.first()) ){
                   //insertamos primero a persona porque es una entidad fuerte
                   String tableI = "persona_direccion";
                   String camposI = "direccion,persona_id,usuario_id,fecha";
                   String valoresI = " '"+direccion+"','"+this.CurrentPersonaID+"','"+this.usuarioID+"',now() ";
                   this.conector.insertData(tableI,camposI , valoresI);
                  this.MostrarDirecciones();
               }else{
                   JOptionPane.showMessageDialog(null, "El dirección ya existe, favor ingrese otro");
               }
            }   catch (SQLException ex) {
                    Logger.getLogger(ProveedorAdministrar.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{
                this.PersonaDireccion.add(this.jTextAreaDireccion.getText());
                this.PersonaDireccionIDs.add("0");
                this.cargarTablaDireccion();
            }
        this.MostrarBotonesDireccion(true, false, false);
         this.limpiarDireccion();
        }
   }
   public void cargarTablaDireccion(){
        int total = this.PersonaDireccion.size();
        String[] titulos = {"ID","DIRECCION"};
        Object[][] fila = new Object[total][3];
        int c = 0; 
          while(c < total){
                fila[c][0] = this.PersonaDireccionIDs.get(c);
                fila[c][1] = this.PersonaDireccion.get(c);
                c++;
             }
          DefaultTableModel modelo = new DefaultTableModel(fila,titulos);
          this.jTableDireccion.setModel(modelo);
        
   }
   public void MostrarBotonesDireccion(boolean agregar,boolean editar,boolean cancelar){
      this.jButtonDireccionAgregar.setVisible(agregar);
      this.jButtonDireccionEditar.setVisible(editar);
      this.jButtonDireccionCancelar.setVisible(cancelar);   
  }
   public void limpiarDireccion(){
       this.jTextAreaDireccion.setText("");
   }
   public void eliminarDireccion(){
       int index = this.jTableDireccion.getSelectedRow();
        if(index >= 0){
           if(JOptionPane.showConfirmDialog(null, "Esta seguro que desea eliminar la dirección?", "Eliminar dirección", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION ){ 
              
               //Validamos que se este actualizando un cliente para poder saber que hacer
            if(this.EditarClienteActivado){
               try {
                   String id = this.jTableDireccion.getValueAt( index,0 ).toString();
                   String tableE = "persona_direccion";
                   String camposE = "visible = false ";
                   String otrosE = " id = '"+id+"'  ";
                   this.conector.ActualizarDato(tableE,camposE , otrosE);
                  this.MostrarDirecciones();
               
               } catch (Exception ex) {
                    Logger.getLogger(ProveedorAdministrar.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{
            
               this.PersonaDireccionIDs.remove(index);
               this.PersonaDireccion.remove(index);
                this.cargarTablaDireccion();
               }
                this.limpiarDireccion();
                this.MostrarBotonesDireccion(true, false, false);
           }
        }else{
            JOptionPane.showMessageDialog(null, "No hay fila seleccionada");

       }
       
       
    }
   public void MostrarDirecciones(){
       
        //String tipo = this.jTable1.getValueAt(index, 0).toString();
        //JOptionPane.showMessageDialog(null, nombre+" "+id+" "+tipo);
        try {
            String table_name = "persona_direccion ";
            String campos = " * ";
            String otros = " where persona_id = '"+this.CurrentPersonaID+"' and visible = true ";
            java.sql.ResultSet resultSet = this.conector.ObtenerDatosParaTabla(table_name,campos ,otros);
            this.PersonaDireccionIDs.clear();
            this.PersonaDireccion.clear();
            if( resultSet.first() ){
                do{
                       this.PersonaDireccionIDs.add(resultSet.getString("id"));
                       this.PersonaDireccion.add(resultSet.getString("direccion"));
                }while(resultSet.next());
               
                //this.MostrarBotones(false, true,true );
                this.cargarTablaDireccion();
          }else{
            JOptionPane.showMessageDialog(null, "No hay registro");
            //System.out.println("");
        }
            this.MostrarBotonesDireccion(true, false, false);
          } catch (Exception ex) {
            System.out.println(ex.getCause().toString());
          }
        
    }
    public void MostrarDatoDireccion(){
        int index = this.jTableDireccion.getSelectedRow();
        if(index >= 0){
        
        //String tipo = this.jTable1.getValueAt(index, 0).toString();
        //JOptionPane.showMessageDialog(null, nombre+" "+id+" "+tipo);
        try {
            if(this.EditarClienteActivado){
                this.CurrentDireccionID = this.jTableDireccion.getValueAt(index, 0).toString();
            }else{
                this.CurrentDireccionID = index+"";
            }
            String direccion= this.jTableDireccion.getValueAt(index, 1).toString();
            this.jTextAreaDireccion.setText(direccion);
            
        this.MostrarBotonesDireccion(false, true, true);
          } catch (Exception ex) {
            System.out.println(ex.getCause().toString());
          }
        }else{
            JOptionPane.showMessageDialog(null, "No hay fila seleccionada");
        }
    }
    public void editarDireccion(){
        String direccion =this.jTextAreaDireccion.getText();
        if(direccion.isEmpty()){
            JOptionPane.showMessageDialog(null, "El campo dirección esta vacío");
        }else{
            //Validamos que se este actualizando un cliente para poder saber que hacer
            if(this.EditarClienteActivado){
                //validamos de que ese email no exista
                String table = " persona_direccion ";
                String campos = " id ";
                String otros = " where direccion like '%"+direccion+"%' and persona_id = '"+this.CurrentPersonaID+"'   ";
                java.sql.ResultSet resultSet = this.conector.ObtenerDatosParaTabla(table,campos ,otros);
           try {
               //validamos de que el resultado de la db sea 0 si lo es podemos crear el cliente
               if( !(resultSet.first()) ){
                   //insertamos primero a persona porque es una entidad fuerte
                   String tableE = "persona_direccion";
                   String camposE = "direccion = '"+direccion+"' ";
                   String otrosE = " id = '"+this.CurrentDireccionID+"'  ";
                   this.conector.ActualizarDato(tableE,camposE , otrosE);
                  this.MostrarDirecciones();
               }else{
                   JOptionPane.showMessageDialog(null, "La dirección ya existe, favor ingrese otro");
               }
            }   catch (SQLException ex) {
                    Logger.getLogger(ProveedorAdministrar.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{
            
                 this.PersonaDireccion.set(Integer.parseInt(this.CurrentDireccionID),this.jTextAreaDireccion.getText() );
                 this.cargarTablaDireccion();
            }
           /* String table = "cliente as c "
                + "inner join persona as p on c.persona_id = p.id";
           String campos = "p.nombre='"+this.nombre+"',p.apellido='"+this.apellido+"',p.cedula='"+this.cedula+"',c.rnc='"+this.rnc+"' ";
           String where = " c.id = '"+this.CurrentClienteID+"' ";
           this.conector.ActualizarDato(table,campos , where);*/
           JOptionPane.showMessageDialog(null,"Se edito");
           this.limpiarDireccion();
           this.MostrarBotonesDireccion(true, false, false);
      
       }
    }
    
    
   /*
   ADMINISTRAR TELEFONO
   */
   
     public void agregarTelefono(){
       
        String telefono =this.jTextFieldTelefono.getText();
        String tipo = this.jComboBoxTipoTelefono.getSelectedItem().toString();
        if(telefono.isEmpty()){
            JOptionPane.showMessageDialog(null, "El campo teléfono esta vacío");
        }else if(tipo.isEmpty()){
            JOptionPane.showMessageDialog(null, "El campo tipo teléfono esta vacío");
        }else{
            //Validamos que se este actualizando un cliente para poder saber que hacer
            if(this.EditarClienteActivado){
                //validamos de que ese email no exista
                String table = " persona_telefono ";
                String campos = " id ";
                String otros = " where  (tipo_telefono_id = '"+this.ArrayTipoTelefonoIDs.get(this.jComboBoxTipoTelefono.getSelectedIndex())+"') and ( telefono like '%"+telefono+"%' ) and (persona_id = '"+this.CurrentPersonaID+"' ) ";
                java.sql.ResultSet resultSet = this.conector.ObtenerDatosParaTabla(table,campos ,otros);
           try {
               //validamos de que el resultado de la db sea 0 si lo es podemos crear el cliente
               if( !(resultSet.first()) ){
                   //insertamos primero a persona porque es una entidad fuerte
                   String tableI = "persona_telefono";
                   String camposI = "tipo_telefono_id,telefono,persona_id,usuario_id,fecha";
                   String valoresI = " '"+this.ArrayTipoTelefonoIDs.get(this.jComboBoxTipoTelefono.getSelectedIndex())+"','"+telefono+"','"+this.CurrentPersonaID+"','"+this.usuarioID+"',now() ";
                   this.conector.insertData(tableI,camposI , valoresI);
                  this.MostrarTelefonos();
               }else{
                   JOptionPane.showMessageDialog(null, "El telefono ya existe, favor ingrese otro");
               }
            }   catch (SQLException ex) {
                    Logger.getLogger(ProveedorAdministrar.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{
                this.PersonaTelefono.add(this.jTextFieldTelefono.getText());
                int index = this.jComboBoxTipoTelefono.getSelectedIndex();
                this.PersonaTelefonoTipoID.add(this.ArrayTipoTelefonoIDs.get(index));
                this.PersonaTelefonoTipoNombre.add(this.jComboBoxTipoTelefono.getSelectedItem().toString());
                this.PersonaTelefonoIDs.add("0");
                this.cargarTablaTelefono();
            }
        this.MostrarBotonesTelefono(true, false, false);
         this.limpiarTelefono();
        }
   }
   public void cargarTablaTelefono(){
        int total = this.PersonaTelefono.size();
        String[] titulos = {"ID","TELEFONO","TIPO"};
        Object[][] fila = new Object[total][4];
        int c = 0; 
          while(c < total){
                fila[c][0] = this.PersonaTelefonoIDs.get(c);
                fila[c][1] = this.PersonaTelefono.get(c);
                fila[c][2] = this.PersonaTelefonoTipoNombre.get(c);
                c++;
             }
          DefaultTableModel modelo = new DefaultTableModel(fila,titulos);
          this.jTableTelefono.setModel(modelo);
        
   }
   public void MostrarBotonesTelefono(boolean agregar,boolean editar,boolean cancelar){
      this.jButtonTelefonoAgregar.setVisible(agregar);
      this.jButtonTelefonoEditar.setVisible(editar);
      this.jButtonTelefonoCancelar.setVisible(cancelar);   
  }
   public void limpiarTelefono(){
       this.jTextFieldTelefono.setText("");
       this.jComboBoxTipoTelefono.setSelectedIndex(0);
   }
   public void eliminarTelefono(){
       int index = this.jTableTelefono.getSelectedRow();
        if(index >= 0){
           if(JOptionPane.showConfirmDialog(null, "Esta seguro que desea eliminar el teléfono ?", "Eliminar teléfono", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION ){ 
              
               //Validamos que se este actualizando un cliente para poder saber que hacer
            if(this.EditarClienteActivado){
               try {
                   String id = this.jTableTelefono.getValueAt( index,0 ).toString();
                   String tableE = "persona_telefono";
                   String camposE = "visible = false ";
                   String otrosE = " id = '"+id+"'  ";
                   this.conector.ActualizarDato(tableE,camposE , otrosE);
                  this.MostrarTelefonos();
               
               } catch (Exception ex) {
                    Logger.getLogger(ProveedorAdministrar.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{
            
               this.PersonaTelefonoIDs.remove(index);
               this.PersonaTelefono.remove(index);
               this.PersonaTelefonoTipoNombre.remove(index);
               this.PersonaTelefonoTipoID.remove(index);
               this.cargarTablaTelefono();
               }
                this.limpiarTelefono();
                this.MostrarBotonesTelefono(true, false, false);
           }
        }else{
            JOptionPane.showMessageDialog(null, "No hay fila seleccionada");

       }
       
       
    }
   public void MostrarTelefonos(){
       
        try {
            String table_name = "persona_telefono as pt inner join tipo_telefono as tt on pt.tipo_telefono_id = tt.id ";
            String campos = " pt.*,tt.nombre ";
            String otros = " where pt.persona_id = '"+this.CurrentPersonaID+"' and pt.visible = true ";
            java.sql.ResultSet resultSet = this.conector.ObtenerDatosParaTabla(table_name,campos ,otros);
            this.PersonaTelefonoIDs.clear();
            this.PersonaTelefono.clear();
            this.PersonaTelefonoTipoNombre.clear();
            this.PersonaTelefonoTipoID.clear();
            if( resultSet.first() ){
                do{
                       this.PersonaTelefonoIDs.add(resultSet.getString("id"));
                       this.PersonaTelefono.add(resultSet.getString("telefono"));
                       this.PersonaTelefonoTipoNombre.add(resultSet.getString("nombre"));
                       this.PersonaTelefonoTipoID.add(resultSet.getString("tipo_telefono_id"));
                }while(resultSet.next());
               
                //this.MostrarBotones(false, true,true );
                this.cargarTablaTelefono();
          }else{
            JOptionPane.showMessageDialog(null, "No hay registro");
            //System.out.println("");
        }
            this.MostrarBotonesTelefono(true, false, false);
          } catch (Exception ex) {
            System.out.println(ex.getCause().toString());
          }
        
    }
    public void MostrarDatoTelefono(){
        int index = this.jTableTelefono.getSelectedRow();
        if(index >= 0){
        
        //String tipo = this.jTable1.getValueAt(index, 0).toString();
        //JOptionPane.showMessageDialog(null, nombre+" "+id+" "+tipo);
        try {
            if(this.EditarClienteActivado){
                this.CurrentTelefonoID = this.jTableTelefono.getValueAt(index, 0).toString();
            }else{
                this.CurrentTelefonoID = index+"";
            }
            String telefono= this.jTableTelefono.getValueAt(index, 1).toString();
            String tipo= this.jTableTelefono.getValueAt(index, 2).toString();
            this.jTextFieldTelefono.setText(telefono);
            this.jComboBoxTipoTelefono.setSelectedItem(tipo);
            
        this.MostrarBotonesTelefono(false, true, true);
          } catch (Exception ex) {
            System.out.println(ex.getCause().toString());
          }
        }else{
            JOptionPane.showMessageDialog(null, "No hay fila seleccionada");
        }
    }
    public void editarTelefono(){
        String telefono =this.jTextFieldTelefono.getText();
        String tipoTelefono = this.jComboBoxTipoTelefono.getSelectedItem().toString();
        if(telefono.isEmpty()){
            JOptionPane.showMessageDialog(null, "El campo teléfono esta vacío");
        }if(tipoTelefono.isEmpty()){
            JOptionPane.showMessageDialog(null, "El campo tipo teléfono esta vacío");
        }else{
            //Validamos que se este actualizando un cliente para poder saber que hacer
            if(this.EditarClienteActivado){
                //validamos de que ese email no exista
                String table = " persona_telefono ";
                String campos = " id ";
                String otros = " where (tipo_telefono_id = '"+this.ArrayTipoTelefonoIDs.get(this.jComboBoxTipoTelefono.getSelectedIndex())+"' ) and (telefono like '%"+telefono+"%') and (persona_id = '"+this.CurrentPersonaID+"')  ";
                java.sql.ResultSet resultSet = this.conector.ObtenerDatosParaTabla(table,campos ,otros);
           try {
               //validamos de que el resultado de la db sea 0 si lo es podemos crear el cliente
               if( !(resultSet.first()) ){
                   //insertamos primero a persona porque es una entidad fuerte
                   String tableE = "persona_telefono";
                   String camposE = "telefono = '"+telefono+"', tipo_telefono_id = '"+this.ArrayTipoTelefonoIDs.get(this.jComboBoxTipoTelefono.getSelectedIndex())+"' ";
                   String otrosE = " id = '"+this.CurrentTelefonoID+"'  ";
                   this.conector.ActualizarDato(tableE,camposE , otrosE);
                  this.MostrarTelefonos();
               }else{
                   JOptionPane.showMessageDialog(null, "La teléfono ya existe, favor ingrese otro");
               }
            }   catch (SQLException ex) {
                    Logger.getLogger(ProveedorAdministrar.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{
            
                 this.PersonaTelefono.set(Integer.parseInt(this.CurrentTelefonoID),this.jTextFieldTelefono.getText() );
                 this.cargarTablaTelefono();
            }
           /* String table = "cliente as c "
                + "inner join persona as p on c.persona_id = p.id";
           String campos = "p.nombre='"+this.nombre+"',p.apellido='"+this.apellido+"',p.cedula='"+this.cedula+"',c.rnc='"+this.rnc+"' ";
           String where = " c.id = '"+this.CurrentClienteID+"' ";
           this.conector.ActualizarDato(table,campos , where);*/
           JOptionPane.showMessageDialog(null,"Se edito");
           this.limpiarTelefono();
           this.MostrarBotonesTelefono(true, false, false);
      
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
        jPopupMenuEmail = new javax.swing.JPopupMenu();
        EditarEmail = new javax.swing.JMenuItem();
        EliminarEmail = new javax.swing.JMenuItem();
        jPopupMenuDireccion = new javax.swing.JPopupMenu();
        EditarDireccion = new javax.swing.JMenuItem();
        EliminarDireccion = new javax.swing.JMenuItem();
        jPopupMenuTelefono = new javax.swing.JPopupMenu();
        EditarTelefono = new javax.swing.JMenuItem();
        EliminarTelefono = new javax.swing.JMenuItem();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jTFApellido = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTFNombre = new javax.swing.JTextField();
        jTFCedula = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTFRNC = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jButtonEmailEditar = new javax.swing.JButton();
        jButtonEmailCancelar = new javax.swing.JButton();
        jButtonEmailAgregar = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jTextFieldEmail = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableEmail = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jButtonDireccionAgregar = new javax.swing.JButton();
        jButtonDireccionCancelar = new javax.swing.JButton();
        jButtonDireccionEditar = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTableDireccion = new javax.swing.JTable();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTextAreaDireccion = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jTextFieldTelefono = new javax.swing.JTextField();
        jComboBoxTipoTelefono = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jButtonTelefonoAgregar = new javax.swing.JButton();
        jButtonTelefonoCancelar = new javax.swing.JButton();
        jButtonTelefonoEditar = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTableTelefono = new javax.swing.JTable();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableCliente = new javax.swing.JTable();
        jTextFieldBuscador = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jButtonAgregarCliente = new javax.swing.JButton();
        jButtonCancelarCliente = new javax.swing.JButton();
        jButtonEditarCliente = new javax.swing.JButton();

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

        EditarEmail.setText("Editar");
        EditarEmail.setToolTipText("");
        EditarEmail.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                EditarEmailMousePressed(evt);
            }
        });
        jPopupMenuEmail.add(EditarEmail);

        EliminarEmail.setText("Eliminar");
        EliminarEmail.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                EliminarEmailMousePressed(evt);
            }
        });
        jPopupMenuEmail.add(EliminarEmail);

        EditarDireccion.setText("Editar");
        EditarDireccion.setToolTipText("");
        EditarDireccion.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                EditarDireccionMousePressed(evt);
            }
        });
        jPopupMenuDireccion.add(EditarDireccion);

        EliminarDireccion.setText("Eliminar");
        EliminarDireccion.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                EliminarDireccionMousePressed(evt);
            }
        });
        jPopupMenuDireccion.add(EliminarDireccion);

        EditarTelefono.setText("Editar");
        EditarTelefono.setToolTipText("");
        EditarTelefono.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                EditarTelefonoMousePressed(evt);
            }
        });
        jPopupMenuTelefono.add(EditarTelefono);

        EliminarTelefono.setText("Eliminar");
        EliminarTelefono.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                EliminarTelefonoMousePressed(evt);
            }
        });
        jPopupMenuTelefono.add(EliminarTelefono);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jTabbedPane1.setComponentPopupMenu(jPopupMenuEmail);

        jPanel1.setPreferredSize(new java.awt.Dimension(300, 346));

        jLabel3.setText("Nombre");

        jLabel1.setText("Apellido");

        jLabel2.setText("Cedula");

        jLabel4.setText("RNC");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTFNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jTFApellido, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jTFCedula, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jTFRNC, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addContainerGap(69, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTFNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTFApellido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTFCedula, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTFRNC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(144, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Datos Personales", jPanel1);

        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButtonEmailEditar.setText("Editar");
        jButtonEmailEditar.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jButtonEmailEditar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButtonEmailEditarMousePressed(evt);
            }
        });
        jPanel3.add(jButtonEmailEditar, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 11, 70, 20));

        jButtonEmailCancelar.setText("Cancelar");
        jButtonEmailCancelar.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jButtonEmailCancelar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonEmailCancelarMouseClicked(evt);
            }
        });
        jPanel3.add(jButtonEmailCancelar, new org.netbeans.lib.awtextra.AbsoluteConstraints(86, 11, 93, 20));

        jButtonEmailAgregar.setText("Agregar");
        jButtonEmailAgregar.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jButtonEmailAgregar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonEmailAgregarMouseClicked(evt);
            }
        });
        jPanel3.add(jButtonEmailAgregar, new org.netbeans.lib.awtextra.AbsoluteConstraints(259, 11, 80, 20));

        jLabel7.setText("Email");
        jPanel3.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 37, -1, -1));
        jPanel3.add(jTextFieldEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 57, 329, -1));

        jTableEmail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTableEmail.setComponentPopupMenu(jPopupMenuEmail);
        jTableEmail.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTableEmailMousePressed(evt);
            }
        });
        jScrollPane3.setViewportView(jTableEmail);

        jPanel3.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 88, 329, 285));

        jTabbedPane1.addTab("Emails", jPanel3);

        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButtonDireccionAgregar.setText("Agregar");
        jButtonDireccionAgregar.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jButtonDireccionAgregar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonDireccionAgregarMouseClicked(evt);
            }
        });
        jPanel4.add(jButtonDireccionAgregar, new org.netbeans.lib.awtextra.AbsoluteConstraints(259, 11, 80, 20));

        jButtonDireccionCancelar.setText("Cancelar");
        jButtonDireccionCancelar.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jButtonDireccionCancelar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonDireccionCancelarMouseClicked(evt);
            }
        });
        jPanel4.add(jButtonDireccionCancelar, new org.netbeans.lib.awtextra.AbsoluteConstraints(92, 11, 92, 20));

        jButtonDireccionEditar.setText("Editar");
        jButtonDireccionEditar.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jButtonDireccionEditar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonDireccionEditarMouseClicked(evt);
            }
        });
        jPanel4.add(jButtonDireccionEditar, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 11, 76, 20));

        jLabel8.setText("Dirección");
        jPanel4.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 49, -1, -1));

        jTableDireccion.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTableDireccion.setComponentPopupMenu(jPopupMenuDireccion);
        jScrollPane4.setViewportView(jTableDireccion);

        jPanel4.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 171, 329, 202));

        jTextAreaDireccion.setColumns(20);
        jTextAreaDireccion.setRows(5);
        jScrollPane6.setViewportView(jTextAreaDireccion);

        jPanel4.add(jScrollPane6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 69, 329, -1));

        jTabbedPane1.addTab("Dirección", jPanel4);

        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel5.setText("Tipo de teléfono");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 50, 90, -1));
        jPanel2.add(jTextFieldTelefono, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 320, -1));

        jPanel2.add(jComboBoxTipoTelefono, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 320, -1));

        jLabel6.setText("Teléfono");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 100, 90, -1));

        jButtonTelefonoAgregar.setText("Agregar");
        jButtonTelefonoAgregar.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jButtonTelefonoAgregar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButtonTelefonoAgregarMousePressed(evt);
            }
        });
        jPanel2.add(jButtonTelefonoAgregar, new org.netbeans.lib.awtextra.AbsoluteConstraints(251, 10, 80, 20));

        jButtonTelefonoCancelar.setText("Cancelar");
        jButtonTelefonoCancelar.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jButtonTelefonoCancelar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButtonTelefonoCancelarMousePressed(evt);
            }
        });
        jPanel2.add(jButtonTelefonoCancelar, new org.netbeans.lib.awtextra.AbsoluteConstraints(94, 10, 90, 20));

        jButtonTelefonoEditar.setText("Editar");
        jButtonTelefonoEditar.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jButtonTelefonoEditar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButtonTelefonoEditarMousePressed(evt);
            }
        });
        jPanel2.add(jButtonTelefonoEditar, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 78, 20));

        jTableTelefono.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTableTelefono.setComponentPopupMenu(jPopupMenuTelefono);
        jScrollPane5.setViewportView(jTableTelefono);

        jPanel2.add(jScrollPane5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 146, 320, 216));

        jLabel10.setText("+");
        jLabel10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel10MousePressed(evt);
            }
        });
        jPanel2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 50, -1, -1));

        jTabbedPane1.addTab("Teléfonos", jPanel2);

        jTableCliente.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTableCliente.setToolTipText("Click izquierdo para seleccionar la fila y luego click derecho para desplegar las opciones");
        jTableCliente.setComponentPopupMenu(jPopupMenu1);
        jScrollPane1.setViewportView(jTableCliente);

        jTextFieldBuscador.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldBuscadorKeyReleased(evt);
            }
        });

        jLabel9.setText("Que deseas buscar?");

        jButtonAgregarCliente.setText("Agregar");
        jButtonAgregarCliente.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButtonAgregarClienteMousePressed(evt);
            }
        });

        jButtonCancelarCliente.setText("Cancelar");
        jButtonCancelarCliente.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonCancelarClienteMouseClicked(evt);
            }
        });

        jButtonEditarCliente.setText("Editar");
        jButtonEditarCliente.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButtonEditarClienteMousePressed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 354, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(36, 36, 36)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 569, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonEditarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jButtonCancelarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(73, 73, 73)
                        .addComponent(jButtonAgregarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(77, 77, 77)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(jTextFieldBuscador, javax.swing.GroupLayout.PREFERRED_SIZE, 520, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(58, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonEditarCliente)
                    .addComponent(jButtonCancelarCliente)
                    .addComponent(jButtonAgregarCliente)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldBuscador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 412, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonEmailEditarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonEmailEditarMousePressed
        // TODO add your handling code here:
        this.editarEmail();
    }//GEN-LAST:event_jButtonEmailEditarMousePressed

    private void jButtonEmailCancelarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonEmailCancelarMouseClicked
        // TODO add your handling code here:
        this.MostrarBotonesEmail(true, false, false);
        this.limpiarEmail();
    }//GEN-LAST:event_jButtonEmailCancelarMouseClicked

    private void jButtonEmailAgregarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonEmailAgregarMouseClicked
        // TODO add your handling code here:
        this.agregarEmail();
    }//GEN-LAST:event_jButtonEmailAgregarMouseClicked

    private void jTableEmailMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableEmailMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTableEmailMousePressed

    private void jButtonDireccionAgregarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonDireccionAgregarMouseClicked
        // TODO add your handling code here:
        this.agregarDireccion();
    }//GEN-LAST:event_jButtonDireccionAgregarMouseClicked

    private void jButtonDireccionCancelarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonDireccionCancelarMouseClicked
        // TODO add your handling code here:
        this.MostrarBotonesDireccion(true, false, false);
        this.limpiarDireccion();
    }//GEN-LAST:event_jButtonDireccionCancelarMouseClicked

    private void jButtonDireccionEditarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonDireccionEditarMouseClicked
        // TODO add your handling code here:
        this.editarDireccion();
    }//GEN-LAST:event_jButtonDireccionEditarMouseClicked

    private void jButtonTelefonoAgregarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonTelefonoAgregarMousePressed
        // TODO add your handling code here:
        this.agregarTelefono();
    }//GEN-LAST:event_jButtonTelefonoAgregarMousePressed

    private void jButtonTelefonoCancelarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonTelefonoCancelarMousePressed
        // TODO add your handling code here:
        this.MostrarBotonesTelefono(true, false, false);
        this.limpiarTelefono();
    }//GEN-LAST:event_jButtonTelefonoCancelarMousePressed

    private void jButtonTelefonoEditarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonTelefonoEditarMousePressed
        // TODO add your handling code here:
        this.editarTelefono();
    }//GEN-LAST:event_jButtonTelefonoEditarMousePressed

    private void jLabel10MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel10MousePressed
        // TODO add your handling code here:
        this.agregarTipoTelefono();
    }//GEN-LAST:event_jLabel10MousePressed

    private void jTextFieldBuscadorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldBuscadorKeyReleased
        // TODO add your handling code here:
        this.BuscadorCliente();
    }//GEN-LAST:event_jTextFieldBuscadorKeyReleased

    private void jButtonAgregarClienteMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonAgregarClienteMousePressed
        // TODO add your handling code here:
        this.insert();
    }//GEN-LAST:event_jButtonAgregarClienteMousePressed

    private void jButtonCancelarClienteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonCancelarClienteMouseClicked
        // TODO add your handling code here:
        this.MostrarBotones(true, false, false);
        this.EditarClienteActivado = false;
        this.limpiar();
    }//GEN-LAST:event_jButtonCancelarClienteMouseClicked

    private void jButtonEditarClienteMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonEditarClienteMousePressed
        // TODO add your handling code here:
        this.editar();
    }//GEN-LAST:event_jButtonEditarClienteMousePressed

    private void EditarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_EditarMousePressed
        // TODO add your handling code here:
        this.MostrarDato();
    }//GEN-LAST:event_EditarMousePressed

    private void EliminarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_EliminarMousePressed
        // TODO add your handling code here:
        this.eliminar();
    }//GEN-LAST:event_EliminarMousePressed

    private void EditarEmailMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_EditarEmailMousePressed
        // TODO add your handling code here:
        this.MostrarDatoEmail();
    }//GEN-LAST:event_EditarEmailMousePressed

    private void EliminarEmailMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_EliminarEmailMousePressed
        // TODO add your handling code here:
        this.eliminarEmail();
    }//GEN-LAST:event_EliminarEmailMousePressed

    private void EditarDireccionMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_EditarDireccionMousePressed
        // TODO add your handling code here:
        this.MostrarDatoDireccion();
    }//GEN-LAST:event_EditarDireccionMousePressed

    private void EliminarDireccionMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_EliminarDireccionMousePressed
        // TODO add your handling code here:
        this.eliminarDireccion();
    }//GEN-LAST:event_EliminarDireccionMousePressed

    private void EditarTelefonoMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_EditarTelefonoMousePressed
        // TODO add your handling code here:
        this.MostrarDatoTelefono();
    }//GEN-LAST:event_EditarTelefonoMousePressed

    private void EliminarTelefonoMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_EliminarTelefonoMousePressed
        // TODO add your handling code here:
        this.eliminarTelefono();
    }//GEN-LAST:event_EliminarTelefonoMousePressed

    /**
     * @param args the command line arguments
     */
   

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem Editar;
    private javax.swing.JMenuItem EditarDireccion;
    private javax.swing.JMenuItem EditarEmail;
    private javax.swing.JMenuItem EditarTelefono;
    private javax.swing.JMenuItem Eliminar;
    private javax.swing.JMenuItem EliminarDireccion;
    private javax.swing.JMenuItem EliminarEmail;
    private javax.swing.JMenuItem EliminarTelefono;
    private javax.swing.JButton jButtonAgregarCliente;
    private javax.swing.JButton jButtonCancelarCliente;
    private javax.swing.JButton jButtonDireccionAgregar;
    private javax.swing.JButton jButtonDireccionCancelar;
    private javax.swing.JButton jButtonDireccionEditar;
    private javax.swing.JButton jButtonEditarCliente;
    private javax.swing.JButton jButtonEmailAgregar;
    private javax.swing.JButton jButtonEmailCancelar;
    private javax.swing.JButton jButtonEmailEditar;
    private javax.swing.JButton jButtonTelefonoAgregar;
    private javax.swing.JButton jButtonTelefonoCancelar;
    private javax.swing.JButton jButtonTelefonoEditar;
    private javax.swing.JComboBox<String> jComboBoxTipoTelefono;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
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
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JPopupMenu jPopupMenuDireccion;
    private javax.swing.JPopupMenu jPopupMenuEmail;
    private javax.swing.JPopupMenu jPopupMenuTelefono;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTextField jTFApellido;
    private javax.swing.JTextField jTFCedula;
    private javax.swing.JTextField jTFNombre;
    private javax.swing.JTextField jTFRNC;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTableCliente;
    private javax.swing.JTable jTableDireccion;
    private javax.swing.JTable jTableEmail;
    private javax.swing.JTable jTableTelefono;
    private javax.swing.JTextArea jTextAreaDireccion;
    private javax.swing.JTextField jTextFieldBuscador;
    private javax.swing.JTextField jTextFieldEmail;
    private javax.swing.JTextField jTextFieldTelefono;
    // End of variables declaration//GEN-END:variables
}
