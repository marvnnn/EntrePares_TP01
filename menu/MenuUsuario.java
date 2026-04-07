package menu;
import arquivo.*;
import entidades.Curso;
import entidades.Usuario;

import java.io.File;
import java.time.LocalDate;
import java.util.Scanner;

public class MenuUsuario {

    ArquivoUsuario arqUsuario;
    ArquivoCurso arqCurso;
    Scanner console;

    public void menu() {

        try {
            console = new Scanner(System.in);
            arqUsuario = new ArquivoUsuario();
            arqCurso = new ArquivoCurso();

            int opcao;
            do {
                System.out.println("\n\nAEDs III");
                System.out.println(    "--------");
                System.out.println("\n> Início > Pessoas\n");
                System.out.println("1 - Inserir");
                System.out.println("2 - Buscar por Email");
                System.out.println("3 - Buscar por Nome");
                System.out.println("4 - Alterar");
                System.out.println("5 - Excluir");
                System.out.println("8 - Listagem");
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
                        buscarCPF();
                        break;
                    case 3: 
                        buscarNome();
                        break;
                    case 4: 
                        alterar();
                        break;
                    case 5: 
                        excluir();
                        break;
                    case 8:
                        listagem();
                        break;
                    case 9: 
                        popular();
                        break;
                    case 0: break;
                    default:
                        System.out.println("Opção inválida");
                }
            } while (opcao != 0);

            // fecha os arquivos
            arqUsuario.close();
            arqCurso.close();

        } catch(Exception e) {
            System.err.println("Não foi possível criar o menu de pessoas!");
            e.printStackTrace();
        }
    }

    private void inserir() throws Exception {
        String nome;
        String email;
        String senha;
        String pergunta;
        String resposta;
        boolean dadosValidos;                  
        System.out.println("INCLUSÃO");
        System.out.print("Nome: ");

        nome = console.nextLine();
        if(nome.length()==0)
            return;

        // email
        dadosValidos = false;
        do {
            System.out.print("Email: ");
            email = console.nextLine();
            if(email.length()==0)
                return;
    
            Usuario p1 = arqUsuario.readEmail(email); // confere se existe alguem com o email  -- criar o readEmail
            if(p1!=null)
                System.out.println("Email já cadastrado!");
            else
                dadosValidos = true;
        } while(dadosValidos != true);

       // senha
       dadosValidos = false;
       do { 
            System.out.println("Senha: ");
            senha = console.nextLine();
            if(senha.length() < 6)
                System.out.println("Senha deve conter pelo menos 6 caracteres");
            else 
                dadosValidos = true;
       } while (dadosValidos != true);

        // pergunta
       dadosValidos = false;
       do { 
            System.out.println("Pergunta Secreta: ");
            pergunta = console.nextLine();
            if(pergunta.length() < 6)
                System.out.println("Pergunta secreta deve conter pelo menos 6 caracteres");
            else 
                dadosValidos = true;
       } while (dadosValidos != true);

        // resposta
       dadosValidos = false;
       do { 
            System.out.println("Resposta: ");
            resposta = console.nextLine();
            if(resposta.length() < 6)
                System.out.println("Resposta deve conter pelo menos 6 caracteres");
            else 
                dadosValidos = true;
       } while (dadosValidos != true);

        System.out.print("Confirmar inclusão (S/N) ?");
        String confirma = console.nextLine();
        if(confirma.charAt(0)=='S' || confirma.charAt(0)=='s') {
            Usuario p = new Usuario(nome, email, senha.hashCode(), pergunta, resposta.hashCode());
            arqUsuario.create(p);
            System.out.println("Pessoa incluída!");
        }
    }

    private void buscarCPF() throws Exception {     
        System.out.println("BUSCA");
        System.out.print("CPF: ");
        String cpf = console.nextLine();
        if(cpf.length()==0)
            return;
        if(!cpf.matches("\\d{11}")) {
            System.out.println("CPF inválido!");
            return;
        }
        Usuario p = arqUsuario.readEmail(email);
        if(p!=null)
            mostraPessoa(p);
        else
            System.out.println("Pessoa não encontrada!");
    }

    private void buscarNome() throws Exception {     
        System.out.println("BUSCA");
        System.out.print("Nome: ");
        String nome = console.nextLine();
        if(nome.length()==0)
            return;
        Usuario[] pessoas = arqUsuario.readNome(nome);
        if(pessoas.length>0) {
            for(Usuario p : pessoas)
                mostraPessoa(p);
        }
        else
            System.out.println("Nenhuma pessoa encontrada!");
    }

    private void excluir() throws Exception {
        System.out.println("EXCLUSÃO");
        System.out.print("Email: ");
        String email = console.nextLine();
        if(email.length()==0)
            return;
        Usuario p = arqUsuario.readEmail(email);
        if(p!=null) {
            mostraPessoa(p);

            Curso[] cursos = arqCurso.readAutor(p.getID());
            if(cursos.length>0) {
                System.out.println("\nATENÇÃO: Esta pessoa é autora dos seguintes livros:");
                for(Curso l : cursos)
                    System.out.println(" - "+l.getNome() + "(Código: " + l.getCod() + ")");
                System.out.println("Exclusão não permitida!");
                return;
            }

            System.out.print("\nConfirma exclusão (S/N) ?");
            String confirma = console.nextLine();
            if(confirma.charAt(0)=='S' || confirma.charAt(0)=='s') {
                if(arqUsuario.delete(p.getID()))
                    System.out.println("Pessoa excluída!");
                else
                    System.out.println("Erro na exclusão!");
            }
        }
        else
            System.out.println("Pessoa não encontrada!");
    }

    private void alterar() throws Exception {
        System.out.println("ALTERAÇÃO");
        System.out.print("CPF: ");
        String cpf = console.nextLine();
        if(cpf.length()==0)
            return;
        if(!cpf.matches("\\d{11}")) {
            System.out.println("CPF inválido!");
            return;
        }
        Usuario p = arqUsuario.readCPF(cpf);

        if(p!=null) {
            mostraPessoa(p);

            System.out.println("\nAltere os dados a seguir. Deixe o campo em branco quando não quiser alterar.");
            String novoNome;
            String novoEmail;
            String novaPergunta;
            String novaSenha;
            String novaResposta;
            LocalDate novaDN;

            // Alteração do nome
            System.out.print("Nome: ");
            novoNome = console.nextLine();
            if(novoNome.length()>0)
                p.setNome(novoNome);

            // Alteração do email
            boolean dadosValidos = false;
            do {
                System.out.print("Email: ");
                novoEmail = console.nextLine();
                if(novoEmail.length()==0) {
                    dadosValidos = true;
                } else {
                    Usuario p1 = arqUsuario.readEmail(novoEmail);
                    if(p1!=null)
                        System.out.println("Email já cadastrado!");
                    else 
                        dadosValidos = true;
                }
            } while(!dadosValidos);
            if(novoEmail.length()>0)
                p.setEmail(novoEmail);

            // Alteração da pergunta
            dadosValidos = false;
            String aux = "";
            do {
                System.out.print("Data de nascimento (dd/mm/aaaa): ");
                aux = console.nextLine();
                if(aux.length()==0) {
                    dadosValidos = true;
                }
                else {
                    try {
                        String[] dadosData = aux.split("/");
                        novaDN = LocalDate.of(
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
                p.setDataNascimento(novaDN);


            System.out.print("\nConfirma alteração (S/N) ?");
            String confirma = console.nextLine();
            if(confirma.charAt(0)=='S' || confirma.charAt(0)=='s') {
                if(arqPessoas.update(p))
                    System.out.println("Pessoa atualizada!");
                else
                    System.out.println("Erro na alteração!");
            }
        }
        else
            System.out.println("Pessoa não encontrada!");
        
    }

    public void listagem() throws Exception {
        System.out.println("LISTAGEM");
        Pessoa[] pessoas = arqPessoas.readAll();
        if(pessoas.length>0) {
            for(Pessoa p : pessoas)
                mostraPessoa(p);
        }
        else
            System.out.println("Nenhuma pessoa cadastrada!");
    }

    public void mostraPessoa(Usuario p) {
        System.out.println( 
            "Nome....: " + p.getNome() +
            "\nEmail.....: " + p.getEmail() +
            "\nPergunta: " + p.getPergunta() + "\n"
        );
    }

    public  void popular() throws Exception {
        arqUsuario.close();
        arqUsuario = null;

        (new File("./dados/pessoa/dados.db")).delete();
        (new File("./dados/pessoa/indiceDireto.d.db")).delete();
        (new File("./dados/pessoa/indiceDireto.c.db")).delete();
        (new File("./dados/pessoa/indiceCPF.d.db")).delete();
        (new File("./dados/pessoa/indiceCPF.c.db")).delete();
        (new File("./dados/pessoa/indiceNome.db")).delete();


        arqPessoas = new ArquivoUsuario();

        arqPessoas.create(new Pessoa("Johann Hari", "11111111111", LocalDate.of(1979, 1, 21)));
        arqPessoas.create(new Pessoa( "Brian Traci", "22222222222", LocalDate.of(1944, 1, 5)));
        arqPessoas.create(new Pessoa( "James Clear", "33333333333", LocalDate.of(1986, 1, 22)));
        arqPessoas.create(new Pessoa( "Morgan Housel", "44444444444", LocalDate.of(1986, 7, 20)));
    }

    
}
