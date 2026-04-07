
import menu.MenuLivros;
import menu.MenuPessoas;

import java.util.Scanner;

public class Principal {
    
    public static void main(String[] args) {

        Scanner console = new Scanner(System.in);

        int opcao;
        do {
            System.out.println("\n\nEntrePares 1.0");
            System.out.println(    "--------");
            System.out.println("\n> Desconectado\n");
            System.out.println("1 - Login");
            System.out.println("2 - Registrar");
            System.out.println("0 - Sair");

            System.out.print("\nOpção: ");
            try {
                opcao = Integer.parseInt(console.nextLine());
            } catch (NumberFormatException e) {
                opcao = -1;
            }

            switch (opcao) {
                case 1: 
                    (new MenuPessoas()).menu();
                    break;
                case 2:
                    (new MenuLivros()).menu();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        } while (opcao != 0);
    }

}
