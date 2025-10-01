/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;


import java.util.*;

/**
 * Analizador Sintáctico (mínimo) para:
 *  1) Chequeo de delimitadores (), {}, []
 *  2) Chequeo de punto y coma al final de statements
 */
public class analizadorSintactico {

    public static class ErrorSintactico {
        public final String mensaje;
        public final int linea;
        public ErrorSintactico(String mensaje, int linea){
            this.mensaje = mensaje; this.linea = linea;
        }
        @Override public String toString(){ return "[SINTAXIS] " + mensaje + " (línea " + linea + ")"; }
    }

    private static final Map<String, String> PAIRS = new HashMap<>();
    static {
        PAIRS.put("(", ")");
        PAIRS.put("{", "}");
        PAIRS.put("[", "]");
    }

    private static boolean esApertura(String lex){
        return "(".equals(lex) || "{".equals(lex) || "[".equals(lex);
    }
    private static boolean esCierre(String lex){
        return ")".equals(lex) || "}".equals(lex) || "]".equals(lex);
    }

    /** Recorre los tokens en orden y valida balance y ; faltantes */
    public List<ErrorSintactico> revisar(List<analizadorLexico.Token> tokens){
        List<ErrorSintactico> errores = new ArrayList<>();
        Deque<analizadorLexico.Token> pila = new ArrayDeque<>();

        for (int i=0; i<tokens.size(); i++){
            analizadorLexico.Token t = tokens.get(i);

            // ----- Balance de delimitadores -----
            if (t.tipo == analizadorLexico.TK_DELIM){
                String lex = t.lexema;
                if (esApertura(lex)){
                    pila.push(t);
                } else if (esCierre(lex)){
                    if (pila.isEmpty()){
                        errores.add(new ErrorSintactico(
                            "Delimitador de cierre '"+lex+"' sin apertura correspondiente",
                            t.linea
                        ));
                    } else {
                        analizadorLexico.Token open = pila.pop();
                        String esperado = PAIRS.get(open.lexema);
                        if (!lex.equals(esperado)){
                            errores.add(new ErrorSintactico(
                                "Delimitador '"+lex+"' no coincide. Se esperaba '"+esperado+"' para abrir en línea "+open.linea,
                                t.linea
                            ));
                        }
                    }
                }
            }

          
        // ----- Falta de ';' -----
if (t.tipo == analizadorLexico.TK_KEYWORD || t.tipo == analizadorLexico.TK_ID) {
    String lex = t.lexema.toLowerCase();

    boolean esTipoDeclaracion = 
            lex.equals("int") || lex.equals("double") || lex.equals("float") ||
            lex.equals("boolean") || lex.equals("char") || lex.equals("string") ||
            lex.equals("long") || lex.equals("short") || lex.equals("byte");
    boolean esReturn = lex.equals("return");

    if (esTipoDeclaracion || esReturn) {
        // buscamos hasta ; o hasta otro tipo de declaración/llave
        int j = i+1;
        boolean encontradoPuntoComa = false;
        while (j < tokens.size()) {
            analizadorLexico.Token next = tokens.get(j);

            if(next.tipo == analizadorLexico.TK_DELIM && ";".equals(next.lexema)){
                encontradoPuntoComa = true;
                break;
            }

            if(next.tipo == analizadorLexico.TK_KEYWORD) {
                String kw = next.lexema.toLowerCase();
                if(kw.equals("int")||kw.equals("double")||kw.equals("float")||
                   kw.equals("boolean")||kw.equals("char")||kw.equals("string")||
                   kw.equals("long")||kw.equals("short")||kw.equals("byte")||
                   kw.equals("return")||kw.equals("class")||kw.equals("public")||
                   kw.equals("private")||kw.equals("protected")) {
                    // Nuevo statement detectado sin haber visto ';'
                    break;
                }
            }

            if(next.tipo == analizadorLexico.TK_DELIM && ("{".equals(next.lexema) || "}".equals(next.lexema))){
                break;
            }
            j++;
        }

        if(!encontradoPuntoComa){
            errores.add(new ErrorSintactico(
                "Falta ';' al final de la instrucción que inicia en '"+t.lexema+"'",
                t.linea
            ));
        }
    }
}

        }

        // Lo que quedó abierto sin cerrar
        while(!pila.isEmpty()){
            analizadorLexico.Token open = pila.pop();
            String esperado = PAIRS.get(open.lexema);
            errores.add(new ErrorSintactico(
                "Falta delimitador de cierre '"+esperado+"' (apertura en línea "+open.linea+")",
                open.linea
            ));
        }

        return errores;
    }
}
