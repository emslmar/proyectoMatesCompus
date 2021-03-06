//Emiliano Sloth A01636165
//CYK

import java.io.*;
import java.util.*;

public class AlgoritmoCYK {
    public static String cadena;
    public static String s0;   //Simbolo inicial

    public static ArrayList<String> terminales = new ArrayList<String>();
    public static ArrayList<String> noTerminales = new ArrayList<String>();

    public static HashMap<String, ArrayList<String>> gramatica = new HashMap<>();

    public static boolean isTerminal = false;

    public static boolean leerGramatica(String[] args) {
        Scanner sc;
        String archivo = args[0];

        try {
            sc = new Scanner(new File(archivo));
        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found: " + archivo + "!");
            System.exit(1);
            return false;
        }

        ArrayList<String> arrTmp = new ArrayList<>();
        int line = 2;

        cadena = getPalabra(args);
        s0 = sc.next();
        sc.nextLine();

        while (sc.hasNextLine() && line <= 3) {   //solo queremos recibir las 2 primeras lineas/ la que contiene los simbolos terminaleses y la de los simbolos no terminaleses
            arrTmp.addAll(Arrays.<String>asList(sc.nextLine().split("\\s")));  //agregamos al array list
            if (line == 2) {  //simbolos terminaleses
                terminales.addAll(arrTmp);
            }
            if (line == 3) { //simbolos no terminaleses
                noTerminales.addAll(arrTmp);
            }
            arrTmp.clear();
            line++;
        }

        while (sc.hasNextLine()) {  //las Producciones
            arrTmp.addAll(Arrays.<String>asList(sc.nextLine().split("\\s")));
            String leftSide = arrTmp.get(0);  //Simbolo no terminales
            arrTmp.remove(0);
            gramatica.put(leftSide, new ArrayList<String>());  //Insertamos simbolo no terminales y un arraylist
            gramatica.get(leftSide).addAll(arrTmp);     //insertamos en ese simbolo no terminales lo que se pueda producir
            arrTmp.clear();
        }
        sc.close();
        return true;
    }

    public static String getPalabra(String[] args) {
        if (!isTerminal) {
            return args[1];
        }
        String[] sinPalabraTerminal = new String[args.length - 1];
        for (int i = 1; i < args.length; i++) {
            sinPalabraTerminal[i - 1] = args[i];
        }
        return toString(sinPalabraTerminal);
    }

    public static String[][] crearTablaCYK() {    //creamos la tabla CYK sin valores adentro

        int length = isTerminal ? cadena.split("\\s").length : cadena.length();

        String[][] tablaCYK = new String[length + 1][];
        tablaCYK[0] = new String[length];
        for (int i = 1; i < tablaCYK.length; i++) {
            tablaCYK[i] = new String[length - (i - 1)];
        }
        for (int i = 1; i < tablaCYK.length; i++) {
            for (int j = 0; j < tablaCYK[i].length; j++) {
                tablaCYK[i][j] = "";
            }
        }
        return tablaCYK;
    }

    public static String[][] implementarCYK(String[][] cykTable) {
        // Crear encabezado con palabra
        for (int i = 0; i < cykTable[0].length; i++) {
            cykTable[0][i] = obtenerLetraEnIndice(cadena, i);
        }

        // Hacer producciones para s??mbolos terminaleses con longitud 1
        for (int i = 0; i < cykTable[1].length; i++) {
            String[] combinacionesValidas = esProductor(new String[] { cykTable[0][i] });
            cykTable[1][i] = toString(combinacionesValidas);
        }
        if (cadena.length() <= 1) {
            return cykTable;
        }

        // Hacer producciones para s??mbolos con longitud 2
        for (int i = 0; i < cykTable[2].length; i++) {
            String[] diagonal = cykTable[1][i + 1].split("\\s");
            String[] downwards = cykTable[1][i].split("\\s");
            String[] combinacionesValidas = esProductor(obtenerCombinaciones(downwards, diagonal));
            cykTable[2][i] = toString(combinacionesValidas);
        }
        if (cadena.length() <= 2) {
            return cykTable;
        }

        // Producciones para s??mbolos con longitud n
        TreeSet<String> currentValues = new TreeSet<String>();

        for (int i = 3; i < cykTable.length; i++) {
            for (int j = 0; j < cykTable[i].length; j++) {
                for (int compareFrom = 1; compareFrom < i; compareFrom++) {
                    String[] diagonal = cykTable[i - compareFrom][j + compareFrom].split("\\s");
                    String[] downwards = cykTable[compareFrom][j].split("\\s");
                    String[] todasCombinaciones = obtenerCombinaciones(downwards, diagonal);
                    String[] combinacionesValidas = esProductor(todasCombinaciones);
                    if (cykTable[i][j].isEmpty()) {
                        cykTable[i][j] = toString(combinacionesValidas);
                    } else {
                        String[] valoresAntiguos = cykTable[i][j].split("\\s");
                        ArrayList<String> valoresNuevos = new ArrayList<String>(Arrays.asList(valoresAntiguos));
                        valoresNuevos.addAll(Arrays.asList(combinacionesValidas));
                        currentValues.addAll(valoresNuevos);
                        cykTable[i][j] = toString(currentValues.toArray(new String[currentValues.size()]));
                    }
                }
                currentValues.clear();
            }
        }
        return cykTable;
    }

    public static String[] esProductor(String[] toCheck) {    //este metodo agrega al hashmap las producciones validas de cada caracter
        ArrayList<String> tmp = new ArrayList<>();
        for (String s : gramatica.keySet()) {
            for (String current : toCheck) {
                if (gramatica.get(s).contains(current)) {
                    tmp.add(s);
                }
            }
        }
        if (tmp.size() == 0) {
            return new String[] {};
        }
        return tmp.toArray(new String[tmp.size()]);
    }

    public static String[] obtenerCombinaciones(String[] from, String[] to) {
        int length = from.length * to.length;
        int counter = 0;
        String[] comb = new String[length];
        if (length == 0) {
            return comb;
        }
        ;
        for (int i = 0; i < from.length; i++) {
            for (int j = 0; j < to.length; j++) {
                comb[counter] = from[i] + to[j];
                counter++;
            }
        }
        return comb;
    }

    public static void imprimirTodo(String[][] tablaCYK) {
        System.out.println();
        System.out.println("Palabra: " + cadena);
        System.out.println();
        System.out.println("Definicion formal de la gramatica:");
        System.out.println("G = (" + terminales.toString() + ", " + noTerminales.toString()+ ", P, " + s0 + ")");
        System.out.println();
        System.out.println("Producciones P:");
        for (String s : gramatica.keySet()) {
            System.out.println(s + " -> " + gramatica.get(s).toString().replaceAll("[\\[\\]\\,]", "").replaceAll("\\s", " | "));
        }
        System.out.println();
        System.out.println("Resultado CYK:");
        System.out.println();
        dibujarTabla(tablaCYK);
    }

    public static void dibujarTabla(String[][] tablaCYK) {
        int l = 12;
        String formatString = "| %-" + l + "s ";
        String s = "";

        // Print Table
        for (int i = 0; i < tablaCYK.length; i++) {
            System.out.println();
            for (int j = 0; j < tablaCYK[i].length; j++) {
                s = (tablaCYK[i][j].isEmpty()) ? "-" : tablaCYK[i][j];
                System.out.format(formatString, s.replaceAll("\\s", ","));
                if (j == tablaCYK[i].length - 1) {
                    System.out.print("|");
                }
            }
            System.out.println();
        }

        System.out.println();

        // Evaluar si si pertenece es decir si la ultima casilla de nuestro arreglo contiene nuestro simbolo inicial
        if (tablaCYK[tablaCYK.length - 1][tablaCYK[tablaCYK.length - 1].length - 1].contains(s0)) {
            System.out.println("La cadena " + cadena + " SI es un elemento de la gramatica ingresada");
        } else {
            System.out.println("La cadena " + cadena + " NO es un elemento de la gramatica ingresada");
        }
    }


    public static String toString(String[] input) {
        return Arrays.toString(input).replaceAll("[\\[\\]\\,]", "");
    }

    public static String obtenerLetraEnIndice(String word, int position) {
        if (!isTerminal) {
            return Character.toString(word.charAt(position));
        }
        return (word).split("\\s")[position];
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (args.length < 2) {
            System.out.println("Ingresar: java CYK <Nombre de Archivo> <Palabra>.");
            System.exit(1);
        } else if (args.length > 2) {
            isTerminal = true;
        }
        leerGramatica(args);
        String[][] cykTable = crearTablaCYK();
        imprimirTodo(implementarCYK(cykTable));
    }

}