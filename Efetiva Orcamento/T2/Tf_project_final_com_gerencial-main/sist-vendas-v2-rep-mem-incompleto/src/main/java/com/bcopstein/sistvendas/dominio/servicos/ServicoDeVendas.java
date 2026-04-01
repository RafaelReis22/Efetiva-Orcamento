package com.bcopstein.sistvendas.dominio.servicos;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bcopstein.sistvendas.dominio.modelos.ItemDeEstoqueModel;
import com.bcopstein.sistvendas.dominio.modelos.ItemPedidoModel;
import com.bcopstein.sistvendas.dominio.modelos.OrcamentoModel;
import com.bcopstein.sistvendas.dominio.modelos.ProdutoModel;
import com.bcopstein.sistvendas.dominio.persistencia.IEstoqueRepository;
import com.bcopstein.sistvendas.dominio.persistencia.IOrcamentoRepository;
import com.bcopstein.sistvendas.dominio.persistencia.IProdutoRepository; 

import com.bcopstein.sistvendas.dominio.regras.imposto.ImpostoStrategyFactory;
import com.bcopstein.sistvendas.dominio.regras.imposto.ImpostoEstadualStrategy;
import com.bcopstein.sistvendas.dominio.regras.desconto.DescontoFactory;
import com.bcopstein.sistvendas.dominio.regras.desconto.DescontoChain;

@Service
public class ServicoDeVendas {
    private IOrcamentoRepository orcamentosRep;
    private IEstoqueRepository estoqueRep;
    private IProdutoRepository produtosRep; 
    private ServicoDeEstoque servicoDeEstoque; 
    private ImpostoStrategyFactory impostoFactory;
    private DescontoFactory descontoFactory;

    @Autowired
    public ServicoDeVendas(IOrcamentoRepository orcamentosRep, IEstoqueRepository estoqueRep, IProdutoRepository produtosRep, ServicoDeEstoque servicoDeEstoque, ImpostoStrategyFactory impostoFactory, DescontoFactory descontoFactory){
        this.orcamentosRep = orcamentosRep;
        this.estoqueRep = estoqueRep;
        this.produtosRep = produtosRep;
        this.servicoDeEstoque = servicoDeEstoque;
        this.impostoFactory = impostoFactory;
        this.descontoFactory = descontoFactory;
    }
    

    public Optional<OrcamentoModel> recuperaOrcamentoPorId(long id) {
        return this.orcamentosRep.findById(id);
    }

    @Transactional // Ensure atomicity
    public OrcamentoModel criaOrcamento(List<ItemPedidoModel> itensPedido, String nomeCliente, String estadoEntrega, String paisEntrega) {
        // 1. Validar Local de Entrega (Simplificado - Apenas Brasil e estados definidos)
        if (!validaLocalEntrega(estadoEntrega, paisEntrega)) {
            System.err.println("Local de entrega inválido: " + estadoEntrega + ", " + paisEntrega);
            OrcamentoModel orcamentoRecusado = new OrcamentoModel(nomeCliente, estadoEntrega, paisEntrega);
            orcamentoRecusado.addItensPedido(itensPedido); // Garante que itens sejam associados
            orcamentoRecusado.setStatus("RECUSADO_LOCAL");
            return this.orcamentosRep.save(orcamentoRecusado);
        }

        OrcamentoModel novoOrcamento = new OrcamentoModel(nomeCliente, estadoEntrega, paisEntrega);
        novoOrcamento.addItensPedido(itensPedido);

        // 2. Calcular Custo dos Itens
        // AJUSTE AQUI: Usar precoUnitarioNoOrcamento
        double custoItens = novoOrcamento.getItens().stream()
            .mapToDouble(it -> it.getPrecoUnitarioNoOrcamento() * it.getQuantidade())
            .sum();
        novoOrcamento.setCustoItens(custoItens);

        // 3. Calcular Impostos (ANTES dos descontos)
        double impostoEstadual = impostoFactory.getStrategy(novoOrcamento.getEstadoEntrega())
                                               .map(strategy -> strategy.calcular(custoItens, novoOrcamento.getItens()))
                                               .orElse(0.0);
        double impostoFederal = custoItens * 0.15; 
        novoOrcamento.setImpostoEstadual(impostoEstadual);
        novoOrcamento.setImpostoFederal(impostoFederal);
        double impostoTotal = impostoEstadual + impostoFederal;

        // 4. Calcular Descontos
        DescontoChain cadeiaDesconto = descontoFactory.getDescontoChain();
        double desconto = cadeiaDesconto.calcular(custoItens, novoOrcamento.getItens());
        novoOrcamento.setDesconto(desconto);

        // 5. Calcular Custo Final
        double custoFinal = custoItens + impostoTotal - desconto;
        novoOrcamento.setCustoConsumidor(custoFinal);

        // 6. Salvar Orçamento
        return this.orcamentosRep.save(novoOrcamento);
    }
 
    @Transactional 
    public Optional<OrcamentoModel> efetivaOrcamento(long idOrcamento) {
        Optional<OrcamentoModel> orcamentoOpt = orcamentosRep.findById(idOrcamento);

        if (orcamentoOpt.isEmpty()) {
            return Optional.empty(); 
        }

        OrcamentoModel orcamento = orcamentoOpt.get();

        // 1. Verificar Validade (Data)
        if (!orcamento.isValido()) {
            orcamento.setStatus("EXPIRADO");
            orcamentosRep.save(orcamento);
            return Optional.of(orcamento); // Retorna orçamento com status EXPIRADO
        }

        // 2. Verificar Status (Só pode efetivar se PENDENTE)
        if (!"PENDENTE".equalsIgnoreCase(orcamento.getStatus())) {
             // Já foi efetivado, expirado ou recusado
             return Optional.of(orcamento); // Retorna orçamento com status atual
        }

        // 3. Verificar Disponibilidade de Estoque
        boolean disponivel = true;
        for (ItemPedidoModel item : orcamento.getItens()) {
            int qtdNecessaria = item.getQuantidade();
            int qtdDisponivel = servicoDeEstoque.qtdadeEmEstoque(item.getProduto().getId());
            if (qtdDisponivel < qtdNecessaria) {
                disponivel = false;
                break;
            }
        }

        // 4. Efetivar ou Marcar como Indisponível
        if (disponivel) {
            // Dar baixa no estoque
            for (ItemPedidoModel item : orcamento.getItens()) {
                servicoDeEstoque.baixaEstoque(item.getProduto().getId(), item.getQuantidade());
            }
            // Marcar como efetivado
            orcamento.setStatus("EFETIVADO");
        } else {
            orcamento.setStatus("RECUSADO_ESTOQUE");
        }

        // 5. Salvar e retornar
        return Optional.of(orcamentosRep.save(orcamento));
    }

    // Retorna lista de orçamentos efetivados em um período
    public List<OrcamentoModel> orcamentosEfetivadosPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        return orcamentosRep.findEfetivadosByPeriodo(dataInicio, dataFim);
    }


    private boolean validaLocalEntrega(String estado, String pais) {
        // Simplificado: Apenas Brasil e estados específicos
        if (!"Brasil".equalsIgnoreCase(pais)) {
            return false;
        }
        List<String> estadosAtendidos = List.of("RS", "SP", "PE"); 
        return estadosAtendidos.contains(estado.toUpperCase());
    }


}