package com.bcopstein.sistvendas.dominio.regras.imposto;

import com.bcopstein.sistvendas.dominio.modelos.ItemPedidoModel;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class ImpostoPEStrategy implements ImpostoEstadualStrategy {
    @Override
    public double calcular(double custoItens, List<ItemPedidoModel> itens) {
        // Alíquota única de 15% sobre todos os produtos menos aqueles considerados essenciais 
        // que tem uma alíquota de 5%. Produtos essenciais tem um "*" ao final da descrição do produto.
        double imposto = 0.0;
        for (ItemPedidoModel item : itens) {
            double valorItem = item.getPrecoUnitarioNoOrcamento() * item.getQuantidade();
            boolean essencial = item.getProduto().getDescricao().endsWith("*");
            imposto += valorItem * (essencial ? 0.05 : 0.15);
        }
        return imposto;
    }
}
