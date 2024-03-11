package proy_compilador_lyc;

/**
 *
 * @author JeanSL
 */
public class Token {
    public static enum TipoFormatoTexto {Lower, Upper, Len, Normal}
    public static enum TipoSeleccionTexto {Left, Right}
    
    private final String lexema;
    private TipoFormatoTexto tipoFT;
    private TipoSeleccionTexto tipoST;
    private final Lexico.TokenTipo tipo;
    private int cantidad;

    public Token(String lexema, Lexico.TokenTipo tipo) {
        this.lexema = lexema;
        this.tipo = tipo;
    }
    
    public Token(String lexema, TipoFormatoTexto tipoFT) {
        this.lexema = lexema;
        this.tipoFT = tipoFT;
        this.tipo = null;
    }

    public Token(String lexema, TipoSeleccionTexto tipoST, int cantidad) {
        this.lexema = lexema;
        this.tipoST = tipoST;
        this.cantidad = cantidad;
        this.tipo = null;
    }
    
    public String getLexema() {
        return lexema;
    }

    public Lexico.TokenTipo getTipo() {
        return tipo;
    }
    
    public TipoFormatoTexto getTipoFT() {
        return tipoFT;
    }
    
    public TipoSeleccionTexto getTipoST() {
        return tipoST;
    }

    public int getCantidad() {
        return cantidad;
    }
    
    
}
