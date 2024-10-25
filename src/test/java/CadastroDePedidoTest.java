import br.com.dao.ClienteDao;
import br.com.dao.PedidoDao;
import br.com.dao.ProdutoDao;
import br.com.modelo.*;
import br.com.vo.RelatorioDeVendasVo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CadastroDePedidoTest {
    private EntityManager em;
    private ProdutoDao produtoDao;
    private ClienteDao clienteDao;
    private PedidoDao pedidoDao;

    @BeforeEach
    public void setup() {
        // Configura a conexão com o banco de dados PostgreSQL
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PostgresPU"); // Verifique seu persistence.xml
        em = emf.createEntityManager();

        produtoDao = new ProdutoDao(em);
        clienteDao = new ClienteDao(em);
        pedidoDao = new PedidoDao(em);

        // Popula o banco de dados com os dados necessários
        popularBancoDeDados();
    }

    @Test
    public void deveCadastrarPedidosERetornarValorTotalVendido() {
        // Recupera os produtos e cliente do banco de dados
        Produto produto1 = produtoDao.buscarPorId(1L);
        Produto produto2 = produtoDao.buscarPorId(2L);
        Produto produto3 = produtoDao.buscarPorId(3L);
        Cliente cliente = clienteDao.buscarPorId(1L);

        // Cria e cadastra pedidos
        em.getTransaction().begin();

        Pedido pedido1 = new Pedido(cliente);
        pedido1.adicionarItem(new ItemPedido(10, pedido1, produto1));
        pedido1.adicionarItem(new ItemPedido(40, pedido1, produto2));

        Pedido pedido2 = new Pedido(cliente);
        pedido2.adicionarItem(new ItemPedido(2, pedido2, produto3));

        pedidoDao.cadastrar(pedido1);
        pedidoDao.cadastrar(pedido2);

        em.getTransaction().commit();

        // Verifica o valor total vendido
        BigDecimal totalVendido = pedidoDao.valorTotalVendido();
        assertEquals(new BigDecimal("356000.00"), totalVendido);

        // Verifica o relatório de vendas
        List<RelatorioDeVendasVo> relatorio = pedidoDao.relatorioDeVendas();
        assertNotNull(relatorio);
        assertFalse(relatorio.isEmpty());

        // Exibe o relatório para verificar manualmente (opcional)
        relatorio.forEach(System.out::println);
    }

    private void popularBancoDeDados() {
        // Popula o banco de dados real com dados para o teste
        Categoria celulares = new Categoria("CELULARES");
        Categoria videogames = new Categoria("VIDEOGAMES");
        Categoria informatica = new Categoria("INFORMATICA");

        Produto celular = new Produto("Xiaomi Redmi", "Muito legal", new BigDecimal("800"), celulares);
        Produto videogame = new Produto("PS5", "Playstation 5", new BigDecimal("8000"), videogames);
        Produto macbook = new Produto("Macbook", "Macboo pro retina", new BigDecimal("14000"), informatica);

        Cliente cliente = new Cliente("Rodrigo", "123456");

        em.getTransaction().begin();

        em.persist(celulares);
        em.persist(videogames);
        em.persist(informatica);
        em.persist(celular);
        em.persist(videogame);
        em.persist(macbook);
        em.persist(cliente);

        em.getTransaction().commit();
    }
}
