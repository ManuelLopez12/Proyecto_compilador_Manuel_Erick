/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

/**
 *
 * @author ericknungaray
 */


import java.util.Stack;
import javax.swing.JTextArea;

public class Generador3Direcciones {

    private int tempCounter = 1;
    private StringBuilder codigo = new StringBuilder();

    // ------------------------------
    // CLASE NODO LOCAL (mini parser)
    // ------------------------------
    private static class Nodo {
        String valor;
        Nodo izq, der;
        Nodo(String v) { valor = v; }
    }

    // ------------------------------
    // MÉTODO PRINCIPAL PARA GUI
    // ------------------------------
    /**
     * Genera código de 3 direcciones desde txtMensaje y lo muestra en txtSalida
     * @param txtMensaje JTextArea con el código fuente
     * @param txtSalida JTextArea donde se mostrará el resultado
     */
    public void generarDesdeGUI(JTextArea txtMensaje, JTextArea txtSalida) {
        try {
            String codigoFuente = txtMensaje.getText();
            
            if (codigoFuente == null || codigoFuente.trim().isEmpty()) {
                txtSalida.setText("Error: No hay código para procesar.");
                return;
            }

            String resultado = generarCodigo(codigoFuente);
            txtSalida.setText(resultado);

        } catch (Exception e) {
            txtSalida.setText("Error: " + e.getMessage());
        }
    }

    // ------------------------------
    // MÉTODO DE GENERACIÓN
    // ------------------------------
    public String generarCodigo(String codigoFuente) {
        try {
            String expr = extraerPrimeraExpresion(codigoFuente);
            if (expr == null) {
                return "No se encontró ninguna expresión aritmética válida.\n\n" +
                       "Asegúrate de que el código contenga una asignación con operadores matemáticos.\n" +
                       "Ejemplo: resultado = a + b * c;";
            }

            Nodo arbol = construirArbol(expr);
            tempCounter = 1;
            codigo.setLength(0);

            String resultadoFinal = procesar(arbol);
            
            // Agregar información adicional
            StringBuilder salida = new StringBuilder();
            salida.append("=== CÓDIGO DE TRES DIRECCIONES ===\n\n");
            salida.append("Expresión encontrada: ").append(expr).append("\n\n");
            salida.append("Código generado:\n");
            salida.append(codigo.toString());
            salida.append("\nResultado final: ").append(resultadoFinal);

            return salida.toString();

        } catch (Exception e) {
            return "Error al generar código de 3 direcciones:\n" + e.getMessage();
        }
    }

    // ------------------------------
    // 1) EXTRAER PRIMERA EXPRESIÓN
    // ------------------------------
    private String extraerPrimeraExpresion(String texto) {
        // Quitar comentarios de bloque /* */
        texto = texto.replaceAll("/\\*[\\s\\S]*?\\*/", " ");
        // Quitar comentarios de línea //
        texto = texto.replaceAll("//[^\\n]*", " ");

        // Buscar todas las asignaciones
        int inicio = 0;
        while (inicio < texto.length()) {
            int posIgual = texto.indexOf("=", inicio);
            
            if (posIgual == -1) return null;
            
            // Verificar que no sea ==, !=, <=, >=
            if (posIgual > 0 && "!<>=".indexOf(texto.charAt(posIgual - 1)) >= 0) {
                inicio = posIgual + 1;
                continue;
            }
            if (posIgual + 1 < texto.length() && texto.charAt(posIgual + 1) == '=') {
                inicio = posIgual + 2;
                continue;
            }

            // Buscar el primer ';' después del '='
            int posPuntoYComa = texto.indexOf(";", posIgual);
            if (posPuntoYComa == -1) return null;

            // Extraer lo que está ENTRE = y ;
            String expr = texto.substring(posIgual + 1, posPuntoYComa).trim();

            // Guardar la expresión original para debug
            String exprOriginal = expr;

            // Eliminar todo lo que no sea parte de una expresión aritmética
            expr = expr.replaceAll("[^a-zA-Z0-9_+\\-*/^()]", "");

            // Validar que tenga operadores aritméticos
            if (!expr.isEmpty() && expr.matches(".*[+\\-*/^].*")) {
                return expr;
            }

            // Si no es válida, buscar la siguiente asignación
            inicio = posPuntoYComa + 1;
        }

        return null;
    }

    // ------------------------------
    // 2) CONSTRUIR ÁRBOL (Shunting Yard)
    // ------------------------------
    private Nodo construirArbol(String expr) {
        // Eliminar espacios
        expr = expr.replaceAll("\\s+", "");

        Stack<String> operadores = new Stack<>();
        Stack<Nodo> operandos = new Stack<>();

        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);

            // Si es letra o número → token completo
            if (Character.isLetterOrDigit(c)) {
                StringBuilder token = new StringBuilder();
                token.append(c);

                // Construir tokens largos
                while (i + 1 < expr.length() && Character.isLetterOrDigit(expr.charAt(i + 1))) {
                    token.append(expr.charAt(++i));
                }

                operandos.push(new Nodo(token.toString()));
                continue;
            }

            // Paréntesis de apertura
            if (c == '(') {
                operadores.push("(");
                continue;
            }

            // Paréntesis de cierre
            if (c == ')') {
                while (!operadores.isEmpty() && !operadores.peek().equals("(")) {
                    crearNodo(operadores.pop(), operandos);
                }
                if (!operadores.isEmpty()) {
                    operadores.pop(); // quitar "("
                }
                continue;
            }

            // Operadores
            if ("+-*/^".indexOf(c) >= 0) {
                String op = String.valueOf(c);

                while (!operadores.isEmpty() &&
                       !operadores.peek().equals("(") &&
                       prioridad(operadores.peek()) >= prioridad(op)) {
                    crearNodo(operadores.pop(), operandos);
                }

                operadores.push(op);
                continue;
            }
        }

        // Procesar operadores pendientes
        while (!operadores.isEmpty()) {
            crearNodo(operadores.pop(), operandos);
        }

        return operandos.pop();
    }

    private void crearNodo(String op, Stack<Nodo> nodos) {
        Nodo der = nodos.pop();
        Nodo izq = nodos.pop();
        Nodo nuevo = new Nodo(op);
        nuevo.izq = izq;
        nuevo.der = der;
        nodos.push(nuevo);
    }

    private int prioridad(String op) {
        switch (op) {
            case "^": return 3;
            case "*": 
            case "/": return 2;
            case "+": 
            case "-": return 1;
            default: return 0;
        }
    }

    // ------------------------------
    // 3) GENERAR 3 DIRECCIONES
    // ------------------------------
    private String procesar(Nodo n) {
        if (n.izq == null && n.der == null) {
            return n.valor;
        }

        String izq = procesar(n.izq);
        String der = procesar(n.der);

        String temp = "temp" + (tempCounter++);
        codigo.append(temp)
              .append(" = ")
              .append(izq).append(" ")
              .append(n.valor).append(" ")
              .append(der)
              .append("\n");

        return temp;
    }
}