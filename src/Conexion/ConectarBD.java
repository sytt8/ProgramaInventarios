/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Conexion;

import Formularios.Login1;
import java.sql.*;
import javax.swing.JOptionPane;


/**
 *
 * @author hp
 */
public class ConectarBD {
    
    private String servidor="jdbc:mysql://localhost:3306/curso_inventario";
    private String user="root";
    private String pass="";
    private Connection con;
    
   
    Statement st;
    ResultSet rs;
   
    
    public ConectarBD(){
        try{
        Class.forName("com.mysql.jdbc.Driver");
        con= DriverManager.getConnection(servidor, user, pass);
        System.out.println("Conectado");
            
            
        }
     catch (SQLException e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        catch (ClassNotFoundException ex){
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    
    }  
     public boolean ActualizarDato(String table_name, String campos, String where){
       try{
           String query = "Update " +table_name + " set " +campos + " where " +where+ "";
           System.out.println(query);
           st=con.createStatement();
            st.executeUpdate(query);
            return true;
        }
       catch (SQLException e){
           System.out.println(e.getMessage());
           JOptionPane.showMessageDialog(null, "Error de actualizacion de la base de datos");
       return false;
       }
   }
       public void insertData(String table_name, String campos, String valores){
          try{
           String query = "INSERT INTO "+table_name+" ("+campos+") VALUES ("+valores+")";
           System.out.println(query);
           st=con.createStatement();
           st.execute(query);
            }
       catch (SQLException e){
           System.out.println(e.getCause()+" " +e.getErrorCode() +" " +e.getMessage());
           JOptionPane.showMessageDialog(null, "Error en el almacenamiento de datos");
      
       } 
       }
   public String obtenerUltimoID(String table_name){
       String id="1";
        try{
           String query = "Select max(id) as id from " +table_name;
           st=con.createStatement();
           rs=st.executeQuery(query);
           
           if (rs.next()){
               id = rs.getString("id");
              System.out.println("ID: " + rs.getString("ID"));   
           }
           
             }
       catch (SQLException e){
         
           JOptionPane.showMessageDialog(null, "Error en la adquisicion del ultimo ID");
      
       } 
        return id;
   }
        
    public java.sql.ResultSet ObtenerDatosParaTabla(String table_name, String campos, String otros){
        
        
        try{
           String query = "Select "+campos+" from " +table_name+" "+otros;
           System.out.println("Obteniendo datos de la tabla: "+query);
           st=con.createStatement();
           rs=st.executeQuery(query);
           }
       catch (SQLException e){
          JOptionPane.showMessageDialog(null, "Error en la adquisicion de obtencion de datos de la tabla: " +e.getMessage());
         } 
        return rs;
    }   
        
    
}
