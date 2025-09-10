package com.bcopstein.sistvendas.dominio.regras.desconto;

import com.bcopstein.sistvendas.dominio.modelos.ItemPedidoModel;
import java.util.List;

public class DescontoPorVolume extends DescontoChain {

    public DescontoPorVolume(DescontoChain proximo) {
        super(proximo);
    }

    @Override
    protected double calcularDescontoAtual(double custoItens, List<ItemPedidoModel> itens) {
        // 10% de desconto sobre o valor total do orçamento quando este contiver mais de dez itens.
        if (itens.size() > 10) { 
            return custoItens * 0.10; 
        }
        return 0.0;
    }
}
