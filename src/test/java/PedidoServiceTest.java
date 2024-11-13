import br.com.dao.ClienteDao;
import br.com.dao.ProdutoDao;
import br.com.model.*;
import br.com.service.PedidoService;
import br.com.vo.RelatorioDeVendasVo;
import br.com.vo.RelatorioFinanceiroVo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PedidoServiceTest {
    private EntityManager em;
    private PedidoService pedidoService;
    private ProdutoDao produtoDao;
    private ClienteDao clienteDao;

    @BeforeEach
    public void setup() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PostgresPU");
        em = emf.createEntityManager();

        pedidoService = new PedidoService(em);

        popularBancoDeDados();
    }

    @AfterEach
    public void limparBanco() {
        em.getTransaction().begin();

        em.createQuery("delete from PedidoItem ip").executeUpdate();
        em.createQuery("delete from Pedido pd").executeUpdate();
        em.createQuery("delete from Produto p").executeUpdate();
        em.createQuery("delete from Categoria c").executeUpdate();
        //em.createQuery("delete from Cliente c").executeUpdate();

        em.getTransaction().commit();
    }

    @Test
    public void cadastrarPedidos() {
        List<Produto> produtos = em.createQuery("SELECT p FROM Produto p", Produto.class).getResultList();
        Produto produto1 = produtos.get(0);
        Produto produto2 = produtos.get(1);

        Cliente cliente = em.createQuery("SELECT c FROM Cliente c", Cliente.class).setMaxResults(1).getSingleResult();

        Pedido pedido = new Pedido(cliente);
        pedido.adicionarItem(new PedidoItem(10, pedido, produto1));
        pedido.adicionarItem(new PedidoItem(40, pedido, produto2));

        pedidoService.inserir(pedido);

        assertNotNull(em.find(Pedido.class, pedido.getId()));
    }

    @Test
    public void cadastrarProdutoComPrecoZero() {
        Categoria categoria = new Categoria("BRINDES");
        Produto produtoGratuito = new Produto("Caneta", "Caneta promocional", BigDecimal.ZERO, categoria);
        Cliente cliente = em.createQuery("SELECT c FROM Cliente c", Cliente.class).setMaxResults(1).getSingleResult();

        em.getTransaction().begin();
        em.persist(categoria);
        em.persist(produtoGratuito);
        em.getTransaction().commit();

        Pedido pedido = new Pedido(cliente);
        pedido.adicionarItem(new PedidoItem(5, pedido, produtoGratuito));
        pedidoService.inserir(pedido);

        assertEquals(BigDecimal.ZERO, pedido.getValorTotal());
    }

    @Test
    public void alterarQuantidadeDeItemPedido() {
        Produto produto = em.createQuery("SELECT p FROM Produto p", Produto.class).setMaxResults(1).getSingleResult();
        Cliente cliente = em.createQuery("SELECT c FROM Cliente c", Cliente.class).setMaxResults(1).getSingleResult();

        Pedido pedido = new Pedido(cliente);
        PedidoItem item = new PedidoItem(5, pedido, produto);
        pedido.adicionarItem(item);
        pedidoService.inserir(pedido);

        pedido.removerItem(item);
        item.setQuantidade(10);
        pedido.adicionarItem(item);
        pedidoService.alterar(pedido);

        Pedido pedidoAtualizado = em.find(Pedido.class, pedido.getId());
        PedidoItem itemAtualizado = pedidoAtualizado.getItens().stream()
                .filter(i -> i.getId().equals(item.getId()))
                .findFirst()
                .orElse(null);

        assertNotNull(itemAtualizado);
        assertEquals(10, itemAtualizado.getQuantidade());
    }

    @Test
    public void excluirItemPedidoERetornarValorTotalDoPedidoAtualizado() {
        List<Produto> produtos = em.createQuery("SELECT p FROM Produto p", Produto.class).getResultList();
        Produto produto1 = produtos.get(0);
        Produto produto2 = produtos.get(1);

        Cliente cliente = em.createQuery("SELECT c FROM Cliente c", Cliente.class).setMaxResults(1).getSingleResult();

        Pedido pedido = new Pedido(cliente);
        PedidoItem item1 = new PedidoItem(5, pedido, produto1);
        PedidoItem item2 = new PedidoItem(1, pedido, produto2);
        pedido.adicionarItem(item1);
        pedido.adicionarItem(item2);
        pedidoService.inserir(pedido);

        pedido.removerItem(item2);
        pedidoService.alterar(pedido);

        Pedido pedidoAtualizado = em.find(Pedido.class, pedido.getId());
        PedidoItem itemRemovido = pedidoAtualizado.getItens().stream()
                .filter(i -> i.getProduto().equals(item2))
                .findFirst()
                .orElse(null);

        assertNull(itemRemovido);
        assertEquals(new BigDecimal("4000"), pedidoAtualizado.getValorTotal());
    }

    @Test
    public void excluirPedido() {
        Produto produto1 = em.createQuery("SELECT p FROM Produto p", Produto.class).setMaxResults(1).getSingleResult();
        Cliente cliente = em.createQuery("SELECT c FROM Cliente c", Cliente.class).setMaxResults(1).getSingleResult();

        Pedido pedido = new Pedido(cliente);
        pedido.adicionarItem(new PedidoItem(10, pedido, produto1));
        pedidoService.inserir(pedido);

        pedidoService.excluir(pedido);

        Pedido pedidoExcluido = em.find(Pedido.class, pedido.getId());
        assertNull(pedidoExcluido);
    }

    @Test
    public void consultarPedidoPorId() {
        Produto produto1 = em.createQuery("SELECT p FROM Produto p", Produto.class).setMaxResults(1).getSingleResult();
        Cliente cliente = em.createQuery("SELECT c FROM Cliente c", Cliente.class).setMaxResults(1).getSingleResult();

        em.getTransaction().begin();

        Pedido pedido = new Pedido(cliente);
        pedido.adicionarItem(new PedidoItem(10, pedido, produto1));
        em.persist(pedido);

        em.getTransaction().commit();

        Pedido pedidoCadastrado = pedidoService.buscarPedidoPorId(pedido.getId());
        assertNotNull(pedidoCadastrado);
    }


    @Test
    public void consultarPedidosFiltradosPorPeriodo() {
        Produto produto = em.createQuery("SELECT p FROM Produto p", Produto.class).setMaxResults(1).getSingleResult();
        Cliente cliente = em.createQuery("SELECT c FROM Cliente c", Cliente.class).setMaxResults(1).getSingleResult();


        Pedido pedidoOntem = new Pedido(cliente);
        pedidoOntem.adicionarItem(new PedidoItem(1, pedidoOntem, produto));
        LocalDate ontem = LocalDate.now().minusDays(1);
        pedidoOntem.setData(ontem);
        pedidoService.inserir(pedidoOntem);

        Pedido pedidoHoje = new Pedido(cliente);
        pedidoHoje.adicionarItem(new PedidoItem(1, pedidoHoje, produto));
        pedidoHoje.setData(LocalDate.now());
        pedidoService.inserir(pedidoHoje);

        Pedido pedidoAmanha = new Pedido(cliente);
        pedidoAmanha.adicionarItem(new PedidoItem(1, pedidoAmanha, produto));
        LocalDate amanha = LocalDate.now().plusDays(1);
        pedidoAmanha.setData(amanha);
        pedidoService.inserir(pedidoAmanha);


        List<Pedido> pedidosPeriodo = pedidoService.buscarPedidoPorPeriodo(ontem, amanha);
        assertEquals(3, pedidosPeriodo.size());
        assertEquals(pedidoOntem, pedidosPeriodo.getFirst());
    }

    @Test
    public void consultarPedidosDeUmCliente() {
        List<Produto> produtos = em.createQuery("SELECT p FROM Produto p", Produto.class).getResultList();
        Produto produto1 = produtos.get(0);
        Produto produto2 = produtos.get(1);

        Cliente cliente = em.createQuery("SELECT c FROM Cliente c", Cliente.class).setMaxResults(1).getSingleResult();

        Pedido pedido1 = new Pedido(cliente);
        PedidoItem item1 = new PedidoItem(5, pedido1, produto1);
        PedidoItem item2 = new PedidoItem(1, pedido1, produto2);
        pedido1.adicionarItem(item1);
        pedido1.adicionarItem(item2);
        pedidoService.inserir(pedido1);

        Pedido pedido2 = new Pedido(cliente);
        PedidoItem item3 = new PedidoItem(2, pedido2, produto2);
        pedido2.adicionarItem(item3);
        pedidoService.inserir(pedido2);

        List<Pedido> pedidosCliente = pedidoService.buscarPedidoDeUmCliente(cliente.getId());
        assertEquals(2, pedidosCliente.size());
    }

    private void popularBancoDeDados() {
        Categoria celulares = new Categoria("CELULARES");
        Categoria videogames = new Categoria("VIDEOGAMES");
        Categoria informatica = new Categoria("INFORMATICA");
        Categoria utilitarios = new Categoria("UTILITARIOS");

        Produto celular = new Produto("Xiaomi Redmi", "O preferido", new BigDecimal("800"), celulares);
        Produto videogame = new Produto("PS5", "Playstation 5", new BigDecimal("8000"), videogames);
        Produto macbook = new Produto("Macbook", "Macbook pro", new BigDecimal("14000"), informatica);
        Produto mouse = new Produto("Mouse", "Mouse de computador", new BigDecimal("45"), utilitarios);
        Produto teclado = new Produto("Teclado", "Teclado de computador", new BigDecimal("130"), utilitarios);

        Cliente cliente = new Cliente("Fran", "123456");
        Cliente cliente2 = new Cliente("Celso", "987654");

        em.getTransaction().begin();

        em.persist(celulares);
        em.persist(videogames);
        em.persist(informatica);
        em.persist(utilitarios);
        em.persist(celular);
        em.persist(videogame);
        em.persist(macbook);
        em.persist(mouse);
        em.persist(teclado);
        em.persist(cliente);
        em.persist(cliente2);

        em.getTransaction().commit();
    }
}
