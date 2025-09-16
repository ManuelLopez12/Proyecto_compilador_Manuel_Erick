/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import compiladoresaul.vista.Ventana;
import java.util.ArrayList;
import javax.swing.JTextArea;

/**
 *
 * @author JM LOPEZ HURTADO
 */
public class analizadorLexico {
     private Ventana v;
    private ArrayList<String> line;
    private ArrayList<tablaSimbolos> tablaSimboles = new ArrayList<>();
    private String corregirPalabra(String palabra) {
    // Definimos un umbral de "distancia" de 1, para errores de una sola letra.
    String palabraCorregida = palabra;
    int distanciaMinima = 2; // Un valor grande para empezar

    // Tu lista de palabras clave, clases y métodos correctos
    String[] palabrasCorrectas = {
        "abstract", "assert", "boolean", "break", "byte", "case", "catch",
        "char", "class", "const", "continue", "default", "do", "double", "else",
        "enum", "exports", "extends", "final", "finally", "float", "for", "goto",
        "if", "implements", "import", "instanceof", "int", "interface", "long",
        "module", "native", "new", "package", "private", "protected", "public",
        "requires", "return", "short", "static", "strictfp", "super", "switch",
        "synchronized", "this", "throw", "throws", "to", "transient", "try", "var",
        "void", "volatile", "while",
        "Object", "String", "System", "Integer", "Double", "toString", "equals",
        "Math", "Thread", "Runnable", "Exception", "Error", "Throwable", "Number",
        "Package", "Process", "Runtime", "StackTraceElement", "StringBuffer",
        "StringBuilder", "Void", "main", "println", "charAt", "substring", "indexOf"
        // Asegúrate de que esta lista incluya TODAS tus palabras clave, clases y métodos
    };
    
    // Recorre la lista de palabras correctas
    for (String correcta : palabrasCorrectas) {
        // Si las palabras tienen la misma longitud o difieren en 1
        if (Math.abs(palabra.length() - correcta.length()) <= 1) {
            int distancia = 0;
            // Compara carácter por carácter para ver cuántos difieren
            for (int i = 0; i < Math.min(palabra.length(), correcta.length()); i++) {
                if (palabra.toLowerCase().charAt(i) != correcta.toLowerCase().charAt(i)) {
                    distancia++;
                }
            }
            // Agrega la diferencia de longitud a la distancia
            distancia += Math.abs(palabra.length() - correcta.length());

            if (distancia < distanciaMinima) {
                distanciaMinima = distancia;
                palabraCorregida = correcta;
            }
        }
    }

    // Si encontramos una corrección con solo 1 diferencia, la usamos.
    if (distanciaMinima == 1) {
        return palabraCorregida;
    }

    return palabra; // Si no hay una corrección clara, devuelve la palabra original.
}
        public void abrirArchivo1(JTextArea txt) {
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
        private String comentariosEliminados(String codigo){
            String expresionRegularS = "//.*";
            codigo = codigo.replaceAll(expresionRegularS,"");
            
            String expresionRgularM ="/\\\\*.*?\\\\*/";
            Pattern compilo = Pattern.compile(expresionRgularM,Pattern.DOTALL);
            Matcher busco = compilo.matcher(codigo);
            codigo = busco.replaceAll("");
            
            return codigo;
        }
         public void obtenerToken(JTextArea texto, JTextArea txtSalida) {
             String sinComentarios = comentariosEliminados(texto.getText());
        ArrayList<String> id = new ArrayList<>();
       // ArrayList<String> aux = new ArrayList<>();// para los elementos repetidos 
        String ExR = "0|[0-9]+|[a-zA-Z][a-zA-Z0-9_]*|\\.|\\+|\\*|\\/|\\-|\\,|\\;|\\<>|\\=|\\_|\\->|\\<-|\\<=|\\>=|\\:|\\{|\\}|\"[^\"]*\"";
        Pattern compilar = Pattern.compile(ExR);
        Matcher buscar = compilar.matcher(texto.getText());
        tablaSimboles.clear();
        while (buscar.find()) {
            String elemento = buscar.group();    
            int token = obtenido(elemento);
            id.add(String.format("%s\t%d\n", elemento, token));
         String tipoToken;
         switch (token){
            case 2:  tipoToken = "Identificador"; break;
            case 3:  tipoToken = "Cadena de Texto"; break;
            case 8:  tipoToken = "Palabra Reservada"; break;
            case 9:  tipoToken = "Clase"; break;
            case 10: tipoToken = "Método"; break;
            case 11: tipoToken = "Modificador de Atributo"; break;
            case 12: tipoToken = "Operador"; break;
            case 13: tipoToken = "Delimitador";break;
            default: tipoToken = "Desconocido"; break;    
         }
           Simbolo nuevoSimbolo = new Simbolo(elemento, tipoToken, token);
        tablaDeSimbolos.add(nuevoSimbolo);
        }
        
        txtSalida.setText("");

        for (int i = 0; i < id.size(); i++) {
            txtSalida.append(id.get(i));
        }
    }
         private int obtenido(String elemento) {
             private int obtenerCadena(String elemento){
              if (elemento.startsWith("\"")){
                  return 11;
              }
              
          }
             public void mostrarTablaDeSimbolos(JTextArea txtSalida) {
    txtSalida.setText("=== TABLA DE SÍMBOLOS ===\n");
    if (tablaDeSimbolos.isEmpty()) {
        txtSalida.append("La tabla de símbolos está vacía.\n");
    } else {
        for (int i = 0; i < tablaDeSimbolos.size(); i++) {
            Simbolo simbolo = tablaDeSimbolos.get(i);
            txtSalida.append(simbolo.toString() + "\n");
        }
    }
    txtSalida.append("==========================\n");
}
             
         //para las palabras reservadas 
          switch(elemento){
        case "abstract":
        case "assert":
        case "boolean":
        case "break":
        case "byte":
        case "case":
        case "catch":
        case "char":
        case "class":
        case "const":    
        case "continue":
        case "default":
        case "do":
        case "double":
        case "else":
        case "enum":
        case "exports":
        case "extends":
        case "final":
        case "finally":
        case "float":
        case "for":
        case "goto":     
        case "if":
        case "implements":
        case "import":
        case "instanceof":
        case "int":
        case "interface":
        case "long":
        case "module":
        case "native":
        case "new":
        case "package":
        case "private":
        case "protected":
        case "public":
        case "requires":
        case "return":
        case "short":
        case "static":
        case "strictfp":
        case "super":
        case "switch":
        case "synchronized":
        case "this":
        case "throw":
        case "throws":
        case "to":
        case "transient":
        case "try":
        case "var":
        case "void":
        case "volatile":
        case "while":
            return 8; 
        }
          //clases
             switch (elemento) {
        case "Object":
        case "Class":
        case "String":
        case "System":
        case "Integer":
        case "Double":
        case "Float":
        case "Long":
        case "Short":
        case "Byte":
        case "Character":
        case "Boolean":
        case "Math":
        case "Thread":
        case "Runnable":
        case "Exception":
        case "Error":
        case "Throwable":
        case "Number":
        case "Package":
        case "Process":
        case "Runtime":
        case "StackTraceElement":
        case "StringBuffer":
        case "StringBuilder":
        case "Void":
            return 9;
             }
        //token para los numeros 
        if (elemento.matches("[0-9]+")) {
            return 1;
        }
        // token para los indentificadores 
        if (elemento.matches("[a-zA-Z][a-zA-Z0-9_]*")) {
            return 2;
        }
        switch(elemento){
           case "toString":
        case "equals":
        case "hashCode":
        case "getClass":
        case "notify":
        case "notifyAll":
        case "wait":
        case "finalize":
        // Métodos de String
        case "length":
        case "charAt":
        case "substring":
        case "indexOf":
        case "lastIndexOf":
        case "contains":
        case "startsWith":
        case "endsWith":
        case "toLowerCase":
        case "toUpperCase":
        case "trim":
        case "replace":
        case "split":
        // Métodos de Math
        case "abs":
        case "max":
        case "min":
        case "pow":
        case "sqrt":
        case "random":
        case "round":
        case "floor":
        case "ceil":
        // Métodos de Thread
        case "start":
        case "run":
        case "sleep":
        case "interrupt":
        case "join":
        case "isAlive":
        // Métodos de System
        case "currentTimeMillis":
        case "nanoTime":
        case "exit":
        case "gc":
        case "arraycopy":
        case "getProperty":
        return 10;
        }
         // para los aritmeticos 
        switch(elemento){
          case "+":
          case "-":
          case "*":
          case "/":
          case "=":
          case "<>":
          case "<=":
          case ">=":
          case "->":
          case "<-":
            return 12;      
        }
       switch (elemento) {
         case "(":
         case ")":
         case "{":
         case "}":
         case "[":
         case "]":
         case ",":
         case ";":
         case ":":
         case ".":
        return 13; 
}
          
        
        return -1;

    }


}
