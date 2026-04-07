package menu;

import arquivo.*;
import entidades.Usuario;
import entidades.Curso;
import visao.VisaoUsuario;

import java.util.Scanner;

public class MenuUsuario {
    int idUsuario;
    ArquivoUsuario arqUsuario;
    ArquivoCurso arqCurso;
    Scanner console;
    VisaoUsuario visao;

    public void menu() {
        try {
            console = new Scanner(System.in);
            arqUsuario = new ArquivoUsuario();
            arqCurso = new ArquivoCurso();
            visao = new VisaoUsuario(console);
            this.idUsuario = id;

            int opcao;
            do {
                System.out.println("\n\nEntrePares");
                System.out.println("--------");
                System.out.println("\n> Gestão de Usuários\n");
                System.out.println("1 - Inserir Usuário");
                System.out.println("2 - Buscar por Email");
                System.out.println("3 - Buscar por Nome");
                System.out.println("4 - Alterar Usuário");
                System.out.println("5 - Excluir Usuário");
                System.out.println("6 - Listar Todos");
                System.out.println("0 - Retornar ao menu anterior");
                System.out.print("\nOpção: ");
                try {
                    opcao = Integer.parseInt(console.nextLine());
                } catch (NumberFormatException e) {
                    opcao = -1;
                }

                switch (opcao) {
                    case 1:
                        inserir();
                        break;
                    case 2:
                        buscarPorEmail();
                        break;
                    case 3:
                        buscarPorNome();
                        break;
                    case 4:
                        alterar();
                        break;
                    case 5:
                        excluir();
                        break;
                    case 6:
                        listagem();
                        break;
                    case 0: break;
                    default:
                        System.out.println("Opção inválida");
                }
            } while (opcao != 0);

            arqUsuario.close();
            arqCurso.close();

        } catch(Exception e) {
            System.err.println("Erro no menu de usuários!");
            e.printStackTrace();
        }
    }

    private void inserir() throws Exception {
        System.out.println("\n=== INCLUSÃO DE USUÁRIO ===");

        Usuario usuario = visao.leUsuario();
        if (usuario == null) {
            System.out.println("Operação cancelada.");
            return;
        }

        // Verifica se email já está cadastrado
        Usuario existente = arqUsuario.readEmail(usuario.getEmail());
        if (existente != null) {
            System.out.println("Email já cadastrado!");
            return;
        }

        int id = arqUsuario.create(usuario);
        System.out.println("Usuário incluído com ID: " + id);
    }

    private void buscarPorEmail() throws Exception {
        System.out.println("\n=== BUSCA POR EMAIL ===");
        System.out.print("Email: ");
        String email = console.nextLine();
        if (email.isEmpty()) return;

        Usuario usuario = arqUsuario.readEmail(email);
        if (usuario != null) {
            visao.mostraUsuario(usuario);
        } else {
            System.out.println("Usuário não encontrado!");
        }
    }

    private void buscarPorNome() throws Exception {
        System.out.println("\n=== BUSCA POR NOME ===");
        System.out.print("Nome: ");
        String nome = console.nextLine();
        if (nome.isEmpty()) return;

        Usuario[] usuarios = arqUsuario.readNome(nome);
        if (usuarios.length > 0) {
            System.out.println("\n=== USUÁRIOS ENCONTRADOS ===");
            for (Usuario u : usuarios) {
                visao.mostraUsuario(u);
            }
        } else {
            System.out.println("Nenhum usuário encontrado!");
        }
    }

    private void alterar() throws Exception {
        System.out.println("\n=== ALTERAÇÃO DE USUÁRIO ===");
        System.out.print("Email do usuário: ");
        String email = console.nextLine();
        if (email.isEmpty()) return;

        Usuario usuario = arqUsuario.readEmail(email);
        if (usuario == null) {
            System.out.println("Usuário não encontrado!");
            return;
        }

        visao.mostraUsuario(usuario);
        Usuario alterado = visao.leAlteracaoUsuario(usuario);

        if (arqUsuario.update(alterado)) {
            System.out.println("Usuário atualizado!");
        } else {
            System.out.println("Erro na atualização!");
        }
    }

    private void excluir() throws Exception {
        System.out.println("\n=== EXCLUSÃO DE USUÁRIO ===");
        System.out.print("Email do usuário: ");
        String email = console.nextLine();
        if (email.isEmpty()) return;

        Usuario usuario = arqUsuario.readEmail(email);
        if (usuario == null) {
            System.out.println("Usuário não encontrado!");
            return;
        }

        visao.mostraUsuario(usuario);

        // Verifica cursos vinculados
        Curso[] cursosDoUsuario = arqCurso.readPorUsuario(usuario.getID());
        if (cursosDoUsuario.length > 0) {
            System.out.println("\n⚠️  ATENÇÃO: Este usuário possui " + cursosDoUsuario.length + " curso(s) vinculado(s):");
            int cursosAtivos = 0;
            for (Curso c : cursosDoUsuario) {
                String estadoStr = c.getEstadoDescricao();
                System.out.println("  - " + c.getNome() + " (Estado: " + estadoStr + ")");
                if (c.getEstado() == Curso.ATIVO_INSCRICOES || c.getEstado() == Curso.ATIVO_SEM_INSCRICOES) {
                    cursosAtivos++;
                }
            }

            if (cursosAtivos > 0) {
                System.out.println("\n❌ NÃO É POSSÍVEL EXCLUIR: Existem " + cursosAtivos + " curso(s) ATIVO(S).");
                System.out.println("   Exclua ou inative os cursos ativos primeiro.");
                return;
            }

            System.out.println("\nTodos os cursos estão INATIVOS. Eles também serão excluídos.");
        }

        // Exclui cursos inativos primeiro
        for (Curso c : cursosDoUsuario) {
            arqCurso.delete(c.getID());
        }

        // Exclui o usuário
        if (arqUsuario.delete(usuario.getID())) {
            System.out.println("Usuário e " + cursosDoUsuario.length + " curso(s) excluído(s)!");
        } else {
            System.out.println("Erro na exclusão!");
        }
    }

    private void listagem() throws Exception {
        System.out.println("\n=== LISTAGEM DE USUÁRIOS ===");
        Usuario[] usuarios = arqUsuario.readAll();
        if (usuarios.length > 0) {
            for (Usuario u : usuarios) {
                visao.mostraUsuario(u);
                System.out.println();
            }
        } else {
            System.out.println("Nenhum usuário cadastrado!");
        }
    }
}
