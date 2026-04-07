package arquivo;
import aed3.*;
import entidades.Usuario;

import java.util.ArrayList;

public class ArquivoUsuario extends Arquivo<Usuario> {
    
    HashExtensivel<ParCpfId> indiceCPF;
    ArvoreBMais<ParNomeId> indiceNome;

    public ArquivoUsuario() throws Exception {
        super("Usuario", Usuario.class.getConstructor());
        indiceEmail = new HashExtensivel<>(
            ParEmailId.class.getConstructor(),
            4, 
            "./dados/usuario/indiceCPF.d.db", 
            "./dados/usuario/indiceCPF.c.db");
        indiceNome = new ArvoreBMais<>(
            ParNomeId.class.getConstructor(),
            4,
           "./dados/usuario/indiceNome.db");
    }

    @Override
    public int create(Usuario p) throws Exception {
        int id = super.create(p);
        indiceCPF.create(new ParCpfId(p.getCpf(), id));
        indiceNome.create(new ParNomeId(p.getNome(), id));
        return id;
    }

    public Usuario readCPF(String cpf) throws Exception {
        ParCpfId pci = indiceCPF.read(Math.abs(cpf.hashCode()));
        if(pci == null)
            return null;
        Usuario p = read(pci.getId());
        return p;
    }

    public Usuario[] readNome(String nome) throws Exception {
        ArrayList<ParNomeId> pnis = indiceNome.read(new ParNomeId(nome,-1));  // O -1 indica que a comparação só deve verificar o nome (e não o ID)
        if(pnis.isEmpty())
            return new Usuario[0];

        Usuario[] Usuarios = new Usuario[pnis.size()];
        int i=0;
        for (ParNomeId pni : pnis) {
            Usuarios[i++] = super.read(pni.getId());            
        }
        return Usuarios;
    }

    public Usuario[] readAll() throws Exception {
        ArrayList<ParNomeId> pnis = indiceNome.read(null);  // null retorna todos os registros do índice
        if(pnis.isEmpty())
            return new Usuario[0];

        Usuario[] Usuarios = new Usuario[pnis.size()];
        int i=0;
        for (ParNomeId pni : pnis) {
            Usuarios[i++] = super.read(pni.getId());            
        }
        return Usuarios;
    }

    @Override
    public boolean delete(int id) throws Exception {
        Usuario p = read(id);
        if(p!=null)
            if(super.delete(id)) {
                indiceCPF.delete(Math.abs(p.getCpf().hashCode()));
                indiceNome.delete(new ParNomeId(p.getNome(), p.getID()));
                return true;
            }
        return false;
    }

    @Override
    public boolean update(Usuario novaUsuario) throws Exception {
        Usuario p = read(novaUsuario.getID());
        if(p==null)
            return false;
        if(super.update(novaUsuario)) {
            if(p.getCpf().compareTo(novaUsuario.getCpf())!=0) {
                indiceCPF.delete(Math.abs(p.getCpf().hashCode()));
                indiceCPF.create(new ParCpfId(novaUsuario.getCpf(), novaUsuario.getID()));
            }
            if(p.getNome().compareTo(novaUsuario.getNome())!=0) {
                indiceNome.delete(new ParNomeId(p.getNome(), p.getID()));
                indiceNome.create(new ParNomeId( novaUsuario.getNome(), novaUsuario.getID()));
            }
            return true;
        }
        return false;
    }


    public void close() throws Exception {
        super.close();
        indiceCPF.close();
        indiceNome.close();
    }
}
