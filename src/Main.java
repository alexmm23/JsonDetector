public class Main {
    public static void main(String[] args) {
        int a = 10, b = 5;
        System.out.println("Potencia: " + potencia(a,b));
    }
    public static int multiplicar(int a, int b) {
        int mult = 0;
        for(int i = 0; i < b; i++) {
            mult += a;
        }
        return mult;
    }
    public static int potencia(int base, int exponente){
        int resultado = 1;
        int actual;
        for(int i = 0; i < exponente; i++){
            actual = multiplicar(resultado, base);
            resultado = actual;
        }
        return resultado;
    }
}