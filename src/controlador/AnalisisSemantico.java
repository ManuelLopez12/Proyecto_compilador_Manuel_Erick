/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import java.util.*;
/**
 *
 * @author ericknungaray
 */


public class AnalisisSemantico {

    private final List<Simbolo> tabla;       // Tu tabla de símbolos real
    private final List<String> expresiones;  // Lista de expresiones aritméticas
    private String expresionLimpia;
public String getExpresionLimpia() { return expresionLimpia; }

    public AnalisisSemantico(List<Simbolo> tabla, List<String> expresiones) {
        this.tabla = tabla;
        this.expresiones = expresiones;
    }

    // ================================================
    // MÉTODO PRINCIPAL DEL ANÁLISIS SEMÁNTICO
    // ================================================
 public void ejecutar() {

    if (expresiones == null || expresiones.isEmpty()) {
        System.out.println("El análisis semántico no encontró operaciones aritméticas.");
        return;
    }

    // SOLO se analiza la primera expresión de acuerdo a la rúbrica
    String expr = expresiones.get(0);
    System.out.println("\n=== ANÁLISIS SEMÁNTICO ===");
    System.out.println("Expresión encontrada: " + expr);

    // Separar lado izquierdo (variable destino)
    String[] partes = expr.split("=");
    if (partes.length < 2) {
        System.out.println("No es una expresión válida.");
        return;
    }

    String variableDestino = partes[0].trim();
    String parteDerecha = partes[1].replace(";", "").trim();
    
    // GUARDAR la expresión limpia para el generador de 3 direcciones
    expresionLimpia = parteDerecha;

    // Validar que la expresión realmente es ARITMÉTICA
    if (!parteDerecha.matches(".*[+\\-*/].*")) {
        System.out.println("La primera expresión no es aritmética, no requiere análisis semántico.");
        return;
    }

    // Construir árbol
    Nodo raiz;
    try {
        raiz = construirArbol(parteDerecha);
    } catch (Exception e) {
        System.out.println("ERROR: La expresión es inválida para análisis aritmético.");
        return;
    }

    // PILA IRD
    List<String> pilaIRD = new ArrayList<>();
    inorder(raiz, pilaIRD);
    System.out.println("PILA IRD (Izq - Raíz - Der): " + pilaIRD);

    // PILA IDR
    List<String> pilaIDR = new ArrayList<>();
    postorder(raiz, pilaIDR);
    System.out.println("PILA IDR (Izq - Der - Raíz): " + pilaIDR);

    // Verificar tipos
    verificarTipos(variableDestino, raiz);
}


    private boolean esOperador(char c) {
        return "+-*/".indexOf(c) >= 0;
    }

    private int prioridad(char c) {
        return (c == '*' || c == '/') ? 2 : 1;
    }

    // ================================================
    // RECORRIDOS: IRD y IDR
    // ================================================
    private void inorder(Nodo n, List<String> pila) {
        if (n == null) return;
        inorder(n.izq, pila);
        pila.add(n.valor);
        inorder(n.der, pila);
    }

    private void postorder(Nodo n, List<String> pila) {
        if (n == null) return;
        postorder(n.izq, pila);
        postorder(n.der, pila);
        pila.add(n.valor);
    }

    // ================================================
    // VERIFICACIÓN SEMÁNTICA DE TIPOS
    // ================================================
    private void verificarTipos(String destino, Nodo raiz) {

        Simbolo sDestino = buscarSimbolo(destino);

        if (sDestino == null) {
            System.out.println("Error: la variable '" + destino + "' no está declarada.");
            return;
        }

        String tipoDestino = sDestino.getTipo();
        String tipoResultado = evaluarTipo(raiz);

        System.out.println("Tipo de destino: " + tipoDestino);
        System.out.println("Tipo de resultado: " + tipoResultado);

        if (compatible(tipoResultado, tipoDestino)) {
            System.out.println("✔ Compatible: El análisis semántico es correcto.");
        } else {
            System.out.println("❌ Error semántico: No se puede asignar " + tipoResultado +
                               " a una variable de tipo " + tipoDestino);
        }
    }

    // Calcula tipo resultante de la expresión
    private String evaluarTipo(Nodo n) {

        if (n == null) return "int";

        if (n.esHoja()) {
            Simbolo s = buscarSimbolo(n.valor);
            return (s != null ? s.getTipo() : "int");
        }

        String t1 = evaluarTipo(n.izq);
        String t2 = evaluarTipo(n.der);

        return tipoSuperior(t1, t2);
    }

    // Jerarquía simple
    private int nivel(String t) {
        switch (t) {
            case "int": return 1;
            case "float": return 2;
            case "double": return 3;
            default: return 1;
        }
    }

    private String tipoSuperior(String t1, String t2) {
        return nivel(t1) >= nivel(t2) ? t1 : t2;
    }

   private boolean compatible(String res, String dest) {

    // === REGLA: boolean solo acepta boolean ===
    if (dest.equals("boolean")) {
        return res.equals("boolean");
    }

    // === REGLA: String solo acepta String ===
    if (dest.equals("String")) {
        return res.equals("String");
    }

    // === REGLA: si el resultado NO es numérico → incompatible ===
    if (!esNumerico(res) || !esNumerico(dest)) {
        return false;
    }

    // === REGLA: si ambos son numéricos → permitir asignaciones hacia arriba ===
    return tamaño(res) <= tamaño(dest);
}


    // ================================================
    // CLASE NODO DEL ÁRBOL
    // ================================================
    class Nodo {
        String valor;
        Nodo izq, der;

        Nodo(String v) { this.valor = v; }
        Nodo(String v, Nodo i, Nodo d) {
            this.valor = v;
            this.izq = i;
            this.der = d;
        }

        boolean esHoja() { return izq == null && der == null; }
    }
 private Nodo construirArbol(String expr) {
    Stack<Nodo> operandos = new Stack<>();
    Stack<Character> operadores = new Stack<>();

    expr = expr.replace(" ", "");

    for (int i = 0; i < expr.length(); i++) {
        char c = expr.charAt(i);

        // 1. MANEJO DE PARÉNTESIS DE APERTURA
        if (c == '(') {
            operadores.push(c);
        }
        // 2. MANEJO DE PARÉNTESIS DE CIERRE
        else if (c == ')') {
            while (!operadores.isEmpty() && operadores.peek() != '(') {
                if (operandos.size() < 2)
                    throw new RuntimeException("Expresión inválida, faltan operandos.");

                Nodo der = operandos.pop();
                Nodo izq = operandos.pop();
                char op = operadores.pop();
                operandos.push(new Nodo(String.valueOf(op), izq, der));
            }
            
            if (!operadores.isEmpty())
                operadores.pop(); // Quitar el '('
        }
        // 3. MANEJO DE LETRAS Y NÚMEROS (identificadores y literales)
        else if (Character.isLetterOrDigit(c)) {
            StringBuilder operando = new StringBuilder();
            
            // Leer todo el identificador o número
            while (i < expr.length() && Character.isLetterOrDigit(expr.charAt(i))) {
                operando.append(expr.charAt(i));
                i++;
            }
            i--; // Retroceder uno porque el for hará i++
            
            operandos.push(new Nodo(operando.toString()));
        }
        // 4. MANEJO DE OPERADORES
        else if (esOperador(c)) {
            while (!operadores.isEmpty() && 
                   operadores.peek() != '(' &&
                   prioridad(operadores.peek()) >= prioridad(c)) {

                if (operandos.size() < 2)
                    throw new RuntimeException("Expresión inválida, faltan operandos.");

                Nodo der = operandos.pop();
                Nodo izq = operandos.pop();
                char op = operadores.pop();
                operandos.push(new Nodo(String.valueOf(op), izq, der));
            }

            operadores.push(c);
        }
    }

    // Procesar operadores restantes
    while (!operadores.isEmpty()) {
        if (operandos.size() < 2)
            throw new RuntimeException("Expresión inválida al finalizar.");

        Nodo der = operandos.pop();
        Nodo izq = operandos.pop();
        char op = operadores.pop();
        operandos.push(new Nodo(String.valueOf(op), izq, der));
    }

    if (operandos.isEmpty())
        throw new RuntimeException("Expresión inválida: no hay operandos finales.");

    return operandos.pop();
}

// BÚSQUEDA EN LA TABLA DE SÍMBOLOS

private Simbolo buscarSimbolo(String nombre) {
    for (Simbolo s : tabla) {
        if (s.getNombre().equals(nombre)) {
            return s;
        }
    }
    return null;
}
private boolean esNumerico(String tipo) {
    return tipo.equals("int") || tipo.equals("float") || tipo.equals("double") ||
           tipo.equals("long") || tipo.equals("short") || tipo.equals("byte");
}

private int tamaño(String tipo) {
    switch (tipo) {
        case "byte": return 1;
        case "short": return 2;
        case "int": return 3;
        case "long": return 4;
        case "float": return 5;
        case "double": return 6;
        default: return 0; // no numérico
    }
}

}
