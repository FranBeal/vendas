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
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CriteriaTest {
    private EntityManager em;
    private ProdutoDao produtoDao;
    private CategoriaDao categoriaDao;

    @BeforeEach
    public void setup() {
        // Configura a conexão com o banco de dados PostgreSQL
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PostgresPU");
        em = emf.createEntityManager();

        produtoDao = new ProdutoDao(em);
        categoriaDao = new CategoriaDao(em);

        // Popula o banco de dados com os dados necessários
        popularBancoDeDados();
    }

    @Test
    public void deveBuscarProdutosPorParametrosComCriteria() {
        // Testa a consulta usando a API Criteria
        List<Produto> produtos = produtoDao.buscarPorParametrosComCriteria(null, null, LocalDate.now());
        assertNotNull(produtos);
        assertFalse(produtos.isEmpty());

        // Verifica se os produtos cadastrados são retornados
        assertEquals(3, produtos.size());  // 3 produtos foram cadastrados
        produtos.forEach(p -> System.out.println(p.getNome()));
    }

    private void popularBancoDeDados() {
        // Popula o banco de dados real com dados para o teste
        Categoria celulares = new Categoria("CELULARES");
        Categoria videogames = new Categoria("VIDEOGAMES");
        Categoria informatica = new Categoria("INFORMATICA");

        Produto celular = new Produto("Xiaomi Redmi", "Muito legal", new BigDecimal("800"), celulares);
        Produto videogame = new Produto("PS5", "Playstation 5", new BigDecimal("8000"), videogames);
        Produto macbook = new Produto("Macbook", "Macbook pro retina", new BigDecimal("14000"), informatica);

        em.getTransaction().begin();

        categoriaDao.cadastrar(celulares);
        categoriaDao.cadastrar(videogames);
        categoriaDao.cadastrar(informatica);

        produtoDao.cadastrar(celular);
        produtoDao.cadastrar(videogame);
        produtoDao.cadastrar(macbook);

        em.getTransaction().commit();
    }
}
