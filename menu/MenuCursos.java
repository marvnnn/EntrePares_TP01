package menu;
import java.io.File;
import java.time.LocalDate;
import java.util.Scanner;

import arquivo.*;
import entidades.Pessoa;
import entidades.Curso;

public class MenuCursos {

    ArquivoCurso arqLivros;
    ArquivoUsuario arqPessoas;
    Scanner console;

    public void menu() {

        try {
            console = new Scanner(System.in);
            arqLivros = new ArquivoCurso();
            arqPessoas = new ArquivoUsuario();

            int opcao;
            do {
                System.out.println("\n\nEntrePares");
                System.out.println(    "--------");
                System.out.println("\n> Início > Cursos\n");
                System.out.println("1 - Inserir");
                System.out.println("2 - Buscar por Código");
                System.out.println("3 - Buscar por Nome");
                System.out.println("4 - Alterar");
                System.out.println("5 - Excluir");
                System.out.println("6 - Listagem");
                System.out.println("7 - Popular BD");
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
                        buscarIsbn();
                        break;
                    case 3: 
                        buscarTitulo();
                        break;
                    case 4: 
                        buscarAutor();
                        break;
                    case 5: 
                        alterar();
                        break;
                    case 6: 
                        excluir();
                        break;
                    case 8:
                        listagem();
                        break;
                    case 0: break;
                    default:
                        System.out.println("Opção inválida");
                }
            } while (opcao != 0);

            // fechar arquivos
            arqLivros.close();
            arqPessoas.close();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void inserir() throws Exception {
        String isbn;
        String titulo;
        String cpfAutor;
        byte edicao;
        LocalDate dataPublicacao;
        float preco;  
        boolean dadosValidos;                  
        System.out.println("INCLUSÃO");

        // ISBN
        dadosValidos = false;
        do {
            System.out.print("ISBN: ");
            isbn = console.nextLine();
            if(isbn.length()==0)
                return;
            if(!isbn.matches("\\d{13}")) {
                System.out.println("ISBN inválido!");
            } else {
                Curso l1 = arqLivros.readIsbn(isbn);
                if(l1!=null)
                    System.out.println("ISBN já cadastrado!");
                else if(isbn.length()==13)
                    dadosValidos = true;
            }
        } while(!dadosValidos);

        // Título
        System.out.print("Título: ");
        titulo = console.nextLine();
        if(titulo.length()==0)
            return;

        // Autor 
        dadosValidos = false;
        Pessoa autor = null;
        int idAutor = -1;
        do {
            System.out.print("CPF do autor: ");
            cpfAutor = console.nextLine();
            if(cpfAutor.length()==0)
                return;
            if(!cpfAutor.matches("\\d{11}")) {
                System.out.println("CPF inválido!");
            } else {
                autor = arqPessoas.readCPF(cpfAutor);
                if(autor==null)
                    System.out.println("Autor não encontrado!");
                else {
                    System.out.println("Autor: " + autor.getNome());
                    idAutor = autor.getID();
                    dadosValidos = true;
                }
            }
        } while(!dadosValidos);

        // Edição
        dadosValidos = false;
        edicao = (byte)0;
        do {
            System.out.print("Edição: ");
            String r = console.nextLine();
            try {
                edicao = Byte.parseByte(r);
                if(edicao>0)
                    dadosValidos = true;
                else
                    System.out.println("Edição precisa ser maior que 1!");
            } catch(Exception e) {
                System.out.println("Edição inválida!");
            }
        } while(!dadosValidos);

        // Data de publicação
        dadosValidos = false;
        dataPublicacao = LocalDate.now();
        do {
            System.out.print("Data de publicação (dd/mm/aaaa): ");
            String data = console.nextLine();
            try {
                String[] dadosData = data.split("/");
                dataPublicacao = LocalDate.of(
                    Integer.parseInt(dadosData[2]),
                    Integer.parseInt(dadosData[1]),
                    Integer.parseInt(dadosData[0]));
                dadosValidos = true;
            } catch(Exception e) {
                System.out.println("Data inválida!");
            }
        } while(!dadosValidos);

        // Preço
        dadosValidos = false;
        preco = 0F;
        do {
            System.out.print("Preço: R$ ");
            String p = console.nextLine();
            try {
                preco = Float.parseFloat(p);
                dadosValidos = true;
            } catch(Exception e) {
                System.out.println("Valor inválido!");
            }
        } while(!dadosValidos);

        System.out.print("Confirmar inclusão (S/N) ?");
        String confirma = console.nextLine();
        if(confirma.charAt(0)=='S' || confirma.charAt(0)=='s') {
            Curso l = new Curso(isbn, titulo, idAutor, edicao, dataPublicacao, preco);
            arqLivros.create(l);
            System.out.println("Livro incluído!");
        }
    }

    private void buscarIsbn() throws Exception {     
        System.out.println("BUSCA POR ISBN");
        System.out.print("ISBN: ");
        String isbn = console.nextLine();
        if(isbn.length()==0)
            return;
        if(!isbn.matches("\\d{13}")) {
            System.out.println("ISBN inválido!");
            return;
        }
        Curso l = arqLivros.readIsbn(isbn);
        if(l!=null)
            mostraLivro(l);
        else
            System.out.println("Livro não encontrado!");
    }

    private void buscarTitulo() throws Exception {     
        System.out.println("BUSCA POR TÍTULO");
        System.out.print("Título: ");
        String nome = console.nextLine();
        if(nome.length()==0)
            return;
        Curso[] livros = arqLivros.readTitulo(nome);
        if(livros.length>0) {
            for(Curso l : livros)
                mostraLivro(l);
        }
        else
            System.out.println("Nenhum livro encontrado!");
    }

    private void buscarAutor() throws Exception {     
        System.out.println("BUSCA POR AUTOR");
        System.out.print("CPF do autor: ");
        String cpfAutor = console.nextLine();
        if(cpfAutor.length()==0)
            return;
        if(!cpfAutor.matches("\\d{11}")) {
            System.out.println("CPF inválido!");
            return;
        }
        Pessoa autor = arqPessoas.readCPF(cpfAutor);
        if(autor==null) {
            System.out.println("Autor não encontrado!");
            return;
        }
        System.out.println("Autor: " + autor.getNome()+"\n");
        Curso[] livros = arqLivros.readAutor(autor.getID());
        if(livros.length>0) {
            for(Curso l : livros)
                mostraLivro(l);
        }
        else
            System.out.println("Nenhum livro encontrado para esse autor!");
    }

    private void excluir() throws Exception {
        System.out.println("EXCLUSÃO");
        System.out.print("ISBN: ");
        String isbn = console.nextLine();
        if(isbn.length()==0)
            return;
        if(!isbn.matches("\\d{13}")) {
            System.out.println("ISBN inválido!");
            return;
        }
        Curso l = arqLivros.readIsbn(isbn);

        if(l!=null) {
            mostraLivro(l);
            System.out.print("\nConfirma exclusão (S/N) ?");
            String confirma = console.nextLine();
            if(confirma.charAt(0)=='S' || confirma.charAt(0)=='s') {
                if(arqLivros.delete(l.getID()))
                    System.out.println("Livro excluído!");
                else
                    System.out.println("Erro na exclusão!");
            }
        }
        else
            System.out.println("Livro não encontrado!");
    }

    private void alterar() throws Exception {
        System.out.println("ALTERAÇÃO");
        System.out.print("ISBN: ");
        String isbn = console.nextLine();
        if(isbn.length()==0)
            return;
        if(!isbn.matches("\\d{13}")) {
            System.out.println("ISBN inválido!");
            return;
        }
        Curso l = arqLivros.readIsbn(isbn);

        if(l!=null) {
            mostraLivro(l);

            System.out.println("\nAltere os dados a seguir. Deixe o campo em branco quando não quiser alterar.");
            String novoIsbn;
            String novoTitulo;
            String novoCpfAutor;
            byte novaEdicao;
            LocalDate novaDataPublicacao;
            float novoPreco;

            // Alteração do ISBN
            boolean dadosValidos = false;
            do {
                System.out.print("ISBN: ");
                novoIsbn = console.nextLine();
                if(novoIsbn.length()==0) {
                    dadosValidos = true;
                } else {
                    if(!novoIsbn.matches("\\d{13}")) {
                        System.out.println("ISBN inválido!");
                    } else {
                        Curso l1 = arqLivros.readIsbn(novoIsbn);
                        if(l1!=null)
                            System.out.println("ISBN já cadastrado!");
                        else 
                            dadosValidos = true;
                    }
                }
            } while(!dadosValidos);
            if(novoIsbn.length()>0)
                l.setIsbn(novoIsbn);

            // Alteração do título
            System.out.print("Título: ");
            novoTitulo = console.nextLine();
            if(novoTitulo.length()>0)
                l.setTitulo(novoTitulo);

            // Alteração do autor
            dadosValidos = false;
            Pessoa autor = null;
            int idAutor = -1;
            do {
                System.out.print("CPF do autor: ");
                novoCpfAutor = console.nextLine();
                if(novoCpfAutor.length()==0) {
                    dadosValidos = true;
                } else {
                    if(!novoCpfAutor.matches("\\d{11}")) {
                        System.out.println("CPF inválido!");
                    } else {
                        autor = arqPessoas.readCPF(novoCpfAutor);
                        if(autor==null)
                            System.out.println("Autor não encontrado!");
                        else {
                            System.out.println("Autor: " + autor.getNome());
                            idAutor = autor.getID();
                            dadosValidos = true;
                        }
                    }
                }
            } while(!dadosValidos);
            if(novoCpfAutor.length()>0)
                l.setAutor(idAutor);

            // Alteração da edição
            dadosValidos = false;
            novaEdicao = (byte)0;
            String aux = "";
            do {
                System.out.print("Edição: ");
                aux = console.nextLine();
                if(aux.length()==0) {
                    dadosValidos = true;
                } else {
                    try {
                        novaEdicao = Byte.parseByte(aux);
                        dadosValidos = true;
                    } catch(Exception e) {
                        System.out.println("Valor inválido!");
                    }
                }
            } while(!dadosValidos);
            if(aux.length()>0)
                l.setEdicao(novaEdicao);

            // Alteração da data de publicação
            dadosValidos = false;
            novaDataPublicacao = LocalDate.now();
            aux = "";
            do {
                System.out.print("Data de publicação (dd/mm/aaaa): ");
                aux = console.nextLine();
                if(aux.length()==0) {
                    dadosValidos = true;
                }
                else {
                    try {
                        String[] dadosData = aux.split("/");
                        novaDataPublicacao = LocalDate.of(
                            Integer.parseInt(dadosData[2]),
                            Integer.parseInt(dadosData[1]),
                            Integer.parseInt(dadosData[0]));
                        dadosValidos = true;
                    } catch(Exception e) {
                        System.out.println("Data inválida!");
                    }
                }
            } while(!dadosValidos);
            if(aux.length()>0)
                l.setDataPublicacao(novaDataPublicacao);

            // Alteração do preço
            dadosValidos = false;
            novoPreco = 0F;
            aux = "";
            do {
                System.out.print("Preço: R$ ");
                aux = console.nextLine();
                if(aux.length()==0) {
                    dadosValidos = true;
                } else {
                    try {
                        novoPreco = Float.parseFloat(aux);
                        dadosValidos = true;
                    } catch(Exception e) {
                        System.out.println("Valor inválido!");
                    }
                }
            } while(!dadosValidos);
            if(aux.length()>0)
                l.setPreco(novoPreco);

            System.out.print("\nConfirma alteração (S/N) ?");
            String confirma = console.nextLine();
            if(confirma.charAt(0)=='S' || confirma.charAt(0)=='s') {
                if(arqLivros.update(l))
                    System.out.println("Livro atualizado!");
                else
                    System.out.println("Erro na alteração!");
            }
        }
        else
            System.out.println("Livro não encontrado!");
        
    }

    public void listagem() throws Exception {
        System.out.println("LISTAGEM DE LIVROS");
        Curso[] livros = arqLivros.readAll();
        if(livros.length>0) {
            for(Curso l : livros)
                mostraLivro(l);
        }
        else
            System.out.println("Nenhum livro cadastrado!");
    }


    public void mostraCurso(Curso l) throws Exception {
        Usuario usu = arqUsuario.read(l.getIdAutor());
        String nomeAutor;
        if(autor!=null)
            nomeAutor = autor.getNome() + " (CPF: " + autor.getCpf() + ")";
        else
            nomeAutor = "Autor não encontrado!";
        System.out.println(
            "Código......: " + l.getCod() +
            "\nNome....: " + l.getNome() +
            "\nDescrição.....: " + l.getDesc() +
            "\nData de Início....: " + l.getData() +
            "\nEstado.: " + l.getEstado()
        );
    }

}
