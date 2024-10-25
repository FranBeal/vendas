import br.com.dao.CategoriaDao;
import br.com.dao.ClienteDao;
import br.com.dao.PedidoDao;
import br.com.dao.ProdutoDao;
import br.com.modelo.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PerformanceConsultasTest {
    private EntityManager em;
    private PedidoDao pedidoDao;
    private ProdutoDao produtoDao;
    private CategoriaDao categoriaDao;
    private ClienteDao clienteDao;

    @BeforeEach
    public void setup() {
        // Configura a conexão com o banco de dados PostgreSQL
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PostgresPU"); // Verifique seu persistence.xml
        em = emf.createEntityManager();

        pedidoDao = new PedidoDao(em);
        produtoDao = new ProdutoDao(em);
        categoriaDao = new CategoriaDao(em);
        clienteDao = new ClienteDao(em);

        // Popula o banco de dados com os dados necessários para os testes
        popularBancoDeDados();
    }

    @Test
    public void deveBuscarPedidoComCliente() {
        // Testa a consulta de um pedido junto com o cliente
        Pedido pedido = pedidoDao.buscarPedidoComCliente(1L);
        assertNotNull(pedido);
        assertNotNull(pedido.getCliente());
        assertEquals("Rodrigo", pedido.getCliente().getNome());
    }

    private void popularBancoDeDados() {
        // Popula o banco de dados real com dados para o teste
        Categoria celulares = new Categoria("CELULARES");
        Categoria videogames = new Categoria("VIDEOGAMES");
        Categoria informatica = new Categoria("INFORMATICA");

        Produto celular = new Produto("Xiaomi Redmi", "Muito legal", new BigDecimal("800"), celulares);
        Produto videogame = new Produto("PS5", "Playstation 5", new BigDecimal("8000"), videogames);
        Produto macbook = new Produto("Macbook", "Macbook Pro retina", new BigDecimal("14000"), informatica);

        Cliente cliente = new Cliente("Rodrigo", "123456");

        Pedido pedido = new Pedido(cliente);
        pedido.adicionarItem(new ItemPedido(10, pedido, celular));
        pedido.adicionarItem(new ItemPedido(40, pedido, videogame));

        Pedido pedido2 = new Pedido(cliente);
        pedido2.adicionarItem(new ItemPedido(2, pedido2, macbook));

        em.getTransaction().begin();

        categoriaDao.cadastrar(celulares);
        categoriaDao.cadastrar(videogames);
        categoriaDao.cadastrar(informatica);

        produtoDao.cadastrar(celular);
        produtoDao.cadastrar(videogame);
        produtoDao.cadastrar(macbook);

        clienteDao.cadastrar(cliente);

        pedidoDao.cadastrar(pedido);
        pedidoDao.cadastrar(pedido2);

        em.getTransaction().commit();
    }
}
