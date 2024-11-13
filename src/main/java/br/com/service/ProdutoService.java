package br.com.service;

import br.com.dao.ProdutoDao;
import br.com.model.Produto;
import jakarta.persistence.EntityManager;

import java.util.List;

public class ProdutoService {
    private ProdutoDao produtoDao;

    public ProdutoService(EntityManager em){
        produtoDao = new ProdutoDao(em);
    }

    public void inserir(Produto produto){
        produtoDao.cadastrar(produto);
    }

    public void alterar(Produto produto){
        produtoDao.atualizar(produto);
    }

    public void excluir(Produto produto){
        produtoDao.remover(produto);
    }

    public Produto buscarProdutoPorId(long id){
        return produtoDao.buscarPorId(id);
    }

    public List<Produto> buscarTodosOsProdutos(){
        return produtoDao.buscarTodos();
    }

    public List<Produto> buscarProdutoPorNome(String nome){
        return produtoDao.buscarPorNome(nome);
    }

    public List<Produto> buscarProdutosDaCategoria(long idCategoria){
        return produtoDao.buscarPorCategoria(idCategoria);
    }

}

