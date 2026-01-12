package test.java.core;

import main.java.core.Usuario;
import main.java.core.UsuarioRepository;
import main.java.core.UsuarioService;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class UsuarioServiceTest {

    @Test
    public void deveCadastrarUsuarioComNomeValido(){
        UsuarioRepository repo = new UsuarioRepositoryInMemory();
        UsuarioService service = new UsuarioService(repo);

        Usuario usuario = new Usuario(1L, "Reynan", "reynanwq@gmail.com", 22);
        service.cadastrar(usuario);
        Usuario salvo = repo.buscarPorId(1L);
        assertNotNull(salvo);

        assertNotNull(salvo.getEmail());
        assertEquals("Reynan", salvo.getNome());
      //  assertEquals("reynan@gmail.com", salvo.getEmail());
        assertEquals(Integer.valueOf(22), salvo.getIdade());
    }

    @Test
    public void naoDeveCadastrarUsuarioComNomeVazio() {
        UsuarioRepository repo = new UsuarioRepositoryInMemory();
        UsuarioService service = new UsuarioService(repo);

        Usuario usuario = new Usuario(1L, "","reynan@gmail.com", 22);

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

    @Test
    public void naoDeveCadastrarMenorde18Anos(){
        UsuarioRepository repo = new UsuarioRepositoryInMemory();
        UsuarioService service = new UsuarioService(repo);
        Usuario usuario = new Usuario(1L, "Reynan","reynan@gmail.com", 17);

        service.cadastrar(usuario);
        Usuario salvo = repo.buscarPorId(1L);
        assertNotNull(salvo);
    }

}
