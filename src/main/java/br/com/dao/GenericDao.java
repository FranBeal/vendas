package br.com.dao;

import br.com.exception.DataAccessException;// Exceção personalizada
import jakarta.persistence.EntityManager;
import java.util.List;

// Classe genérica para operações CRUD no banco de dados.
public abstract class GenericDao<T> {

    protected EntityManager em; // Gerenciador de entidades (JPA).
    private Class<T> entityClass; // Classe da entidade genérica.

    public GenericDao(EntityManager em, Class<T> entityClass) {
        this.em = em;
        this.entityClass = entityClass;
    }

    // Metodo para cadastrar uma nova entidade.
    public void cadastrar(T entity) {
        try {
            em.getTransaction().begin();// Inicia a transação.
            em.persist(entity);// Persiste a entidade no banco de dados.
            em.getTransaction().commit();// Confirma a transação.
        } catch (Exception e) {
            em.getTransaction().rollback();// Reverte a transação em caso de erro.

            // Lança uma exceção personalizada com detalhes do erro.
            throw new DataAccessException("Erro ao cadastrar a entidade: " + entity.getClass().getSimpleName(), e);
        }
    }

    // Metodo para atualizar uma entidade existente.
    public void atualizar(T entity) {
        try {
            em.getTransaction().begin();
            em.merge(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new DataAccessException("Erro ao atualizar a entidade: " + entity.getClass().getSimpleName(), e);
        }
    }

    // Metodo para remover uma entidade do banco de dados.
    public void remover(T entity) {
        try {
            em.getTransaction().begin();
            em.remove(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new DataAccessException("Erro ao atualizar a entidade: " + entity.getClass().getSimpleName(), e);
        }
    }

    // Metodo para buscar uma entidade pelo ID.
    public T buscarPorId(Long id) {
        try {
            return em.find(entityClass, id);// Busca a entidade pelo ID.
        } catch (Exception e) {
            throw new DataAccessException("Erro ao buscar o id: "+ id +" da entidade: "+ entityClass.getSimpleName(), e);
        }
    }

    // Metodo para buscar todas as entidades de um tipo.
    public List<T> buscarTodos() {
        try {
            // Monta uma consulta JPQL para buscar todas as entidades.
            String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";

            return em.createQuery(jpql, entityClass).getResultList();

        } catch (Exception e) {
            throw new DataAccessException("Erro ao buscar todos de: "+ entityClass.getSimpleName(), e);
        }
    }
}
