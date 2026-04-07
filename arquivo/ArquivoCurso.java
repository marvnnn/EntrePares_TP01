package arquivo;
import aed3.*;
import entidades.Curso;

import java.util.ArrayList;

public class ArquivoCurso extends Arquivo<Curso> {
    
    HashExtensivel<ParIsbnId> indiceIsbn;
    ArvoreBMais<ParNomeId> indiceTitulo;
    ArvoreBMais<ParIdId> indiceAutor;

    public ArquivoCurso() throws Exception {
        super("livro", Curso.class.getConstructor());
        indiceIsbn = new HashExtensivel<>(
            ParIsbnId.class.getConstructor(),
            4, 
            "./dados/livro/indiceISBN.d.db", 
            "./dados/livro/indiceISBN.c.db");
        indiceTitulo = new ArvoreBMais<>(
            ParNomeId.class.getConstructor(),
            4,
           "./dados/livro/indiceTitulo.db");
        indiceAutor = new ArvoreBMais<>(
            ParIdId.class.getConstructor(),
            4,
            "./dados/livro/indiceAutor.db");

    }

    @Override
    public int create(Curso p) throws Exception {
        int id = super.create(p);
        indiceIsbn.create(new ParIsbnId(p.getIsbn(), id));
        indiceTitulo.create(new ParNomeId(p.getTitulo(), id));
        indiceAutor.create(new ParIdId(p.getIdAutor(), id));
        return id;
    }

    public Curso readIsbn(String Isbn) throws Exception {
        ParIsbnId pci = indiceIsbn.read(Math.abs(Isbn.hashCode()));
        if(pci == null)
            return null;
        Curso p = read(pci.getId());
        return p;
    }

    public Curso[] readTitulo(String titulo) throws Exception {
        ArrayList<ParNomeId> pnis = indiceTitulo.read(new ParNomeId(titulo,-1));  // O -1 indica que a comparação só deve verificar o titulo (e não o ID)
        if(pnis.isEmpty())
            return new Curso[0];

        Curso[] Livros = new Curso[pnis.size()];
        int i=0;
        for (ParNomeId pni : pnis) {
            Livros[i++] = super.read(pni.getId());            
        }
        return Livros;
    }

    public Curso[] readAutor(int idAutor) throws Exception {
        ArrayList<ParIdId> piis = indiceAutor.read(new ParIdId(idAutor, -1));  // O -1 indica que a comparação só deve verificar o idAutor (e não o ID do livro)
        if(piis.isEmpty())
            return new Curso[0];

        Curso[] Livros = new Curso[piis.size()];
        int i=0;
        for (ParIdId pii : piis) {
            Livros[i++] = super.read(pii.getId2());            
        }
        return Livros;
    }

    public Curso[] readAll() throws Exception {
        ArrayList<ParNomeId> pnis = indiceTitulo.read(null);  // null retorna todos os registros do índice
        if(pnis.isEmpty())
            return new Curso[0];

        Curso[] Livros = new Curso[pnis.size()];
        int i=0;
        for (ParNomeId pni : pnis) {
            Livros[i++] = super.read(pni.getId());            
        }
        return Livros;
    }

    @Override
    public boolean delete(int id) throws Exception {
        Curso p = read(id);
        if(p!=null)
            if(super.delete(id)) {
                indiceIsbn.delete(Math.abs(p.getIsbn().hashCode()));
                indiceTitulo.delete(new ParNomeId(p.getTitulo(), p.getID()));
                indiceAutor.delete(new ParIdId(p.getIdAutor(), p.getID()));
                return true;
            }
        return false;
    }

    @Override
    public boolean update(Curso novoLivro) throws Exception {
        Curso p = read(novoLivro.getID());
        if(p==null)
            return false;
        if(super.update(novoLivro)) {
            if(p.getIsbn().compareTo(novoLivro.getIsbn())!=0) {
                indiceIsbn.delete(Math.abs(p.getIsbn().hashCode()));
                indiceIsbn.create(new ParIsbnId(novoLivro.getIsbn(), novoLivro.getID()));
            }
            if(p.getTitulo().compareTo(novoLivro.getTitulo())!=0) {
                indiceTitulo.delete(new ParNomeId(p.getTitulo(), p.getID()));
                indiceTitulo.create(new ParNomeId( novoLivro.getTitulo(), novoLivro.getID()));
            }
            if(p.getIdAutor() != novoLivro.getIdAutor()) {
                indiceAutor.delete(new ParIdId(p.getIdAutor(), p.getID()));
                indiceAutor.create(new ParIdId(novoLivro.getIdAutor(), novoLivro.getID()));
            }
            return true;
        }
        return false;
    }


    public void close() throws Exception {
        super.close();
        indiceIsbn.close();
        indiceTitulo.close();
        indiceAutor.close();
    }
}
