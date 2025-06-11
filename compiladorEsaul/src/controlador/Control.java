/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import compiladoresaul.vista.Ventana;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/**
 *
 * @author JM LOPEZ HURTADO
 */
public class Control {

    //aegunda revision 14/03/2025
    private Ventana v;
    private ArrayList<String> line;
    private sintatico s;
    
    
  
    
    public void abrirArchivo(JTextArea txt) {
        line = new ArrayList<>();
        String parrafo = "";
        JFileChooser muestra = new JFileChooser();
        int resultado = muestra.showOpenDialog(v);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivo = muestra.getSelectedFile();
            try {
                BufferedReader br = new BufferedReader(new FileReader(archivo));
                parrafo = br.readLine();
                while (parrafo != null) {
                    line.add(parrafo);
                    parrafo = br.readLine();
                }
                txt.setText("");
                for (int i = 0; i < line.size(); i++) {
                    txt.append(line.get(i) + "\n");

                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                        "Error \n" + e, "Error", JOptionPane.ERROR_MESSAGE);
            }

        }
    }

    public void obtenerToken(JTextArea texto, JTextArea txtSalida) {
        ArrayList<String> id = new ArrayList<>();
       // ArrayList<String> aux = new ArrayList<>();// para los elementos repetidos 
        String ExR = "0|[0-9]+|[a-zA-Z][a-zA-Z0-9_]*|\\.|\\+|\\*|\\/|\\-|\\,|\\;|\\<>|\\=|\\_|\\->|\\<-|\\<=|\\>=|\\:";
        Pattern compilar = Pattern.compile(ExR);
        Matcher buscar = compilar.matcher(texto.getText());
        while (buscar.find()) {
            String elemento = buscar.group();
            
          /*boolean repeticiones=false;
            for (int i = 0; i < aux.size(); i++) {
                if(aux.get(i).equals(elemento)){
                    repeticiones=true;
                    break;
                }
            }*/
            
            int token = obtenido(elemento);
            id.add(String.format("%s\t%d\n", elemento, token));
         
         
        }
        
        txtSalida.setText("");

        for (int i = 0; i < id.size(); i++) {
            txtSalida.append(id.get(i));
        }
    }

    private int obtenido(String elemento) {
         //para las palabras reservadas 
          switch(elemento){
             case "const":
                return 8;
            case "var":
                return 14;
            case "proced":
                return 16;
            case "print":
                return 34;
            case "input":
                return 36;
            case "exec":
                return 38;
            case "if":
                return 40;
            case "while":
                return 44;
            case "for":
                return 46;
        }
        //token para los numeros 
        if (elemento.matches("[0-9]+")) {
            return 1;
        }
        // token para los indentificadores 
        if (elemento.matches("[a-zA-Z][a-zA-Z0-9_]*")) {
            return 2;
        }
        //token para los separadores
        switch (elemento) {
            case ".":
                return 4;
            case ",":
                return 6;
            case ";":
                return 12;
            case "{":
                return 30;
            case "}":
                return 32;
            case "(":
                return 56;
            case ")":
                return 58;
            case ":":
                return 42;

        }
        //para los operadores relacionales 
        switch (elemento) {
            case "==":
                return 18;
            case "<>":
                return 20;
            case "<":
                return 22;
            case ">":
                return 24;
            case "<=":
                return 26;
            case ">=":
                return 28;
        }
        // para los aritmeticos 
        switch(elemento){
           case "+":
                return 52;
            case "-":
                return 54;
            case "*":
                return 60;
            case "/":
                return 62; 
        }
       
      
        //para los de asignacion 
        switch (elemento){
              case "=":
                return 10;
            case "->":
                return 48;
            case "<-":
                return 50;
        }
        return -1;

    }
 // Método principal que realiza el análisis sintáctico
public void analizador(JTextArea txtTokens, JTextArea txtSalida) {
    txtSalida.setText("");
    // Obtiene el texto completo del área de tokens
    String texto = txtTokens.getText();
    
    // Divide el texto en líneas usando el salto de línea como separador
    String[] lineas = texto.split("\n");
    
    // Listas para almacenar temporalmente los tokens y sus tipos
    ArrayList<String> tokens = new ArrayList<>();
    ArrayList<Integer> tipos = new ArrayList<>();
    
    // Recorre cada línea del texto de entrada
    for(int i = 0; i < lineas.length; i++) {
        // Elimina espacios en blanco al inicio y final de cada línea
        String linea = lineas[i].trim();
        
        // Verifica que la línea no esté vacía
        if(!linea.isEmpty()) {
            // Divide la línea en partes usando el tabulador como separador
            String[] partes = linea.split("\t");
            
            // Verifica que haya al menos 2 partes (token y tipo)
            if(partes.length >= 2) {
                // Agrega el token (primera parte) a la lista de tokens
                tokens.add(partes[0].trim());
                
                // Convierte la segunda parte a entero y la agrega a la lista de tipos
                tipos.add(Integer.parseInt(partes[1].trim()));
            }
        }
    }
    
    // Convierte la lista de tokens a un array estándar
    String[] arrayTokens = new String[tokens.size()];
    
    // Crea un array para los tipos de tokens del mismo tamaño
    int[] arrayTipos = new int[tipos.size()];
    
    // Copia los elementos de las listas a los arrays
    for(int i = 0; i < tokens.size(); i++) {
        arrayTokens[i] = tokens.get(i);
        arrayTipos[i] = tipos.get(i);
    }
    
    // Crea una instancia del analizador sintáctico con:
    // - Los tokens encontrados
    // - Los tipos de tokens
    // - La referencia a la ventana (v)
    // - El área de texto para mostrar salida
    s = new sintatico(arrayTokens, arrayTipos, v, txtSalida);
    
    // Ejecuta el análisis sintáctico comenzando por la regla 'programa'
    s.programa();
}
  
}
