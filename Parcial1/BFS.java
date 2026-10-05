import java.util.*;

public class BFS {

    // Clase interna para representar el estado del tablero
    static class Estado {
        int[][] tablero;
        int filavacia;
        int columnavacia;
        Estado padre;
        String movimiento;

        public Estado(int[][] tablero, int filavacia, int columnavacia, Estado padre, String movimiento) {
            this.tablero = new int[3][3];
            for (int i = 0; i < 3; i++) {
                System.arraycopy(tablero[i], 0, this.tablero[i], 0, 3);
            }
            this.filavacia = filavacia;
            this.columnavacia = columnavacia;
            this.padre = padre;
            this.movimiento = movimiento;
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Estado estado = (Estado) o;
            for (int i = 0; i < 3; i++) {
                if (!Arrays.equals(tablero[i], estado.tablero[i])) return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int resultado = 0;
            for (int i = 0; i < 3; i++) {
                resultado = 31 * resultado + Arrays.hashCode(tablero[i]);
            }
            return resultado;
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

        Estado inicial = new Estado(tableroi, fila1, colm1, null, "Inicio");

        if (inicial.esObjetivo()) {
            System.out.println("El estado inicial ya es el objetivo.");
            return;
        }

        Queue<Estado> queue = new LinkedList<>();
        Set<Estado> visitado = new HashSet<>();

        queue.add(inicial);
        visitado.add(inicial);

        // Medir el tiempo de inicio
        long tiempoin = System.currentTimeMillis();
        
        Estado meta = null;
        int nodosexplorados = 0;

        // Movimientos posibles: Arriba, Abajo, Izquierda, Derecha
        int[] filad = {-1, 1, 0, 0};
        int[] cold = {0, 0, -1, 1};
        String[] nombres = {"Arriba", "Abajo", "Izquierda", "Derecha"};

        while (!queue.isEmpty()) {
            Estado actual = queue.poll();
            nodosexplorados++;

            if (actual.esObjetivo()) {
                meta = actual;
                break;
            }

            // Generar vecinos
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

                    Estado vecino = new Estado(tableronuevo, filanueva, colnueva, actual, nombres[i]);

                    if (!visitado.contains(vecino)) {
                        visitado.add(vecino);
                        queue.add(vecino);
                    }
                }
            }
        }

        // Medir el tiempo de fin
        long tiempofin = System.currentTimeMillis();
        long tiempototal = tiempofin - tiempoin;

        if (meta != null) {
            imprimirCamino(meta);
            System.out.println("Estados explorados: " + nodosexplorados);
            System.out.println("Tiempo transcurrido: " + tiempototal + " ms");
        } else {
            System.out.println("No se encontró solución.");
        }
    }

    private static void imprimirCamino(Estado meta) {
        List<Estado> camino = new ArrayList<>();
        Estado actual = meta;
        while (actual != null) {
            camino.add(actual);
            actual = actual.padre;
        }
        Collections.reverse(camino);

        System.out.println("Solución: " + (camino.size() - 1) + " movimientos:\n");
        for (int paso = 0; paso < camino.size(); paso++) {
            Estado s = camino.get(paso);
            System.out.println("Paso " + paso + " (" + s.movimiento + "):");
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