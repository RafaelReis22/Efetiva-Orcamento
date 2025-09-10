package com.bcopstein.sistvendas.aplicacao.casosDeUso;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bcopstein.sistvendas.aplicacao.dtos.ItemPedidoDTO;
import com.bcopstein.sistvendas.aplicacao.dtos.OrcamentoDTO;
import com.bcopstein.sistvendas.aplicacao.dtos.ParamNovoOrcamentoDTO;
import com.bcopstein.sistvendas.dominio.modelos.ItemPedidoModel;
import com.bcopstein.sistvendas.dominio.modelos.OrcamentoModel;
import com.bcopstein.sistvendas.dominio.modelos.ProdutoModel;
import com.bcopstein.sistvendas.dominio.servicos.ServicoDeEstoque;
import com.bcopstein.sistvendas.dominio.servicos.ServicoDeVendas;

@Component
public class CriaOrcamentoUC {
    private ServicoDeVendas servicoDeVendas;
    private ServicoDeEstoque servicoDeEstoque;

    @Autowired
    public CriaOrcamentoUC(ServicoDeVendas servicoDeVendas, ServicoDeEstoque servicoDeEstoque) {
        this.servicoDeVendas = servicoDeVendas;
        this.servicoDeEstoque = servicoDeEstoque;
    }

    public OrcamentoDTO run(ParamNovoOrcamentoDTO param) {

        List<ItemPedidoModel> itensPedidoModel = param.getItens().stream()
                .map(itemDTO -> {
                    ProdutoModel produto = servicoDeEstoque.produtoPorCodigo(itemDTO.getIdProduto());
                    if (produto == null) {
                        throw new IllegalArgumentException("Produto não encontrado: ID " + itemDTO.getIdProduto());
                    }
                    // MODIFICAÇÃO AQUI: Passa o precoUnitario do produto para o novo campo
                    return new ItemPedidoModel(produto, itemDTO.getQtdade(), produto.getPrecoUnitario());
                })
                .collect(Collectors.toList());

        OrcamentoModel orcamento = servicoDeVendas.criaOrcamento(
                itensPedidoModel,
                param.getNomeCliente(),
                param.getEstadoEntrega(),
                param.getPaisEntrega());

        return OrcamentoDTO.fromModel(orcamento);
    }
}
