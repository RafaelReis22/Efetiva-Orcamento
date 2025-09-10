package com.bcopstein.sistvendas.dominio.regras.imposto;

import com.bcopstein.sistvendas.dominio.modelos.ItemPedidoModel;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class ImpostoSPStrategy implements ImpostoEstadualStrategy {
    @Override
    public double calcular(double custoItens, List<ItemPedidoModel> itens) {
        // Alíquota única de 12%.
        return custoItens * 0.12;
    }
}
