package br.com.service;

import br.com.dao.CategoriaDao;
import br.com.model.Categoria;
import jakarta.persistence.EntityManager;

import java.util.List;

public class CategoriaService {
    private CategoriaDao categoriaDao;

    public CategoriaService(EntityManager em){
        categoriaDao = new CategoriaDao(em);
    }

    public void inserir(Categoria categoria){
        categoriaDao.cadastrar(categoria);
    }

    public void alterar(Categoria categoria){
        categoriaDao.atualizar(categoria);
    }

    public void excluir(Categoria categoria){
        categoriaDao.remover(categoria);
    }

    public Categoria buscarCategoriaPorId(long id){
        return categoriaDao.buscarPorId(id);
    }

    public List<Categoria> buscarTodosAsCategorias(){
        return categoriaDao.buscarTodos();
    }
}
