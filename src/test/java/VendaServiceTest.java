import br.com.dao.*;
import br.com.model.*;
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

public class VendaServiceTest {
    private EntityManager em;
    private VendaDAO vendaDAO;

    @BeforeEach
    public void setup() {
        // Configura a conexão com o banco de dados PostgreSQL
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PostgresPU"); // Verifique seu persistence.xml
        em = emf.createEntityManager();

        vendaDAO = new VendaDAO(em);

        // Popula o banco de dados com os dados necessários para os testes
        popularBancoDeDados();
    }

    @AfterEach
    public void limparBanco() {
        em.getTransaction().begin();

        em.createQuery("delete from PedidoItem ip").executeUpdate();
        em.createQuery("delete from Pedido pd").executeUpdate();
        em.createQuery("delete from Produto p").executeUpdate();
        em.createQuery("delete from Categoria c").executeUpdate();

        em.getTransaction().commit();
    }

    @Test
    public void retornarValorTotalVendido() {
        BigDecimal totalVendido = vendaDAO.retornaValorTotalVendidoEmUmPeriodo(LocalDate.now(), LocalDate.now());
        assertEquals(new BigDecimal("356000.00"), totalVendido);
    }

    @Test
    public void retornarRelatorioDeVendas() {

        List<RelatorioDeVendasVo> relatorio = vendaDAO.relatorioDeVendas();
        assertNotNull(relatorio);
        assertFalse(relatorio.isEmpty());
    }

    @Test
    public void retornarRelatorioFinanceiro() {
        List<RelatorioFinanceiroVo> relatorio = vendaDAO.relatorioFinanceiro();
        assertNotNull(relatorio);
        assertFalse(relatorio.isEmpty());
    }

    private void popularBancoDeDados() {
        // Popula o banco de dados real com dados para o teste
        Categoria celulares = new Categoria("CELULARES");
        Categoria videogames = new Categoria("VIDEOGAMES");
        Categoria informatica = new Categoria("INFORMATICA");

        Produto celular = new Produto("Xiaomi Redmi", "O preferido", new BigDecimal("800"), celulares);
        Produto videogame = new Produto("PS5", "Playstation 5", new BigDecimal("8000"), videogames);
        Produto macbook = new Produto("Macbook", "Macbook Pro", new BigDecimal("14000"), informatica);

        Cliente cliente = new Cliente("Franciele", "123456");

        Pedido pedido = new Pedido(cliente);
        pedido.adicionarItem(new PedidoItem(10, pedido, celular));
        pedido.adicionarItem(new PedidoItem(40, pedido, videogame));

        Pedido pedido2 = new Pedido(cliente);
        pedido2.adicionarItem(new PedidoItem(2, pedido2, macbook));

        em.getTransaction().begin();

        em.persist(celulares);
        em.persist(videogames);
        em.persist(informatica);

        em.persist(celular);
        em.persist(videogame);
        em.persist(macbook);

        em.persist(cliente);

        em.persist(pedido);
        em.persist(pedido2);

        em.getTransaction().commit();
    }
}
