/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

/**
 *
 * @author JM LOPEZ HURTADO
 */
public class tablaSimbolos {
    public String nombre;
    public String tipo;
    public String valor;
       public tablaSimbolos(String nombre, String tipo, String valor) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.valor = valor;
    }
    
    @Override
    public String toString() {
        return "Nombre: " + nombre + ", Tipo: " + tipo + ", Valor: " + valor;
    }
}
