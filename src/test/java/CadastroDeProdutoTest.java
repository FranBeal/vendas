import br.com.dao.CategoriaDao;
import br.com.dao.ProdutoDao;
import br.com.modelo.Categoria;
import br.com.modelo.Produto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class CadastroDeProdutoTest {
    private EntityManager em;
    private ProdutoDao produtoDao;
    private CategoriaDao categoriaDao;

    @BeforeEach
    public void setup() {
        // Configura a conexão com o banco de dados PostgreSQL
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PostgresPU"); // Verifique seu persistence.xml
        em = emf.createEntityManager();

        produtoDao = new ProdutoDao(em);
        categoriaDao = new CategoriaDao(em);

        // Popula o banco de dados com os dados necessários
        cadastrarProduto();
    }

    @Test
    public void deveCadastrarEConsultarProdutoPorId() {
        // Consulta o produto pelo ID
        Produto produto = produtoDao.buscarPorId(1L);
        assertNotNull(produto);
        assertEquals(new BigDecimal("800"), produto.getPreco());
        assertEquals("Xiaomi Redmi", produto.getNome());
    }

    @Test
    public void deveBuscarProdutosPorCategoria() {
        // Busca produtos pela categoria
        List<Produto> produtos = produtoDao.buscarPorNomeDaCategoriaComNamedQuery("CELULARES");
        assertNotNull(produtos);
        assertFalse(produtos.isEmpty());
        assertEquals(1, produtos.size());  // Verifica se há um produto cadastrado
        assertEquals("Xiaomi Redmi", produtos.get(0).getNome());
    }

    @Test
    public void deveBuscarPrecoDoProdutoPorNome() {
        // Busca o preço do produto pelo nome
        BigDecimal precoDoProduto = produtoDao.buscarPrecoDoProdutoComNome("Xiaomi Redmi");
        assertNotNull(precoDoProduto);
        assertEquals(new BigDecimal("800.00"), precoDoProduto);
    }

    private void cadastrarProduto() {
        // Popula o banco de dados real com dados para o teste
        Categoria celulares = new Categoria("CELULARES");
        Produto celular = new Produto("Xiaomi Redmi", "Muito legal", new BigDecimal("800"), celulares);

        em.getTransaction().begin();

        categoriaDao.cadastrar(celulares);
        produtoDao.cadastrar(celular);

        em.getTransaction().commit();
    }
}
