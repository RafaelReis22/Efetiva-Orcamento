package com.bcopstein.sistvendas.dominio.regras.imposto;

import com.bcopstein.sistvendas.dominio.modelos.ItemPedidoModel;
import java.util.List;

public interface ImpostoEstadualStrategy {
    double calcular(double custoItens, List<ItemPedidoModel> itens);
}
