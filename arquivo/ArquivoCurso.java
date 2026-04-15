package arquivo;

import aed3.*;
import entidades.Curso;

import java.util.ArrayList;

public class ArquivoCurso extends Arquivo<Curso> {

    HashExtensivel<ParCodigoId> indiceCodigo;
    ArvoreBMais<ParNomeId> indiceNome;
    ArvoreBMais<ParIdUsuarioId> indiceUsuario;

    public ArquivoCurso() throws Exception {
        super("curso", Curso.class.getConstructor());
        indiceCodigo = new HashExtensivel<>(
            ParCodigoId.class.getConstructor(),
            4,
            "./dados/curso/indiceCodigo.d.db",
            "./dados/curso/indiceCodigo.c.db");
        indiceNome = new ArvoreBMais<>(
            ParNomeId.class.getConstructor(),
            4,
            "./dados/curso/indiceNome.db");
        indiceUsuario = new ArvoreBMais<>(
            ParIdUsuarioId.class.getConstructor(),
            4,
            "./dados/curso/indiceUsuario.db");
    }

    @Override
    public int create(Curso curso) throws Exception {
        int id = super.create(curso);
        indiceCodigo.create(new ParCodigoId(curso.getCodigoCompartilhavel(), id));
        indiceNome.create(new ParNomeId(curso.getNome(), id));
        indiceUsuario.create(new ParIdUsuarioId(curso.getIdUsuario(), id));
        return id;
    }

    public Curso readCodigo(String codigo) throws Exception {
        ParCodigoId pci = indiceCodigo.read(Math.abs(codigo.hashCode()));
        if (pci == null)
            return null;
        return read(pci.getId());
    }

    public Curso[] readNome(String nome) throws Exception {
        ArrayList<ParNomeId> pnis = indiceNome.read(new ParNomeId(nome, -1));
        if (pnis.isEmpty())
            return new Curso[0];

        Curso[] cursos = new Curso[pnis.size()];
        int i = 0;
        for (ParNomeId pni : pnis) {
            cursos[i++] = super.read(pni.getId());
        }
        return cursos;
    }

    /**
     * Retorna todos os cursos de um usuário específico, ordenados por nome.
     * Usa a árvore B+ para busca eficiente.
     */
    public Curso[] readPorUsuario(int idUsuario) throws Exception {
        ArrayList<ParIdUsuarioId> piuis = indiceUsuario.read(new ParIdUsuarioId(idUsuario, -1));
        if (piuis.isEmpty())
            return new Curso[0];

        Curso[] cursos = new Curso[piuis.size()];
        int i = 0;
        for (ParIdUsuarioId piui : piuis) {
            cursos[i++] = super.read(piui.getIdCurso());
        }
        return cursos;
    }

    public Curso[] readAll() throws Exception {
        ArrayList<ParNomeId> pnis = indiceNome.read(null);
        if (pnis.isEmpty())
            return new Curso[0];

        Curso[] cursos = new Curso[pnis.size()];
        int i = 0;
        for (ParNomeId pni : pnis) {
            cursos[i++] = super.read(pni.getId());
        }
        return cursos;
    }

    @Override
    public boolean delete(int id) throws Exception {
        Curso curso = read(id);
        if (curso != null) {
            if (super.delete(id)) {
                indiceCodigo.delete(Math.abs(curso.getCodigoCompartilhavel().hashCode()));
                indiceNome.delete(new ParNomeId(curso.getNome(), curso.getID()));
                indiceUsuario.delete(new ParIdUsuarioId(curso.getIdUsuario(), curso.getID()));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean update(Curso novoCurso) throws Exception {
        Curso curso = read(novoCurso.getID());
        if (curso == null)
            return false;
        if (super.update(novoCurso)) {
            if (!curso.getCodigoCompartilhavel().equals(novoCurso.getCodigoCompartilhavel())) {
                indiceCodigo.delete(Math.abs(curso.getCodigoCompartilhavel().hashCode()));
                indiceCodigo.create(new ParCodigoId(novoCurso.getCodigoCompartilhavel(), novoCurso.getID()));
            }
            if (!curso.getNome().equals(novoCurso.getNome())) {
                indiceNome.delete(new ParNomeId(curso.getNome(), curso.getID()));
                indiceNome.create(new ParNomeId(novoCurso.getNome(), novoCurso.getID()));
            }
            if (curso.getIdUsuario() != novoCurso.getIdUsuario()) {
                indiceUsuario.delete(new ParIdUsuarioId(curso.getIdUsuario(), curso.getID()));
                indiceUsuario.create(new ParIdUsuarioId(novoCurso.getIdUsuario(), novoCurso.getID()));
            }
            return true;
        }
        return false;
    }

    public void close() throws Exception {
        super.close();
        indiceCodigo.close();
        indiceNome.close();
        indiceUsuario.close();
    }
}
