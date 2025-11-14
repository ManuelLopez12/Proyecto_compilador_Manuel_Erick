/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package compiladoresaul.vista;

import controlador.AnalisisSemantico;
import controlador.Control;
import controlador.analizadorLexico;
import java.util.List;
import controlador.Simbolo;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;



/**
 *
 * @author JM LOPEZ HURTADO
 * @author ERICK YAZECK NUNGARAY MATA
 */
public class Ventana extends javax.swing.JFrame {
private Control c;

    
    public Ventana() {
        initComponents();
        c = new Control();
   
        
    }

  
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        txtMensaje = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtSalida = new javax.swing.JTextArea();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuArchivo = new javax.swing.JMenu();
        mnuAbrir = new javax.swing.JMenuItem();
        mnuLimpiar = new javax.swing.JMenuItem();
        mnuSalir = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        mnuLexico = new javax.swing.JMenuItem();
        mnuSintactico = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        txtMensaje.setColumns(20);
        txtMensaje.setRows(5);
        jScrollPane1.setViewportView(txtMensaje);

        txtSalida.setEditable(false);
        txtSalida.setColumns(20);
        txtSalida.setRows(5);
        jScrollPane2.setViewportView(txtSalida);

        mnuArchivo.setText("Archivo");

        mnuAbrir.setText("Abrir");
        mnuAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAbrirActionPerformed(evt);
            }
        });
        mnuArchivo.add(mnuAbrir);

        mnuLimpiar.setText("Limpiar");
        mnuLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuLimpiarActionPerformed(evt);
            }
        });
        mnuArchivo.add(mnuLimpiar);

        mnuSalir.setText("Salir");
        mnuSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSalirActionPerformed(evt);
            }
        });
        mnuArchivo.add(mnuSalir);

        jMenuBar1.add(mnuArchivo);

        jMenu2.setText("Compilar");
        jMenu2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu2ActionPerformed(evt);
            }
        });

        mnuLexico.setText("Léxico");
        mnuLexico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuLexicoActionPerformed(evt);
            }
        });
        jMenu2.add(mnuLexico);

        mnuSintactico.setText("Sintáctico");
        mnuSintactico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSintacticoActionPerformed(evt);
            }
        });
        jMenu2.add(mnuSintactico);

        jMenuItem1.setText("Tabla de simbolos");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem1);

        jMenuItem2.setText("Análisis Semántico");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem2);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 511, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mnuAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAbrirActionPerformed
        // TODO add your handling code here:
        c.abrirArchivo(txtMensaje);
     //  c.abrirArchivo(txtMensaje);
    }//GEN-LAST:event_mnuAbrirActionPerformed

    private void mnuLexicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuLexicoActionPerformed
        // TODO add your handling code here:
        c.obtenerToken(txtMensaje, txtSalida);
        //c.obtenerToken(txtMensaje, txtSalida);
    }//GEN-LAST:event_mnuLexicoActionPerformed

    private void mnuLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuLimpiarActionPerformed
        // TODO add your handling code here:
        txtMensaje.setText("");
        txtSalida.setText("");
    }//GEN-LAST:event_mnuLimpiarActionPerformed

    private void mnuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalirActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_mnuSalirActionPerformed

    private void mnuSintacticoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSintacticoActionPerformed
        // TODO add your handling code here:
        c.sintactico(txtMensaje, txtSalida);
    }//GEN-LAST:event_mnuSintacticoActionPerformed

    private void jMenu2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu2ActionPerformed
     c.mostrarTablaDeSimbolos(txtSalida);
    }//GEN-LAST:event_jMenu2ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        c.mostrarTablaDeSimbolos(txtSalida);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
       // 1. Obtener el código del TextArea donde escribes
    String codigo = txtMensaje.getText();

    // 2. Ejecutar LÉXICO
    analizadorLexico lexico = new analizadorLexico();
    lexico.analizar(codigo);

    // 3. Obtener tabla de símbolos del léxico
    List<Simbolo> tabla = lexico.getTablaSimbolos();

    // 4. Extraer expresiones aritméticas
    List<String> exprs = lexico.extraerExpresionesAritmeticas();

    // 5. Ejecutar semántico
    AnalisisSemantico sem = new AnalisisSemantico(tabla, exprs);

    // 6. Capturar salida de System.out → txtSalida
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(baos);
    PrintStream oldOut = System.out;

    System.setOut(ps);  // redirigimos TODO lo que imprima sem.ejecutar()

    sem.ejecutar();     // aquí se imprime normalmente, pero se captura

    System.out.flush();
    System.setOut(oldOut); // restauramos

    // 7. Mostrar la salida en el text area
    txtSalida.setText(baos.toString());

    }//GEN-LAST:event_jMenuItem2ActionPerformed

    /**
     * @param args the command line arguments
     */
   

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JMenuItem mnuAbrir;
    private javax.swing.JMenu mnuArchivo;
    private javax.swing.JMenuItem mnuLexico;
    private javax.swing.JMenuItem mnuLimpiar;
    private javax.swing.JMenuItem mnuSalir;
    private javax.swing.JMenuItem mnuSintactico;
    private javax.swing.JTextArea txtMensaje;
    private javax.swing.JTextArea txtSalida;
    // End of variables declaration//GEN-END:variables
}
