public class Tarea11_Hilos {
    public static void main(String[] args) {
        // Se crea un hilo que ejecutará la tarea de dibujar un arte ASCII
        Thread hiloDibujo = new Thread(new DibujoASCII());
        // Se crea otro hilo que ejecutará la tarea de contar números
        Thread hiloContador = new Thread(new Contador());

        // Se inician ambos hilos de manera concurrente
        hiloDibujo.start();
        hiloContador.start();
    }
}

// Clase que implementa la interfaz Runnable para definir el comportamiento del hilo de dibujo
class DibujoASCII implements Runnable {
    @Override
    public void run() {
        // Se define el dibujo ASCII como una cadena multilínea (usando texto entre triple comillas)
        String dibujo = """
░░░░░▄▄▄▄▀▀▀▀▀▀▀▀▄▄▄▄▄▄░░░░░░░
░░░░░█░░░░▒▒▒▒▒▒▒▒▒▒▒▒░░▀▀▄░░░░
░░░░█░░░▒▒▒▒▒▒░░░░░░░░▒▒▒░░█░░░
░░░█░░░░░░▄██▀▄▄░░░░░▄▄▄░░░░█░░
░▄▀▒▄▄▄▒░█▀▀▀▀▄▄█░░░██▄▄█░░░░█░
█░▒█▒▄░▀▄▄▄▀░░░░░░░░█░░░▒▒▒▒▒░█
█░▒█░█▀▄▄░░░░░█▀░░░░▀▄░░▄▀▀▀▄▒█
░█░▀▄░█▄░█▀▄▄░▀░▀▀░▄▄▀░░░░█░░█░
░░█░░░▀▄▀█▄▄░█▀▀▀▄▄▄▄▀▀█▀██░█░░
░░░█░░░░██░░▀█▄▄▄█▄▄█▄████░█░░░
░░░░█░░░░▀▀▄░█░░░█░█▀██████░█░░
░░░░░▀▄░░░░░▀▀▄▄▄█▄█▄█▄█▄▀░░█░░
░░░░░░░▀▄▄░▒▒▒▒░░░░░░░░░░▒░░░█░
░░░░░░░░░░▀▀▄▄░▒▒▒▒▒▒▒▒▒▒░░░░█░
░░░░░░░░░░░░░░▀▄▄▄▄▄░░░░░░░░█░░
""";

        try {
            // Bucle infinito que imprime el dibujo cada segundo
            while (true) {
                System.out.println(dibujo); // Muestra el dibujo en consola
                Thread.sleep(1000); // Pausa el hilo durante 1 segundo (1000 ms)
            }
        } catch (InterruptedException e) {
            // Captura la excepción si el hilo es interrumpido
            e.printStackTrace();
        }
    }
}

// Clase que implementa Runnable para definir el comportamiento del hilo contador
class Contador implements Runnable {
    @Override
    public void run() {
        int contador = 0; // Variable para contar números
        try {
            // Bucle infinito que incrementa y muestra el contador
            while (true) {
                contador++; // Aumenta el contador en 1
                System.out.println("Contador: " + contador); // Muestra el valor actual
                Thread.sleep(700); // Pausa el hilo durante 700 milisegundos
            }
        } catch (InterruptedException e) {
            // Captura la excepción si el hilo es interrumpido
            e.printStackTrace();
        }
    }
}
