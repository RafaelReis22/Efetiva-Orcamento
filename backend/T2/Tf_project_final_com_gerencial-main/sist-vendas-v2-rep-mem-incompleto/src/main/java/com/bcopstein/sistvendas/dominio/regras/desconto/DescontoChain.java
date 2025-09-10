package com.bcopstein.sistvendas.dominio.regras.desconto;

import com.bcopstein.sistvendas.dominio.modelos.ItemPedidoModel;
import java.util.List;

public abstract class DescontoChain {
    protected DescontoChain proximo;

    public DescontoChain(DescontoChain proximo) {
        this.proximo = proximo;
    }

    public double calcular(double custoItens, List<ItemPedidoModel> itens) {
        double descontoAtual = calcularDescontoAtual(custoItens, itens);
        if (proximo != null) {
            return descontoAtual + proximo.calcular(custoItens, itens);
        }
        return descontoAtual;
    }

    protected abstract double calcularDescontoAtual(double custoItens, List<ItemPedidoModel> itens);
}
