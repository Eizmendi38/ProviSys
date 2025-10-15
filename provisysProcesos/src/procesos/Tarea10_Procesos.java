package procesos;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class Tarea10_Procesos {

    // Utility para formatear timestamps legibles
    private static final DateTimeFormatter TF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Thread que consume un InputStream y lo imprime a la consola con prefijo
    private static class StreamGobbler extends Thread {
        private final InputStream stream;
        private final String prefix;

        StreamGobbler(InputStream stream, String prefix) {
            this.stream = stream;
            this.prefix = prefix;
        }

        @Override
        public void run() {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println(prefix + " | " + line);
                }
            } catch (IOException e) {
                System.err.println(prefix + " | Error leyendo stream: " + e.getMessage());
            }
        }
    }

    /**
     * Lanza un proceso dado el comando (array) y muestra inicio/fin + salida.
     *
     * @param name nombre identificador del proceso (para logs)
     * @param command array con comando y argumentos
     * @return código de salida del proceso
     * @throws IOException
     * @throws InterruptedException
     */
    private static int launchProcess(String name, String[] command) throws IOException, InterruptedException {
        System.out.println("===============================================");
        System.out.println("INICIO -> " + name + " | " + LocalDateTime.now().format(TF));
        System.out.println("Comando: " + Arrays.toString(command));

        ProcessBuilder pb = new ProcessBuilder(command);
        // No redirigimos a archivo, queremos capturar los streams aquí
        Process proc = pb.start();

        // Gobblers para salida estándar y error
        StreamGobbler outGobbler = new StreamGobbler(proc.getInputStream(), name + " [OUT]");
        StreamGobbler errGobbler = new StreamGobbler(proc.getErrorStream(), name + " [ERR]");
        outGobbler.start();
        errGobbler.start();

        int exitCode = proc.waitFor(); // esperamos a que termine
        // Asegurarnos de que los gobblers terminen de leer
        outGobbler.join();
        errGobbler.join();

        System.out.println("FIN    -> " + name + " | " + LocalDateTime.now().format(TF));
        System.out.println("Código de salida: " + exitCode);
        System.out.println("===============================================");
        return exitCode;
    }

    // Construye el comando ping apropiado según el sistema operativo
    private static String[] buildPingCommand() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            // Windows usa -n para número de paquetes
            return new String[] {"ping", "-n", "4", "google.com"};
        } else {
            // Linux/macOS usan -c
            return new String[] {"ping", "-c", "4", "google.com"};
        }
    }

    // Comando para obtener versión de Java (proceso corto y portable si java está instalado)
    private static String[] buildJavaVersionCommand() {
        return new String[] {"java", "-version"};
    }

    // Si el usuario pasa un único argumento con comillas, lo separamos en tokens simples
    private static String[] splitCommandString(String cmd) {
        // Splitting simple: separa por espacios respetando comillas dobles
        List<String> tokens = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder cur = new StringBuilder();
        for (int i = 0; i < cmd.length(); i++) {
            char c = cmd.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
                continue;
            }
            if (c == ' ' && !inQuotes) {
                if (cur.length() > 0) {
                    tokens.add(cur.toString());
                    cur.setLength(0);
                }
            } else {
                cur.append(c);
            }
        }
        if (cur.length() > 0) tokens.add(cur.toString());
        return tokens.toArray(new String[0]);
    }

    public static void main(String[] args) {
        try {
            // 1) Ejecutar ping (detecta SO)
            String[] pingCmd = buildPingCommand();
            launchProcess("PING_GOOGLE", pingCmd);

            // 2) Ejecutar java -version
            String[] javaVer = buildJavaVersionCommand();
            launchProcess("JAVA_VERSION", javaVer);

            // 3) (Opcional) ejecutar comando pasado por argumento
            if (args.length > 0) {
                // Si se pasó un único string con espacios, lo parseamos
                String joined = String.join(" ", args);
                String[] extra = splitCommandString(joined);
                launchProcess("COMANDO_EXTRA", extra);
            } else {
                System.out.println("No se pasó comando extra por argumentos. Para probar uno:");
                System.out.println("  java Tarea10_Procesos \"ls -la\"  (Unix)");
                System.out.println("  java Tarea10_Procesos \"cmd /c dir\" (Windows)");
            }

            System.out.println("Todos los procesos solicitados han terminado.");
        } catch (IOException e) {
            System.err.println("Excepción de E/S al ejecutar un proceso: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("Ejecución interrumpida: " + e.getMessage());
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }
}
