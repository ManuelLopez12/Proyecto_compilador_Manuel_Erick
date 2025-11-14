package controlador;

import java.util.*;
import controlador.Simbolo;



/**
 * Analizador Léxico:
 
 *  - Tabla de símbolos mínima (variables: nombre | tipo | valor), SIN parámetros.
 *  - Palabras reservadas (lista del profe) con comparación case-insensitive.
 *  - Operadores ampliados: !, !=, &&, ||, ==, ++, --, <<, >>, >>>, %, &, |, ^, ~ ...
 * AUTOR: Manuel Lopez
 *  AUTOR: Erick Nungaray
*/

public class analizadorLexico {

    // === Tipos de token ===
    public static final int TK_NUM      = 1;
    public static final int TK_ID       = 2;
    public static final int TK_STRING   = 3;
    public static final int TK_OP       = 12;
    public static final int TK_DELIM    = 13;
    public static final int TK_PACKAGE  = 90;
    public static final int TK_KEYWORD  = 99;
    
    

    /** Palabras reservadas (minúsculas) — exactas + true/false/null. */
    private static final Set<String> KEYWORDS_LOWER = new HashSet<>(Arrays.asList(
        "abstract","continue","for","new","switch",
        "assert","default","goto","package","synchronized",
        "boolean","do","if","private","this",
        "break","double","implements","protected","throw",
        "byte","else","import","public","throws",
        "case","enum","instanceof","return","transient",
        "catch","extends","int","short","try",
        "char","final","interface","static","void",
        "class","finally","long","strictfp","volatile",
        "const","float","native","super","while",
        "true","false","null"
    ));

    /** Tipos válidos para la tabla de símbolos (incluye String/string). */
    private static final Set<String> TIPOS_SOPORTADOS = new HashSet<>(Arrays.asList(
        "byte","short","int","long","float","double","boolean","char","string","String"
    ));

    public static class Token {
        public final int tipo;
        public final String lexema;
        public final int linea;
        public final int columna;
        public Token(int tipo, String lexema, int linea, int columna) {
            this.tipo = tipo; this.lexema = lexema; this.linea = linea; this.columna = columna;
        }
    }

    

    private final List<Token> tokens = new ArrayList<>();
    private final List<Integer> tipos = new ArrayList<>();
    private final List<String> errores = new ArrayList<>();
    private final List<Simbolo> tablaSimbolos = new ArrayList<>();

    public void analizar(String fuente) {
        tokens.clear(); tipos.clear(); errores.clear(); tablaSimbolos.clear();
        List<Token> crudos = tokenizar(fuente);
        tokens.addAll(crudos);
        for(Token t: tokens) tipos.add(t.tipo);
        if(errores.isEmpty()) construirTablaSimbolos(tokens);
    }

    public List<Token> getTokens(){ return tokens; }
    public List<Integer> getTipos(){ return tipos; }
    public List<String> getErrores(){ return errores; }
    public List<Simbolo> getTablaSimbolos(){ return tablaSimbolos; }

    // ===================== LÉXICO =====================
    private List<Token> tokenizar(String src){
        ArrayList<Token> out = new ArrayList<>();
        int n = src.length(), i = 0, linea = 1, col = 1;

        while(i < n){
            char c = src.charAt(i);

            // espacios
            if(c==' '||c=='\t'||c=='\r'){ i++; col++; continue; }
            if(c=='\n'){ i++; linea++; col=1; continue; }

            // // comentario de línea
            if(c=='/' && i+1<n && src.charAt(i+1)=='/'){
                int j=i+2; while(j<n && src.charAt(j)!='\n') j++; i=j; continue;
            }

            // /* comentario de bloque */
            if(c=='/' && i+1<n && src.charAt(i+1)=='*'){
                int j=i+2, sl=linea, sc=col; boolean cerrado=false;
                while(j<n){
                    char ch=src.charAt(j);
                    if(ch=='\n'){ linea++; col=1; j++; continue; }
                    if(ch=='*' && j+1<n && src.charAt(j+1)=='/'){ cerrado=true; j+=2; col+=2; break; }
                    j++; col++;
                }
                if(!cerrado){ errores.add("Comentario de bloque abierto en "+sl+":"+sc+" y nunca se cerró"); return out; }
                i=j; continue;
            }

            // string
            if(c=='"'){
                int j=i+1; StringBuilder sb=new StringBuilder(); int sc=col;
                while(j<n && src.charAt(j)!='"'){
                    if(src.charAt(j)=='\n'){ errores.add("String sin cerrar en "+linea+":"+sc); return out; }
                    sb.append(src.charAt(j)); j++;
                }
                if(j>=n){ errores.add("String sin cerrar en "+linea+":"+sc); return out; }
                out.add(new Token(TK_STRING, sb.toString(), linea, col));
                int consumed = (j - i) + 1; i+=consumed; col+=consumed; continue;
            }

            // id / keyword / package
            if(isStartId(c)){
                int j=i+1; while(j<n && isIdPart(src.charAt(j))) j++;
                String word = src.substring(i, j);
                String lower = word.toLowerCase(Locale.ROOT);

                // paquete como token completo
                if("package".equals(lower)){
                    int k=j; int startCol=col; StringBuilder full=new StringBuilder("package");
                    while(k<n && Character.isWhitespace(src.charAt(k))){
                        if(src.charAt(k)=='\n'){ linea++; col=1; } else col++; k++;
                    }
                    boolean ok=false, expectId=true;
                    while(k<n){
                        char ch=src.charAt(k);
                        if(expectId){
                            if(!isStartId(ch)){ errores.add("Se esperaba identificador de paquete en "+linea+":"+col); break; }
                            int h=k+1; while(h<n && isIdPart(src.charAt(h))) h++;
                            full.append(" ").append(src.substring(k,h));
                            col += (h-k); k=h; expectId=false; continue;
                        }else{
                            if(ch=='.'){ full.append("."); k++; col++; expectId=true; continue; }
                            if(ch==';'){ full.append(";"); k++; col++; ok=true; break; }
                            if(Character.isWhitespace(ch)){ if(ch=='\n'){ linea++; col=1; } else col++; k++; continue; }
                            errores.add("Se esperaba '.' o ';' en paquete, encontrado '"+ch+"' en "+linea+":"+col);
                            break;
                        }
                    }
                    if(ok){ out.add(new Token(TK_PACKAGE, full.toString(), linea, startCol)); i=k; continue; }
                    else return out;
                }

                int tipo = KEYWORDS_LOWER.contains(lower) ? TK_KEYWORD : TK_ID;
                out.add(new Token(tipo, word, linea, col));
                int consumed = (j - i); i+=consumed; col+=consumed; continue;
            }

            // números
            if(Character.isDigit(c)){
                int j=i+1; while(j<n && Character.isDigit(src.charAt(j))) j++;
                String num = src.substring(i,j);
                out.add(new Token(TK_NUM, num, linea, col));
                int consumed = (j - i); i+=consumed; col+=consumed; continue;
            }

            // ===== Operadores =====
            // 1) Triplos (>>>)
            if(i+2<n){
                String three = ""+c+src.charAt(i+1)+src.charAt(i+2);
                if(three.equals(">>>")){
                    out.add(new Token(TK_OP, three, linea, col));
                    i+=3; col+=3; continue;
                }
            }

            // 2) Dobles
            if(i+1<n){
                String two = ""+c+src.charAt(i+1);
                if(two.equals("==") || two.equals("!=") || two.equals("<=") || two.equals(">=") ||
                   two.equals("->") || two.equals("<-") || two.equals("&&") || two.equals("||") ||
                   two.equals("++") || two.equals("--") || two.equals("<<") || two.equals(">>")){
                    out.add(new Token(TK_OP, two, linea, col));
                    i+=2; col+=2; continue;
                }
            }

            // 3) Simples
            if("+-*/=%!&|^~<>".indexOf(c) >= 0){
                out.add(new Token(TK_OP, ""+c, linea, col));
                i++; col++; continue;
            }

            // delimitadores
            if("(){}[],;:.".indexOf(c)>=0){
                out.add(new Token(TK_DELIM, ""+c, linea, col));
                i++; col++; continue;
            }

            // desconocido
            errores.add("Carácter no reconocido '"+c+"' en "+linea+":"+col);
            i++; col++;
        }
        return out;
    }

    private boolean isStartId(char c){ return Character.isLetter(c) || c=='_' || c=='$'; }
    private boolean isIdPart(char c){ return Character.isLetterOrDigit(c) || c=='_' || c=='$'; }

    // ===================== TABLA DE SÍMBOLOS =====================
    private void construirTablaSimbolos(List<Token> toks){
        for(int i=0;i<toks.size();i++){
            Token t = toks.get(i);

            // patrón: <tipo> id (= literal)? (, ...)* ;
            boolean esTipo = (t.tipo==TK_KEYWORD && TIPOS_SOPORTADOS.contains(t.lexema))
                           || (t.tipo==TK_ID      && TIPOS_SOPORTADOS.contains(t.lexema));
            if(!esTipo) continue;

            String tipo = t.lexema;
            int j = i+1;
            j = consumirListaDeclaracionesSoloStatement(toks, j, tipo);
            i = j-1;
        }
    }

   public List<Simbolo> escanear(String entrada) {
    List<Simbolo> simbolos = new ArrayList<>();

    String[] tokens = entrada.split("\\s+|(?=[.,;{}()=+\\-*/<>])|(?<=[.,;{}()=+\\-*/<>])");

    for (String t : tokens) {
        if (t.isBlank()) continue;

        if (t.matches("[0-9]+")) {
            simbolos.add(new Simbolo("num", "entero", t));
        } else if (t.matches("[a-zA-Z][a-zA-Z0-9_]*")) {
            switch (t) {
                case "const": case "var": case "proced":
                case "print": case "input": case "exec":
                case "if": case "while": case "for":
                    simbolos.add(new Simbolo(t, "reservada", t));
                    break;
                default:
                    simbolos.add(new Simbolo("id", "identificador", t));
            }
        } else {
            simbolos.add(new Simbolo(t, "simbolo", t));
        }
    }

    return simbolos;
}

    /**
     * Lee una lista de declaraciones SÓLO si pertenecen a un statement de variables,
     * es decir, nombres separados por ',' y terminados en ';'.
     * Si el siguiente token después del nombre NO es ',' ni ';', NO agrega nada (evita parámetros).
     */
  private int consumirListaDeclaracionesSoloStatement(List<Token> toks, int j, String tipo){
    while(j<toks.size()){
        int nombreIdx = j;
        if(nombreIdx>=toks.size() || toks.get(nombreIdx).tipo!=TK_ID) return j;

        String nombre = toks.get(nombreIdx).lexema;
        j = nombreIdx + 1;

        String valor = null;

        if(j<toks.size() && toks.get(j).tipo==TK_OP && "=".equals(toks.get(j).lexema)){
            j++; // mirar el primer token del lado derecho

            // Si el PRIMER token tras '=' es literal, lo uso como "valor" mostrado
            if(j<toks.size() && (toks.get(j).tipo==TK_NUM || toks.get(j).tipo==TK_STRING ||
                (toks.get(j).tipo==TK_KEYWORD && (
                    "true".equalsIgnoreCase(toks.get(j).lexema) ||
                    "false".equalsIgnoreCase(toks.get(j).lexema) ||
                    "null".equalsIgnoreCase(toks.get(j).lexema)
                )))){
                valor = toks.get(j).lexema;
            }

            // Avanzar por toda la expresión hasta ',' o ';', respetando paréntesis
            int paren = 0;
            while(j<toks.size()){
                Token tk = toks.get(j);
                if(tk.tipo==TK_DELIM){
                    if("(".equals(tk.lexema)) { paren++; }
                    else if(")".equals(tk.lexema) && paren>0) { paren--; }
                    else if(paren==0 && (",".equals(tk.lexema) || ";".equals(tk.lexema))){
                        break; // fin de la expresión para esta variable
                    }
                }
                j++;
            }
        }

        // Validar separador/terminador: sólo aceptamos ',' o ';'
        if(j<toks.size() && ",".equals(toks.get(j).lexema)){
            tablaSimbolos.add(new Simbolo(nombre, tipo, valor));
            j++; // sigue otra variable del mismo statement
            continue;
        }
        if(j<toks.size() && ";".equals(toks.get(j).lexema)){
            tablaSimbolos.add(new Simbolo(nombre, tipo, valor));
            j++; // fin del statement
            break;
        }

        // Si no hay ',' ni ';', NO es un statement de variables (parámetros, etc.)
        return j;
    }
    return j;
}
public List<String> extraerExpresionesAritmeticas() {
    List<String> exprs = new ArrayList<>();

    for (int i = 0; i < tokens.size(); i++) {
        analizadorLexico.Token t = tokens.get(i);

        // patrón:  ID  =  algo...  ;
        if (t.tipo == TK_ID &&
            i + 1 < tokens.size() &&
            tokens.get(i + 1).tipo == TK_OP &&
            "=".equals(tokens.get(i + 1).lexema)) {

            int j = i + 2;
            StringBuilder sb = new StringBuilder(t.lexema + " = ");

            boolean contieneOp = false;

            // leer hasta ;
            while (j < tokens.size() && !";".equals(tokens.get(j).lexema)) {
                String lx = tokens.get(j).lexema;
                sb.append(lx).append(" ");

                if (tokens.get(j).tipo == TK_OP && "+-*/".contains(lx)) {
                    contieneOp = true;
                }
                j++;
            }

            sb.append(";");
            
            // solo agregar si contiene operación aritmética
            if (contieneOp) {
                exprs.add(sb.toString().trim());
            }

            i = j;
        }
    }

    return exprs;
}

   
  
}
