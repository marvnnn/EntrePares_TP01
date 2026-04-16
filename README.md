Relatório


1) Há um CRUD de usuários (que estende a classe ArquivoIndexado, acrescentando Tabelas Hash Extensíveis e Árvores B+ como índices diretos e indiretos conforme necessidade) que funciona corretamente?

Sim. O sistema possui cadastro, leitura, alteração e exclusão de usuários, com índice direto e índice por email/nome usando as estruturas pedidas.



2) Há um CRUD de cursos (que estende a classe ArquivoIndexado, acrescentando Tabelas Hash Extensíveis e Árvores B+ como índices diretos e indiretos conforme necessidade) que funciona corretamente?

Sim. O sistema possui cadastro, leitura, alteração e exclusão de cursos, com índice por código compartilhável, nome e vínculo com usuário.



3) Os cursos estão vinculados aos usuários usando o idUsuario como chave estrangeira?

Sim. Cada curso armazena o idUsuario do seu dono.



4) Há uma árvore B+ que registre o relacionamento 1:N entre usuários e cursos?

Sim. O relacionamento foi implementado com o par (idUsuario, idCurso) em árvore B+.



5) Há um CRUD de usuários (que estende a classe ArquivoIndexado, acrescentando Tabelas Hash Extensíveis e Árvores B+ como índices diretos e indiretos conforme necessidade)?

Sim.



6) O trabalho compila corretamente?

Sim. A versão atual do projeto compila corretamente.



7) O trabalho está completo e funcionando sem erros de execução?

Sim. As funcionalidades do TP1 estão implementadas e funcionando.



8) O trabalho é original e não a cópia de um trabalho de outro grupo?

Sim. O código foi produzido pelo meu grupo.


Link do vídeo: https://www.youtube.com/watch?v=mtJ9vNV7I7o
