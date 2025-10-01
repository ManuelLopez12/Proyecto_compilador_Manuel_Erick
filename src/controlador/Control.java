package controlador;

import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Control "fachada" para conectar la UI con el analizador.
 * Expone utilidades para:
 *  - Abrir archivo en un JTextArea
 *  - Ejecutar análisis léxico
 *  - Mostrar tabla de símbolos simple (nombre | tipo | valor)
 *  - Reportar errores (comentarios sin cerrar, etc.)
 */
public class Control {

    private final analizadorLexico lexer = new analizadorLexico();

    /* === Abrir archivo en el editor === */
  public void abrirArchivo(JTextArea destino){
    JFileChooser fc = new JFileChooser();
    // Ahora el filtro es para archivos .java
    fc.setFileFilter(new FileNameExtensionFilter("Archivos Java", "java"));

    int r = fc.showOpenDialog(null);
    if(r == JFileChooser.APPROVE_OPTION){
        File f = fc.getSelectedFile();
        try(BufferedReader br = new BufferedReader(new FileReader(f))){
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = br.readLine()) != null){
                sb.append(line).append("\n");
            }
            destino.setText(sb.toString());
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null, "Error al abrir: "+ex.getMessage(), "Abrir", JOptionPane.ERROR_MESSAGE);
        }
    }
}


    /* === Ejecuta el léxico y plasma tokens + tabla de símbolos === */
    public void analizarLexico(String codigo, JTextArea txtSalida){
        lexer.analizar(codigo);
        StringBuilder sb = new StringBuilder();

        // Errores primero (por ejemplo, comentario sin cerrar)
        List<String> errs = lexer.getErrores();
        if(!errs.isEmpty()){
            for(String e: errs) sb.append("[ERROR] ").append(e).append("\n");
            txtSalida.setText(sb.toString());
            return;
        }

        // Listado de tokens:
        for(analizadorLexico.Token t: lexer.getTokens()){
            sb.append(String.format("%-4d %-12s @ %d:%d\n", t.tipo, t.lexema, t.linea, t.columna));
        }
        sb.append("\n");

        // Tabla de símbolos: SOLO variables {nombre | tipo | valor}
        sb.append("TABLA DE SÍMBOLOS (variables)\n");
        sb.append(String.format("%-20s | %-10s | %-15s\n", "Variable", "Tipo", "Valor"));
        sb.append("---------------------+------------+----------------\n");
        for(analizadorLexico.Simbolo s: lexer.getTablaSimbolos()){
            sb.append(String.format("%-20s | %-10s | %-15s\n",
                    s.nombre, s.tipo, (s.valor==null? "": s.valor)));
        }

        txtSalida.setText(sb.toString());
    }

    /* === Overload para compatibilidad con tu Ventana (le pasas el JTextArea de entrada) === */
    public void analizarLexico(JTextArea txtMensaje, JTextArea txtSalida){
        analizarLexico(txtMensaje.getText(), txtSalida);
    }

    /* === Solo tokens (equivalente a tu obtenerToken anterior) === */
    public void obtenerToken(JTextArea txtMensaje, JTextArea txtSalida){
    lexer.analizar(txtMensaje.getText());

    // Errores léxicos primero
    List<String> errs = lexer.getErrores();
    if(!errs.isEmpty()){
        StringBuilder se = new StringBuilder();
        for(String e: errs) se.append("[ERROR] ").append(e).append("\n");
        txtSalida.setText(se.toString());
        return;
    }

    StringBuilder sb = new StringBuilder();
    List<analizadorLexico.Token> toks = lexer.getTokens();

    int i = 1;
    for (analizadorLexico.Token t : toks) {
        final int code = t.tipo;
        final String tipo;
        switch (code) {
            case analizadorLexico.TK_NUM:     tipo = "NÚMERO"; break;
            case analizadorLexico.TK_ID:      tipo = "IDENTIFICADOR"; break;
            case analizadorLexico.TK_STRING:  tipo = "CADENA"; break;
            case analizadorLexico.TK_OP:      tipo = "OPERADOR"; break;
            case analizadorLexico.TK_DELIM:   tipo = "DELIMITADOR"; break;
            case analizadorLexico.TK_KEYWORD: tipo = "PALABRA_RESERVADA"; break;
            case analizadorLexico.TK_PACKAGE: tipo = "PAQUETE"; break;
            default:                          tipo = "TOKEN_" + code; break;
        }
        // # consecutivo (3 dígitos), código (3 ancho), tipo (alineado 18), y lexema
        sb.append(String.format("%03d [%3d %-18s] %s%n", i, code, tipo, t.lexema));
        i++;
    }

    txtSalida.setText(sb.toString());
}

public void sintactico(JTextArea txtFuente, JTextArea txtSalida){
    lexer.analizar(txtFuente.getText());

    // Si hay errores léxicos
    List<String> errs = lexer.getErrores();
    if(!errs.isEmpty()){
        StringBuilder se = new StringBuilder();
        for(String e: errs) se.append("[ERROR] ").append(e).append("\n");
        txtSalida.setText(se.toString());
        return;
    }

    analizadorSintactico syn = new analizadorSintactico();
    List<analizadorSintactico.ErrorSintactico> errores = syn.revisar(lexer.getTokens());

    if(errores.isEmpty()){
        txtSalida.setText("OK: delimitadores y ';' balanceados.");
    }else{
        StringBuilder sb = new StringBuilder();
        for(var e : errores){
            sb.append(e.toString()).append("\n");
        }
        txtSalida.setText(sb.toString());
    }
}

    /* === Mostrar únicamente la tabla de símbolos (nombre | tipo | valor) === */
    public void mostrarTablaDeSimbolos(JTextArea txtSalida){
        StringBuilder sb = new StringBuilder();
        sb.append("TABLA DE SÍMBOLOS (variables)\n");
        sb.append(String.format("%-20s | %-10s | %-15s\n", "Variable", "Tipo", "Valor"));
        sb.append("---------------------+------------+----------------\n");
        for(analizadorLexico.Simbolo s: lexer.getTablaSimbolos()){
            sb.append(String.format("%-20s | %-10s | %-15s\n",
                    s.nombre, s.tipo, (s.valor==null? "": s.valor)));
        }
        txtSalida.setText(sb.toString());
    }

    /** Exponer tokens si el sintáctico u otra capa los requiere. */
    public List<analizadorLexico.Token> getTokens(){ return lexer.getTokens(); }
    public List<Integer> getTipos(){ return lexer.getTipos(); }
    public List<String> getErrores(){ return lexer.getErrores(); }
}
