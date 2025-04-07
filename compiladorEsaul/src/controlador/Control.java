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
        ArrayList<String> aux = new ArrayList<>();// para los elementos repetidos 
        String ExR = "0|[1-9]+|[a-zA-Z][a-zA-Z0-9_]+|\\.|\\+|\\*|\\/|\\-|\\,|\\;|\\<>|\\=|\\_";
        Pattern compilar = Pattern.compile(ExR);
        Matcher buscar = compilar.matcher(texto.getText());
        while (buscar.find()) {
            String elemento = buscar.group();
            
          boolean repeticiones=false;
            for (int i = 0; i < aux.size(); i++) {
                if(aux.get(i).equals(elemento)){
                    repeticiones=true;
                    break;
                }
            }
            if(!repeticiones){
            int token = obtenido(elemento);
            id.add(String.format("%s\t%d\n", elemento, token));
            aux.add(elemento);
         }
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
            case "[":
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

}
