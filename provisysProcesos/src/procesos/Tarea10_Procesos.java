import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Tarea10_Procesos {

    // Utility para formatear timestamps legibles
    private static final DateTimeFormatter TF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Thread que consume un InputStream y lo imprime a la consola con prefijo (Cuando ejecutas un proceso externo, este puede generar salida estándar y errores, Esta clase lee continuamente esos flujos y los imprime en consola, evitando bloqueos y mostrando la información en tiempo real.)
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
     * Lanza un proceso dado el comando (array) y muestra inicio/fin + salida detallada.
     *
     * @param name nombre identificador del proceso (para logs)
     * @param command array con comando y argumentos
     * @return código de salida del proceso
     * @throws IOException
     * @throws InterruptedException
     */
    // Lanza el proceso externo usando ProcessBuilder.
    private static int launchProcess(String name, String[] command) throws IOException, InterruptedException {
        System.out.println("===============================================");
        System.out.println("INICIO -> " + name + " | " + LocalDateTime.now().format(TF));
        System.out.println("Comando: " + Arrays.toString(command));

        ProcessBuilder pb = new ProcessBuilder(command);
        Process proc = pb.start();

        // Gobblers para salida estándar y error
        StreamGobbler outGobbler = new StreamGobbler(proc.getInputStream(), name + " [OUT]");
        StreamGobbler errGobbler = new StreamGobbler(proc.getErrorStream(), name + " [ERR]");
        outGobbler.start();
        errGobbler.start();

        int exitCode = proc.waitFor(); // esperamos a que termine
        outGobbler.join();
        errGobbler.join();

        System.out.println("FIN    -> " + name + " | " + LocalDateTime.now().format(TF));
        System.out.println("Código de salida: " + exitCode);
        System.out.println("===============================================");
        return exitCode;
    }

    // Wrapper que imprime INICIO y FIN alrededor de launchProcess
    private static int runAndLog(String name, String[] cmd) throws IOException, InterruptedException {
        System.out.println("-> Inicio proceso: " + name);
        int code = launchProcess(name, cmd);
        System.out.println("-> Fin proceso: " + name);
        return code;
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

    // Construye comando para abrir explorador de archivos en el directorio actual
    private static String[] buildExplorerCommand() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return new String[] {"explorer.exe", "."};
        } else if (os.contains("mac")) {
            return new String[] {"open", "."};
        } else {
            return new String[] {"xdg-open", "."};
        }
    }

    // Intentar ejecutar varios candidatos (útil para entornos Linux distintos)
    private static int tryCandidates(String name, List<String[]> candidates) throws IOException, InterruptedException {
        IOException lastEx = null;
        for (String[] cmd : candidates) {
            try {
                return launchProcess(name, cmd);
            } catch (IOException e) {
                lastEx = e;
                // probar siguiente candidato
            }
        }
        // si llegamos aquí, todos fallaron
        if (lastEx != null) throw lastEx;
        throw new IOException("No hay candidatos para ejecutar " + name);
    }

    // Wrapper para ejecutar candidatos con mensajes INICIO/FIN
    private static int runCandidatesAndLog(String name, List<String[]> candidates) throws IOException, InterruptedException {
        System.out.println("-> Inicio proceso: " + name + " (probando candidatos)");
        int code = tryCandidates(name, candidates);
        System.out.println("-> Fin proceso: " + name + " (candidato exitoso)");
        return code;
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
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;

        while (!salir) {
            System.out.println();
            System.out.println("===== MENÚ DE PROCESOS =====");
            System.out.println("1. Ping a google (4 paquetes)");
            System.out.println("2. Ejecutar comando introducido ahora");
            System.out.println("3. Abrir Explorador de archivos (directorio actual)");
            System.out.println("4. Salir");
            System.out.print("Seleccione una opción (1-4): ");

            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                System.out.println("No se introdujo opción. Intente de nuevo.");
                continue;
            }

            int opcion;
            try {
                opcion = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Opción no válida. Introduzca un número entre 1 y 4.");
                continue;
            }

            try {
                switch (opcion) {
                    case 1: {
                        String[] pingCmd = buildPingCommand();
                        runAndLog("PING_GOOGLE", pingCmd);
                        break;
                    }

                    case 2: {
                        System.out.print("Introduzca el comando a ejecutar (p. ej. ls -la o \"cmd /c dir\"): ");
                        String cmdLine = scanner.nextLine().trim();
                        if (cmdLine.isEmpty()) {
                            System.out.println("Comando vacío. Cancelado.");
                        } else {
                            String[] cmd = splitCommandString(cmdLine);
                            runAndLog("COMANDO_USUARIO", cmd);
                        }
                        break;
                    }

                    case 3: {
                        String os = System.getProperty("os.name").toLowerCase();
                        if (os.contains("win") || os.contains("mac")) {
                            String[] explorer = buildExplorerCommand();
                            runAndLog("EXPLORADOR", explorer);
                        } else {
                            List<String[]> candidates = new ArrayList<>();
                            candidates.add(new String[] {"xdg-open", "."});
                            candidates.add(new String[] {"nautilus", "."});
                            candidates.add(new String[] {"gio", "open", "."});
                            try {
                                runCandidatesAndLog("EXPLORADOR", candidates);
                            } catch (IOException e) {
                                System.err.println("No se pudo abrir el explorador de archivos. Ningún candidato disponible.");
                            }
                        }
                        break;
                    }

                    case 4:
                        salir = true;
                        System.out.println("Saliendo del programa.");
                        break;

                    default:
                        System.out.println("Opción no válida. Por favor, seleccione una opción entre 1 y 4.");
                }
            } catch (IOException e) {
                System.err.println("Excepción de E/S al ejecutar el proceso: " + e.getMessage());
            } catch (InterruptedException e) {
                System.err.println("Ejecución interrumpida: " + e.getMessage());
                Thread.currentThread().interrupt();
            }
        }

        scanner.close();
    }
}