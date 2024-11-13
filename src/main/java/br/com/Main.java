package br.com;

import br.com.model.*;
import br.com.service.CategoriaService;
import br.com.service.PedidoService;
import br.com.service.ProdutoService;
import br.com.service.VendaService;
import br.com.vo.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static EntityManager em;

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PostgresPU");
        em = emf.createEntityManager();

        CategoriaService categoriaService = new CategoriaService(em);
        ProdutoService produtoService = new ProdutoService(em);
        PedidoService pedidoService = new PedidoService(em);
        VendaService vendaService =  new VendaService(em);

        boolean continuar = true;

        while (continuar) {
            System.out.println("----- MENU -----");
            System.out.println("1. Cadastrar Categoria");
            System.out.println("2. Alterar Categoria");
            System.out.println("3. Excluir Categoria");
            System.out.println("4. Consultar Categoria por ID");
            System.out.println("5. Listar todas as Categorias");
            System.out.println("6. Cadastrar Produto");
            System.out.println("7. Alterar Produto");
            System.out.println("8. Excluir Produto");
            System.out.println("9. Consultar Produto por ID");
            System.out.println("10. Consultar Produtos por Nome");
            System.out.println("11. Consultar Produtos por Categoria");
            System.out.println("12. Cadastrar Pedido");
            System.out.println("13. Consultar Pedido por ID");
            System.out.println("14. Consultar Pedidos por Período");
            System.out.println("15. Consultar Pedidos de um Cliente");
            System.out.println("16. Alterar a quantidade de um item do pedido");
            System.out.println("17. Excluir um item do pedido");
            System.out.println("18. Consultar Valor Total vendido");
            System.out.println("19. Consultar Relatório de Vendas");
            System.out.println("20. Consultar Relatório Financeiro");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");

            int opcao = scanner.nextInt();
            scanner.nextLine(); // Limpar o buffer

            switch (opcao) {
                case 1 -> cadastrarCategoria(categoriaService);
                case 2 -> alterarCategoria(categoriaService);
                case 3 -> excluirCategoria(categoriaService);
                case 4 -> consultarCategoriaPorId(categoriaService);
                case 5 -> listarCategorias(categoriaService);
                case 6 -> cadastrarProduto(produtoService, categoriaService);
                case 7 -> alterarProduto(produtoService);
                case 8 -> excluirProduto(produtoService);
                case 9 -> consultarProdutoPorId(produtoService);
                case 10 -> consultarProdutosPorNome(produtoService);
                case 11 -> consultarProdutosPorCategoria(produtoService, categoriaService);
                case 12 -> cadastrarPedido(pedidoService, produtoService);
                case 13 -> consultarPedidoPorId(pedidoService);
                case 14 -> consultarPedidosPorPeriodo(pedidoService);
                case 15 -> consultarPedidosDeCliente(pedidoService);
                case 16 -> alterarQuantidadeItemPedido(pedidoService);
                case 17 -> excluirItemPedido(pedidoService);
                case 18 -> consultarValorTotalVendido(vendaService);
                case 19 -> consultarRelatorioVendas(vendaService);
                case 20 -> consultarRelatorioFinanceiro(vendaService);
                case 0 -> continuar = false;
                default -> System.out.println("Opção inválida!");
            }
        }

        em.close();
        emf.close();
        System.out.println("Programa encerrado.");

    }

    private static void cadastrarCategoria(CategoriaService categoriaService) {
        System.out.print("Digite o nome da categoria: ");
        String nome = scanner.nextLine();
        Categoria categoria = new Categoria(nome);
        categoriaService.inserir(categoria);
        System.out.println("Categoria cadastrada com sucesso!");
    }

    private static void alterarCategoria(CategoriaService categoriaService) {
        System.out.print("Digite o ID da categoria a ser alterada: ");
        Long id = scanner.nextLong();
        scanner.nextLine();
        Categoria categoria = categoriaService.buscarCategoriaPorId(id);
        if (categoria != null) {
            System.out.print("Digite o novo nome da categoria: ");
            categoria.setNome(scanner.nextLine());
            categoriaService.alterar(categoria);
            System.out.println("Categoria alterada com sucesso!");
        } else {
            System.out.println("Categoria não encontrada.");
        }
    }

    private static void excluirCategoria(CategoriaService categoriaService) {
        System.out.print("Digite o ID da categoria a ser excluída: ");
        Long id = scanner.nextLong();
        scanner.nextLine();
        Categoria categoria = categoriaService.buscarCategoriaPorId(id);
        if (categoria != null) {
            categoriaService.excluir(categoria);
            System.out.println("Categoria excluída com sucesso!");
        } else {
            System.out.println("Categoria não encontrada.");
        }
    }

    private static void consultarCategoriaPorId(CategoriaService categoriaService) {
        System.out.print("Digite o ID da categoria: ");
        Long id = scanner.nextLong();
        scanner.nextLine();
        Categoria categoria = categoriaService.buscarCategoriaPorId(id);
        System.out.println(categoria != null ? categoria.toString() : "Categoria não encontrada.");
    }

    private static void listarCategorias(CategoriaService categoriaService) {
        List<Categoria> categorias = categoriaService.buscarTodosAsCategorias();
        categorias.forEach(System.out::println);
    }

    private static void cadastrarProduto(ProdutoService produtoService, CategoriaService categoriaService) {
        System.out.print("Digite o nome do produto: ");
        String nome = scanner.nextLine();
        System.out.print("Digite a descrição do produto: ");
        String descricao = scanner.nextLine();
        System.out.print("Digite o preço do produto: ");
        BigDecimal preco = scanner.nextBigDecimal();
        scanner.nextLine();
        System.out.print("Digite o ID da categoria: ");
        Long categoriaId = scanner.nextLong();
        scanner.nextLine();
        Categoria categoria = categoriaService.buscarCategoriaPorId(categoriaId);

        if (categoria != null) {
            Produto produto = new Produto(nome, descricao, preco, categoria);
            produtoService.inserir(produto);
            System.out.println("Produto cadastrado com sucesso!");
        } else {
            System.out.println("Categoria não encontrada.");
        }
    }

    private static void alterarProduto(ProdutoService produtoService) {
        System.out.print("Digite o ID do produto a ser alterado: ");
        Long id = scanner.nextLong();
        scanner.nextLine();
        Produto produto = produtoService.buscarProdutoPorId(id);
        if (produto != null) {
            System.out.print("Digite o novo nome do produto: ");
            produto.setNome(scanner.nextLine());
            System.out.print("Digite a nova descrição do produto: ");
            produto.setDescricao(scanner.nextLine());
            System.out.print("Digite o novo preço do produto: ");
            produto.setPreco(scanner.nextBigDecimal());
            scanner.nextLine(); // consumir nova linha
            produtoService.alterar(produto);
            System.out.println("Produto alterado com sucesso!");
        } else {
            System.out.println("Produto não encontrado.");
        }
    }

    private static void excluirProduto(ProdutoService produtoService) {
        System.out.print("Digite o ID do produto a ser alterado: ");
        Long id = scanner.nextLong();
        scanner.nextLine();
        Produto produto = produtoService.buscarProdutoPorId(id);
        if (produto != null) {
            produtoService.excluir(produto);
            System.out.println("Produto excluído com sucesso!");
        } else {
            System.out.println("Produto não encontrado.");
        }
    }

    private static void consultarProdutoPorId(ProdutoService produtoService) {
        System.out.print("Digite o ID do produto: ");
        Long id = scanner.nextLong();
        scanner.nextLine();
        Produto produto = produtoService.buscarProdutoPorId(id);
        if (produto != null) {
            System.out.println("Produto encontrado: " + produto);
        } else {
            System.out.println("Produto não encontrado.");
        }
    }

    private static void consultarProdutosPorNome(ProdutoService produtoService) {
        System.out.print("Digite o nome do produto: ");
        String nome = scanner.nextLine();
        List<Produto> produtos = produtoService.buscarProdutoPorNome(nome);
        if (!produtos.isEmpty()) {
            System.out.println("Produtos encontrados:");
            produtos.forEach(System.out::println);
        } else {
            System.out.println("Nenhum produto encontrado com esse nome.");
        }
    }

    private static void consultarProdutosPorCategoria(ProdutoService produtoService, CategoriaService categoriaService) {
        System.out.print("Digite o ID da categoria: ");
        Long categoriaId = scanner.nextLong();
        scanner.nextLine(); // consumir nova linha
        List<Produto> produtos = produtoService.buscarProdutosDaCategoria(categoriaId);
        if (!produtos.isEmpty()) {
            System.out.println("Produtos da categoria:");
            produtos.forEach(System.out::println);
        } else {
            System.out.println("Nenhum produto encontrado nessa categoria.");
        }
    }

    private static void cadastrarPedido(PedidoService pedidoService, ProdutoService produtoService) {
        System.out.print("Digite o ID do cliente: ");
        Long clienteId = scanner.nextLong();
        scanner.nextLine(); // consumir nova linha

        Cliente cliente = em.find(Cliente.class, clienteId);
        if (cliente != null) {
            Pedido pedido = new Pedido(cliente);

            boolean adicionarProduto;
            do {
                System.out.print("Digite o ID do produto: ");
                Long produtoId = scanner.nextLong();
                scanner.nextLine(); // consumir nova linha

                Produto produto = produtoService.buscarProdutoPorId(produtoId);
                if (produto != null) {
                    System.out.print("Digite a quantidade do produto: ");
                    int quantidade = scanner.nextInt();
                    scanner.nextLine(); // consumir nova linha

                    PedidoItem pedidoItem = new PedidoItem(quantidade, pedido, produto);

                    pedido.adicionarItem(pedidoItem);
                } else {
                    System.out.println("Produto não encontrado.");
                }

                System.out.print("Deseja adicionar outro produto? (s/n): ");
                adicionarProduto = scanner.nextLine().equalsIgnoreCase("s");
            } while (adicionarProduto);

            pedidoService.inserir(pedido);
            System.out.println("Pedido cadastrado com sucesso!");
        }else{
            System.out.println("Cliente não encontrado.");
        }
    }

    private static void consultarPedidoPorId(PedidoService pedidoService) {
        System.out.print("Digite o ID do pedido: ");
        Long pedidoId = scanner.nextLong();
        scanner.nextLine(); // consumir nova linha

        Pedido pedido = pedidoService.buscarPedidoPorId(pedidoId);
        if (pedido != null) {
            System.out.println("Pedido encontrado: " + pedido);
        } else {
            System.out.println("Pedido não encontrado.");
        }
    }

    private static void consultarPedidosPorPeriodo(PedidoService pedidoService) {
        System.out.print("Digite a data de início (yyyy-MM-dd): ");
        LocalDate dataInicio = LocalDate.parse(scanner.nextLine(), DateTimeFormatter.ISO_LOCAL_DATE);
        System.out.print("Digite a data de fim (yyyy-MM-dd): ");
        LocalDate dataFim = LocalDate.parse(scanner.nextLine(), DateTimeFormatter.ISO_LOCAL_DATE);

        List<Pedido> pedidos = pedidoService.buscarPedidoPorPeriodo(dataInicio, dataFim);
        if (!pedidos.isEmpty()) {
            System.out.println("Pedidos no período selecionado:");
            pedidos.forEach(System.out::println);
        } else {
            System.out.println("Nenhum pedido encontrado no período informado.");
        }
    }

    private static void consultarPedidosDeCliente(PedidoService pedidoService) {
        System.out.print("Digite o ID do cliente: ");
        Long clienteId = scanner.nextLong();
        scanner.nextLine(); // consumir nova linha

        List<Pedido> pedidos = pedidoService.buscarPedidoDeUmCliente(clienteId);
        if (!pedidos.isEmpty()) {
            System.out.println("Pedidos do cliente selecionado:");
            pedidos.forEach(System.out::println);
        } else {
            System.out.println("Nenhum pedido encontrado para esse cliente.");
        }
    }

    private static void alterarQuantidadeItemPedido(PedidoService pedidoService){
        System.out.print("Digite o ID do pedido: ");
        Long pedidoId = scanner.nextLong();
        scanner.nextLine();

        Pedido pedido = pedidoService.buscarPedidoPorId(pedidoId);
        if (pedido != null) {
            System.out.print("Digite o ID do item do pedido que deseja alterar a quantidade: ");
            Long itemPedidoId = scanner.nextLong();
            scanner.nextLine();

            List<PedidoItem> itensPedido = pedido.getItens();
            boolean encontrou = false;
            for(PedidoItem item: itensPedido){
                if(item.getId().equals(itemPedidoId)){
                    System.out.print("Digite a nova quantidade: ");
                    int quantidade = scanner.nextInt();
                    scanner.nextLine();

                    pedido.removerItem(item);
                    item.setQuantidade(quantidade);
                    pedido.adicionarItem(item);
                    pedidoService.alterar(pedido);
                    System.out.println("Quantidade alterada com sucesso! ");
                    encontrou = true;
                    break;
                }
            }
            if(!encontrou){
                System.out.println("Item do pedido não encontrado.");
            }

        } else {
            System.out.println("Pedido não encontrado.");
        }
    }

    private static void excluirItemPedido(PedidoService pedidoService){
        System.out.print("Digite o ID do pedido: ");
        Long pedidoId = scanner.nextLong();
        scanner.nextLine();

        Pedido pedido = pedidoService.buscarPedidoPorId(pedidoId);
        if (pedido != null) {
            System.out.print("Digite o ID do item do pedido que deseja excluir: ");
            Long itemPedidoId = scanner.nextLong();
            scanner.nextLine();

            List<PedidoItem> itensPedido = pedido.getItens();
            boolean encontrou = false;
            for(PedidoItem item: itensPedido){
                if(item.getId().equals(itemPedidoId)){
                    pedido.removerItem(item);
                    pedidoService.alterar(pedido);
                    pedidoService.excluirItem(item);
                    System.out.println("Item excluido com sucesso! ");
                    encontrou = true;
                    break;
                }
            }
            if(!encontrou){
                System.out.println("Item do pedido não encontrado.");
            }

        } else {
            System.out.println("Pedido não encontrado.");
        }
    }

    private static void consultarValorTotalVendido(VendaService vendaService){
        System.out.print("Digite a data de início (yyyy-MM-dd) do período de consulta: ");
        LocalDate dataInicio = LocalDate.parse(scanner.nextLine(), DateTimeFormatter.ISO_LOCAL_DATE);
        System.out.print("Digite a data de fim (yyyy-MM-dd)  do período de consulta: ");
        LocalDate dataFim = LocalDate.parse(scanner.nextLine(), DateTimeFormatter.ISO_LOCAL_DATE);

        BigDecimal valorTotal = vendaService.retornaValorTotalVendido(dataInicio, dataFim);
        System.out.println("Valor total vendido no período: " + valorTotal);
    }

    private static void  consultarRelatorioVendas(VendaService vendaService){
        List<RelatorioDeVendasVo> relatorioVendas = vendaService.retornaRelatorioDeVendas();
        System.out.println("Relatório de Vendas:");
        relatorioVendas.forEach(System.out::println);
    }

    private static void  consultarRelatorioFinanceiro(VendaService vendaService){
        List<RelatorioFinanceiroVo> relatorioFinanceiro = vendaService.retornaRelatorioFinanceiro();
        System.out.println("Relatório Financeiro:");
        relatorioFinanceiro.forEach(System.out::println);
    }
}