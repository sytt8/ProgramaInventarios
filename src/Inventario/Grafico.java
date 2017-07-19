/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Inventario;

import Formularios.Login1;
import Conexion.ConectarBD;

/**
 *
 * @author hp
 */
public class Grafico {

    /**
     * @param args the command line arguments
     */
   
    public static void main(String[] arg){
        ConectarBD conexion = new ConectarBD();
         Login1 log = new Login1();
            log.setVisible(true);
    }
}
