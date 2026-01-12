package main.java.core;

public class UsuarioService {

    private UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository){
        this.repository = repository;
    }

    public Usuario buscarPorId(Long id){
        return repository.buscarPorId(id);
    }

    public void cadastrar(Usuario usuario){
        if(usuario == null){
            throw new IllegalArgumentException("Usuário não pode ser nulo");
        }

        if(usuario.getEmail() == null || usuario.getEmail().isBlank() ){
            throw new IllegalArgumentException("O email não pode ser nulo");
        }

        if(usuario.getNome() == null || usuario.getNome().isBlank()){
            throw new IllegalArgumentException("Nome Inválido");
        }

        if (usuario.getIdade() < 18) {
            throw new IllegalArgumentException("O usuário deve ser maior de idade");
        }

        repository.salvar(usuario);
    }

    public Usuario bucarPorEmail(String email){
        return repository.buscarPorEmail(email);
    }
}
