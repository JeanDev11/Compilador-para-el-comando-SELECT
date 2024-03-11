package proy_compilador_lyc;

public class My_String {
    
    public int length(String str) {
        int length = 0;
        for (char c : str.toCharArray()) {
            length++;
        }
        return length;
    }
    
    public char charAt(String str, int index) {
        if (index < 0 || index >= length(str)) {
            throw new IndexOutOfBoundsException("Índice fuera de los límites de la cadena");
        }
        return str.toCharArray()[index];
    }
    
    public boolean sonIguales(String S1, String S2){
        
        int L1 = length(S1);
        int L2 = length(S2);
        if (L1 != L2)
            return false;
        
        for (int i = 0; i < L1; i++){
            if (charAt(toLower(S1),i) != charAt(toLower(S2),i))
                return false;
        }
        return true;
    }
    
    public String subCadena(String s, int i, int n){
        String T = "";
        int L = length(s);
        if(i >= 0){
            if(i + n <= L)
                T = substring(s,i, i+n);
            else
                T = substring(s,i, L);
        }else{
            // Error
            System.out.println("Error. Ocurrió un problema al extraer la subcadena. \n Verifique si los índices proporcionados están dentro del rango válido.");
        }
        return T;
    }
    
    public String substring(String str, int beginIndex, int endIndex) {
        if (beginIndex < 0 || endIndex > length(str) || beginIndex > endIndex) {
            throw new IndexOutOfBoundsException("Índice fuera de los límites de la cadena");
        }
        StringBuilder sb = new StringBuilder();
        for (int i = beginIndex; i < endIndex; i++) {
            sb.append(charAt(str, i));
        }
        return sb.toString();
    }
    
    public String toUpper(String s){
        String T = "";
        int L = length(s);
        char c;
        for (int i = 0; i <= L-1; i++) {
            c = charAt(s,i);
            if('a'<=c && c<='z'){
                T = T + (char)((int)c-32);
            }else if(c=='ñ'){
                T = T + 'Ñ'; 
            }else{
                T = T + c;
            }
        }
        return T;
    }
    
    public String toLower(String s){
        String T = "";
        int L = length(s);
        char c;
        for (int i = 0; i <= L-1; i++) {
            c = charAt(s,i);
            if('A'<=c && c<='Z'){
                T = T + (char)((int)c+32);
            }else if(c=='Ñ'){
                T = T + 'ñ'; 
            }else{
                T = T + c;
            }
        }
        return T;
    }
    
    public String eliminarEspaciosIzq(String s){
        int index = 0;
        while (index < length(s) && charAt(s, index)==' ') {
            index++;
        }
        return s.substring(index);
    }

    public String eliminarEspaciosDer(String s){
        int index = length(s) - 1;
        while (index >= 0 && charAt(s,index)==' ') {
            index--;
        }
        return substring(s,0, index + 1);
    }
    
    // Función para agregar espacios a la derecha de una cadena hasta que tenga una longitud específica
    public String paddingDerecha(String s, int length) {
        if (length(s) >= length) {
            return s;
        } else {
            int spacesToAdd = length - length(s);
            for (int i = 0; i < spacesToAdd; i++) {
                s += " ";
            }
            return s;
        }
    }
    
    public String parseDate(String date){
        String dateFormato = subCadena(date, 0, 4) + "/" + subCadena(date, 4, 2) + "/" + subCadena(date, 6, 2);
        return dateFormato;
    }
    
    public String formatDecimal(double valor) {
        long parteEntera = (long) valor;
        double parteDecimal = valor - parteEntera;
        long decimales = Math.round(parteDecimal * 100); // Redondea a dos decimales
    
        return parteEntera + "." + String.format("%02d", Math.abs(decimales));
    }
}
