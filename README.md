# 📚 Trabalho Prático – AEDs III

## 👥 Participantes
- Arthur Campos Pereira
- Felipe Barros Silva
- Mateus Martins Parreiras

---

## 🧾 Descrição do Sistema

O sistema desenvolvido tem como objetivo realizar o gerenciamento de usuários e cursos, permitindo operações completas de cadastro, consulta, atualização e exclusão (CRUD).

A aplicação utiliza estruturas de dados como Tabelas Hash Extensíveis e Árvores B+, garantindo maior eficiência nas buscas e organização dos dados.

Os usuários podem possuir vários cursos associados, formando um relacionamento do tipo 1:N (um usuário para vários cursos).

---

## 🖥️ Telas do Sistema

### 🔹 Cadastro de Usuário
![Cadastro de Usuário](./assets/cadastro_usuario.png)

### 🔹 Cadastro de Curso
![Cadastro de Curso](./assets/novo_curso.png)

### 🔹 Busca de Curso
![Busca de Curso](./assets/busca_curso.png)

### 🔹 Busca de Usuário
![Busca de Usuário](./assets/busca_usuario.png)

---

## 🧱 Estrutura e Classes

Principais classes implementadas:

- `ArquivoIndexado`  
  Classe base responsável pelo gerenciamento dos arquivos com índices.

- `Usuario`  
  Representa os usuários do sistema.

- `Curso`  
  Representa os cursos cadastrados e vinculados aos usuários.

- Estruturas auxiliares:
  - Tabela Hash Extensível (índice direto)
  - Árvore B+ (índices indiretos e relacionamento)

---

## ⚙️ Operações Especiais

- Índices diretos com **Hash Extensível**
- Índices indiretos com **Árvore B+**
- Relacionamento 1:N com par `(idUsuario, idCurso)`
- Busca por:
  - ID
  - Nome
  - Email
- Código compartilhável para cursos

---

## ✅ Checklist

**1) Há um CRUD de usuários que funciona corretamente?**  
Sim. O sistema possui cadastro, leitura, alteração e exclusão de usuários, com índice direto e índice por email/nome.

**2) Há um CRUD de cursos que funciona corretamente?**  
Sim. O sistema possui cadastro, leitura, alteração e exclusão de cursos, com índice por código, nome e vínculo com usuário.

**3) Os cursos estão vinculados aos usuários usando o idUsuario como chave estrangeira?**  
Sim. Cada curso armazena o idUsuario do seu dono.

**4) Há uma árvore B+ que registre o relacionamento 1:N entre usuários e cursos?**  
Sim. Implementado com o par (idUsuario, idCurso).

**5) Há um CRUD de usuários com ArquivoIndexado e índices?**  
Sim.

**6) O trabalho compila corretamente?**  
Sim.

**7) O trabalho está completo e funcionando sem erros de execução?**  
Sim.

**8) O trabalho é original?**  
Sim.

---

## 🎥 Vídeo de Demonstração

🔗 https://www.youtube.com/watch?v=mtJ9vNV7I7o
