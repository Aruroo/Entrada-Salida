package icc.vigenere;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.Buffer;
import java.io.IOException;
import java.io.PrintWriter;

public class Vigenere {
    private String path, secret;

    public Vigenere(String path, String secret) {
        this.path = path;
        this.secret = secret;
    }

    /***
     * Un metodo que genera una matriz de 26x26 con el abecedario recorriendose 1
     * vez por linea
     * 
     * @return se devuelve una matriz con dicha caracteristica
     */
    private char[][] generaMatriz() {
        char[][] matriz = new char[26][26]; // creacion de la matriz de 26x26

        char letra = 'A'; // letra que ira tomando diferentes valores
        for (int i = 0; i < 26; i++) {
            matriz[0][i] = letra; // rellenamos primero la primera fila
            letra++;
        }

        for (int j = 1; j < 26; j++) { // ciclo for para rellenar toda la matriz
            for (int i = 0; i < 26; i++) {
                int valorSiguiente = i + 1;
                if (valorSiguiente < 26) {
                    matriz[j][i] = matriz[j - 1][valorSiguiente];
                } else {
                    valorSiguiente = 0;
                    matriz[j][i] = matriz[j - 1][valorSiguiente];
                }

            }
        }
        return matriz;
    }

    private String readFile() {
        String lector = "";

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            lector = br.readLine();
        } catch (IOException e) {
            System.err.println("no se pudo leer: " + e);
        }
        return lector;
    }

    private void writeFile(String text) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            PrintWriter escritor = new PrintWriter(bw);
            escritor.write(text);
            escritor.close();
        } catch (IOException e) {
            System.err.println("no se pudo escribir: " + e);
        }
    }

    public void encode() {
        String text = this.readFile();
        text = text.toUpperCase(); // usamos mayusculas para generar la codificacion
        secret = secret.toUpperCase();
        String textoConLlave = text;
        String encodedText = "";
        int valor = 0; // un contador que usaremos mas adelante
        int posicionTexto, posicionLlave;
        char[][] matrizActual = generaMatriz();
        for (int i = 0; i < textoConLlave.length(); i++) {
            char caracter = textoConLlave.charAt(i); // el caracter actual

            if (asignaNumero(caracter) != -1) { // tiene que ser una letra de la A a la Z
                if (valor < secret.length()) {
                    /**
                     * Se remplaza el caracter actual con uno de la llave
                     */
                    textoConLlave = textoConLlave.replace(caracter, secret.charAt(valor));
                    valor++; // el caracter de la llave ira iterando
                } else {
                    valor = 0; // si llega al limite, se reinicia y comienza desde 0
                    textoConLlave = textoConLlave.replace(caracter, secret.charAt(valor));
                    valor++;
                }
            }
        }

        for (int i = 0; i < textoConLlave.length(); i++) {
            // se procede a codificar usando la matriz
            posicionLlave = asignaNumero(textoConLlave.charAt(i));
            posicionTexto = asignaNumero(text.charAt(i));
            if (posicionLlave != -1 && posicionTexto != -1) { // para evitar un indexOfBounds
                encodedText = encodedText + matrizActual[posicionTexto][posicionLlave];
            } else { // los caracteres especiales quedan igual
                encodedText = encodedText + text.charAt(i);
            }

        }
        this.writeFile(encodedText);

    }

    public void decode() {
        String text = this.readFile();
        String decodedText = "";
        String llave = secret.toUpperCase();
        char[][] matrizActual = generaMatriz();
        int posicionLlave;
        int contador;
        char caracterActual;
        for (int i = 0; i < text.length(); i++) {
            posicionLlave = asignaNumero(llave.charAt(i)); // a cada letra se le asocia un renglon
            contador = 0;
            caracterActual = text.charAt(i);
            if (posicionLlave != -1 && asignaNumero(caracterActual) != -1) { // si es un caracter valido
                while (matrizActual[posicionLlave][contador] != caracterActual) {
                    contador++;
                }
                decodedText = decodedText + caracterActual;
            } else {
                decodedText = decodedText + caracterActual;
            }

        }
        this.writeFile(decodedText);
    }

    /***
     * Un metodo para asignarle un numero a una letra dependiendo de su posicion en
     * el alfabeto
     * 
     * @param letra la letra que se pasara a numero, si se le pasa un caracter
     *              especial, devuelve -1
     * @return la posicion de la letra en el alfabeto
     */
    public static int asignaNumero(char letra) {
        int contador = 0;
        char variable = 'A';
        while (variable != letra && variable < 'Z') { // iteramos sobre el alfabeto
            variable++;
            contador++;
        }
        if (variable != letra) { // quiere decir que no se encontro el caracter en el alfabeto.
            return -1;
        }
        return contador;
    }
}