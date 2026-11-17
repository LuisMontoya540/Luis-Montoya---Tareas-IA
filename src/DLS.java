import java.util.*;

public class DLS {

    // Clase interna para representar el estado del tablero
    static class Estado {
        int[][] tablero;
        int filavacia;
        int columnavacia;
        Estado parent;
        String movimiento;
        int profundidad; // Profundidad actual en el árbol de búsqueda

        public Estado(int[][] tablero, int filavacia, int columnavacia, Estado parent, String movimiento, int profundidad) {
            this.tablero = new int[3][3];
            for (int i = 0; i < 3; i++) {
                System.arraycopy(tablero[i], 0, this.tablero[i], 0, 3);
            }
            this.filavacia = filavacia;
            this.columnavacia = columnavacia;
            this.parent = parent;
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

    public static void Resolver(int[][] tableroi, int profundidadmax) {
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

        Estado initialState = new Estado(tableroi, fila1, colm1, null, "Inicio", 0);

        if (initialState.esObjetivo()) {
            System.out.println("El estado inicial ya es el objetivo.");
            return;
        }

        // Pila para DLS (LIFO)
        Deque<Estado> pila = new ArrayDeque<>();
        Set<Estado> visitado = new HashSet<>();

        pila.push(initialState);

        // Medir el tiempo de inicio
        long startTime = System.currentTimeMillis();
        
        Estado meta = null;
        int nodosexplorados = 0;

        // Movimientos posibles: Arriba, Abajo, Izquierda, Derecha
        int[] dRow = {-1, 1, 0, 0};
        int[] dCol = {0, 0, -1, 1};
        String[] nombres = {"Arriba", "Abajo", "Izquierda", "Derecha"};

        while (!pila.isEmpty()) {
            Estado actual = pila.pop();

            // Evitar ciclos procesando estados ya visitados
            if (visitado.contains(actual)) {
                continue;
            }
            visitado.add(actual);
            nodosexplorados++;

            if (actual.esObjetivo()) {
                meta = actual;
                break;
            }

            // Si aún no alcanzamos el límite de profundidad, expandimos los vecinos
            if (actual.profundidad < profundidadmax) {
                // Recorremos en orden inverso para que al hacer push queden en el orden correcto
                for (int i = 3; i >= 0; i--) {
                    int filanueva = actual.filavacia + dRow[i];
                    int colnueva = actual.columnavacia + dCol[i];

                    if (filanueva >= 0 && filanueva < 3 && colnueva >= 0 && colnueva < 3) {
                        int[][] tableronuevo = new int[3][3];
                        for (int r = 0; r < 3; r++) {
                            System.arraycopy(actual.tablero[r], 0, tableronuevo[r], 0, 3);
                        }

                        // Intercambiar el espacio en blanco con la casilla adyacente
                        tableronuevo[actual.filavacia][actual.columnavacia] = tableronuevo[filanueva][colnueva];
                        tableronuevo[filanueva][colnueva] = 0;

                        Estado neighbor = new Estado(tableronuevo, filanueva, colnueva, actual, nombres[i], actual.profundidad + 1);

                        if (!visitado.contains(neighbor)) {
                            pila.push(neighbor);
                        }
                    }
                }
            }
        }

        // Medir el tiempo de fin
        long tiempofin = System.currentTimeMillis();
        long tiempototal = tiempofin - startTime;

        if (meta != null) {
            imprimirCamino(meta);
            System.out.println("Límite de profundidad: " + profundidadmax);
            System.out.println("Estados explorados: " + nodosexplorados);
            System.out.println("Tiempo transcurrido: " + tiempototal + " ms");
        } else {
            System.out.println("No se encontró solución dentro del límite de profundidad de " + profundidadmax + ".");
        }
    }

    private static void imprimirCamino(Estado meta) {
        List<Estado> camino = new ArrayList<>();
        Estado actual = meta;
        while (actual != null) {
            camino.add(actual);
            actual = actual.parent;
        }
        Collections.reverse(camino);

        System.out.println("Solución: " + (camino.size() - 1) + " movimientos:\n");
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
