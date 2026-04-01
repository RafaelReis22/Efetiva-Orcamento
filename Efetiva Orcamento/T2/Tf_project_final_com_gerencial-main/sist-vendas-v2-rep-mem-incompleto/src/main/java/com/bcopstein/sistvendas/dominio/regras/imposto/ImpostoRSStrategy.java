package com.bcopstein.sistvendas.dominio.regras.imposto;

import com.bcopstein.sistvendas.dominio.modelos.ItemPedidoModel;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class ImpostoRSStrategy implements ImpostoEstadualStrategy {
    @Override
    public double calcular(double custoItens, List<ItemPedidoModel> itens) {
        // Orçamentos de menos de R$ 100,00 são isentos.
        // Para orçamentos maiores que R$ 100,00 a alíquota é de 10% sobre o valor que ultrapassa R$ 100,00.
        if (custoItens <= 100.0) {
            return 0.0;
        }
        return (custoItens - 100.0) * 0.10;
    }
}
