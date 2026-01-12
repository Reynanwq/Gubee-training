package main.java.core;

public interface UsuarioRepository {
    void salvar(Usuario usuario);
    Usuario buscarPorId(Long id);
}
