package main.java.core;

public class Usuario {
    private Long id;
    private String nome;
    private String email;
    private Integer idade;

    public Usuario(Long id, String nome,  String email, Integer idade){
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.idade = idade;
    }

    public Long getId(){
        return id;
    }

    public String getNome(){
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public Integer getIdade() {
        return idade;
    }
}
