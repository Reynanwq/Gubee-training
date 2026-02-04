public interface DatabaseService {
    void salvarUsuario(String nome);
    void atualizarUsuario(String nome);
    void deletarUsuario(String nome);
    void consultarUsuario(String nome);
}