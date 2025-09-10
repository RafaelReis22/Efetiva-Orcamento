package com.bcopstein.sistvendas.aplicacao.casosDeUso;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bcopstein.sistvendas.aplicacao.dtos.OrcamentoDTO;
import com.bcopstein.sistvendas.dominio.modelos.OrcamentoModel;
import com.bcopstein.sistvendas.dominio.servicos.ServicoDeVendas;

@Component
public class EfetivaOrcamentoUC {
    private ServicoDeVendas servicoDeVendas;
    
    @Autowired
    public EfetivaOrcamentoUC(ServicoDeVendas servicoDeVendas){
        this.servicoDeVendas = servicoDeVendas;
    }

    public OrcamentoDTO run(long idOrcamento){
        Optional<OrcamentoModel> orcamentoOpt = servicoDeVendas.efetivaOrcamento(idOrcamento);
        if (orcamentoOpt.isPresent()) {
            return OrcamentoDTO.fromModel(orcamentoOpt.get());
        } else {
            throw new IllegalArgumentException("Orçamento não encontrado: ID " + idOrcamento);
        }
    }
}
