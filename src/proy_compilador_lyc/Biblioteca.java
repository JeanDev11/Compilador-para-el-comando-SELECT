package proy_compilador_lyc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JeanSL
 */
public class Biblioteca {
    private final Global  GLB = new Global();
    private final My_String  STR = new My_String();
    
    public String leerArchivo(String nombre){
        String texto="", REG="";
        int p;
        FileReader fr = null;
        BufferedReader br = null;

        try {
            fr = new FileReader(nombre);
            br = new BufferedReader(fr);

            while((REG = br.readLine()) != null) {
                p = localizar(REG, 0, '#', 1);
                if(p != -1) {
                    REG = REG.substring(0, p);
                }
                if(REG.length() != 0) {
                    texto = texto + REG +'\n';
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(br != null)
                    br.close();
                if(fr != null)
                    fr.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return texto;
    }

    public String getCodigoTabla(String tabla){
        String texto=leerArchivo("CATALOG.DAT");
        int inicioLinea = 0;
        
        while (inicioLinea < texto.length()) {  // Iterar mientras haya líneas restantes
            int finLinea = texto.indexOf('\n', inicioLinea);
            if (finLinea == -1) {
                finLinea = texto.length();
            }
            String linea = STR.subCadena(texto, inicioLinea, finLinea-inicioLinea);
            if(linea.length() != 0) {
                if(STR.sonIguales(STR.subCadena(linea, 6, 10).trim(), tabla))
                    return STR.subCadena(linea,1,2);
            }
            inicioLinea = finLinea + 1;
        }
        return "";
    }
    
    public List<String> getTablaAll(String tabla){
        String texto=leerArchivo("SM.DAT");
        String cod = getCodigoTabla(tabla);
        List<String> filas = new ArrayList<>();
        int inicioLinea = 0;
        
        while (inicioLinea < texto.length()) {  // Iterar mientras haya líneas restantes
            int finLinea = texto.indexOf('\n', inicioLinea);
            if (finLinea == -1) {
                finLinea = texto.length();
            }
            String linea = STR.subCadena(texto, inicioLinea, finLinea-inicioLinea);
            if(linea.length() != 0) {
                if(STR.sonIguales(STR.subCadena(linea, 1, 2).trim(), cod)){
                    linea = derecha(linea, linea.length()-4);
                    filas.add(linea);
                }
            }
            inicioLinea = finLinea + 1;
        }
        return filas;
    }
    
    // Verificar si existe una columna especifica.
    public boolean existeColumna(String tabla, String columna){
        String a = getColumnaInfo(tabla, columna, 1);
        return a != null;
    }
    
    public int longColumnasAnt(String tabla, String columna){
        String texto = leerArchivo("CATALOG.DAT");
        String cod = getCodigoTabla(tabla);
        int inicioLinea = 0;
        int lonT = 0;
        
        while (inicioLinea < texto.length()) {  // Iterar mientras haya líneas restantes
            int finLinea = texto.indexOf('\n', inicioLinea);
            if (finLinea == -1) {
                finLinea = texto.length();
            }
            String linea = STR.subCadena(texto, inicioLinea, finLinea-inicioLinea);
            if(linea.length() != 0) {
                if(STR.sonIguales(STR.subCadena(linea, 1, 2).trim(), cod)){
                    String columnaAct = STR.subCadena(linea, 6, 10).trim();
                    if(!STR.sonIguales(columnaAct, tabla)){
                        if(STR.sonIguales(columnaAct, columna))
                            return lonT;
                        else
                            lonT = lonT + Integer.parseInt(STR.subCadena(linea, 19, 2)) + 1;
                    }
                }
            }
            inicioLinea = finLinea + 1;
        }
        return lonT;
    }
    
    // Obtener todos los registros de una columna especifica.
    public List<String> getRegColumna(String tabla, String columna){
        String cod = getCodigoTabla(tabla);
        List<String> datosColumna = new ArrayList<>();
        
        if(cod!=null && existeColumna(tabla, columna)){
            List<String> lista = getTablaAll(tabla);
            for (String s : lista){
                int preLongitud = longColumnasAnt(tabla, columna);
                int longitud = Integer.parseInt(getColumnaInfo(tabla, columna,3));
                String dato = STR.subCadena(s, preLongitud, longitud);
                datosColumna.add(dato);
            }
        }
        return datosColumna;
    }
    
    // Obtener todos los registros solicitados por la consulta SELECT.
    public List<List<String>> getDatosColumnas(String tabla, List<Column> columnas) {
        List<List<String>> datosColumnas = new ArrayList<>();

        // Obtener todos los datos de la tabla
        List<String> filas = getTablaAll(tabla);
        
        // Calcular el numero de veces de repitencia.
        int nVeces = 1;
        for (Column c : columnas) {
            if(!Lexico.esFunAgregacion(c.getTipo().toString()) && 
                    !esCadena(c.getNombre()) && !esNumero(c.getNombre())){
                nVeces = filas.size();
                break;
            }
        }
            
        // Iterar sobre lista de columnas.
        for (Column columna : columnas) {
            List<String> aux = new ArrayList<>();
            String dato;
            if(esCadena(columna.getNombre())){
                int tam = STR.length(columna.getNombre());
                dato = STR.subCadena(columna.getNombre(), 1, tam-2);
                for (int i = 0; i < nVeces; i++) {
                    aux.add(dato);
                }
                datosColumnas.add(aux);
            }else if(esNumero(columna.getNombre())){
                dato = columna.getNombre();
                for (int i = 0; i < nVeces; i++) {
                    aux.add(dato);
                }
                datosColumnas.add(aux);
            }else{
                if(columna.getFormato() == Column.FormatoTexto.Lower){
                    datosColumnas.add(getLower(tabla, columna.getNombre()));
                }else if(columna.getFormato() == Column.FormatoTexto.Upper){
                    datosColumnas.add(getUpper(tabla, columna.getNombre()));
                }else{
                    if(columna.getTipo() == Column.Tipo.Void){
                        datosColumnas.add(getRegColumna(tabla, columna.getNombre()));
                    }else{
                        String columnaName = STR.subCadena(columna.getNombre(), 4, columna.getNombre().length());
                        if(columna.getTipo() == Column.Tipo.Left){
                            datosColumnas.add(getLeft(tabla, columnaName, columna.getCantidad()));
                        }else if(columna.getTipo() == Column.Tipo.Right){
                            datosColumnas.add(getRight(tabla, columnaName, columna.getCantidad()));
                        }else if(columna.getTipo() == Column.Tipo.Len){
                            datosColumnas.add(getLen(tabla, columnaName));
                        }else if(columna.getTipo() == Column.Tipo.Max){
                            datosColumnas.add(getMax(tabla, columnaName, nVeces));
                        }else if(columna.getTipo() == Column.Tipo.Min){
                            datosColumnas.add(getMin(tabla, columnaName, nVeces));
                        }else if(columna.getTipo() == Column.Tipo.Avg){
                            datosColumnas.add(getAverage(tabla, columnaName,nVeces));
                        }else if(columna.getTipo() == Column.Tipo.Sum){
                            datosColumnas.add(getSum(tabla, columnaName,nVeces));
                        }else if(columna.getTipo() == Column.Tipo.Count){
                            datosColumnas.add(getCount(tabla, columnaName,nVeces));
                        }
                    }
                }
            }
        }
        datosColumnas = transponer(datosColumnas);

        return datosColumnas;
    }

    public List<List<String>> transponer(List<List<String>> datosColumnas) {
        List<List<String>> resultado = new ArrayList<>();

        // Iterar sobre las filas de la lista original.
        for (int i = 0; i < datosColumnas.get(0).size(); i++) {
            List<String> columna = new ArrayList<>();
            // Iterar sobre cada lista (fila) en la lista original.
            for (List<String> fila : datosColumnas) {
                // Verificar si la fila tiene suficientes elementos para evitar errores.
                if (i < fila.size()) {
                    // Agregar el elemento de la fila a la nueva columna.
                    columna.add(fila.get(i));
                } else {
                    // Agregar una cadena vacía si la fila no tiene suficientes elementos para esta posición.
                    columna.add("");
                }
            }
            // Agregar la columna a la lista de resultados.
            resultado.add(columna);
        }
        return resultado;
    }
    
    public List<String> getLower(String tabla, String columna){
        String cod = getCodigoTabla(tabla);
        List<String> datosColumna = new ArrayList<>();
        
        if(cod!=null && existeColumna(tabla, columna)){
            List<String> lista = getTablaAll(tabla);
            for (String s : lista){
                int preLongitud = longColumnasAnt(tabla, columna);
                int longitud = Integer.parseInt(getColumnaInfo(tabla, columna,3));
                String dato = STR.toLower(STR.subCadena(s, preLongitud, longitud));
                datosColumna.add(dato);
            }
        }
        return datosColumna;
    }
    public List<String> getUpper(String tabla, String columna){
        String cod = getCodigoTabla(tabla);
        List<String> datosColumna = new ArrayList<>();
        
        if(cod!=null && existeColumna(tabla, columna)){
            List<String> lista = getTablaAll(tabla);
            for (String s : lista){
                int preLongitud = longColumnasAnt(tabla, columna);
                int longitud = Integer.parseInt(getColumnaInfo(tabla, columna,3));
                String dato = STR.toUpper(STR.subCadena(s, preLongitud, longitud));
                datosColumna.add(dato);
            }
        }
        return datosColumna;
    }
    
    public List<String> getLeft(String tabla, String columna, int cantidad){
        String cod = getCodigoTabla(tabla);
        List<String> datosColumna = new ArrayList<>();
        
        if(cod!=null && existeColumna(tabla, columna)){
            List<String> lista = getTablaAll(tabla);
            for (String s : lista){
                int preLongitud = longColumnasAnt(tabla, columna);
                int longitud = Integer.parseInt(getColumnaInfo(tabla, columna,3));
                String dato = izquierda(STR.subCadena(s, preLongitud, longitud), cantidad);
                datosColumna.add(dato);
            }
        }
        return datosColumna;
    }
    
    public List<String> getRight(String tabla, String columna, int cantidad){
        String cod = getCodigoTabla(tabla);
        List<String> datosColumna = new ArrayList<>();
        
        if(cod!=null && existeColumna(tabla, columna)){
            List<String> lista = getTablaAll(tabla);
            for (String s : lista){
                int preLongitud = longColumnasAnt(tabla, columna);
                int longitud = Integer.parseInt(getColumnaInfo(tabla, columna,3));
                String dato = derecha(STR.eliminarEspaciosDer(STR.subCadena(s, preLongitud, longitud)), cantidad);
                datosColumna.add(dato);
            }
        }
        return datosColumna;
    }
    
    public List<String> getLen(String tabla, String columna){
        String cod = getCodigoTabla(tabla);
        List<String> datosColumna = new ArrayList<>();
        
        if(cod!=null && existeColumna(tabla, columna)){
            List<String> lista = getTablaAll(tabla);
            for (String s : lista){
                int preLongitud = longColumnasAnt(tabla, columna);
                int longitud = Integer.parseInt(getColumnaInfo(tabla, columna,3));
                String datoLimpio = STR.eliminarEspaciosDer(STR.subCadena(s, preLongitud, longitud));
                String dato = String.valueOf(STR.length(datoLimpio));
                datosColumna.add(dato);
            }
        }
        return datosColumna;
    }
    
    private List<String> getMax(String tabla, String columna, int nVeces){
        List<String> regColumna = getRegColumna(tabla, columna);
        List<String> datosColumna = new ArrayList<>();
        String maximo = "";
        if(!regColumna.isEmpty()){
            maximo = regColumna.get(0);
            for (String elemento : regColumna) {
                if (elemento.compareTo(maximo) > 0) {
                    maximo = elemento;
                }
            }
        } 
        for (int i = 0; i < nVeces; i++) {
            datosColumna.add(maximo);
        }
        return datosColumna;
    }
    
    private List<String> getMin(String tabla, String columna, int nVeces){
        List<String> regColumna = getRegColumna(tabla, columna);
        List<String> datosColumna = new ArrayList<>();
        String minimo = "";
        if(!regColumna.isEmpty()){
            minimo = regColumna.get(0);
            for (String elemento : regColumna) {
                if (elemento.compareTo(minimo) < 0) {
                    minimo = elemento;
                }
            }
        } 
        for (int i = 0; i < nVeces; i++) {
            datosColumna.add(minimo);
        }
        return datosColumna;
    }

    private List<String> getAverage(String tabla, String columna, int nVeces) {
        List<String> regColumna = getRegColumna(tabla, columna);
        List<String> datosColumna = new ArrayList<>();
        double suma = 0;

        if (!regColumna.isEmpty()) {
            for (String elemento : regColumna) {
                if(elemento!=null)
                    suma += Double.parseDouble(elemento);
            }
            suma = suma / regColumna.size();
        }
        for (int i = 0; i < nVeces; i++) {
            datosColumna.add(String.valueOf(STR.formatDecimal(suma)));
        }
        return datosColumna;
    }
    
    private List<String> getSum(String tabla, String columna, int nVeces) {
        List<String> regColumna = getRegColumna(tabla, columna);
        List<String> datosColumna = new ArrayList<>();
        double suma = 0;

        if (!regColumna.isEmpty()) {
            for (String elemento : regColumna) {
                if(elemento!=null)
                    suma += Double.parseDouble(elemento);
            }
        }
        for (int i = 0; i < nVeces; i++) {
            datosColumna.add(String.valueOf(STR.formatDecimal(suma)));
        }
        return datosColumna;
    }
    
    private List<String> getCount(String tabla, String columna, int nVeces) {
        List<String> regColumna = getRegColumna(tabla, columna);
        List<String> datosColumna = new ArrayList<>();
        int cuenta = 0;

        if (!regColumna.isEmpty()) {
            for (String elemento : regColumna) {
                if(elemento!=null)
                    cuenta++;
            }
        }
        for (int i = 0; i < nVeces; i++) {
            datosColumna.add(String.valueOf(cuenta));
        }
        return datosColumna;
    }

    public List<List<String>> getWhere(List<List<String>> lista, String tabla, String columnaWhere, String opWhere, String condicionWhere) {
        // Obtener los índices de las filas que cumplen con la condición WHERE.
        List<Integer> indicesFiltrados = whereIndex(tabla, columnaWhere, opWhere, condicionWhere);
        
        // Crear una nueva lista para almacenar las filas filtradas.
        List<List<String>> listaFiltrada = new ArrayList<>();

        // Recorrer los índices filtrados y agregar las filas correspondientes a la lista filtrada.
        for (Integer indice : indicesFiltrados) {
            listaFiltrada.add(lista.get(indice));
        }
        
        return listaFiltrada;
    }

    public List<Integer> whereIndex(String tabla, String columna, String op, String condicion){
        List<Integer> indexWhereOk = new ArrayList<>();
        List<String> l2 = getRegColumna(tabla, columna);
        
        for (int i = 0; i < l2.size(); i++) {
            if(esNumero(condicion)){
                if(!STR.sonIguales(getColumnaInfo(tabla, columna, 2), "C")){
                    System.out.println("Error: Where. Incompatibilidad de datos.");
                }else{
                    int condicionInt = Integer.parseInt(condicion);
                    int numInt = Integer.parseInt(l2.get(i));
                    switch (op) {
                        case "<":
                            if(numInt < condicionInt)
                                indexWhereOk.add(i);
                            break;
                        case ">":
                            if(numInt > condicionInt)
                                indexWhereOk.add(i);
                            break;
                        case "=":
                            if(numInt == condicionInt)
                                indexWhereOk.add(i);
                            break;
                        case ">=":
                            if(numInt >= condicionInt)
                                indexWhereOk.add(i);
                            break;
                        case "<=":
                            if(numInt <= condicionInt)
                                indexWhereOk.add(i);
                            break;
                        case "!=":
                            if(numInt != condicionInt)
                                indexWhereOk.add(i);
                            break;
                        default:
                            throw new AssertionError();
                    }
                }
                
            }else{
                int tam = STR.length(condicion);
                if(STR.sonIguales(l2.get(i), STR.subCadena(condicion, 1, tam-2)))
                    indexWhereOk.add(i);
            }
        }
        return indexWhereOk;
    }
        
    public List<List<String>> getLike(List<List<String>> lista, String tabla, String columnaLike, String condicionLike, int acceso) {
        // Obtener los índices de las filas que cumplen con la condición WHERE-LIKE.
        List<Integer> indicesFiltrados = likeIndex(tabla, columnaLike, condicionLike, acceso);
        
        // Crear una nueva lista para almacenar las filas filtradas.
        List<List<String>> listaFiltrada = new ArrayList<>();

        // Recorrer los índices filtrados y agregar las filas correspondientes a la lista filtrada.
        for (Integer indice : indicesFiltrados) {
            listaFiltrada.add(lista.get(indice));
        }
        
        return listaFiltrada;
    }
    
    public List<Integer> likeIndex(String tabla, String columna, String condicion, int acceso){
        List<Integer> indexLikeOk = new ArrayList<>();
        List<String> listaDatos = getRegColumna(tabla, columna);
        int longitud = condicion.length();
        
        if (acceso == 1){        // Acceso 1: inicio '%ID'.
            for (int i = 0; i < listaDatos.size(); i++) {
                String subCadena = derecha(listaDatos.get(i), longitud);
                if(STR.sonIguales(condicion, subCadena)){
                    indexLikeOk.add(i);
                }
            }
        }else{                  // Acceso 2: final 'ID%'.
            for (int i = 0; i < listaDatos.size(); i++) {
                String subCadena = izquierda(listaDatos.get(i), longitud);
                if(STR.sonIguales(condicion, subCadena)){
                    indexLikeOk.add(i);
                }
            }
        }
        
        return indexLikeOk;
    }
    
    //**************************************
    
    public List<List<String>> ordenarDatos(List<List<String>> datos, List<Column> columnas, String columnaOrder, String tipoOrder) {
        if (STR.sonIguales(tipoOrder, "asc")) {
            return ascendente(datos, columnas, columnaOrder);
        } else {
            return descendente(datos, columnas, columnaOrder);
        }
    }
    
    public List<List<String>> ascendente(List<List<String>> listaDeListas, List<Column> listColumnas, String columna) {
        int indiceDeColumnaPrincipal = indexColumna(listColumnas, columna);
        List<List<String>> listaOrdenada = new ArrayList<>(listaDeListas);
        boolean intercambiado;
        do {
            intercambiado = false;
            for (int i = 0; i < listaOrdenada.size() - 1; i++) {
                List<String> listaActual = listaOrdenada.get(i);
                List<String> siguienteLista = listaOrdenada.get(i + 1);
                if (listaActual.get(indiceDeColumnaPrincipal).compareTo(siguienteLista.get(indiceDeColumnaPrincipal)) > 0) {
                    // Intercambiar las listas
                    listaOrdenada.set(i, siguienteLista);
                    listaOrdenada.set(i + 1, listaActual);
                    intercambiado = true;
                }
            }
        } while (intercambiado);
        return listaOrdenada;
    }
    
    public List<List<String>> descendente(List<List<String>> listaDeListas, List<Column> listColumnas, String columna) {
        int indiceDeColumnaPrincipal = indexColumna(listColumnas, columna);
        List<List<String>> listaOrdenada = new ArrayList<>(listaDeListas);
        boolean intercambiado;
        do {
            intercambiado = false;
            for (int i = 0; i < listaOrdenada.size() - 1; i++) {
                List<String> listaActual = listaOrdenada.get(i);
                List<String> siguienteLista = listaOrdenada.get(i + 1);
                if (listaActual.get(indiceDeColumnaPrincipal).compareTo(siguienteLista.get(indiceDeColumnaPrincipal)) < 0) {
                    // Intercambiar las listas
                    listaOrdenada.set(i, siguienteLista);
                    listaOrdenada.set(i + 1, listaActual);
                    intercambiado = true;
                }
            }
        } while (intercambiado);
        return listaOrdenada;
    }

    // Obtener el index de una columna especifica dentro de una lista de columnas. Usado para la ordenación.
    private int indexColumna(List<Column> listColumnas, String columna){
        for (int i = 0; i < listColumnas.size(); i++) {
            if(STR.sonIguales(listColumnas.get(i).getNombre(), columna)){
                return i;
            }
        }
        return -1;
    }
    
    public String izquierda(String s, int n) {
        String T = "";
        int L = s.length();
        if(n <= L){
            T = s.substring(0, n);
        }else{
            // Si n es mayor que la longitud de la cadena s.
            // simplemente devolvemos la cadena completa.
            T = s;
        }
        return T;
    }
    
    public String derecha(String s, int n){
        String T = "";
        int L = s.length();
        if(n <= L){
            T = s.substring(L-n);
        }else{
            // Error: n excede la longitud de la cadena
            // Lanzar una excepción, imprimir un mensaje de error.
        }
        return T;
    }
    
    // Obtener todas las tablas.
    public List<String> getTablas(){
        String texto = leerArchivo("CATALOG.DAT");
        List<String> tablas = new ArrayList<>();
        int inicioLinea = 0;
        
        while (inicioLinea < texto.length()) {  // Iterar mientras haya líneas restantes
            int finLinea = texto.indexOf('\n', inicioLinea);
            if (finLinea == -1) {
                finLinea = texto.length();
            }
            String linea = STR.subCadena(texto, inicioLinea, finLinea-inicioLinea);
            if(linea.length() != 0) {
                if(esNumero(STR.subCadena(linea, 1, 2)) && STR.sonIguales(STR.subCadena(linea, 3, 1), " ")){
                    tablas.add(STR.toLower(STR.eliminarEspaciosDer(STR.subCadena(linea, 6, 10))));
                }
            }
            inicioLinea = finLinea + 1;
        }
        return tablas;
    }

    // Obtener todos los nombres de las columnas de una tabla.
    public List<String> getColumnas(String tabla){
        String texto = leerArchivo("CATALOG.DAT");
        String cod = getCodigoTabla(tabla);
        int inicioLinea = 0;
        List<String> columnas = new ArrayList<>();
        
        while (inicioLinea < texto.length()) {  // Iterar mientras haya líneas restantes
            int finLinea = texto.indexOf('\n', inicioLinea);
            if (finLinea == -1) {
                finLinea = texto.length();
            }
            String linea = STR.subCadena(texto, inicioLinea, finLinea-inicioLinea);
            if(linea.length() != 0) {
                if(STR.sonIguales(STR.subCadena(linea, 1, 2).trim(), cod) && !STR.sonIguales(STR.subCadena(linea, 3, 1), " ")){
                    columnas.add(STR.toLower(STR.eliminarEspaciosDer(STR.subCadena(linea, 6, 10))));
                }
            }
            inicioLinea = finLinea + 1;
        }
        return columnas;
    }
    
    // Obtener informacion de una columna especifica.
    public String getColumnaInfo(String tabla, String columna, int acceso){
        String texto = leerArchivo("CATALOG.DAT");
        String cod = getCodigoTabla(tabla);
        int inicioLinea = 0;
        
        while (inicioLinea < texto.length()) {  // Iterar mientras haya líneas restantes
            int finLinea = texto.indexOf('\n', inicioLinea);
            if (finLinea == -1) {
                finLinea = texto.length();
            }
            String linea = STR.subCadena(texto, inicioLinea, finLinea-inicioLinea);
            if(linea.length() != 0) {
                if(STR.sonIguales(STR.subCadena(linea, 1, 2).trim(), cod) && !STR.sonIguales(STR.subCadena(linea, 3, 1), " ")){
                    if(STR.sonIguales(STR.subCadena(linea, 6, 10).trim(), columna)){
                        switch (acceso) {
                            case 1:
                                return STR.subCadena(linea, 6, 10);     // Acceso 1: Nombre de la columna.
                            case 2:
                                return STR.subCadena(linea, 17, 1);     // Acceso 2: Tipo de dato de la columna.
                            case 3: 
                                return STR.subCadena(linea, 19, 2);     // Acceso 3: Tamaño (p. entera) de la columna.
                            case 4:
                                return STR.subCadena(linea, 22, 2);     // Acceso 4: Tamaño (p. decimal) de la columna.
                            default:
                                throw new AssertionError();     // Lanzar error.
                        }
                    }
                }
            }
            inicioLinea = finLinea + 1;
        }
        return null;
    }
    
    private int localizar(String S,int k, char E, int H) {
	int i,C,P,L;
 	L = S.length();
 	i = k;
 	C = 0;
 	P = -1;
 	while((i<=L-1)&&(P==-1)) {
            if(S.charAt(i)==E) {
                C++;
                if(C==H) {
                    P = i;
                }
            }
            i++;
 	}
   	return P;
    }
    
    public int indexOfList(List<Token> lista, String var){
        for (int i=0; i<lista.size(); i++) {
            Token t = lista.get(i);
            if(STR.sonIguales(t.getLexema(), var)){
                return i;
            }
        }
        return -1;
    }
    
    public boolean esIdentificador(char E) {
        return localizar(GLB.T_CHAR_IDENTIFIER,0,E,1)!=-1;
    }

    public boolean esNumero(char E) {
        return (localizar(GLB.T_NUMERIC,0,E,1)!=-1);
    }
    
    public boolean esNumero(String E) {
        for (int i = 0; i < E.length(); i++) {
            char c = E.charAt(i);
            if(localizar(GLB.T_NUMERIC, 0, c, 1) == -1)
                return false;
        }
        return true;
    }
    
    private boolean esCadena(String cadena){
        return cadena.length() > 2 && cadena.charAt(0) == '\'' && cadena.charAt(cadena.length()-1) == '\'';
    }

    public boolean esMayuscula(char E) {
        return (localizar(GLB.T_ALPHABETIC,0,E,1)!=-1);
    }

    public boolean esMinuscula(char E) {
        return (localizar(GLB.T_ALPHABETIC,0,E,1)!=-1);
    }

    public void imprimir(List<List<String>> lista, List<Column> columnasNombre) {
        List<Integer> maxLengths = new ArrayList<>();
        int max = 0;
        // Inicializar maxLengths con 0 para cada columna.
        for (String get : lista.get(0)) {
            maxLengths.add(0);
        }
        
        // Iterar sobre cada columna para encontrar la longitud máxima de los datos.
        for (List<String> columna : lista) {
            for (int i = 0; i < columna.size(); i++) {
                int length = STR.length(columna.get(i));
                if (length > maxLengths.get(i)) {
                    maxLengths.set(i, length);
                }
            }
        }
        // Iterar sobre los nombres de cada columna para encontrar la longitud máxima.
        for (int i = 0; i < maxLengths.size(); i++) {
            Column c = columnasNombre.get(i);
            int lonColumna = STR.length(columnasNombre.get(i).getNombre());
            if(maxLengths.get(i)<=lonColumna)
                maxLengths.set(i, lonColumna);
            max += maxLengths.get(i)+3;
        }

        System.out.println("\n"+linea(max));
        
        // Imprimir los nombres de las columnas con padding derecho basado en las longitudes máximas.
        for (int i = 0; i < columnasNombre.size(); i++) {
            Column c = columnasNombre.get(i);
            System.out.print(STR.paddingDerecha(STR.toUpper(c.getNombre()), maxLengths.get(i) + 3));
        }
        
        System.out.println("\n"+linea(max));
        
        // Imprimir los datos con padding derecho basado en las longitudes máximas.
        for (List<String> columna : lista) {
            for (int i = 0; i < columna.size(); i++) {
                System.out.print(STR.paddingDerecha(columna.get(i), maxLengths.get(i) + 3));
            }
            System.out.println(); // Salto de línea después de imprimir una columna completa.
        }
        
        System.out.println(linea(max)+"\n");
    }
    
    private String linea(int cantidad){
        String linea = "";
        for (int i = 0; i < cantidad; i++) {
            linea += "-";
        }
        return linea;
    }
}
