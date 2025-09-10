package com.bcopstein.sistvendas.dominio.regras.desconto;

import com.bcopstein.sistvendas.dominio.modelos.ItemPedidoModel;
import java.util.List;

public class DescontoPorItem extends DescontoChain {

    public DescontoPorItem(DescontoChain proximo) {
        super(proximo);
    }

    @Override
    protected double calcularDescontoAtual(double custoItens, List<ItemPedidoModel> itens) {
        // 5% de desconto por item cuja quantidade solicitada seja superior a três unidades
        return itens.stream()
            .filter(it -> it.getQuantidade() > 3)
            .mapToDouble(it -> (it.getPrecoUnitarioNoOrcamento() * it.getQuantidade()) * 0.05)
            .sum();
    }
}
