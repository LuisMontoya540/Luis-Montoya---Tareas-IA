

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author luisg
 */
public class App {
    public static void main(String[] args) {
        // Tablero de prueba inicial (0 representa el espacio vacío)
        int[][] tableroinicial = {
            {1, 2, 3},
            {4, 0, 5},
            {7, 8, 6}      
        };
        
       BFS bfs = new BFS();
       UCS ucs = new UCS();
       DFS dfs = new DFS();
       DLS dls = new DLS();
       IDDFS iddfs = new IDDFS();
       
       System.out.println("--------BFS--------");
       bfs.Resolver(tableroinicial);
       System.out.println("");
       
       System.out.println("--------UCS--------");
       ucs.Resolver(tableroinicial);
       System.out.println("");
      
       System.out.println("--------DFS--------");
       dfs.Resolver(tableroinicial);
       System.out.println("");
       
       System.out.println("--------DLS--------");
       dls.Resolver(tableroinicial, 20);
       System.out.println("");
       
       System.out.println("--------IDDFS--------");
       iddfs.Resolver(tableroinicial);
       System.out.println("");
       
       
    }
}
