package test.java.core;

import main.java.core.Usuario;
import main.java.core.UsuarioRepository;
import main.java.core.UsuarioService;
import org.junit.Test;

import static org.junit.Assert.*;

public class UsuarioServiceTest {

    @Test
    public void deveCadastrarUsuarioComNomeValido(){
        UsuarioRepository repo = new UsuarioRepositoryInMemory();
        UsuarioService service = new UsuarioService(repo);

        Usuario usuario = new Usuario(1L, "Reynan");

        service.cadastrar(usuario);
        Usuario salvo = repo.buscarPorId(1L);
        assertNotNull(salvo);
        assertEquals("Reynan", salvo.getNome());
    }

    @Test
    public void naoDeveCadastrarUsuarioComNomeVazio() {
        UsuarioRepository repo = new UsuarioRepositoryInMemory();
        UsuarioService service = new UsuarioService(repo);

        Usuario usuario = new Usuario(1L, "");

        assertThrows(IllegalArgumentException.class,
                () -> service.cadastrar(usuario));
    }

    @Test
    public void naoDeveCadastrarUsuarioNulo(){
        UsuarioRepository repo = new UsuarioRepositoryInMemory();
        UsuarioService service = new UsuarioService(repo);

        assertThrows(IllegalArgumentException.class,
                () -> service.cadastrar(null));
    }

}
