import java.util.*;

public class IDDFS {

    // Clase interna para representar el estado del tablero
    static class Estado {
        int[][] tablero;
        int filavacia;
        int columnavacia;
        Estado padre;
        String movimiento;
        int profundidad;

        public Estado(int[][] tablero, int filavacia, int columnavacia, Estado padre, String movimiento, int profundidad) {
            this.tablero = new int[3][3];
            for (int i = 0; i < 3; i++) {
                System.arraycopy(tablero[i], 0, this.tablero[i], 0, 3);
            }
            this.filavacia = filavacia;
            this.columnavacia = columnavacia;
            this.padre = padre;
            this.movimiento = movimiento;
            this.profundidad = profundidad;
        }

        // Comprueba si el estado actual es la meta
        public boolean esObjetivo() {
            int[][] meta = {
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 0}
            };
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (this.tablero[i][j] != meta[i][j]) return false;
                }
            }
            return true;
        }

        // Verifica si un tablero ya está en los ancestros del camino actual para evitar ciclos en DFS
        public boolean esAnterior(int[][] tableronuevo) {
            Estado actual = this;
            while (actual != null) {
                boolean coincide = true;
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        if (actual.tablero[i][j] != tableronuevo[i][j]) {
                            coincide = false;
                            break;
                        }
                    }
                    if (!coincide) break;
                }
                if (coincide) return true;
                actual = actual.padre;
            }
            return false;
        }
    }

    // Clase auxiliar para almacenar el resultado de cada DLS
    static class Resultado {
        Estado edofinal;
        int explorado;
        Resultado(Estado goalState, int statesExplored) {
            this.edofinal = goalState;
            this.explorado = statesExplored;
        }
    }

    public static void Resolver(int[][] tableroi) {
        // Encontrar la posición del espacio vacío (0)
        int fila1 = -1;
        int colm1 = -1;
        outer:
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (tableroi[i][j] == 0) {
                    fila1 = i;
                    colm1 = j;
                    break outer;
                }
            }
        }

        Estado inicial = new Estado(tableroi, fila1, colm1, null, "Inicio", 0);

        if (inicial.esObjetivo()) {
            System.out.println("El estado inicial ya es el objetivo.");
            return;
        }

        // Medir el tiempo de inicio
        long tiempoin = System.currentTimeMillis();
        
        Estado meta = null;
        int nodostotalesexplorados = 0;
        int profundidamaxima = 31; // El 8-puzzle tiene una distancia máxima de solución de 31 movimientos

        // Bucle IDDFS: Incrementa el límite de profundidad en cada iteración
        for (int limit = 0; limit <= profundidamaxima; limit++) {
            Resultado resultado = dls(inicial, limit);
            nodostotalesexplorados += resultado.explorado;

            if (resultado.edofinal != null) {
                meta = resultado.edofinal;
                break;
            }
        }

        // Medir el tiempo de fin
        long tiempofinal = System.currentTimeMillis();
        long tiempototal = tiempofinal - tiempoin;

        if (meta != null) {
            imprimirCamino(meta);
            System.out.println("Estados totales explorados (en todas las iteraciones): " + nodostotalesexplorados);
            System.out.println("Profundidad de la solución óptima: " + meta.profundidad);
            System.out.println("Tiempo transcurrido: " + tiempototal + " ms");
        } else {
            System.out.println("No se encontró solución dentro del límite máximo.");
        }
    }

    // Búsqueda en Profundidad Limitada (DLS) recursiva para cada iteración de IDDFS
    private static Resultado dls(Estado actual, int limite) {
        int nodosexplorados = 1;

        if (actual.esObjetivo()) {
            return new Resultado(actual, nodosexplorados);
        }

        if (actual.profundidad >= limite) {
            return new Resultado(null, nodosexplorados);
        }

        // Movimientos posibles: Arriba, Abajo, Izquierda, Derecha
        int[] filad = {-1, 1, 0, 0};
        int[] cold = {0, 0, -1, 1};
        String[] nombres = {"Arriba", "Abajo", "Izquierda", "Derecha"};

        for (int i = 0; i < 4; i++) {
            int filanueva = actual.filavacia + filad[i];
            int colnueva = actual.columnavacia + cold[i];

            if (filanueva >= 0 && filanueva < 3 && colnueva >= 0 && colnueva < 3) {
                int[][] tableronuevo = new int[3][3];
                for (int r = 0; r < 3; r++) {
                    System.arraycopy(actual.tablero[r], 0, tableronuevo[r], 0, 3);
                }

                // Intercambiar el espacio en blanco con la casilla adyacente
                tableronuevo[actual.filavacia][actual.columnavacia] = tableronuevo[filanueva][colnueva];
                tableronuevo[filanueva][colnueva] = 0;

                // Evitar bucles verificando si el tablero ya está en el camino actual (ancestros)
                if (!actual.esAnterior(tableronuevo)) {
                    Estado vecino = new Estado(tableronuevo, filanueva, colnueva, actual, nombres[i], actual.profundidad + 1);
                    
                    Resultado res = dls(vecino, limite);
                    nodosexplorados += res.explorado;

                    if (res.edofinal != null) {
                        return new Resultado(res.edofinal, nodosexplorados);
                    }
                }
            }
        }

        return new Resultado(null, nodosexplorados);
    }

    private static void imprimirCamino(Estado meta) {
        List<Estado> camino = new ArrayList<>();
        Estado actual = meta;
        while (actual != null) {
            camino.add(actual);
            actual = actual.padre;
        }
        Collections.reverse(camino);

        System.out.println("Solución encontrada en " + (camino.size() - 1) + " movimientos:\n");
        for (int paso = 0; paso < camino.size(); paso++) {
            Estado s = camino.get(paso);
            System.out.println("Paso " + paso + " (" + s.movimiento + ") - Profundidad: " + s.profundidad);
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    System.out.print((s.tablero[i][j] == 0 ? " " : s.tablero[i][j]) + " ");
                }
                System.out.println();
            }
            System.out.println();
        }
    }
}