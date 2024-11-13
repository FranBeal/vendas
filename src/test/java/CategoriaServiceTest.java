import br.com.model.Categoria;
import br.com.service.CategoriaService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CategoriaServiceTest {
    private EntityManager em;
    private CategoriaService categoriaService;

    @BeforeEach
    public void setup() {
        // Configura a conexão com o banco de dados PostgreSQL
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PostgresPU"); // Verifique seu persistence.xml
        em = emf.createEntityManager();
        categoriaService = new CategoriaService(em);
    }

    @AfterEach
    public void limparBanco() {
        em.getTransaction().begin();
        em.createQuery("delete from Categoria c").executeUpdate();
        em.getTransaction().commit();
    }

    @Test
    public void cadastrarCategoria() {
        Categoria celulares = new Categoria("CELULARES");

        categoriaService.inserir(celulares);
        Categoria categ = em.find(Categoria.class, celulares.getId());
        assertNotNull(categ);
    }

    @Test
    public void alterarCategoria(){
        Categoria celulares = new Categoria("CELULARES");
        em.getTransaction().begin();
        em.persist(celulares);
        em.getTransaction().commit();

        celulares.setNome("smartphones");
        categoriaService.alterar(celulares);
        Categoria prod = em.find(Categoria.class, celulares.getId());
        assertEquals("smartphones", prod.getNome());
    }

    @Test
    public void excluirCategoria(){
        Categoria celulares = new Categoria("CELULARES");
        em.getTransaction().begin();
        em.persist(celulares);
        em.getTransaction().commit();

        categoriaService.excluir(celulares);
        Categoria prod = em.find(Categoria.class, celulares.getId());
        assertNull(prod);
    }

    @Test
    public void consultarCategoriaPorId() {
        Categoria celulares = new Categoria("CELULARES");
        em.getTransaction().begin();
        em.persist(celulares);
        em.getTransaction().commit();

        Categoria categoria = categoriaService.buscarCategoriaPorId(celulares.getId());
        assertNotNull(celulares);
        assertEquals("CELULARES", categoria.getNome());
    }

    @Test
    public void buscarTodasAsCategorias() {
        Categoria celulares = new Categoria("CELULARES");
        Categoria teclados = new Categoria("TECLADOS");
        Categoria mouses = new Categoria("MOUSES");
        em.getTransaction().begin();
        em.persist(celulares);
        em.persist(teclados);
        em.persist(mouses);
        em.getTransaction().commit();


        List<Categoria> categoria = categoriaService.buscarTodosAsCategorias();
        assertFalse(categoria.isEmpty());
        assertEquals(3, categoria.size());
    }
}
