package menu;

import arquivo.*;
import entidades.Curso;
import entidades.Usuario;
import visao.VisaoUsuario;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class MenuCursos {

    ArquivoCurso arqCurso;
    ArquivoUsuario arqUsuario;
    Scanner console;

    // Usuário atualmente logado no sistema
    private static Usuario usuarioAtivo = null;

    public MenuCursos() {}

    public MenuCursos(ArquivoCurso arqCurso, ArquivoUsuario arqUsuario, Scanner console) {
        this.arqCurso = arqCurso;
        this.arqUsuario = arqUsuario;
        this.console = console;
    }

    public static void setUsuarioAtivo(Usuario usuario) {
        usuarioAtivo = usuario;
    }

    public static Usuario getUsuarioAtivo() {
        return usuarioAtivo;
    }

    public void menu() {
        try {
            console = new Scanner(System.in);
            arqCurso = new ArquivoCurso();
            arqUsuario = new ArquivoUsuario();

            int opcao;
            do {
                System.out.println("\n\nEntrePares");
                System.out.println("--------");
                System.out.println("\n> Gestão de Cursos\n");
                if (usuarioAtivo != null) {
                    System.out.println("Usuário ativo: " + usuarioAtivo.getNome() + " (" + usuarioAtivo.getEmail() + ")\n");
                } else {
                    System.out.println("⚠️  Nenhum usuário ativo! Vincule um usuário ao criar um curso.\n");
                }
                System.out.println("1 - Inserir Curso");
                System.out.println("2 - Buscar por Código");
                System.out.println("3 - Buscar por Nome");
                System.out.println("4 - Listar Meus Cursos");
                System.out.println("5 - Alterar Curso");
                System.out.println("6 - Excluir Curso");
                System.out.println("7 - Listar Todos os Cursos");
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
                        buscarPorCodigo();
                        break;
                    case 3:
                        buscarPorNome();
                        break;
                    case 4:
                        listarMeusCursos();
                        break;
                    case 5:
                        alterar();
                        break;
                    case 6:
                        excluir();
                        break;
                    case 7:
                        listagem();
                        break;
                    case 0: break;
                    default:
                        System.out.println("Opção inválida");
                }
            } while (opcao != 0);

            arqCurso.close();
            arqUsuario.close();

        } catch(Exception e) {
            System.err.println("Erro no menu de cursos!");
            e.printStackTrace();
        }
    }

    private void inserir() throws Exception {
        System.out.println("\n=== INCLUSÃO DE CURSO ===");

        // Verifica se há usuário ativo
        if (usuarioAtivo == null) {
            System.out.println("⚠️  Nenhum usuário ativo! Você precisa vincular um curso a um usuário.");
            System.out.print("Email do usuário proprietário: ");
            String email = console.nextLine();
            if (email.isEmpty()) return;

            usuarioAtivo = arqUsuario.readEmail(email);
            if (usuarioAtivo == null) {
                System.out.println("Usuário não encontrado!");
                return;
            }
            System.out.println("Usuário ativo definido: " + usuarioAtivo.getNome());
        }

        System.out.print("Nome do curso: ");
        String nome = console.nextLine();
        if (nome.isEmpty()) return;

        System.out.print("Descrição: ");
        String descricao = console.nextLine();
        if (descricao.isEmpty()) return;

        // Data de início
        LocalDate dataInicio = LocalDate.now();
        boolean dadosValidos = false;
        do {
            System.out.print("Data de início (dd/mm/aaaa) [Enter para hoje]: ");
            String data = console.nextLine();
            if (data.isEmpty()) {
                dadosValidos = true;
            } else {
                try {
                    String[] dadosData = data.split("/");
                    dataInicio = LocalDate.of(
                        Integer.parseInt(dadosData[2]),
                        Integer.parseInt(dadosData[1]),
                        Integer.parseInt(dadosData[0]));
                    dadosValidos = true;
                } catch (Exception e) {
                    System.out.println("Data inválida!");
                }
            }
        } while (!dadosValidos);

        // Estado do curso
        System.out.println("\nEstado do curso:");
        System.out.println("0 - Ativo e recebendo inscrições");
        System.out.println("1 - Ativo, mas sem novas inscrições");
        System.out.println("2 - Curso concluído");
        System.out.println("3 - Curso cancelado");
        short estado = Curso.ATIVO_INSCRICOES;
        dadosValidos = false;
        do {
            System.out.print("Estado (0-3) [Enter para 0]: ");
            String estadoStr = console.nextLine();
            if (estadoStr.isEmpty()) {
                dadosValidos = true;
            } else {
                try {
                    estado = Short.parseShort(estadoStr);
                    if (estado >= 0 && estado <= 3) {
                        dadosValidos = true;
                    } else {
                        System.out.println("Estado deve ser entre 0 e 3!");
                    }
                } catch (Exception e) {
                    System.out.println("Estado inválido!");
                }
            }
        } while (!dadosValidos);

        // Gera código compartilhável único
        String codigoCompartilhavel = gerarCodigoCompartilhavel();

        Curso curso = new Curso(usuarioAtivo.getID(), nome, dataInicio, descricao, codigoCompartilhavel, estado);

        int id = arqCurso.create(curso);
        System.out.println("Curso incluído com ID: " + id);
        System.out.println("Código compartilhável: " + codigoCompartilhavel);
    }

    private void buscarPorCodigo() throws Exception {
        System.out.println("\n=== BUSCA POR CÓDIGO ===");
        System.out.print("Código: ");
        String codigo = console.nextLine();
        if (codigo.isEmpty()) return;

        Curso curso = arqCurso.readCodigo(codigo);
        if (curso != null) {
            mostraCurso(curso);
        } else {
            System.out.println("Curso não encontrado!");
        }
    }

    private void buscarPorNome() throws Exception {
        System.out.println("\n=== BUSCA POR NOME ===");
        System.out.print("Nome: ");
        String nome = console.nextLine();
        if (nome.isEmpty()) return;

        Curso[] cursos = arqCurso.readNome(nome);
        if (cursos.length > 0) {
            System.out.println("\n=== CURSOS ENCONTRADOS ===");
            for (Curso c : cursos) {
                mostraCurso(c);
                System.out.println();
            }
        } else {
            System.out.println("Nenhum curso encontrado!");
        }
    }

    private void listarMeusCursos() throws Exception {
        System.out.println("\n=== MEUS CURSOS ===");

        if (usuarioAtivo == null) {
            System.out.println("Nenhum usuário ativo! Faça login primeiro.");
            return;
        }

        Curso[] cursos = arqCurso.readPorUsuario(usuarioAtivo.getID());
        if (cursos.length > 0) {
            System.out.println("Cursos de " + usuarioAtivo.getNome() + ":");
            for (Curso c : cursos) {
                mostraCurso(c);
                System.out.println();
            }
        } else {
            System.out.println("Você não possui cursos cadastrados!");
        }
    }

    private void alterar() throws Exception {
        System.out.println("\n=== ALTERAÇÃO DE CURSO ===");
        System.out.print("Código do curso: ");
        String codigo = console.nextLine();
        if (codigo.isEmpty()) return;

        Curso curso = arqCurso.readCodigo(codigo);
        if (curso == null) {
            System.out.println("Curso não encontrado!");
            return;
        }

        // Verifica se o usuário pode alterar este curso
        if (usuarioAtivo != null && curso.getIdUsuario() != usuarioAtivo.getID()) {
            System.out.println("Você só pode alterar seus próprios cursos!");
            return;
        }

        mostraCurso(curso);
        System.out.println("\nDeixe em branco para manter o valor atual.\n");

        // Nome
        System.out.print("Nome (" + curso.getNome() + "): ");
        String nome = console.nextLine();
        if (!nome.isEmpty()) curso.setNome(nome);

        // Descrição
        System.out.print("Descrição (" + curso.getDescricao() + "): ");
        String descricao = console.nextLine();
        if (!descricao.isEmpty()) curso.setDescricao(descricao);

        // Data de início
        System.out.print("Data de início (dd/mm/aaaa) [" + curso.getDataInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "]: ");
        String data = console.nextLine();
        if (!data.isEmpty()) {
            try {
                String[] dadosData = data.split("/");
                curso.setDataInicio(LocalDate.of(
                    Integer.parseInt(dadosData[2]),
                    Integer.parseInt(dadosData[1]),
                    Integer.parseInt(dadosData[0])));
            } catch (Exception e) {
                System.out.println("Data inválida, mantendo anterior.");
            }
        }

        // Estado
        System.out.println("Estado atual: " + curso.getEstadoDescricao());
        System.out.println("Novo estado (0-3) [Enter para manter]:");
        System.out.println("0 - Ativo e recebendo inscrições");
        System.out.println("1 - Ativo, mas sem novas inscrições");
        System.out.println("2 - Curso concluído");
        System.out.println("3 - Curso cancelado");
        String estadoStr = console.nextLine();
        if (!estadoStr.isEmpty()) {
            try {
                short novoEstado = Short.parseShort(estadoStr);
                if (novoEstado >= 0 && novoEstado <= 3) {
                    curso.setEstado(novoEstado);
                } else {
                    System.out.println("Estado inválido, mantendo anterior.");
                }
            } catch (Exception e) {
                System.out.println("Estado inválido, mantendo anterior.");
            }
        }

        if (arqCurso.update(curso)) {
            System.out.println("Curso atualizado!");
        } else {
            System.out.println("Erro na atualização!");
        }
    }

    private void excluir() throws Exception {
        System.out.println("\n=== EXCLUSÃO DE CURSO ===");
        System.out.print("Código do curso: ");
        String codigo = console.nextLine();
        if (codigo.isEmpty()) return;

        Curso curso = arqCurso.readCodigo(codigo);
        if (curso == null) {
            System.out.println("Curso não encontrado!");
            return;
        }

        // Verifica se o usuário pode excluir este curso
        if (usuarioAtivo != null && curso.getIdUsuario() != usuarioAtivo.getID()) {
            System.out.println("Você só pode excluir seus próprios cursos!");
            return;
        }

        mostraCurso(curso);

        if (arqCurso.delete(curso.getID())) {
            System.out.println("Curso excluído!");
        } else {
            System.out.println("Erro na exclusão!");
        }
    }

    private void listagem() throws Exception {
        System.out.println("\n=== LISTAGEM DE TODOS OS CURSOS ===");
        Curso[] cursos = arqCurso.readAll();
        if (cursos.length > 0) {
            for (Curso c : cursos) {
                mostraCurso(c);
                System.out.println();
            }
        } else {
            System.out.println("Nenhum curso cadastrado!");
        }
    }

    private void mostraCurso(Curso curso) throws Exception {
        Usuario dono = arqUsuario.read(curso.getIdUsuario());
        String nomeDono = dono != null ? dono.getNome() : "Usuário não encontrado";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        System.out.println("\n=== CURSO ===");
        System.out.println("ID............: " + curso.getID());
        System.out.println("Código........: " + curso.getCodigoCompartilhavel());
        System.out.println("Nome..........: " + curso.getNome());
        System.out.println("Descrição.....: " + curso.getDescricao());
        System.out.println("Data de Início: " + curso.getDataInicio().format(formatter));
        System.out.println("Estado........: " + curso.getEstadoDescricao());
        System.out.println("Proprietário..: " + nomeDono + " (ID: " + curso.getIdUsuario() + ")");
    }

    private String gerarCodigoCompartilhavel() {
        // Gera um código único de 8 caracteres
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder codigo = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            codigo.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return codigo.toString();
    }
}
