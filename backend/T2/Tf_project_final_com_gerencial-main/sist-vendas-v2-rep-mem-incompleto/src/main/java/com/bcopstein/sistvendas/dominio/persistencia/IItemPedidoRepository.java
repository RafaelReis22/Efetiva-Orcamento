package com.bcopstein.sistvendas.dominio.persistencia;

import com.bcopstein.sistvendas.dominio.modelos.ItemPedidoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; // Import necessário para potenciais métodos futuros
import java.util.Optional; // Import necessário para potenciais métodos futuros

// Interface de repositório para a entidade ItemPedidoModel
public interface IItemPedidoRepository extends JpaRepository<ItemPedidoModel, Long> {
    // Métodos CRUD básicos são herdados de JpaRepository.
    // Você pode adicionar métodos de consulta personalizados aqui no futuro, se necessário.
    // Exemplo: List<ItemPedidoModel> findByOrcamentoId(Long orcamentoId);
}