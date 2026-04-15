package menu;

import arquivo.ArquivoCurso;
import arquivo.ArquivoUsuario;
import entidades.Curso;
import entidades.Usuario;

import java.util.Scanner;

public class MenuUsuario {

    private ArquivoUsuario arqUsuario;
    private ArquivoCurso arqCurso;
    private Scanner console;

    public void menu() {
        try {
            console = new Scanner(System.in);
            arqUsuario = new ArquivoUsuario();
            arqCurso = new ArquivoCurso();

            int opcao;
            do {
                System.out.println("\n\nEntrePares 1.0");
                System.out.println("--------------");
                System.out.println("> Início > Meus dados\n");
                System.out.println("(1) Buscar meus dados por email");
                System.out.println("(2) Corrigir meus dados");
                System.out.println("(3) Excluir meu usuário");
                System.out.println("(R) Retornar");

                System.out.print("\nOpção: ");
                String entrada = console.nextLine().trim().toUpperCase();

                switch (entrada) {
                    case "1":
                        buscarPorEmail();
                        break;
                    case "2":
                        alterar();
                        break;
                    case "3":
                        excluir();
                        break;
                    case "R":
                        opcao = 0;
                        continue;
                    default:
                        System.out.println("Opção inválida!");
                }
                opcao = 1;
            } while (opcao != 0);

            arqUsuario.close();
            arqCurso.close();

        } catch (Exception e) {
            System.err.println("Erro no menu de usuários!");
            e.printStackTrace();
        }
    }

    public void cadastrarNovoUsuario() {
        try {
            console = new Scanner(System.in);
            arqUsuario = new ArquivoUsuario();
            inserir();
            arqUsuario.close();
        } catch (Exception e) {
            System.err.println("Erro no cadastro de usuário!");
            e.printStackTrace();
        }
    }

    private void inserir() throws Exception {
        System.out.println("\n=== NOVO USUÁRIO ===");

        System.out.print("Nome: ");
        String nome = console.nextLine();
        if (nome.isEmpty()) return;

        String email;
        while (true) {
            System.out.print("Email: ");
            email = console.nextLine();
            if (email.isEmpty()) return;

            Usuario existente = arqUsuario.readEmail(email);
            if (existente != null) {
                System.out.println("Email já cadastrado!");
            } else {
                break;
            }
        }

        String senha;
        while (true) {
            System.out.print("Senha: ");
            senha = console.nextLine();
            if (senha.isEmpty()) return;

            if (senha.length() < 6) {
                System.out.println("A senha deve ter pelo menos 6 caracteres.");
            } else {
                break;
            }
        }

        System.out.print("Pergunta secreta: ");
        String pergunta = console.nextLine();
        if (pergunta.isEmpty()) return;

        System.out.print("Resposta secreta: ");
        String resposta = console.nextLine();
        if (resposta.isEmpty()) return;

        Usuario usuario = new Usuario(nome, email, senha.hashCode(), pergunta, resposta.hashCode());
        int id = arqUsuario.create(usuario);
        System.out.println("Usuário incluído com ID: " + id);
    }

    private void buscarPorEmail() throws Exception {
        System.out.println("\n=== BUSCA DE USUÁRIO ===");
        System.out.print("Email: ");
        String email = console.nextLine();
        if (email.isEmpty()) return;

        Usuario usuario = arqUsuario.readEmail(email);
        if (usuario != null) {
            mostraUsuario(usuario);
        } else {
            System.out.println("Usuário não encontrado!");
        }
    }

    private void alterar() throws Exception {
        System.out.println("\n=== CORREÇÃO DOS DADOS DO USUÁRIO ===");
        System.out.print("Email do usuário: ");
        String email = console.nextLine();
        if (email.isEmpty()) return;

        Usuario usuario = arqUsuario.readEmail(email);
        if (usuario == null) {
            System.out.println("Usuário não encontrado!");
            return;
        }

        mostraUsuario(usuario);
        System.out.println("\nDeixe em branco para manter o valor atual.\n");

        System.out.print("Nome (" + usuario.getNome() + "): ");
        String novoNome = console.nextLine();
        if (!novoNome.isEmpty()) {
            usuario.setNome(novoNome);
        }

        while (true) {
            System.out.print("Email (" + usuario.getEmail() + "): ");
            String novoEmail = console.nextLine();

            if (novoEmail.isEmpty()) {
                break;
            }

            Usuario outro = arqUsuario.readEmail(novoEmail);
            if (outro != null && outro.getID() != usuario.getID()) {
                System.out.println("Esse email já está cadastrado!");
            } else {
                usuario.setEmail(novoEmail);
                break;
            }
        }

        System.out.print("Nova senha [Enter para manter]: ");
        String novaSenha = console.nextLine();
        if (!novaSenha.isEmpty() && novaSenha.length() >= 6) {
            usuario.setSenha(novaSenha);
        }

        System.out.print("Pergunta secreta (" + usuario.getPerguntaSecreta() + "): ");
        String novaPergunta = console.nextLine();
        if (!novaPergunta.isEmpty()) {
            usuario.setPerguntaSecreta(novaPergunta);
        }

        System.out.print("Resposta secreta [Enter para manter]: ");
        String novaResposta = console.nextLine();
        if (!novaResposta.isEmpty()) {
            usuario.setRespostaSecreta(novaResposta);
        }

        if (arqUsuario.update(usuario)) {
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

        Curso[] cursosDoUsuario = arqCurso.readPorUsuario(usuario.getID());

        int cursosAtivos = 0;
        for (Curso curso : cursosDoUsuario) {
            if (curso.getEstado() == Curso.ATIVO_INSCRICOES || curso.getEstado() == Curso.ATIVO_SEM_INSCRICOES) {
                cursosAtivos++;
            }
        }

        if (cursosAtivos > 0) {
            System.out.println("Usuário não pode ser excluído, pois possui curso(s) ativo(s) vinculado(s).");
            return;
        }

        for (Curso curso : cursosDoUsuario) {
            arqCurso.delete(curso.getID());
        }

        if (arqUsuario.delete(usuario.getID())) {
            System.out.println("Usuário excluído com sucesso!");
        } else {
            System.out.println("Erro na exclusão!");
        }
    }

    private void mostraUsuario(Usuario usuario) {
        System.out.println("\nID.................: " + usuario.getID());
        System.out.println("Nome...............: " + usuario.getNome());
        System.out.println("Email..............: " + usuario.getEmail());
        System.out.println("Pergunta secreta...: " + usuario.getPerguntaSecreta());
    }
}
