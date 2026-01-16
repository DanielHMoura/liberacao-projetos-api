package com.metrica.liberacao.repository;

import com.metrica.liberacao.domain.Projeto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositório para gerenciar operações de persistência da entidade {@link Projeto}.
 *
 * Fornece acesso aos dados de projetos no banco de dados, incluindo
 * operações CRUD padrão (herdadas de {@link JpaRepository}) e consultas customizadas.
 *
 * @author DanielHMoura
 * @version 1.0
 * @since 2026-01-15
 */
public interface ProjetoRepository extends JpaRepository<Projeto, Long> {

    /**
     * Busca um projeto pelo código de acesso.
     *
     * @param codigoAcesso o código de acesso único do projeto
     * @return um {@link Optional} contendo o projeto se encontrado, ou vazio caso contrário
     */
    Optional<Projeto> findByCodigoAcesso(String codigoAcesso);
}
