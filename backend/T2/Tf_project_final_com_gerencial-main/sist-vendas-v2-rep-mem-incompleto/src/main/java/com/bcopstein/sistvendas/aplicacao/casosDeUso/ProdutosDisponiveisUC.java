package com.bcopstein.sistvendas.aplicacao.casosDeUso;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bcopstein.sistvendas.aplicacao.dtos.ProdutoDTO;
import com.bcopstein.sistvendas.dominio.modelos.ProdutoModel;
import com.bcopstein.sistvendas.dominio.servicos.ServicoDeEstoque;

@Component
public class ProdutosDisponiveisUC {
    private ServicoDeEstoque servicoDeEstoque;

    @Autowired
    public ProdutosDisponiveisUC(ServicoDeEstoque servicoDeEstoque) {
        this.servicoDeEstoque = servicoDeEstoque;
    }

    public List<ProdutoDTO> run() {
        List<ProdutoModel> produtos = servicoDeEstoque.produtosDisponiveis();
        return produtos.stream()
                       .map(ProdutoDTO::fromModel)
                       .collect(Collectors.toList());
    }
}
