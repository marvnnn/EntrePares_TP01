package entidades;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.time.LocalDate;

import aed3.InterfaceEntidade;

public class Curso implements InterfaceEntidade {
    
    private int id;
    private int idUsuario;
    private String nome;
    private LocalDate dataInicio;
    private String desc;
    private String codComp;
    private short estado;

    public Curso() {
        this(-1, -1, "", LocalDate.now(), "", "", (short)-1);
    }

    public Curso(int i, String n, LocalDate d, String de, String c, short e) {
        this(-1, i, n, d, de, c, e);
    }

    public Curso(int i, int iu, String n, LocalDate d, String de, String c, short e) {
        id = i;
        idUsuario = iu;
        nome = n;
        dataInicio = d;
        desc = de;
        codComp = c;
        estado = e;
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getData() {
        return dataInicio;
    }

    public void setData(LocalDate d) {
        this.dataInicio = d;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCod() {
        return codComp;
    }

    public void setCod(String cod) {
        this.codComp = cod;
    }

    public short getEstado() {
        return estado;
    }

    public void setEstado(short n) {
        this.estado = n;
    }

    @Override
    public String toString() {
        return   "ID........: " + id + 
               "\nNome......: " + nome +
               "\nDataInicio....: " + dataInicio +
               "\nDescrição.....: " + desc +
               "\nCód.Comp....: " + codComp +
               "\nEstado.: " + estado;
    }
    
    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(id);
        dos.writeInt(idUsuario);
        dos.writeUTF(nome);
        dos.writeInt((int)dataInicio.toEpochDay());
        dos.writeUTF(desc);
        dos.writeUTF(codComp);
        dos.writeShort(estado);
        return baos.toByteArray();
    }

    public void fromByteArray(byte[] vb) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(vb);
        DataInputStream dis = new DataInputStream(bais);
        id = dis.readInt();
        idUsuario = dis.readInt();
        nome = dis.readUTF();
        dataInicio = LocalDate.ofEpochDay(dis.readInt());
        desc = dis.readUTF();
        codComp = dis.readUTF();
        estado = dis.readShort();
    }

}
