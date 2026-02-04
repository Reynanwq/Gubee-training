public class DatabaseServiceImpl implements DatabaseService {

    @Transaction
    @Override
    public void salvarUsuario(String nome) {
        System.out.println("  -> Salvando usuário: " + nome);
    }

    @Transaction
    @Override
    public void atualizarUsuario(String nome) {
        System.out.println("  -> Atualizando usuário: " + nome);
    }

    @Transaction
    @Override
    public void deletarUsuario(String nome) {
        System.out.println("  -> Deletando usuário: " + nome);
        if (nome.equals("admin")) {
            throw new RuntimeException("Não é possível deletar usuário admin");
        }
    }

    @Override
    public void consultarUsuario(String nome) {
        System.out.println("  -> Consultando usuário: " + nome);
    }
}