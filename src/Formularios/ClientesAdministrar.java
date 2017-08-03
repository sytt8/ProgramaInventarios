/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Formularios;

import Conexion.ConectarBD;
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
public class ClientesAdministrar extends javax.swing.JFrame {

    /**
     * Creates new form ClientesAdministrar
     */
    
    private String usuarioID="1";
    private String CurrentClienteID="1";
    private String CurrentPersonaID="1";
    private String usuarioNombre="admin";
    private ConectarBD conector=null;
           
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
    private ClientesAdministrar me;

    
    public void setMe(ClientesAdministrar me) {
        this.me = me;
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

       public ClientesAdministrar(ConectarBD conector,String usuarioID,String usuarioNombre) {
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
    }
       
       public void BuscadorCliente(){
         this.Buscador = "";
        if(!(this.jTextFieldBuscador.getText().isEmpty())){
            this.Buscador = " and concat(p.nombre,' ',p.apellido,' ',p.cedula,' ',c.rnc,' ',c.id) like '%"+this.jTextFieldBuscador.getText()+"%'";
            //this.cargarTabla();
        }
            this.cargarTabla();
        
    }

         public void agregarTipoTelefono(){
        TipoTelefonoAdministrar tipoTelefono = new TipoTelefonoAdministrar(this.conector,this.usuarioID,this.usuarioNombre);
        tipoTelefono.setFOrigen(this.me,"cliente");
        tipoTelefono.setVisible(true);
    }
    
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
          } }  
       
       public void insert() {
           //valida sipodemos continuar
           if(this.validarVacioContinuo()){
               //queremos evitar que un cliente se ingrese dos veces por esto validadmos si existe la cedula o el rnc
               String table = "cliente as c inner join persona as p on c.persona_id = p.id";
               String campos = "c.id";
               String otros = " where (c.rnc = '"+this.rnc+"') or (p.cedula = '"+this.cedula+"')";
               java.sql.ResultSet  rs = this.conector.ObtenerDatosParaTabla(table, campos, otros);
           try {
               //validamos dde que el resultado de la db sea 0 si lo es podemos crear el cliente
               if (!(rs.first())){
                   //insertamos pimero a persona porque es una entidad fuerte
                   String tableI="persona";
                   String camposI = "nombre, apellido, cedula, usuario_id, fecha";
                   String valoresI = "'"+this.nombre+"','"+this.apellido+"','"+this.cedula+"','"+this.usuarioID+"', now()";
                   this.conector.insertData(tableI, camposI, valoresI);
               
                   //obtenemos el ultimo id de la entidad persona
                   String idPersona = this.conector.obtenerUltimoID(tableI);

                                      //insertamos cliente
                   String tableIC = "cliente";
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
                    if(this.PersonaDireccion.size() > 0){
                            for (int i = 0; i < this.PersonaDireccion.size(); i++) {
                            String tableIPE = "persona_direccion    ";
                            String camposIPE = "persona_id,direccion,usuario_id,fecha";
                            String valoresIPE = "'"+idPersona+"', '"+this.PersonaDireccion.get(i)+"','"+this.usuarioID+"',now() ";
                            this.conector.insertData(tableIPE,camposIPE , valoresIPE);
                         }
                        }        
                    if(this.PersonaTelefono.size() > 0){
                            for (int i = 0; i < this.PersonaTelefono.size(); i++) {
                            String tableIPE = "persona_telefono";
                            String camposIPE = "persona_id,telefono,usuario_id,fecha";
                            String valoresIPE = "'"+idPersona+"', '"+this.PersonaTelefono.get(i)+"','"+this.usuarioID+"',now() ";
                            this.conector.insertData(tableIPE,camposIPE , valoresIPE);
                         }
                        } 
                    this.limpiar();
                    this.cargarTabla();
               }else{
               JOptionPane.showMessageDialog(null, "El cliente ya existe, favor ingrese otro");
               }
           }
               catch (SQLException ex){
                       Logger.getLogger (ClientesAdministrar.class.getName()).log(Level.SEVERE, null, ex);
                       }
               
           }
           }
       
       private String nombre, apellido, cedula, rnc;
       public boolean validarVacioContinuo(){
           this.nombre = this.jTNombre.getText();
           this.apellido =this. jTextFieldApellido.getText();
           this.cedula =this. jTextFieldCedula.getText();
           this.rnc = this.jTextFieldRNC.getText();
           //significa no puede contunuar
           boolean respuesta = false;
           //validamos que el dato no este vacio
           if (nombre.isEmpty()){
               JOptionPane.showMessageDialog(null, "El campo nombre esta vacio");
           }else if (apellido.isEmpty()){
               JOptionPane.showMessageDialog(null,"El campo nombre esta vacio" );
           }else if (rnc.isEmpty() && cedula.isEmpty()){
               JOptionPane.showMessageDialog(null,"Debe agregar la cedula o el RNC" );
           }else{
               //como los campos necesario no estan vacio le decimos que puede continuar
               respuesta=true;
           }
           return respuesta;
       }
       
       public void editar(){
           if (this.validarVacioContinuo()){
              String table = "cliente as c inner join persona as p on c.persona_id = p.id";
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
       this.jTextFieldApellido.setText("");
       this.jTNombre.setText("");
       this.jTextFieldRNC.setText("");
       this.jTextFieldCedula.setText("");
       this.jTableEmail.setModel (new DefaultTableModel());
       this.jTabbedPane1.setSelectedIndex(0);
       this.PersonaEmailIDs.clear();
       this.PersonaEmails.clear();
       
       this.jTableDireccion.setModel(new DefaultTableModel());
        this.PersonaDireccionIDs.clear();
        this.PersonaDireccion.clear();
        
        this.jTableTel.setModel(new DefaultTableModel());
        this.PersonaTelefonoIDs.clear();
        this.PersonaTelefono.clear();
        this.PersonaTelefonoTipoNombre.clear();
        this.PersonaTelefonoTipoID.clear();
    }
       public void eliminar(){
           int index = this.jTableCliente.getSelectedRow();
           if (index>=0){
               if(JOptionPane.showConfirmDialog(null, "Esta seguro que desea eliminar el cliente?", "Eliminar cliente", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION ){ 
                this.CurrentClienteID = this.jTableCliente.getValueAt(index, 0).toString();
                String table = " cliente ";
                String campos = "visible = false ";
                String where = " id = '"+this.CurrentClienteID+"' ";
                this.conector.ActualizarDato(table,campos , where);
                JOptionPane.showMessageDialog(null,"Se Elimino");
                
                this.limpiar();
                this.cargarTabla();
                
           }
       }else{
               JOptionPane.showMessageDialog(null, "no hay fila seleccionada");
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
        String table_name = "cliente as c inner join persona as p on c.persona_id = p.id inner join usuario as u on p.usuario_id = u.id ";
        String campos = " c.rnc,c.id,p.nombre,p.apellido,p.cedula, u.nombre as usuario, p.id as personaid ";
        String otros = " where c.id = '"+id+"' and c.visible = true ";
        java.sql.ResultSet resultSet = this.conector.ObtenerDatosParaTabla(table_name, campos, otros);
        if( resultSet.first() ){
                   //Mostrar datos personales y del cliente
                   this.CurrentClienteID = resultSet.getString("id");
                   this.jTextFieldApellido.setText(resultSet.getString("apellido"));
                   this.jTNombre.setText(resultSet.getString("nombre"));
                   this.jTextFieldCedula.setText(resultSet.getString("cedula"));
                   this.jTextFieldRNC.setText(resultSet.getString("rnc"));
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
      this.jButtonAdd.setVisible(b1);
      this.jButtonEditar.setVisible(b2);
      this.jButtonCancel.setVisible(b3);  }
         
         public void cargarTabla(){
        try {
        String table_name = "cliente as c inner join persona as p on c.persona_id = p.id inner join usuario as u on p.usuario_id = u.id ";
        String campos = " c.rnc,c.id,p.nombre,p.apellido,p.cedula, u.nombre as usuario ";
        String otros = " where  c.visible = true "+this.Buscador+" ";
        java.sql.ResultSet resultSet = this.conector.ObtenerDatosParaTabla(table_name, campos, otros);
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
                java.sql.ResultSet resultSet = this.conector.ObtenerDatosParaTabla(table, campos, otros);
           try {
               //validamos de que el resultado de la db sea 0 si lo es podemos crear el cliente
               if( !(resultSet.first()) ){
                   //insertamos primero a persona porque es una entidad fuerte
                   String tableI = "persona_email";
                   String camposI = "correo,persona_id,usuario_id,fecha";
                   String valoresI = "'"+email+"','"+this.CurrentPersonaID+"','"+this.usuarioID+"',now() ";
                   this.conector.insertData(tableI,camposI , valoresI);
                  this.MostrarEmails();
               }else{
                   JOptionPane.showMessageDialog(null, "El cliente ya existe, favor ingrese otro");
               }
            }   catch (SQLException ex) {
                    Logger.getLogger(ClientesAdministrar.class.getName()).log(Level.SEVERE, null, ex);
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
      this.jButtonEmailAdd1.setVisible(agregar);
      this.jButtonEmailEditar.setVisible(editar);
      this.jButtonEmailCancel1.setVisible(cancelar);   
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
                   this.conector.ActualizarDato(tableE, camposE, otrosE);
                  this.MostrarEmails();
               
               } catch (Exception ex) {
                    Logger.getLogger(ClientesAdministrar.class.getName()).log(Level.SEVERE, null, ex);
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
                this.conector.actulizarDatos(table,campos , where);
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
            java.sql.ResultSet resultSet = this.conector.ObtenerDatosParaTabla(table_name, campos, otros);
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
            JOptionPane.showMessageDialog(null, "No hay registro de email");
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
                    Logger.getLogger(ClientesAdministrar.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{
            
                 this.PersonaEmails.set(Integer.parseInt(this.CurrentEmailID),this.jTextFieldEmail.getText() );
                 this.cargarTablaEmail();
            }
           /* String table = "cliente as c "
                + "inner join persona as p on c.persona_id = p.id";
           String campos = "p.nombre='"+this.nombre+"',p.apellido='"+this.apellido+"',p.cedula='"+this.cedula+"',c.rnc='"+this.rnc+"' ";
           String where = " c.id = '"+this.CurrentClienteID+"' ";
           this.conector.actulizarDatos(table,campos , where);*/
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
                    Logger.getLogger(ClientesAdministrar.class.getName()).log(Level.SEVERE, null, ex);
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
      this.jButtonDireccionlAdd.setVisible(agregar);
      this.jButtonDireccionEditar.setVisible(editar);
      this.jButtonDireccionCancel.setVisible(cancelar);   
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
                    Logger.getLogger(ClientesAdministrar.class.getName()).log(Level.SEVERE, null, ex);
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
            JOptionPane.showMessageDialog(null, "No hay registro de dirección");
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
                    Logger.getLogger(ClientesAdministrar.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{
            
                 this.PersonaDireccion.set(Integer.parseInt(this.CurrentDireccionID),this.jTextAreaDireccion.getText() );
                 this.cargarTablaDireccion();
            }
           /* String table = "cliente as c "
                + "inner join persona as p on c.persona_id = p.id";
           String campos = "p.nombre='"+this.nombre+"',p.apellido='"+this.apellido+"',p.cedula='"+this.cedula+"',c.rnc='"+this.rnc+"' ";
           String where = " c.id = '"+this.CurrentClienteID+"' ";
           this.conector.actulizarDatos(table,campos , where);*/
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
                String otros = " where (tipo_telefono_id = '"+this.ArrayTipoTelefonoIDs.get(this.jComboBoxTipoTelefono.getSelectedIndex())+"') and (telefono like '%"+telefono+"%' ) and (persona_id = '"+this.CurrentPersonaID+"' )  ";
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
                    Logger.getLogger(ClientesAdministrar.class.getName()).log(Level.SEVERE, null, ex);
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
          this.jTableTel.setModel(modelo);
        
   }
   public void MostrarBotonesTelefono(boolean agregar,boolean editar,boolean cancelar){
      this.jButtonTelAdd.setVisible(agregar);
      this.jButtonTelEditar.setVisible(editar);
      this.jButtonTelCancel.setVisible(cancelar);   
  }
   public void limpiarTelefono(){
       this.jTextFieldTelefono.setText("");
       this.jComboBoxTipoTelefono.setSelectedIndex(0);
   }
   public void eliminarTelefono(){
       int index = this.jTableTel.getSelectedRow();
        if(index >= 0){
           if(JOptionPane.showConfirmDialog(null, "Esta seguro que desea eliminar el teléfono ?", "Eliminar teléfono", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION ){ 
              
               //Validamos que se este actualizando un cliente para poder saber que hacer
            if(this.EditarClienteActivado){
               try {
                   String id = this.jTableTel.getValueAt( index,0 ).toString();
                   String tableE = "persona_telefono";
                   String camposE = "visible = false ";
                   String otrosE = " id = '"+id+"'  ";
                   this.conector.ActualizarDato(tableE,camposE , otrosE);
                  this.MostrarTelefonos();
               
               } catch (Exception ex) {
                    Logger.getLogger(ClientesAdministrar.class.getName()).log(Level.SEVERE, null, ex);
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
            JOptionPane.showMessageDialog(null, "No hay registro de telefono");
            //System.out.println("");
        }
            this.MostrarBotonesTelefono(true, false, false);
          } catch (Exception ex) {
            System.out.println(ex.getCause().toString());
          }
        
    }
    public void MostrarDatoTelefono(){
        int index = this.jTableTel.getSelectedRow();
        if(index >= 0){
        
        //String tipo = this.jTable1.getValueAt(index, 0).toString();
        //JOptionPane.showMessageDialog(null, nombre+" "+id+" "+tipo);
        try {
            if(this.EditarClienteActivado){
                this.CurrentTelefonoID = this.jTableTel.getValueAt(index, 0).toString();
            }else{
                this.CurrentTelefonoID = index+"";
            }
            String telefono= this.jTableTel.getValueAt(index, 1).toString();
            String tipo= this.jTableTel.getValueAt(index, 2).toString();
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
                //String otros = " where telefono like '%"+telefono+"%' and persona_id = '"+this.CurrentPersonaID+"'  ";
                String otros = " where (tipo_telefono_id = '"+this.ArrayTipoTelefonoIDs.get(this.jComboBoxTipoTelefono.getSelectedIndex())+"' ) and (telefono like '%"+telefono+"%') and (persona_id = '"+this.CurrentPersonaID+"')  ";
                
                java.sql.ResultSet resultSet = this.conector.ObtenerDatosParaTabla(table,campos ,otros);
           try {
               //validamos de que el resultado de la db sea 0 si lo es podemos crear el cliente
               if( !(resultSet.first()) ){
                   //insertamos primero a persona porque es una entidad fuerte
                   String tableE = "persona_telefono";
                   String camposE = "telefono = '"+telefono+"', tipo_telefono_id = '"+this.ArrayTipoTelefonoIDs.get(this.jComboBoxTipoTelefono.getSelectedIndex())+"' ";
                   String otrosE = " id = '"+this.CurrentTelefonoID+"'  ";
                   this.conector.ActualizarDato(tableE, camposE, otrosE);
                  this.MostrarTelefonos();
               }else{
                   JOptionPane.showMessageDialog(null, "La teléfono ya existe, favor ingrese otro");
               }
            }   catch (SQLException ex) {
                    Logger.getLogger(ClientesAdministrar.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{
            
                 this.PersonaTelefono.set(Integer.parseInt(this.CurrentTelefonoID),this.jTextFieldTelefono.getText() );
                 this.cargarTablaTelefono();
            }
           /* String table = "cliente as c "
                + "inner join persona as p on c.persona_id = p.id";
           String campos = "p.nombre='"+this.nombre+"',p.apellido='"+this.apellido+"',p.cedula='"+this.cedula+"',c.rnc='"+this.rnc+"' ";
           String where = " c.id = '"+this.CurrentClienteID+"' ";
           this.conector.actulizarDatos(table,campos , where);*/
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

        jLabel1 = new javax.swing.JLabel();
        jPopupMenuCliente = new javax.swing.JPopupMenu();
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
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableCliente = new javax.swing.JTable();
        jTextFieldBuscador = new javax.swing.JTextField();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jTNombre = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldApellido = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextFieldCedula = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTextFieldRNC = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jComboBoxTipoTelefono = new javax.swing.JComboBox<>();
        jTextFieldTelefono = new javax.swing.JTextField();
        jButtonTelEditar = new javax.swing.JButton();
        jButtonTelCancel = new javax.swing.JButton();
        jButtonTelAdd = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableTel = new javax.swing.JTable();
        jLabel15 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jTextFieldEmail = new javax.swing.JTextField();
        jButtonEmailEditar = new javax.swing.JButton();
        jButtonEmailAdd1 = new javax.swing.JButton();
        jButtonEmailCancel1 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableEmail = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextAreaDireccion = new javax.swing.JTextArea();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTableDireccion = new javax.swing.JTable();
        jButtonDireccionlAdd = new javax.swing.JButton();
        jButtonDireccionCancel = new javax.swing.JButton();
        jButtonDireccionEditar = new javax.swing.JButton();
        jButtonEditar = new javax.swing.JButton();
        jButtonAdd = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/Fondo Grande.png"))); // NOI18N
        jLabel1.setText("jLabel1");

        Editar.setText("Editar");
        Editar.setToolTipText("");
        Editar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                EditarMousePressed(evt);
            }
        });
        jPopupMenuCliente.add(Editar);

        Eliminar.setText("Eliminar");
        Eliminar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                EliminarMousePressed(evt);
            }
        });
        jPopupMenuCliente.add(Eliminar);

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
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTableCliente.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTableCliente.setComponentPopupMenu(jPopupMenuCliente);
        jScrollPane1.setViewportView(jTableCliente);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 70, 500, 370));

        jTextFieldBuscador.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldBuscadorKeyReleased(evt);
            }
        });
        getContentPane().add(jTextFieldBuscador, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 40, 500, -1));

        jLabel2.setText("Nombre");

        jTNombre.setPreferredSize(new java.awt.Dimension(250, 20));

        jLabel3.setText("Apellido");

        jTextFieldApellido.setPreferredSize(new java.awt.Dimension(250, 20));

        jLabel5.setText("Cedula");
        jLabel5.setToolTipText("");

        jTextFieldCedula.setPreferredSize(new java.awt.Dimension(250, 20));

        jLabel6.setText("RNC");

        jTextFieldRNC.setPreferredSize(new java.awt.Dimension(250, 20));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextFieldRNC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jTextFieldCedula, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jTextFieldApellido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jTNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldApellido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldCedula, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldRNC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(157, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Datos personales", jPanel1);

        jButtonTelEditar.setText("Editar");
        jButtonTelEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTelEditarActionPerformed(evt);
            }
        });

        jButtonTelCancel.setText("Cancelar");
        jButtonTelCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTelCancelActionPerformed(evt);
            }
        });

        jButtonTelAdd.setText("Añadir");
        jButtonTelAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTelAddActionPerformed(evt);
            }
        });

        jLabel14.setText("Teléfono");

        jTableTel.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTableTel.setComponentPopupMenu(jPopupMenuTelefono);
        jScrollPane3.setViewportView(jTableTel);

        jLabel15.setText("Tipo de teléfono");

        jLabel7.setText("+");
        jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel7MousePressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jButtonTelEditar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonTelCancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonTelAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel14)
                                .addComponent(jComboBoxTipoTelefono, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jTextFieldTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(20, Short.MAX_VALUE))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonTelEditar)
                    .addComponent(jButtonTelCancel)
                    .addComponent(jButtonTelAdd))
                .addGap(23, 23, 23)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxTipoTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextFieldTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(77, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Teléfonos", jPanel4);

        jLabel12.setText("Correo");

        jButtonEmailEditar.setText("Editar");
        jButtonEmailEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEmailEditarActionPerformed(evt);
            }
        });

        jButtonEmailAdd1.setText("Añadir");
        jButtonEmailAdd1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEmailAdd1ActionPerformed(evt);
            }
        });

        jButtonEmailCancel1.setText("Cancelar");
        jButtonEmailCancel1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEmailCancel1ActionPerformed(evt);
            }
        });

        jTableEmail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTableEmail.setComponentPopupMenu(jPopupMenuEmail);
        jScrollPane2.setViewportView(jTableEmail);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addComponent(jButtonEmailEditar)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(jButtonEmailCancel1)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButtonEmailAdd1, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jTextFieldEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonEmailEditar)
                    .addComponent(jButtonEmailCancel1)
                    .addComponent(jButtonEmailAdd1))
                .addGap(6, 6, 6)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextFieldEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(100, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Email", jPanel2);

        jLabel4.setText("Dirección");

        jTextAreaDireccion.setColumns(20);
        jTextAreaDireccion.setRows(5);
        jScrollPane4.setViewportView(jTextAreaDireccion);

        jTableDireccion.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTableDireccion.setColumnSelectionAllowed(true);
        jTableDireccion.setComponentPopupMenu(jPopupMenuDireccion);
        jScrollPane5.setViewportView(jTableDireccion);

        jButtonDireccionlAdd.setText("Añadir");
        jButtonDireccionlAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDireccionlAddActionPerformed(evt);
            }
        });

        jButtonDireccionCancel.setText("Cancelar");
        jButtonDireccionCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDireccionCancelActionPerformed(evt);
            }
        });

        jButtonDireccionEditar.setText("Editar");
        jButtonDireccionEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDireccionEditarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jButtonDireccionEditar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonDireccionCancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonDireccionlAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addGap(0, 15, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonDireccionEditar)
                    .addComponent(jButtonDireccionCancel)
                    .addComponent(jButtonDireccionlAdd))
                .addGap(3, 3, 3)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(61, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Dirección", jPanel3);

        getContentPane().add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, 320, -1));

        jButtonEditar.setText("Editar");
        jButtonEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEditarActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonEditar, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 20, -1, -1));

        jButtonAdd.setText("Añadir");
        jButtonAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonAdd, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 20, -1, -1));

        jButtonCancel.setText("Cancelar");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonCancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 20, -1, -1));

        jLabel13.setText("Que deseas buscar?");
        getContentPane().add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 20, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldBuscadorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldBuscadorKeyReleased
        this.BuscadorCliente();   // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldBuscadorKeyReleased

    private void jButtonEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEditarActionPerformed
        this.editar();        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonEditarActionPerformed

    private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddActionPerformed
        this.insert();        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonAddActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        this.MostrarBotones(true, false, false);
        this.limpiar();        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonEmailEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEmailEditarActionPerformed
       this.editarEmail(); // TODO add your handling code here:
    }//GEN-LAST:event_jButtonEmailEditarActionPerformed

    private void jButtonEmailCancel1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEmailCancel1ActionPerformed
       this.limpiar(); // TODO add your handling code here:
    }//GEN-LAST:event_jButtonEmailCancel1ActionPerformed

    private void jButtonEmailAdd1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEmailAdd1ActionPerformed
      this.agregarEmail();  // TODO add your handling code here:
    }//GEN-LAST:event_jButtonEmailAdd1ActionPerformed

    private void jButtonTelEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTelEditarActionPerformed
    this.editarTelefono();        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonTelEditarActionPerformed

    private void jButtonTelCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTelCancelActionPerformed
        this.MostrarBotonesTelefono(true, false, false);
        this.limpiarTelefono();        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonTelCancelActionPerformed

    private void jButtonTelAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTelAddActionPerformed
       this.agregarTelefono(); // TODO add your handling code here:
    }//GEN-LAST:event_jButtonTelAddActionPerformed

    private void jButtonDireccionlAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDireccionlAddActionPerformed
        this.agregarDireccion();        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonDireccionlAddActionPerformed

    private void jButtonDireccionCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDireccionCancelActionPerformed
      this.MostrarBotonesDireccion(true, false, false);  // TODO add your handling code here:
      this.limpiarDireccion();
    }//GEN-LAST:event_jButtonDireccionCancelActionPerformed

    private void jButtonDireccionEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDireccionEditarActionPerformed
      this.editarDireccion();  // TODO add your handling code here:
    }//GEN-LAST:event_jButtonDireccionEditarActionPerformed

    private void jLabel7MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel7MousePressed
        this.agregarTipoTelefono();      // TODO add your handling code here:
    }//GEN-LAST:event_jLabel7MousePressed

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
    private javax.swing.JButton jButtonAdd;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonDireccionCancel;
    private javax.swing.JButton jButtonDireccionEditar;
    private javax.swing.JButton jButtonDireccionlAdd;
    private javax.swing.JButton jButtonEditar;
    private javax.swing.JButton jButtonEmailAdd1;
    private javax.swing.JButton jButtonEmailCancel1;
    private javax.swing.JButton jButtonEmailEditar;
    private javax.swing.JButton jButtonTelAdd;
    private javax.swing.JButton jButtonTelCancel;
    private javax.swing.JButton jButtonTelEditar;
    private javax.swing.JComboBox<String> jComboBoxTipoTelefono;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPopupMenu jPopupMenuCliente;
    private javax.swing.JPopupMenu jPopupMenuDireccion;
    private javax.swing.JPopupMenu jPopupMenuEmail;
    private javax.swing.JPopupMenu jPopupMenuTelefono;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTextField jTNombre;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTableCliente;
    private javax.swing.JTable jTableDireccion;
    private javax.swing.JTable jTableEmail;
    private javax.swing.JTable jTableTel;
    private javax.swing.JTextArea jTextAreaDireccion;
    private javax.swing.JTextField jTextFieldApellido;
    private javax.swing.JTextField jTextFieldBuscador;
    private javax.swing.JTextField jTextFieldCedula;
    private javax.swing.JTextField jTextFieldEmail;
    private javax.swing.JTextField jTextFieldRNC;
    private javax.swing.JTextField jTextFieldTelefono;
    // End of variables declaration//GEN-END:variables
}
