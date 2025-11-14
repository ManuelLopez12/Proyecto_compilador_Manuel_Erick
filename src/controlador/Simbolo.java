/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

/**
 *
 * @author ericknungaray
 */
public class Simbolo {
 
    public final String nombre;
    public final String tipo;
    public final String valor;

    public Simbolo(String nombre, String tipo, String valor) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.valor = valor;
    }
    
    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }
    public String getValor() { return valor; }

    @Override
    public String toString() {
        return "[" + nombre + " : " + tipo + " = " + valor + "]";
    }
}
