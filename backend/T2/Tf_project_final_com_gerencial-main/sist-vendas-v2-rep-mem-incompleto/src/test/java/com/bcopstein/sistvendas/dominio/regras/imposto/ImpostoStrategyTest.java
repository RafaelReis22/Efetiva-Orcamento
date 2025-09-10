package com.bcopstein.sistvendas.dominio.regras.imposto;

import com.bcopstein.sistvendas.dominio.modelos.ItemPedidoModel;
import com.bcopstein.sistvendas.dominio.modelos.ProdutoModel;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ImpostoStrategyTest {

    @Test
    void testCalculoImpostoRS_AbaixoDeCemIsento() {
        ImpostoEstadualStrategy strategy = new ImpostoRSStrategy();
        double imposto = strategy.calcular(80.0, List.of());
        assertEquals(0.0, imposto, "Deve ser isento para compras até R$ 100 no RS.");
    }

    @Test
    void testCalculoImpostoRS_AcimaDeCem() {
        ImpostoEstadualStrategy strategy = new ImpostoRSStrategy();
        double imposto = strategy.calcular(150.0, List.of());
        // (150 - 100) * 10% = 5.0
        assertEquals(5.0, imposto, "Deve cobrar 10% do que exceder R$ 100 no RS.");
    }

    @Test
    void testCalculoImpostoSP() {
        ImpostoEstadualStrategy strategy = new ImpostoSPStrategy();
        double imposto = strategy.calcular(100.0, List.of());
        assertEquals(12.0, imposto, "Deve cobrar 12% do total em SP.");
    }

    @Test
    void testCalculoImpostoPE_ItensMistos() {
        ImpostoEstadualStrategy strategy = new ImpostoPEStrategy();
        
        ProdutoModel pNormal = new ProdutoModel("Produto Normal", 100.0);
        ProdutoModel pEssencial = new ProdutoModel("Produto Essencial*", 100.0);
        
        ItemPedidoModel itemNormal = new ItemPedidoModel(pNormal, 1, 100.0);
        ItemPedidoModel itemEssencial = new ItemPedidoModel(pEssencial, 1, 100.0);
        
        double imposto = strategy.calcular(200.0, List.of(itemNormal, itemEssencial));
        // Normal: 100 * 15% = 15.0
        // Essencial: 100 * 5% = 5.0
        // Total = 20.0
        assertEquals(20.0, imposto, "Deve cobrar 15% em normais e 5% em essenciais em PE.");
    }
}
