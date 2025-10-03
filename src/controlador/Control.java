package controlador;

import java.util.*;
import javax.swing.*;
import java.util.regex.Pattern;

/**
 * Fachada hacia la UI.
 * - abrirArchivo(...)
 * - obtenerToken(...)  -> lista de tokens
 * - sintactico(...)    -> delimitadores + ';' y sugerencias de palabras reservadas
 * - mostrarTablaDeSimbolos(...)
 *
 * NUEVO:
 * - Detección de palabras reservadas con errores (typos) y botón de autocorrección.
 * AUTOR: Erick Nungaray
 * *
 */
public class Control {

    private final analizadorLexico lexer = new analizadorLexico();

    /* =================== ARCHIVO =================== */
    public void abrirArchivo(JTextArea destino){
        JFileChooser fc = new JFileChooser();
        // Permite .java y .txt
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos fuente", "java", "txt"));
        int r = fc.showOpenDialog(null);
        if(r == JFileChooser.APPROVE_OPTION){
            java.io.File f = fc.getSelectedFile();
            try(java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(f))){
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

    /* =================== LÉXICO =================== */
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
            sb.append(String.format("%03d [%3d %-18s] %s%n", i, code, tipo, t.lexema));
            i++;
        }

        txtSalida.setText(sb.toString());
    }

    /* =================== TABLA DE SÍMBOLOS =================== */
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

    /* =================== SINTÁCTICO =================== */
    /**
     * Corre: léxico -> (si OK) sintáctico (delimitadores + ';') -> detector de typos en reservadas
     * Si hay typos, muestra reporte y ofrece AUTOCORREGIR (botón).
     */
    public void sintactico(JTextArea txtFuente, JTextArea txtSalida){
        // 1) Léxico
        lexer.analizar(txtFuente.getText());
        if(!lexer.getErrores().isEmpty()){
            StringBuilder se = new StringBuilder();
            for(String e: lexer.getErrores()) se.append("[ERROR] ").append(e).append("\n");
            txtSalida.setText(se.toString());
            return;
        }

        // 2) Sintáctico (delimitadores + ';')
        analizadorSintactico syn = new analizadorSintactico();
        List<analizadorSintactico.ErrorSintactico> errores = syn.revisar(lexer.getTokens());

        // 3) Detección de palabras reservadas con posible typo
        List<Sugerencia> sugerencias = detectarTyposReservadas(lexer.getTokens());

        // 4) Mostrar resultado y ofrecer autocorrección si aplica
        StringBuilder sb = new StringBuilder();
        if(errores.isEmpty() && sugerencias.isEmpty()){
            sb.append("OK: delimitadores y ';' balanceados. Sin typos en reservadas.\n");
            txtSalida.setText(sb.toString());
            return;
        }

        for(var e: errores) sb.append(e.toString()).append("\n");
        for(var s: sugerencias){
            sb.append(String.format("[LEXICO] Posible palabra reservada mal escrita '%s' (línea %d). ¿Quisiste decir '%s'?\n",
                    s.actual, s.linea, s.sugerida));
        }
        txtSalida.setText(sb.toString());

        if(!sugerencias.isEmpty()){
            int opt = JOptionPane.showOptionDialog(null,
                    "Se detectaron posibles typos en palabras reservadas.\n¿Deseas autocorregir?",
                    "Autocorrección",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new Object[]{"Autocorregir", "Cancelar"},
                    "Autocorregir");
            if(opt == JOptionPane.YES_OPTION){
                aplicarAutocorreccion(txtFuente, sugerencias);
                // volver a correr léxico y sintáctico tras autocorregir para reflejar el estado actualizado
                sintactico(txtFuente, txtSalida);
            }
        }
    }

    /* =================== DETECTOR DE TYPOS EN RESERVADAS =================== */

    /** Sugerencia de typo: token ID que está a distancia 1–2 de una reservada. */
    private static class Sugerencia {
        final String actual;
        final String sugerida;
        final int linea;
        Sugerencia(String a, String s, int l){ actual=a; sugerida=s; linea=l; }
    }
    
    // TODAS las reservadas (sirve para sugerencias globales)
private static final Set<String> KEYWORDS_ALL = new HashSet<>(Arrays.asList(
    "abstract","assert","boolean","break","byte","case","catch","char","class","const",
    "continue","default","do","double","else","enum","extends","final","finally","float",
    "for","goto","if","implements","import","instanceof","int","interface","long","native",
    "new","package","private","protected","public","return","short","static","strictfp",
    "super","switch","synchronized","this","throw","throws","transient","try","void",
    "volatile","while","true","false","null","string"
));


    /** Lista de reservadas en minúsculas (igual que en el lexer) */
private static final Set<String> KEYWORDS_TYPO = new HashSet<>(Arrays.asList(
       "public","private","protected",
    "class","interface","enum","extends","implements",
    "package","import",
    "return","throw","throws","try","catch","finally",
    "new","if","else","for","while","switch","case","default","do",
    "static","final","abstract","native","synchronized","transient","volatile","strictfp"
    ));

// Tipos que queremos corregir si están mal escritos (solo en contexto de declaración)
private static final Set<String> KEYWORDS_TIPO = new HashSet<>(Arrays.asList(
    "byte","short","int","long","float","double","boolean","char","void","string"
));

// Modificadores que pueden ir antes del tipo
private static final Set<String> MODIFICADORES = new HashSet<>(Arrays.asList(
    "public","private","protected","static","final","abstract","transient",
    "volatile","synchronized","native","strictfp"
));
// ¿El token en i parece estar donde iría un TIPO? (declaración de variable o método)
private boolean esPosibleDeclaracionDeTipo(List<analizadorLexico.Token> toks, int i){
    int n=toks.size(), j=i+1;
    while (j+1<n &&
        toks.get(j).tipo==analizadorLexico.TK_DELIM && "[".equals(toks.get(j).lexema) &&
        toks.get(j+1).tipo==analizadorLexico.TK_DELIM && "]".equals(toks.get(j+1).lexema)) j+=2;
    return j<n && toks.get(j).tipo==analizadorLexico.TK_ID;
}

// ¿El token en i está al "inicio lógico" de sentencia tras sólo modificadores?
private boolean inicioTrasModificadores(List<analizadorLexico.Token> toks, int i){
    int p = i-1;
    while (p>=0 && toks.get(p).tipo==analizadorLexico.TK_KEYWORD &&
           MODIFICADORES.contains(toks.get(p).lexema.toLowerCase(java.util.Locale.ROOT))) p--;
    if (p<0) return true;
    if (toks.get(p).tipo==analizadorLexico.TK_DELIM){
        String lx=toks.get(p).lexema;
        return ";".equals(lx)||"{".equals(lx)||"}".equals(lx);
    }
    return false;
}

private List<Sugerencia> detectarTyposReservadas(List<analizadorLexico.Token> toks){
    List<Sugerencia> res = new ArrayList<>();

    for (int i=0; i<toks.size(); i++){
        var t = toks.get(i);
        if (t.tipo != analizadorLexico.TK_ID) continue;

        String lex = t.lexema;
        String lower = lex.toLowerCase(java.util.Locale.ROOT);
        if (lex.length()<2) continue;

        // si viene justo después de un tipo -> es nombre de variable; no sugerir
        if (i>0){
            var prev = toks.get(i-1);
            if (prev.tipo==analizadorLexico.TK_KEYWORD &&
                KEYWORDS_TIPO.contains(prev.lexema.toLowerCase(java.util.Locale.ROOT))) continue;
        }

        // normalizar repeticiones
        String norm = normalizeForTypos(lower);

        boolean reportado = false;

        // A) Estructurales (if, class, public, return, try, ...)
        String bestS=null; int dS=Integer.MAX_VALUE;
        for (String kw: KEYWORDS_TYPO){
            int d = levenshtein(norm, kw, 2);
            if (d>=0 && d<dS){ dS=d; bestS=kw; if (dS==1) break; }
        }
        if (bestS!=null && dS>=1 && dS<=2){
            boolean ok = (dS==1) || esInicioDeSentencia(toks,i) || inicioTrasModificadores(toks,i);
            // caso 'id' -> 'if' si viene '('
            if (!ok && lower.equals("id") && bestS.equals("if") && i+1<toks.size()
                && toks.get(i+1).tipo==analizadorLexico.TK_DELIM && "(".equals(toks.get(i+1).lexema)) ok=true;
            if (ok){
                res.add(new Sugerencia(lex, ajustarCasoSugerencia(lex, bestS), t.linea));
                reportado=true;
            }
        }

        if (reportado) continue;

        // B) Tipos (int, double, boolean, char, void, String) — sólo en contexto tipo
        boolean contextoTipo = esPosibleDeclaracionDeTipo(toks,i) || inicioTrasModificadores(toks,i);
        if (!contextoTipo) continue;

        String bestT=null; int dT=Integer.MAX_VALUE;
        for (String kw: KEYWORDS_TIPO){
            int d = levenshtein(norm, kw, 2);
            if (d>=0 && d<dT){ dT=d; bestT=kw; if (dT==1) break; }
        }
        if (bestT!=null && dT>=1 && dT<=2){
            res.add(new Sugerencia(lex, ajustarCasoSugerencia(lex, bestT), t.linea));
        }
    }
    return res;
}



private boolean esNombreDeTipo(String lower){
    return KEYWORDS_TIPO.contains(lower);
}


    /** Levenshtein con límite: si distancia > limit, devuelve -1. */
    private static int levenshtein(String a, String b, int limit){
        int n=a.length(), m=b.length();
        if(Math.abs(n-m) > limit) return -1;
        int[] prev = new int[m+1], cur = new int[m+1];
        for(int j=0;j<=m;j++) prev[j]=j;
        for(int i=1;i<=n;i++){
            cur[0]=i;
            int minRow = cur[0];
            for(int j=1;j<=m;j++){
                int cost = (a.charAt(i-1)==b.charAt(j-1))?0:1;
                cur[j] = Math.min(Math.min(cur[j-1]+1, prev[j]+1), prev[j-1]+cost);
                if(cur[j] < minRow) minRow = cur[j];
            }
            if(minRow > limit) return -1;
            int[] tmp=prev; prev=cur; cur=tmp;
        }
        return prev[m] <= limit ? prev[m] : -1;
    }
// ¿Está el token en posición de inicio de sentencia?
private boolean esInicioDeSentencia(List<analizadorLexico.Token> toks, int i){
    if (i==0) return true;
    for (int p=i-1; p>=0; p--){
        var t = toks.get(p);
        if (t.tipo==analizadorLexico.TK_DELIM){
            String lx=t.lexema;
            if (";".equals(lx)||"{".equals(lx)||"}".equals(lx)) return true;
            if (")".equals(lx)) return false;
        }else if (t.tipo==analizadorLexico.TK_KEYWORD){
            String kw=t.lexema.toLowerCase(java.util.Locale.ROOT);
            if (kw.equals("else")||kw.equals("catch")||kw.equals("finally")) return true;
            return false;
        }else{
            return false;
        }
    }
    return true;
}
/** Normaliza repeticiones: "classssrsrrr" -> "clasrsr" para robustecer el matching de typos. */
private static String normalizeForTypos(String s){
    StringBuilder out = new StringBuilder(s.length());
    char prev = 0;
    for(char ch : s.toCharArray()){
        if (ch == prev) continue; // colapsa repeticiones
        out.append(ch);
        prev = ch;
    }
    return out.toString();
}

// ¿Cuadra la keyword sugerida con el contexto "de lo que sigue"?
private boolean contextoCompatible(List<analizadorLexico.Token> toks, int i, String kw){
    int n = toks.size();
    // busca siguiente token no-trivial
    int j = i + 1;
    if (j >= n) return true;
    analizadorLexico.Token next = toks.get(j);

    switch (kw){
        // Palabras que típicamente van seguidas de '('
        case "if": case "for": case "while": case "switch": case "synchronized":
            return next.tipo == analizadorLexico.TK_DELIM && "(".equals(next.lexema);

        // Declaraciones de tipo/clase/interface/enum: suele venir un identificador
        case "class": case "interface": case "enum":
            return next.tipo == analizadorLexico.TK_ID;

        // package/import: típicamente viene un identificador de ruta (id o id . id ... ) y termina en ';'
        case "package": case "import":
            return next.tipo == analizadorLexico.TK_ID;

        // try/catch/finally: validar estructura mínima
        case "try":
            return next.tipo == analizadorLexico.TK_DELIM && "{".equals(next.lexema);
        case "catch":
            return next.tipo == analizadorLexico.TK_DELIM && "(".equals(next.lexema);
        case "finally":
            return next.tipo == analizadorLexico.TK_DELIM && "{".equals(next.lexema);

        // return/throw/throws/new/static/final/abstract/native/transient/volatile/strictfp:
        // Son permisivas; no podemos validar mucho sin parser completo.
        default:
            return true;
    }
}

    /** Mantiene el caso original si parece “TitleCase” vs todo minúsculas. */
    private static String ajustarCasoSugerencia(String original, String sugeridaLower){
        // Si original empieza con mayúscula y resto minúsculas -> TitleCase (ej. "Public")
        if(original.length()>=1 && Character.isUpperCase(original.charAt(0))){
            return sugeridaLower.substring(0,1).toUpperCase(Locale.ROOT) +
                   (sugeridaLower.length()>1 ? sugeridaLower.substring(1) : "");
        }
        return sugeridaLower;
    }

    /** Aplica autocorrección en el texto fuente, reemplazando palabra por palabra con límites. */
    private void aplicarAutocorreccion(JTextArea txtFuente, List<Sugerencia> sugs){
        String src = txtFuente.getText();
        // Para evitar reemplazar múltiples veces la misma palabra, consolidamos por lexema actual -> sugerida
        Map<String,String> mapa = new LinkedHashMap<>();
        for(Sugerencia s: sugs){
            // si hay varias sugerencias para el mismo “actual”, priorizamos la de menor longitud sugerida
            mapa.merge(s.actual, s.sugerida, (oldV, newV) -> newV.length()<oldV.length()? newV : oldV);
        }
        for(var e: mapa.entrySet()){
            String actual = Pattern.quote(e.getKey());
            String sugerida = e.getValue();
            // Reemplazo con límites de palabra: \bActual\b  (sensible a mayúsculas exactas del token visto)
            src = src.replaceAll("\\b" + actual + "\\b", sugerida);
        }
        txtFuente.setText(src);
    }
}
