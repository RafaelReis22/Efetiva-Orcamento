package com.bcopstein.sistvendas.dominio.persistencia;

import com.bcopstein.sistvendas.dominio.modelos.ItemDeEstoqueModel;
import com.bcopstein.sistvendas.dominio.modelos.ProdutoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IEstoqueRepository extends JpaRepository<ItemDeEstoqueModel, Long> {

  
    Optional<ItemDeEstoqueModel> findByProdutoId(long produtoId);

    @Query("SELECT i FROM ItemDeEstoqueModel i WHERE i.quantidade > 0")
    List<ItemDeEstoqueModel> findAllWithEstoque();

    @Query("SELECT i FROM ItemDeEstoqueModel i WHERE i.produto.id IN :productIds")
    List<ItemDeEstoqueModel> findByProdutoIdIn(@Param("productIds") List<Long> productIds);


    @Query("SELECT i.produto FROM ItemDeEstoqueModel i WHERE i.quantidade > 0")
    List<ProdutoModel> findProdutosComEstoque();
}
