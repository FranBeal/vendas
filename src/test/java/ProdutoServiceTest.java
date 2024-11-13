import br.com.dao.CategoriaDao;
import br.com.model.Categoria;
import br.com.model.Produto;
import br.com.service.ProdutoService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class ProdutoServiceTest {
    private EntityManager em;
    private ProdutoService produtoService;

    @BeforeEach
    public void setup() {
        // Configura a conexão com o banco de dados PostgreSQL
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PostgresPU"); // Verifique seu persistence.xml
        em = emf.createEntityManager();

        produtoService = new ProdutoService(em);
    }

    @AfterEach
    public void limparBanco() {
        em.getTransaction().begin();

        em.createQuery("delete from Produto p").executeUpdate();
        em.createQuery("delete from Categoria c").executeUpdate();

        em.getTransaction().commit();
    }

    @Test
    public void cadastrarProduto() {
        Categoria celulares = new Categoria("CELULARES");
        Produto celular = new Produto("Xiaomi Redmi", "O preferido", new BigDecimal("800"), celulares);

        em.getTransaction().begin();
        em.persist(celulares);
        em.getTransaction().commit();


        produtoService.inserir(celular);
        Produto prod = em.find(Produto.class, celular.getId());
        assertNotNull(prod);
    }

    @Test
    public void alterarProduto(){
        Categoria celulares = new Categoria("CELULARES");
        Produto celular = new Produto("Xiaomi Redmi", "O preferido", new BigDecimal("800"), celulares);

        em.getTransaction().begin();
        em.persist(celulares);
        em.persist(celular);
        em.getTransaction().commit();

        celular.setNome("Xiaomi Mi 9 SE");
        produtoService.alterar(celular);
        Produto prod = em.find(Produto.class, celular.getId());
        assertEquals("Xiaomi Mi 9 SE", prod.getNome());
    }

    @Test
    public void excluirProduto(){
        Categoria celulares = new Categoria("CELULARES");
        Produto celular = new Produto("Xiaomi Redmi", "O preferido", new BigDecimal("800"), celulares);

        em.getTransaction().begin();
        em.persist(celulares);
        em.persist(celular);
        em.getTransaction().commit();

        produtoService.excluir(celular);
        Produto prod = em.find(Produto.class, celular.getId());
        assertNull(prod);
    }

    @Test
    public void consultarProdutoPorId() {
        Categoria celulares = new Categoria("CELULARES");
        Produto celular = new Produto("Xiaomi Redmi", "O preferido", new BigDecimal("800"), celulares);

        em.getTransaction().begin();
        em.persist(celulares);
        em.persist(celular);
        em.getTransaction().commit();

        Produto produtoCadastrado = produtoService.buscarProdutoPorId(celular.getId());
        assertNotNull(produtoCadastrado);
    }

    @Test
    public void buscarProdutosPorNome() {
        Categoria celulares = new Categoria("CELULARES");
        Produto celular = new Produto("Xiaomi Redmi", "O preferido", new BigDecimal("800"), celulares);

        em.getTransaction().begin();
        em.persist(celulares);
        em.persist(celular);
        em.getTransaction().commit();

        List<Produto> produtos = produtoService.buscarProdutoPorNome("Xiaomi Redmi");
        assertFalse(produtos.isEmpty());
    }

    @Test
    public void buscarProdutosPorCategoria() {
        Categoria celulares = new Categoria("CELULARES");
        Produto celular = new Produto("Xiaomi Redmi", "O preferido", new BigDecimal("800"), celulares);

        em.getTransaction().begin();
        em.persist(celulares);
        em.persist(celular);
        em.getTransaction().commit();

        List<Produto> produtos = produtoService.buscarProdutosDaCategoria(celulares.getId());
        assertFalse(produtos.isEmpty());
    }


}
