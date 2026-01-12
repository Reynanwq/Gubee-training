package main.java.core;

public class Usuario {
    private Long id;
    private String nome;

    public Usuario(Long id, String nome){
        this.id = id;
        this.nome = nome;
    }

    public Long getId(){
        return id;
    }
    public String getNome(){
        return nome;
    }
}
