package com.bcopstein.sistvendas.dominio.servicos;

import com.bcopstein.sistvendas.dominio.modelos.ItemPedidoModel;
import com.bcopstein.sistvendas.dominio.modelos.OrcamentoModel;
import com.bcopstein.sistvendas.dominio.modelos.ProdutoModel;
import com.bcopstein.sistvendas.dominio.persistencia.IEstoqueRepository;
import com.bcopstein.sistvendas.dominio.persistencia.IOrcamentoRepository;
import com.bcopstein.sistvendas.dominio.persistencia.IProdutoRepository;
import com.bcopstein.sistvendas.dominio.regras.desconto.DescontoFactory;
import com.bcopstein.sistvendas.dominio.regras.imposto.ImpostoStrategyFactory;
import com.bcopstein.sistvendas.dominio.regras.imposto.ImpostoSPStrategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ServicoDeVendasTest {

    @Mock
    private IOrcamentoRepository orcamentosRep;
    @Mock
    private IEstoqueRepository estoqueRep;
    @Mock
    private IProdutoRepository produtosRep;
    @Mock
    private ServicoDeEstoque servicoDeEstoque;
    @Mock
    private ImpostoStrategyFactory impostoFactory;
    @Mock
    private DescontoFactory descontoFactory;

    @InjectMocks
    private ServicoDeVendas servicoDeVendas;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCriaOrcamento_LocalValidoComSucesso() {
        // Arrange
        ProdutoModel produto = new ProdutoModel("Mochila", 150.0);
        ItemPedidoModel item = new ItemPedidoModel(produto, 2, 150.0);
        List<ItemPedidoModel> itens = List.of(item);
        
        when(impostoFactory.getStrategy(anyString())).thenReturn(Optional.of(new ImpostoSPStrategy())); // 12%
        // Mock chain for discounts: return 0 discount
        com.bcopstein.sistvendas.dominio.regras.desconto.DescontoChain mockDescontoChain = mock(com.bcopstein.sistvendas.dominio.regras.desconto.DescontoChain.class);
        when(mockDescontoChain.calcular(anyDouble(), anyList())).thenReturn(0.0);
        when(descontoFactory.getDescontoChain()).thenReturn(mockDescontoChain);
        
        when(orcamentosRep.save(any(OrcamentoModel.class))).thenAnswer(invocation -> {
            OrcamentoModel saved = invocation.getArgument(0);
            saved.setId(100L); // Simulando banco de dados gerando ID
            return saved;
        });

        // Act
        OrcamentoModel resultado = servicoDeVendas.criaOrcamento(itens, "Cliente Teste", "SP", "Brasil");

        // Assert
        assertNotNull(resultado);
        assertEquals(100L, resultado.getId());
        assertEquals("PENDENTE", resultado.getStatus());
        
        // 2 itens de 150 = 300
        assertEquals(300.0, resultado.getCustoItens());
        
        // SP Strategy cobra 12% = 300 * 0.12 = 36
        assertEquals(36.0, resultado.getImpostoEstadual());
        
        // Imposto Federal = 15% de 300 = 45
        assertEquals(45.0, resultado.getImpostoFederal());
        
        // Total = 300 + 36 + 45 - 0(desconto) = 381.0
        assertEquals(381.0, resultado.getCustoConsumidor());
        
        verify(orcamentosRep, times(1)).save(any(OrcamentoModel.class));
    }

    @Test
    void testCriaOrcamento_LocalInvalidoRetornaRecusado() {
        // Act
        OrcamentoModel resultado = servicoDeVendas.criaOrcamento(List.of(), "Cliente Invalido", "NY", "USA");

        // Assert - Mocking null for save unless we mock it, default is null, let's mock it
        // Wait, since we verify status before saving:
        when(orcamentosRep.save(any(OrcamentoModel.class))).thenAnswer(i -> i.getArgument(0));

        resultado = servicoDeVendas.criaOrcamento(List.of(), "Cliente Invalido", "NY", "USA");
        
        assertEquals("RECUSADO_LOCAL", resultado.getStatus());
    }
}
