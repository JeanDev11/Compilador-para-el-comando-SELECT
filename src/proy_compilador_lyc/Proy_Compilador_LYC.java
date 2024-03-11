package proy_compilador_lyc;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author JeanSL
 */
public class Proy_Compilador_LYC {

    private static final Biblioteca BL = new Biblioteca();
    private static final My_String STR = new My_String();

    static boolean selectOk(List<Token> tokens) {
        int indexFrom = BL.indexOfList(tokens, "from");

        // Verificar que el primer token sea "select"
        if (!tokens.get(0).getLexema().equalsIgnoreCase("select")) {
            System.out.println("Error. No se reconoce el comando \"SELECT\"");
            return false;
        }

        // Verificar si el segundo token es "*" o nombres de columnas.
        // Verificar si el segundo token es Upper / Lower, Len, Max / Min, .
        // Verificar si el segundo token es Numero / Cadena (simple).
        // Verificar si el segundo token es Left / Right.
        if (tokens.get(1).getTipo() == Lexico.TokenTipo.ID || STR.sonIguales(tokens.get(1).getLexema(), "*")) {
            return seccionColumnasOK(tokens, indexFrom, 2);
        } else if (Lexico.esFormatoTexto(tokens.get(1).getLexema()) && upperOrLowerCaseOK(tokens, 1) ||
                (Lexico.esLen(tokens.get(1).getLexema()) && lenOK(tokens, 1)) ||
                (Lexico.esFunAgregacion(tokens.get(1).getLexema()) && agregacionOk(tokens, 1))){
            return seccionColumnasOK(tokens, indexFrom, 5);
        } else if (tokens.get(1).getTipo() == Lexico.TokenTipo.NUMEROS || tokens.get(1).getTipo() == Lexico.TokenTipo.CADENA) {
            if (indexFrom > 2) {
                return seccionColumnasOK(tokens, indexFrom, 2);
            } else {
                System.out.println("Error. Se necesita al menos una columna válida.");
                return false;
            }
        } else if (Lexico.esSelecionTexto(tokens.get(1).getLexema()) && lefOrRightOK(tokens, 1)) {
            return seccionColumnasOK(tokens, indexFrom, 7);
        }

        return false;
    }
    
    static boolean seccionColumnasOK(List<Token> tokens, int indexFrom, int inicio) {
        for (int i = inicio; i < indexFrom; i += 2) {
            if (tokens.get(i).getLexema().equals(",")) {
                if (STR.sonIguales(tokens.get(i + 1).getLexema(), "from")) {
                    System.out.println("Error. Se detectó una [,] inválida.");
                    return false;
                } else if ((Lexico.esFormatoTexto(tokens.get(i + 1).getLexema()) && upperOrLowerCaseOK(tokens, i + 1)) ||
                        (Lexico.esLen(tokens.get(i + 1).getLexema()) && lenOK(tokens, i + 1)) ||
                        (Lexico.esFunAgregacion(tokens.get(i+1).getLexema()) && agregacionOk(tokens, i+1))) {
                    i = i + 3;
                } else if (Lexico.esSelecionTexto(tokens.get(i + 1).getLexema()) && lefOrRightOK(tokens, i + 1)) {
                    i = i + 5;
                } else if (tokens.get(i + 1).getTipo() != Lexico.TokenTipo.ID &&
                        tokens.get(i + 1).getTipo() != Lexico.TokenTipo.NUMEROS &&
                        tokens.get(i + 1).getTipo() != Lexico.TokenTipo.CADENA &&
                        tokens.get(i + 1).getTipo() != Lexico.TokenTipo.ES_ALL) {
                    System.out.println("Error. Sección de columnas inválidas.");
                    return false;
                }
            } else {
                System.out.println("Error. Sección de columnas inválidas.");
                return false;
            }
        }
        return true;
    }
    
    private static boolean upperOrLowerCaseOK(List<Token> tokens, int index){
        if(STR.sonIguales(tokens.get(index+1).getLexema(), "(")){
            if(tokens.get(index+2).getTipo() == Lexico.TokenTipo.ID){
                if(STR.sonIguales(tokens.get(index+3).getLexema(), ")")){
                    return true;
                }else{
                    System.out.println("Error: Lower / Upper. Falta el delimitador de cierre [)].");
                }
            }else{
                if(STR.sonIguales(tokens.get(index+2).getLexema(), ")"))
                    System.out.println("Error: Lower / Upper. Se requiere un identificador entre los parentesis.");
                else
                    System.out.println("Error: Lower / Upper. Identifcador [" + tokens.get(index+2).getLexema() + "] invalido");
            }
        }else{
            System.out.println("Error: Lower / Upper. Falta el delimitador de apertura [(].");
        }
        
        return false;
    }
    
    private static boolean lenOK(List<Token> tokens, int index){
        if(STR.sonIguales(tokens.get(index+1).getLexema(), "(")){
            if(tokens.get(index+2).getTipo() == Lexico.TokenTipo.ID){
                if(STR.sonIguales(tokens.get(index+3).getLexema(), ")")){
                    return true;
                }else{
                    System.out.println("Error: LEN. Falta el delimitador de cierre [)].");
                }
            }else{
                if(STR.sonIguales(tokens.get(index+2).getLexema(), ")"))
                    System.out.println("Error: LEN. Se requiere un identificador entre los parentesis.");
                else
                    System.out.println("Error: LEN. Identifcador [" + tokens.get(index+2).getLexema() + "] invalido");
            }
        }else{
            System.out.println("Error: LEN. Falta el delimitador de apertura [(].");
        }
        
        return false;
    }
    
    private static boolean agregacionOk(List<Token> tokens, int index){
        if(STR.sonIguales(tokens.get(index+1).getLexema(), "(")){
            if(tokens.get(index+2).getTipo() == Lexico.TokenTipo.ID){
                if(STR.sonIguales(tokens.get(index+3).getLexema(), ")")){
                    return true;
                }else{
                    System.out.println("Error: MAX / MIN / AVG / SUM / COUNT. Falta el delimitador de cierre [)].");
                }
            }else{
                if(STR.sonIguales(tokens.get(index+2).getLexema(), ")"))
                    System.out.println("Error: MAX / MIN / AVG / SUM / COUNT. Se requiere un identificador entre los parentesis.");
                else
                    System.out.println("Error: MAX / MIN / AVG / SUM / COUNT. Identifcador [" + tokens.get(index+2).getLexema() + "] invalido");
            }
        }else{
            System.out.println("Error: MAX / MIN / AVG / SUM / COUNT. Falta el delimitador de apertura [(].");
        }
        
        return false;
    }
    
    private static boolean lefOrRightOK(List<Token> tokens, int index){
        if(STR.sonIguales(tokens.get(index+1).getLexema(), "(")){
            if(tokens.get(index+2).getTipo() == Lexico.TokenTipo.ID){
                if(STR.sonIguales(tokens.get(index+3).getLexema(), ",")){
                    if(BL.esNumero(tokens.get(index+4).getLexema())){
                        if(STR.sonIguales(tokens.get(index+5).getLexema(), ")")){
                            return true;
                        }else{
                            System.out.println("Error: LEFT / RIGHT. Falta el delimitador de cierre [)].");
                        }
                    }else{
                        System.out.println("Error: LEFT / RIGHT. Indique una cantidad entera de extracción.");
                    }
                }else{
                    System.out.println("Error: LEFT / RIGHT. Falta el delimitador coma [,].");
                }
            }else{
                if(STR.sonIguales(tokens.get(index+2).getLexema(), ")"))
                    System.out.println("Error: LEFT / RIGHT. Se requiere un identificador entre los parentesis.");
                else
                    System.out.println("Error: LEFT / RIGHT. Identifcador [" + tokens.get(index+2).getLexema() + "] invalido");
            }
        }else{
            System.out.println("Error: LEFT / RIGHT. Falta el delimitador de apertura [(].");
        }
        
        return false;
    }
    
    static boolean fromOk(List<Token> tokens){
        int indexFrom = BL.indexOfList(tokens, "from");
        
        if (indexFrom == -1){
            System.out.println("Error. Sentencia incompleta. Declare \"FROM\"");
            return false;
        }
        
        if (indexFrom + 1 >= tokens.size()){
            System.out.println("Error. Sentencia incompleta. Declare nombre de la tabla.");
            return false;
        }
        
        // Verificar si el siguiente token es ID
        if (tokens.get(indexFrom+1).getTipo() != Lexico.TokenTipo.ID){
            System.out.println("Error. Nombre de la tabla invalida.");
            return false;
        }
        
        if (tokens.size() > indexFrom + 2){
            if(!STR.sonIguales(tokens.get(indexFrom+2).getLexema(), "where") && !STR.sonIguales(tokens.get(indexFrom+2).getLexema(), "order by")){
                System.out.println("Error. Verifique la sentencia.");
                return false;
            }
        }
        return true;
    }
    
    static boolean whereOk(List<Token> tokens, String tabla){
        int indexWhere = BL.indexOfList(tokens, "where");
        int indexLike = BL.indexOfList(tokens, "like");

        if (indexWhere + 3 >= tokens.size()){
            System.out.println("Error. Declaracion \"WHERE\" incompleta.");
            return false;
        }
        
        String columna = tokens.get(indexWhere+1).getLexema();
        
        // Verificar si el siguiente token es ID
        if (tokens.get(indexWhere+1).getTipo() != Lexico.TokenTipo.ID){
            System.out.println("Error: WHERE. La columna [" + columna + "] es invalida.");
            //System.out.println("Error. Declaracion \"WHERE\" incompleta.");
            return false;
        }else if (!BL.existeColumna(tabla, columna)){
            System.out.println("Error: WHERE. La columna [" + columna + "] no existe.");
            return false;
        }
        
        if (indexLike == -1){
            // Verificar si el siguiente token es OP_COMP
            if (tokens.get(indexWhere+2).getTipo() != Lexico.TokenTipo.OP_COMPARACION){
                System.out.println("Error: WHERE. Declare el operador de comparación.");
                return false;
            }

            // Verificar si el siguiente token es Numerico o Cadena.
            if (tokens.get(indexWhere+3).getTipo() != Lexico.TokenTipo.CADENA && tokens.get(indexWhere+3).getTipo() != Lexico.TokenTipo.NUMEROS){
                System.out.println("Error: WHERE. Se requiere una comparación numérica o una cadena entre comillas simples (123 / 'ID').");
                return false;
            }
            if (tokens.size() > indexWhere + 4){
                if(!STR.sonIguales(tokens.get(indexWhere+4).getLexema(), "order by")){
                    System.out.println("Error. Verifique la sentencia.");
                    return false;
                }
            }
        }else{
            if (indexLike + 1 >= tokens.size()){
                System.out.println("Error. Declaracion \"LIKE\" incompleta.");
                return false;
            }
            
            String lexema = tokens.get(indexLike + 1).getLexema();
            if (tokens.get(indexLike + 1).getTipo() == Lexico.TokenTipo.CADENA) {
                int tam = STR.length(lexema);
                String cadenaSola = STR.subCadena(lexema, 1, tam - 2);
                String comodinIz = BL.izquierda(cadenaSola, 1);
                String comodinDe = BL.derecha(cadenaSola, 1);
                if (!STR.sonIguales(comodinDe, "%") && !STR.sonIguales(comodinIz, "%")) {
                    System.out.println("Error: LIKE. Declare el comodin [%] de secuencia (%ID / ID%).");
                    return false;
                }
            }
            if (tokens.size() > indexLike + 2){
                if(!STR.sonIguales(tokens.get(indexLike+2).getLexema(), "order by")){
                    System.out.println("Error. Verifique la sentencia.");
                    return false;
                }
            }
        }
        return true;
    }
    
    static boolean orderByOk(List<Token> tokens, String tabla){
        int indexOrderBy = BL.indexOfList(tokens, "order by");
        int indexFrom = BL.indexOfList(tokens, "from");

        if (indexOrderBy + 2 >= tokens.size()){
            System.out.println("Error. Declaracion \"ORDER BY\" incompleta.");
            return false;
        }

        String columna = tokens.get(indexOrderBy+1).getLexema();
        
        // Verificar si el siguiente token es ID
        if (tokens.get(indexOrderBy+1).getTipo() != Lexico.TokenTipo.ID){
            System.out.println("Error: ORDER BY. La columna [" + columna + "] es invalida.");
            return false;
        }
        
        // Verificar si el siguiente token es tipo Ordenamiento
        if (tokens.get(indexOrderBy+2).getTipo() != Lexico.TokenTipo.PALABRA_CLAVE){
            System.out.println("Error: ORDER BY. Declare tipo de ordenamiento (ASC / DESC).");
            return false;
        }

        List<Column> listColumnas = obtenerColumnas(tokens, tabla, indexFrom);
        for (Column c : listColumnas) {
            if(STR.sonIguales(c.getNombre(), columna))
                return true;
        }

        System.out.println("Error: ORDER BY. La columna [" + columna + "] no coincide con las columnas de la consulta.");
        return false;
    }
    
    static boolean tablaOk(List<Token> tokens, String tabla){
        List<String> tablas = BL.getTablas();
        
        for (String t : tablas) {
            if(STR.sonIguales(tabla, t))
                return true;
        }
        System.out.println("Error. La tabla no existe.");
        return false;
    }
    
    // Obtener las columnas y validar.
    static boolean columasOk(List<Token> tokens, String tabla){
        boolean flag = true;
        int indexFrom = BL.indexOfList(tokens, "from");
        
        for (int i = 1; i < indexFrom; i += 2) {
            if (Lexico.esFormatoTexto(tokens.get(i).getLexema())|| 
                    Lexico.esLen(tokens.get(i).getLexema()) ||
                    Lexico.esFunAgregacion(tokens.get(i).getLexema())){
                if(!BL.existeColumna(tabla, tokens.get(i+2).getLexema())){
                    System.out.println("Error. La columna [" + tokens.get(i+2).getLexema() + "] no existe.");
                    flag = false;
                }
                if(STR.sonIguales(tokens.get(i).getLexema(), "avg") ||STR.sonIguales(tokens.get(i).getLexema(), "sum")){
                    if(!STR.sonIguales(BL.getColumnaInfo(tabla, tokens.get(i+2).getLexema(), 2), "C")){
                        System.out.println("Error. La columna [" + tokens.get(i+2).getLexema() + "] debe ser de tipo numérico (C).");
                        flag = false;
                    }
                }
                i = i + 3;
            } else if(Lexico.esSelecionTexto(tokens.get(i).getLexema())){
                if(!BL.existeColumna(tabla, tokens.get(i+2).getLexema())){
                    System.out.println("Error. La columna [" + tokens.get(i+2).getLexema() + "] no existe.");
                    flag = false;
                }
                i = i + 5;
            }else if (tokens.get(i).getTipo() == Lexico.TokenTipo.ID || 
                    STR.sonIguales(tokens.get(i).getLexema(), "*") || 
                    tokens.get(i).getTipo() == Lexico.TokenTipo.NUMEROS || 
                    tokens.get(i).getTipo() == Lexico.TokenTipo.CADENA) {
                if(tokens.get(i).getTipo() == Lexico.TokenTipo.ID  && !BL.existeColumna(tabla, tokens.get(i).getLexema())){
                    System.out.println("Error. La columna [" + tokens.get(i).getLexema() + "] no existe.");
                    flag = false;
                }
            }else{
                System.out.println("Error. La columna [" + tokens.get(i).getLexema() + "] es invalida.");
                flag = false;
            }
        }
        return flag;
    }
    
    
    // Función para verificar si los tokens corresponden a un comando SELECT válido
    static boolean verificaSentencia(List<Token> tokens) {
        int indexWhere = BL.indexOfList(tokens, "where");
        int indexOrderBy = BL.indexOfList(tokens, "order by");
        int indexFrom = BL.indexOfList(tokens, "from");
        
        // Debe haber al menos 4 tokens para un SELECT válido: "select", "*" o nombres de columnas o cadena o numeros, "from", nombre de tabla.
        if (tokens.size() < 4) {
            System.out.println("Error. Verifique la sentencia.");
            return false;
        }
        if (!selectOk(tokens))
            return false;
        
        if (!fromOk(tokens))
            return false;
        
        // Si la sentencia "From" es correcta se obtiene la tabla de la consulta SQL.
        String tabla = tokens.get(indexFrom+1).getLexema();
        
        if (indexWhere != -1){
            if (!whereOk(tokens, tabla))
                return false;
        }
        
        if (indexOrderBy != -1){
            if (!orderByOk(tokens, tabla))
                return false;
        }
        
        if (!tablaOk(tokens, tabla) || !columasOk(tokens, tabla))
            return false;
        
        // La estructura básica de un comando SELECT ha sido verificada.
        return true;
    }
    
    //----------------------------------------
    
    public static void salida(List<Token> tokens){
        int indexFrom = BL.indexOfList(tokens, "from");
        int indexWhere = BL.indexOfList(tokens, "where");
        int indexOrderBy = BL.indexOfList(tokens, "order by");
        String tabla = tokens.get(indexFrom+1).getLexema();
        List<List<String>> listaOutP;
        List<Column> columnas = obtenerColumnas(tokens, tabla, indexFrom);

        listaOutP = BL.getDatosColumnas(tabla, columnas);

        if(indexWhere != -1){
            String columnaWhere = tokens.get(indexWhere+1).getLexema();
            if(STR.sonIguales(tokens.get(indexWhere+2).getLexema(), "like")){
                int tam = STR.length(tokens.get(indexWhere+3).getLexema());
                String cadenaSola = STR.subCadena(tokens.get(indexWhere+3).getLexema(), 1, tam-2);
                String comodinIz = BL.izquierda(cadenaSola, 1);
                if(STR.sonIguales(comodinIz, "%")){
                    String condicionLike = STR.subCadena(cadenaSola, 1, STR.length(cadenaSola));
                    listaOutP = BL.getLike(listaOutP, tabla, columnaWhere, condicionLike, 1);
                }else{
                    String condicionLike = STR.subCadena(cadenaSola, 0, STR.length(cadenaSola)-1);
                    listaOutP = BL.getLike(listaOutP, tabla, columnaWhere, condicionLike, 2);
                }
            }else{
                String opWhere = tokens.get(indexWhere+2).getLexema();
                String condicionWhere = tokens.get(indexWhere+3).getLexema();
                listaOutP = BL.getWhere(listaOutP, tabla, columnaWhere, opWhere, condicionWhere);
            }
        }
        
        if(indexOrderBy != -1){
            String columnaOrder = tokens.get(indexOrderBy + 1).getLexema();
            String tipoOrder = tokens.get(indexOrderBy + 2).getLexema();
            listaOutP = BL.ordenarDatos(listaOutP, columnas, columnaOrder, tipoOrder);
        }
        
        BL.imprimir(listaOutP, obtenerColumnas(tokens, tabla, indexFrom));
    }
    
    public static List<Column> obtenerColumnas(List<Token> tokens, String tabla, int indexFrom) {
        List<Column> columnas = new ArrayList<>();

        for (int i = 1; i < indexFrom; i += 2) {
            if (Lexico.esFormatoTexto(tokens.get(i).getLexema())|| Lexico.esLen(tokens.get(i).getLexema()) ||
                    Lexico.esFunAgregacion(tokens.get(i).getLexema())) {
                Column columna;
                if(STR.sonIguales(tokens.get(i).getLexema(), "lower")){
                    columna = new Column(tokens.get(i + 2).getLexema(), Column.Tipo.Void,Column.FormatoTexto.Lower);
                }else if(STR.sonIguales(tokens.get(i).getLexema(), "upper")){
                    columna = new Column(tokens.get(i + 2).getLexema(), Column.Tipo.Void,Column.FormatoTexto.Upper);
                }else if(STR.sonIguales(tokens.get(i).getLexema(), "max")){
                    columna = new Column("MAX_" + tokens.get(i + 2).getLexema(), Column.Tipo.Max,Column.FormatoTexto.Normal);
                }else if(STR.sonIguales(tokens.get(i).getLexema(), "min")){
                    columna = new Column("MIN_" + tokens.get(i + 2).getLexema(), Column.Tipo.Min,Column.FormatoTexto.Normal);
                }else if(STR.sonIguales(tokens.get(i).getLexema(), "avg")){
                    columna = new Column("AVG_" + tokens.get(i + 2).getLexema(), Column.Tipo.Avg,Column.FormatoTexto.Normal);
                }else if(STR.sonIguales(tokens.get(i).getLexema(), "sum")){
                    columna = new Column("SUM_" + tokens.get(i + 2).getLexema(), Column.Tipo.Sum,Column.FormatoTexto.Normal);
                }else if(STR.sonIguales(tokens.get(i).getLexema(), "count")){
                    columna = new Column("CNT_" + tokens.get(i + 2).getLexema(), Column.Tipo.Count,Column.FormatoTexto.Normal);
                }else{
                    columna = new Column("LEN_" + tokens.get(i + 2).getLexema(), Column.Tipo.Len,Column.FormatoTexto.Normal);
                }
                columnas.add(columna);
                i = i + 3;
            }else if(Lexico.esSelecionTexto(tokens.get(i).getLexema())) {
                Column columna;
                int cantidad = Integer.parseInt(tokens.get(i+4).getLexema());
                if(STR.sonIguales(tokens.get(i).getLexema(), "left")) {
                    columna = new Column("LFT_" + tokens.get(i + 2).getLexema(), Column.Tipo.Left,Column.FormatoTexto.Normal,cantidad);
                }else{
                    columna = new Column("RHT_" + tokens.get(i+2).getLexema(), Column.Tipo.Right, Column.FormatoTexto.Normal, cantidad);
                }
                columnas.add(columna);
                i = i + 5;
            }else if(tokens.get(i).getTipo() == Lexico.TokenTipo.ID) {
                Column columna = new Column(tokens.get(i).getLexema());
                columnas.add(columna);
            }else if(STR.sonIguales(tokens.get(i).getLexema(), "*")) {
                for (String columnaNombre : BL.getColumnas(tabla)) {
                    Column columna = new Column(columnaNombre);
                    columnas.add(columna);
                }
            }else if(tokens.get(i).getTipo() == Lexico.TokenTipo.NUMEROS || tokens.get(i).getTipo() == Lexico.TokenTipo.CADENA) {
                Column columna = new Column(tokens.get(i).getLexema());
                columnas.add(columna);
            }
        }

        return columnas;
    }

    
    //----------------------------------------
    public static void main(String[] args) {
        
        for (int i = 0; i < 10; i++) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Introduce tu sentencia SQL: ");
            String secuencia = scanner.nextLine();

            // Obtener tokens
            List<Token> tokens = new Lexico().tokenizar(secuencia);

            /*System.out.println("Tokens:");
            for (Token token : tokens) {
                System.out.print("Lexema: " + token.getLexema() + "\t-> Tipo: ");
                switch (token.getTipo()) {
                    case PALABRA_CLAVE:
                        System.out.println("Palabra clave");
                        break;
                    case ID:
                        System.out.println("Identificador");
                        break;
                    case NUMEROS:
                        System.out.println("Número");
                        break;
                    case CADENA:
                        System.out.println("Cadena");
                        break;
                    case OP_ARITMETICO:
                        System.out.println("Operador Aritmético");
                        break;
                    case OP_LOGICO:
                        System.out.println("Operador Lógico");
                        break;
                    case ES_ALL:
                        System.out.println("All");
                        break;
                    case DELIMITADOR:
                        System.out.println("Delimitador");
                        break;
                    case NULO:
                        System.out.println("nulo");
                        break;
                }
            }*/
        
            
            // Verificar si la secuencia corresponde a un comando SELECT
            boolean esSelect = verificaSentencia(tokens);
            if (esSelect) {
                System.out.println("La secuencia corresponde a un comando SELECT.");
                salida(tokens);
            } else {
                //System.out.println("La secuencia NO! corresponde a un comando SELECT.");
            }
        }

    }

}
