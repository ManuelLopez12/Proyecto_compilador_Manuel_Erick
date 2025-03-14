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
    
    public void abrirArchivo(JTextArea txt){
        line = new ArrayList<>();
        String parrafo = "";
        JFileChooser muestra = new JFileChooser();
        int resultado = muestra.showOpenDialog(v);
        if(resultado == JFileChooser.APPROVE_OPTION){
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
                    txt.append(line.get(i)+"\n");
                    
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                        "Error \n"+ e, "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } 
        }
    }
    

