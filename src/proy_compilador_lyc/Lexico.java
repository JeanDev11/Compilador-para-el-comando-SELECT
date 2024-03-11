package proy_compilador_lyc;

import java.util.ArrayList;

public class Lexico {
    // Variables
    enum TokenTipo {PALABRA_CLAVE, ID, NUMEROS, CADENA, OP_ARITMETICO, OP_LOGICO, ES_ALL, DELIMITADOR, OP_COMPARACION, NULO}
    private static final My_String STR = new My_String();

    // Función para dividir una cadena en tokens
    public ArrayList<Token> tokenizar(String input) {
        ArrayList<String> tokens = dividirSentencia(input);
        ArrayList<Token> listaTokens = new ArrayList<>();

        for (String tk : tokens) {
            String tokenAux = STR.toLower(tk);
            TokenTipo tipo = obtenerTipoToken(tokenAux);
            listaTokens.add(new Token(tk, tipo));
        }
        return listaTokens;
    }

    // Función para determinar el tipo de token
    private TokenTipo obtenerTipoToken(String tokenAux) {
        if (esPalabraClave(tokenAux)) {
            return TokenTipo.PALABRA_CLAVE;
        } else if (esNumero(tokenAux)) {
            return TokenTipo.NUMEROS;
        } else if (esCadena(tokenAux)) {
            return TokenTipo.CADENA;
        } else if (esOperadorAritmetico(tokenAux.charAt(0))) {
            return TokenTipo.OP_ARITMETICO;
        } else if (esOperadorLogico(tokenAux)) {
            return TokenTipo.OP_LOGICO;
        } else if (esDelimitador(tokenAux.charAt(0))) {
            return TokenTipo.DELIMITADOR;
        } else if( esAll(tokenAux.charAt(0))){
            return TokenTipo.ES_ALL;
        } else if( esOperadorComparacion(tokenAux)){
            return TokenTipo.OP_COMPARACION;
        } else if( esIdentificador(tokenAux)){
            return TokenTipo.ID;
        } else {
            return TokenTipo.NULO;
        }
    }

    // Función para reconocer un identificador.
    public static boolean esIdentificador(String cadena) {
        int estado = 0;
        for (int i = 0; i < cadena.length(); i++) {
            char simbolo = cadena.charAt(i);
            switch (estado) {
                case 0:
                    if ((simbolo >= 'a' && simbolo <= 'z') || simbolo == '_')
                        estado = 1;
                    else
                        return false;
                    break;
                case 1:
                    if ((simbolo >= 'a' && simbolo <= 'z') || (simbolo >= '0' && simbolo <= '9') || simbolo == '_')
                        estado = 1;
                    else
                        return false;
                    break;
            }
        }
        return estado == 1;
    }
    
    // Función para reconocer un número.
    private boolean esNumero(String cadena) {
        try {
            Double.valueOf(cadena);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    // Función para reconocer una cadena "".
    private boolean esCadena(String cadena){
        return STR.length(cadena) > 2 && cadena.charAt(0) == '\'' && cadena.charAt(cadena.length()-1) == '\'';
    }

    // Función para reconocer simbolo '*' (all).
    private boolean esAll(char all) {
        return all == '*';
    }
    
    // Función para reconocer operadores aritméticos.
    private boolean esOperadorAritmetico(char operador) {
        return operador == '+' || operador == '-' || operador == '.' || operador == '/' || operador == '^';
    }

    // Función para reconocer operadores lógicos.
    private boolean esOperadorLogico(String cadena) {
        return cadena.equals("not") || cadena.equals("and") || cadena.equals("or");
    }

    // Función para reconocer delimitadores.
    private boolean esDelimitador(char caracter) {
        return caracter == '%' || caracter == '(' || caracter == ')' || caracter == ',' || Character.isWhitespace(caracter);
    }
    
    // Función para reconocer operadores de comparacion.
    private boolean esOperadorComparacion(String cadena) {
        return cadena.equals(">") || cadena.equals(">=") || cadena.equals("=") || cadena.equals("<=") || cadena.equals("<");
    }

    // Función para dividir una cadena en tokens.
    private ArrayList<String> dividirSentencia(String sentencia) {
        ArrayList<String> tokens = new ArrayList<>();
        StringBuilder token = new StringBuilder();
        boolean dentroCadena = false;
        
        for (int i = 0; i < sentencia.length(); i++) {
            char c = sentencia.charAt(i);
            //System.out.println("char: "+c);
            if (c == '\'') {
                dentroCadena = !dentroCadena;
            }
            if ((esDelimitador(c) || esOperadorAritmetico(c)) && !dentroCadena) {
                if (token.length() >= 1) {
                    tokens.add(token.toString());
                    token.setLength(0);
                }
                if (c == ' ') {
                    continue;
                } else {
                    tokens.add(String.valueOf(c));
                }
            } else {
                token.append(c);
            }
        }
        if (token.length() > 0) {
            tokens.add(token.toString());
        }
        return complementos(tokens);
    }

    private ArrayList<String> complementos(ArrayList<String> lista){
        for (int i = lista.size() - 2; i >= 0; i--) {
            if(STR.sonIguales(lista.get(i), "order") && STR.sonIguales(lista.get(i+1), "by")){
                lista.set(i, "order by");
                lista.remove(i+1);
            }
        }
        return lista;
    }

    // Función para determinar si es una palabra clave.
    private boolean esPalabraClave(String tokenAux) {
        String[] palabrasClave = {"select", "from", "where", "like", "order by", "asc", "desc", "upper", "lower", "len", "left", "right", "max", "min", "avg", "sum","count"};
        for (String palabra : palabrasClave) {
            if (palabra.equals(tokenAux)) {
                return true;
            }
        }
        return false;
    }
    
    // Función para reconocer una cadena L-, R-, N-.
    public boolean esCompuestoCadena(String cadena){
        return STR.length(cadena) > 2 && (STR.sonIguales(STR.subCadena(cadena, 0, 2), "L-") || STR.sonIguales(STR.subCadena(cadena, 0, 2), "R-") || STR.sonIguales(STR.subCadena(cadena, 0, 2), "N-"));
    }
    
    public static boolean esFormatoTexto(String tokenAux){
        String[] palabrasClave = {"upper", "lower"};
        for (String palabra : palabrasClave) {
            if (STR.sonIguales(palabra, tokenAux)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean esLen(String tokenAux){
        String[] palabrasClave = {"len"};
        for (String palabra : palabrasClave) {
            if (STR.sonIguales(palabra, tokenAux)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean esSelecionTexto(String tokenAux){
        String[] palabrasClave = {"left", "right"};
        for (String palabra : palabrasClave) {
            if (STR.sonIguales(palabra, tokenAux)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean esFunAgregacion(String tokenAux){
        String[] palabrasClave = {"max", "min", "avg", "sum", "count"};
        for (String palabra : palabrasClave) {
            if (STR.sonIguales(palabra, tokenAux)) {
                return true;
            }
        }
        return false;
    }
}
