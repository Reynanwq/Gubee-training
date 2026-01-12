package test.java.core;

import main.java.core.Usuario;
import main.java.core.UsuarioRepository;

import java.util.HashMap;
import java.util.Map;

public class UsuarioRepositoryInMemory implements UsuarioRepository {
    private Map<Long, Usuario> banco = new HashMap<>();

    @Override
    public void salvar(Usuario usuario){
        banco.put(usuario.getId(), usuario);
    }

    @Override
    public Usuario buscarPorId(Long id){
        return banco.get(id);
    }
}
