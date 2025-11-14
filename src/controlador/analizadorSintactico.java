/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;
import controlador.Simbolo;




import java.util.*;

/**
 * Analizador Sintáctico (mínimo) para:
 *  1) Chequeo de delimitadores (), {}, []
 *  2) Chequeo de punto y coma al final de statements
 * 
 *  AUTOR: Manuel Lopez
 *  AUTOR: Erick Nungaray
 */
public class analizadorSintactico {

    private final List<Simbolo> tokens;
    private int index;

    public analizadorSintactico(List<Simbolo> tokens) {
        this.tokens = tokens;
        this.index = 0;
    }

    public boolean analizar() {
        if (analizarBloque() && match(".")) {
            System.out.println("Análisis sintáctico exitoso.");
            return true;
        } else {
            error("Error: se esperaba '.' al final del programa.");
            return false;
        }
    }

    private boolean analizarBloque() {
        return analizarC2() && analizarC4() && analizarC6() && analizarProposicion();
    }

    private boolean analizarC2() {
        if (lookahead("const")) {
            match("const");
            if (!match("id")) {
                error("Se esperaba un identificador después de 'const'");
                return false;
            }
            if (!match("=")) {
                error("Se esperaba '=' después del identificador");
                return false;
            }
            if (!match("num")) {
                error("Se esperaba un número después de '='");
                return false;
            }
            while (lookahead(",")) {
                match(",");
                if (!match("id") || !match("=") || !match("num")) {
                    error("Declaración múltiple incorrecta en const.");
                    return false;
                }
            }
            return match(";");
        }
        return true; // c2 → null
    }

    private boolean analizarC4() {
        if (lookahead("var")) {
            match("var");
            if (!match("id")) {
                error("Se esperaba identificador en declaración var.");
                return false;
            }
            while (lookahead(",")) {
                match(",");
                if (!match("id")) {
                    error("Identificador faltante en declaración múltiple de var.");
                    return false;
                }
            }
            return true;
        }
        return true; // c4 → null
    }

    private boolean analizarC6() {
        if (lookahead("proced")) {
            match("proced");
            if (!match("id")) {
                error("Se esperaba id después de 'proced'");
                return false;
            }
            if (!match(";")) return false;
            if (!analizarBloque()) return false;
            return match(";");
        }
        return true; // c6 → null
    }

    private boolean analizarProposicion() {
        if (lookahead("id")) {
            match("id");
            if (!match("=")) return false;
            return analizarExpresion();
        }
        if (lookahead("print")) {
            match("print");
            return lookahead("id") ? match("id") : match("num");
        }
        if (lookahead("input")) {
            return match("input") && match("id");
        }
        if (lookahead("exec")) {
            return match("exec") && match("id");
        }
        return false;
    }

    private boolean analizarExpresion() {
        // expresión simple: num | id
        return match("num") || match("id");
    }

    private boolean match(String esperado) {
        if (index < tokens.size() && tokens.get(index).getNombre().equals(esperado)) {
            index++;
            return true;
        }
        return false;
    }

    private boolean lookahead(String esperado) {
        return index < tokens.size() && tokens.get(index).getNombre().equals(esperado);
    }

    private void error(String mensaje) {
        System.err.println("Error sintáctico en token " + index + ": " + mensaje);
    }
    
    public boolean llavesBalanceadas(String codigo) {
    Stack<Character> pila = new Stack<>();

    for (int i = 0; i < codigo.length(); i++) {
        char c = codigo.charAt(i);

        if (c == '{') pila.push(c);

        if (c == '}') {
            if (pila.isEmpty()) {
                return false; // cierre sin apertura
            }
            pila.pop();
        }
    }

    return pila.isEmpty();  // si queda algo → no está balanceado
}

}