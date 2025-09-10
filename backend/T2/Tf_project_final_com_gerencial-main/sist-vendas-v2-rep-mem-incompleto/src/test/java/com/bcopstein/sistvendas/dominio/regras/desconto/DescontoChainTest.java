package com.bcopstein.sistvendas.dominio.regras.desconto;

import com.bcopstein.sistvendas.dominio.modelos.ItemPedidoModel;
import com.bcopstein.sistvendas.dominio.modelos.ProdutoModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DescontoChainTest {

    private DescontoChain chain;

    @BeforeEach
    void setup() {
        DescontoFactory factory = new DescontoFactory();
        this.chain = factory.getDescontoChain();
    }

    @Test
    void testDescontoPorItemAcimaDeTresUnidades() {
        ProdutoModel p = new ProdutoModel("Produto A", 50.0);
        // 4 unidades a 50 reais = 200 reais.
        // Como quantidade > 3, ganha 5% => 200 * 0.05 = 10.0
        ItemPedidoModel item = new ItemPedidoModel(p, 4, 50.0);
        
        double desconto = chain.calcular(200.0, List.of(item));
        
        assertEquals(10.0, desconto, "Deve conceder 5% de desconto para itens com mais de 3 unidades.");
    }

    @Test
    void testDescontoPorVolumeAcimaDeDezItensNoTotalDoOrcamento() {
        ProdutoModel p = new ProdutoModel("Ticket Simples", 10.0);
        
        // Criaremos 11 itens com qtd 1 (cada um não ganha o desconto individual)
        List<ItemPedidoModel> itens = IntStream.range(0, 11)
            .mapToObj(i -> new ItemPedidoModel(p, 1, 10.0))
            .collect(Collectors.toList());
            
        double custoTotal = 11 * 10.0; // 110.0
        
        // Como o tamanho da lista (n. linhas) é > 10, ganha 10% sobre o total.
        // Se a regra fosse "mais de 10 unidades totais" a logica usava soma, mas o servico verifica `itens.size() > 10`.
        double desconto = chain.calcular(custoTotal, itens);
        
        assertEquals(11.0, desconto, "Deve conceder 10% de desconto no total por haver mais de 10 itens.");
    }
}
