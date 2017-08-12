/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Formularios;

import Conexion.*;
import java.awt.event.KeyEvent;

 
/**
 *
 * @author hp
 */
public class Menuprincipal2 extends javax.swing.JFrame {

   private String usuarioID="1";
   private String usuarioNombre="";
   private String usuarioTipo="";
   private ConectarBD conector = null;
   private int[] combinar = {0,0,0,0};
   
   private boolean administrador =false, supervisor=false, cajero=false;
   
   public void validarTipoUsuario(){
     administrador =false;
     supervisor=false;
     cajero=false;
     switch(this.usuarioTipo){
         case "administrador":
             this.administrador =true;
             break;
         case "supervisor":
             this.supervisor =true;
             break;
         case "cajero":
             this.cajero=true;
             break;
     }
   }
      public void setUsuarioID(String usuarioID){
         this.usuarioID = usuarioID;
         }
   
   
     public void setUsuarioTipo(String usuarioTipo){
         this.usuarioTipo = usuarioTipo;
         }
     public void setUsuarioNombre(String usuarioNombre){
         this.usuarioNombre = usuarioNombre;
         }
     
     public void setConector(ConectarBD conector){
         this.conector = conector;
   }
     
     
     public void cliente (){
         if (this.administrador || this.cajero || this.supervisor){
         ClientesAdministrar cliente = new ClientesAdministrar(this.conector, this.usuarioID,this.usuarioNombre);
        cliente.setVisible(true);
     }
     }
     
     public void Proveedor(){
        if(this.administrador || this.supervisor){
            ProveedorAdministrar proveedor = new ProveedorAdministrar(this.conector,this.usuarioID,this.usuarioNombre);
            proveedor.setYo(proveedor);
            proveedor.setVisible(true);
        }
    }

         public void Producto(){
        if(this.administrador || this.supervisor){
            ProductoAdministrar producto = new ProductoAdministrar(this.conector,
                    this.usuarioID,this.usuarioNombre);
            producto.setVisible(true);
        }
         }
            public void ComprarProducto(){
        if(this.administrador || this.supervisor){
            CompraProductoAdministrar compra = new CompraProductoAdministrar(this.conector,
                    this.usuarioID,this.usuarioNombre);
            compra.setYo(compra);
            compra.setVisible(true);
        }
    }

         public void TipoTelefono(){
        //TipoTelefonoAdministrar
        if(this.administrador || this.cajero || this.supervisor){
            TipoTelefonoAdministrar tipoTelefono = new TipoTelefonoAdministrar(this.conector,
                    this.usuarioID,this.usuarioNombre);
            tipoTelefono.setVisible(true);
        }
    }
         
    public void acercade(){
       if (this.administrador || this.cajero || this.supervisor){
           AcercaDe sobre = new AcercaDe();
           sobre.setVisible(true);
       }
   }
   public void usuario(){
       if (this.administrador){
           UsuariosAdministrar usuario = new UsuariosAdministrar(this.conector, this.usuarioID,this.usuarioNombre);
           usuario.setVisible(true);
       }
   }
   
   

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        usuarioConectado = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jMenuBar2 = new javax.swing.JMenuBar();
        jMenuInicio = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItemSalir = new javax.swing.JMenuItem();
        jMenuAdm = new javax.swing.JMenu();
        jMenuItemUsuarios = new javax.swing.JMenuItem();
        jMenuItemClientes = new javax.swing.JMenuItem();
        jMenuItemProveedor = new javax.swing.JMenuItem();
        jMenuItemTipoTelefono = new javax.swing.JMenuItem();
        jMenuItemProducto = new javax.swing.JMenuItem();
        jMenuAyuda = new javax.swing.JMenu();
        jMenuItemAcercade = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        usuarioConectado.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        usuarioConectado.setText("...");
        usuarioConectado.setName("usuarioConectado"); // NOI18N
        getContentPane().add(usuarioConectado, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 30, 30, -1));

        jButton1.setText("Cerrar programa");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 290, -1, -1));

        jButton2.setText("Agregar/Editar Usuario");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 120, -1, -1));

        jButton3.setText("Acerca de nosotros");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 120, -1, -1));

        jButton4.setText("Clientes");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 170, -1, -1));

        jButton5.setText("Empleados");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 170, -1, -1));

        jButton6.setText("Proveedor");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 220, -1, -1));

        jButton7.setText("Administrar Producto");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton7, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 220, -1, -1));

        jButton8.setText("Tipo de Telefonos");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton8, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 250, -1, -1));

        jButton9.setText("Comprar Producto");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton9, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 250, -1, -1));

        jLabel2.setText("Usuario conectado:");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 10, -1, -1));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/Fondo Grande.png"))); // NOI18N
        jLabel1.setText("jLabel1");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 498, 405));

        jMenuInicio.setText("Inicio");

        jMenuItem4.setText("Cambiar contraseña");
        jMenuInicio.add(jMenuItem4);
        jMenuInicio.add(jSeparator2);

        jMenuItemSalir.setText("Salir");
        jMenuItemSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSalirActionPerformed(evt);
            }
        });
        jMenuInicio.add(jMenuItemSalir);

        jMenuBar2.add(jMenuInicio);

        jMenuAdm.setText("Administración");

        jMenuItemUsuarios.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemUsuarios.setText("Usuarios");
        jMenuItemUsuarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemUsuariosActionPerformed(evt);
            }
        });
        jMenuAdm.add(jMenuItemUsuarios);

        jMenuItemClientes.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemClientes.setText("Clientes");
        jMenuItemClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemClientesActionPerformed(evt);
            }
        });
        jMenuAdm.add(jMenuItemClientes);

        jMenuItemProveedor.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemProveedor.setText("Proveedor");
        jMenuItemProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemProveedorActionPerformed(evt);
            }
        });
        jMenuAdm.add(jMenuItemProveedor);

        jMenuItemTipoTelefono.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemTipoTelefono.setText("Tipo Telefono");
        jMenuItemTipoTelefono.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemTipoTelefonoActionPerformed(evt);
            }
        });
        jMenuAdm.add(jMenuItemTipoTelefono);

        jMenuItemProducto.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemProducto.setText("Producto");
        jMenuItemProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemProductoActionPerformed(evt);
            }
        });
        jMenuAdm.add(jMenuItemProducto);

        jMenuBar2.add(jMenuAdm);

        jMenuAyuda.setText("Ayuda");

        jMenuItemAcercade.setText("Sobre M. A.");
        jMenuItemAcercade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAcercadeActionPerformed(evt);
            }
        });
        jMenuAyuda.add(jMenuItemAcercade);

        jMenuBar2.add(jMenuAyuda);

        setJMenuBar(jMenuBar2);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    public Menuprincipal2(ConectarBD conector, String usuarioID, String usuarioNombre, String Tipo){
         initComponents();
         this.setConector(conector);
         this.setUsuarioID(usuarioID);
         this.setUsuarioNombre(usuarioNombre);
         this.setUsuarioTipo(Tipo);
         this.validarTipoUsuario();
         this.usuarioConectado.setText(usuarioNombre);
         
     }
    
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    System.exit(0);        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jMenuItemSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSalirActionPerformed
        // TODO add your handling code here:
        System.exit(0); 
    }//GEN-LAST:event_jMenuItemSalirActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        this.usuario();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        this.acercade();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
       this.cliente(); // TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
       this.usuario();  // TODO add your handling code here:
    }//GEN-LAST:event_jButton5ActionPerformed

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        // TODO add your handling code here:
        System.out.println(evt.getKeyCode());
        if (evt.getKeyCode()== KeyEvent.VK_CONTROL){
            this.combinar[0]=KeyEvent.VK_CONTROL;
        
        }else if (evt.getKeyCode()== KeyEvent.VK_SHIFT){
        }else if (evt.getKeyCode()== KeyEvent.VK_U){
            this.combinar[1]=KeyEvent.VK_U;
            if(this.combinar[0]==17 && this.combinar[1]==85){
                this.usuario();
            }
        }else if (evt.getKeyCode()== KeyEvent.VK_C){
            this.combinar[1]=KeyEvent.VK_C;
            if(this.combinar[0]==17 && this.combinar[1]==67){
                this.cliente();
            }
            }else if(evt.getKeyCode() == KeyEvent.VK_P ){
            this.combinar[1] = KeyEvent.VK_P;
            if((this.combinar[0] == KeyEvent.VK_CONTROL) &&
                    (this.combinar[1] == KeyEvent.VK_P)){
                this.Proveedor();        
            }    
        }else {
            System.out.println(evt.getKeyCode());
            this.combinar[0]=0;
            this.combinar[1]=0;
        }
    }//GEN-LAST:event_formKeyPressed

    private void jMenuItemAcercadeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAcercadeActionPerformed
       this.acercade();
    }//GEN-LAST:event_jMenuItemAcercadeActionPerformed

    private void jMenuItemUsuariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemUsuariosActionPerformed
        this.usuario();
    }//GEN-LAST:event_jMenuItemUsuariosActionPerformed

    private void jMenuItemProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemProveedorActionPerformed
        this.Proveedor();        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItemProveedorActionPerformed

    private void jMenuItemTipoTelefonoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemTipoTelefonoActionPerformed
        this.TipoTelefono();        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItemTipoTelefonoActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
      this.Proveedor();  // TODO add your handling code here:
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        this.Producto();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jMenuItemClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemClientesActionPerformed
    this.cliente();        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItemClientesActionPerformed

    private void jMenuItemProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemProductoActionPerformed
    this.Producto();        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItemProductoActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
this.ComprarProducto();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
this.TipoTelefono();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton8ActionPerformed

    /**
     * @param args the command line arguments
     */
   
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenu jMenuAdm;
    private javax.swing.JMenu jMenuAyuda;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JMenu jMenuInicio;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItemAcercade;
    private javax.swing.JMenuItem jMenuItemClientes;
    private javax.swing.JMenuItem jMenuItemProducto;
    private javax.swing.JMenuItem jMenuItemProveedor;
    private javax.swing.JMenuItem jMenuItemSalir;
    private javax.swing.JMenuItem jMenuItemTipoTelefono;
    private javax.swing.JMenuItem jMenuItemUsuarios;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JLabel usuarioConectado;
    // End of variables declaration//GEN-END:variables
}
