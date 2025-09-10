package com.bcopstein.sistvendas.dominio.persistencia;

import com.bcopstein.sistvendas.dominio.modelos.OrcamentoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IOrcamentoRepository extends JpaRepository<OrcamentoModel, Long> {

  
    List<OrcamentoModel> findByStatus(String status);

    List<OrcamentoModel> findByDataBetween(LocalDate startDate, LocalDate endDate);

    List<OrcamentoModel> findByNomeClienteContainingIgnoreCase(String nomeCliente);

    @Query("SELECT o FROM OrcamentoModel o WHERE o.dataValidade >= :currentDate")
    List<OrcamentoModel> findValidos(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT o FROM OrcamentoModel o WHERE o.status = 'EFETIVADO' AND o.data BETWEEN :startDate AND :endDate")
    List<OrcamentoModel> findEfetivadosByPeriodo(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}
