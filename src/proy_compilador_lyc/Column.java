package proy_compilador_lyc;

/**
 *
 * @author JeanSL
 */
public class Column {
    public static enum FormatoTexto {Lower, Upper, Normal}
    public static enum Tipo {Len, Left, Right, Max, Min, Avg, Sum, Count, Void}
    
    
    private String nombre;
    private Tipo tipo;
    private FormatoTexto formato;
    private int cantidad;

    public Column(String nombre) {
        this.nombre = nombre;
        this.tipo = Tipo.Void;
        this.formato = FormatoTexto.Normal;
        this.cantidad = 0;
    }
    
    public Column(String nombre, Tipo tipo, FormatoTexto formato) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.formato = formato;
        this.cantidad = 0;
    }
    
    public Column(String nombre, Tipo tipo, FormatoTexto formato, int cantidad) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.formato = formato;
        this.cantidad = cantidad;
    }

    
    public String getNombre() {
        return nombre;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public FormatoTexto getFormato() {
        return formato;
    }

    public int getCantidad() {
        return cantidad;
    }
    
    
}
