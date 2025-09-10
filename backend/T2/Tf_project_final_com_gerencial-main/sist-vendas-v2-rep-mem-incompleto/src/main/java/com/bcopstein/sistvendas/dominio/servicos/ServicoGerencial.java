package com.bcopstein.sistvendas.dominio.servicos;

import com.bcopstein.sistvendas.dominio.modelos.OrcamentoModel;
import com.bcopstein.sistvendas.dominio.persistencia.IOrcamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ServicoGerencial {

    private final IOrcamentoRepository orcamentoRepository;
  

    @Autowired
    public ServicoGerencial(IOrcamentoRepository orcamentoRepository) {
        this.orcamentoRepository = orcamentoRepository;
    }

    /**
     * Consulta 1: Calcula o volume total de vendas (soma do custo ao consumidor)
     * para orçamentos efetivados dentro de um período específico.
     */
    public double calcularVolumeVendasPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        List<OrcamentoModel> orcamentosEfetivados = orcamentoRepository.findEfetivadosByPeriodo(dataInicio, dataFim);
        return orcamentosEfetivados.stream()
                .mapToDouble(OrcamentoModel::getCustoConsumidor)
                .sum();
    }

    /**
     * Consulta 2: Retorna o perfil de compras de um cliente (orçamentos efetivados).
     * Simplificado: Retorna a lista de orçamentos efetivados para o nome do cliente.
     * Uma implementação mais robusta agruparia os produtos e quantidades.
     */
    public List<OrcamentoModel> obterPerfilComprasCliente(String nomeCliente) {
        return orcamentoRepository.findByNomeClienteContainingIgnoreCase(nomeCliente)
                .stream()
                .filter(o -> "EFETIVADO".equalsIgnoreCase(o.getStatus()))
                .collect(Collectors.toList());
    }

    /**
     * Consulta 3: Calcula o total de unidades vendidas por produto em um período.
     * Retorna um Map onde a chave é o ID do produto e o valor é a quantidade total vendida.
     */
    public Map<Long, Integer> calcularTotalVendasPorProduto(LocalDate dataInicio, LocalDate dataFim) {
        List<OrcamentoModel> orcamentosEfetivados = orcamentoRepository.findEfetivadosByPeriodo(dataInicio, dataFim);
        return orcamentosEfetivados.stream()
                .flatMap(orcamento -> orcamento.getItens().stream()) // Flatten all items from all budgets
                .collect(Collectors.groupingBy(
                        item -> item.getProduto().getId(), // Group by product ID
                        Collectors.summingInt(item -> item.getQuantidade()) // Sum quantities for each product
                ));
    }

    /**
     * Consulta 4: Calcula a taxa de conversão de orçamentos em um período.
     * (Orçamentos Efetivados / Total de Orçamentos Criados no período)
     * Exclui orçamentos recusados por local inválido.
     */
    public double calcularTaxaConversao(LocalDate dataInicio, LocalDate dataFim) {
        List<OrcamentoModel> orcamentosNoPeriodo = orcamentoRepository.findByDataBetween(dataInicio, dataFim);
        
        long totalOrcamentosConsiderados = orcamentosNoPeriodo.stream()
                .filter(o -> !"RECUSADO_LOCAL".equalsIgnoreCase(o.getStatus())) 
                .count();
        
        if (totalOrcamentosConsiderados == 0) {
            return 0.0; 
        }

        long efetivadosNoPeriodo = orcamentosNoPeriodo.stream()
                .filter(o -> "EFETIVADO".equalsIgnoreCase(o.getStatus()))
                .count();

        return (double) efetivadosNoPeriodo / totalOrcamentosConsiderados;
    }

    /**
     * Consulta 5 (Relatório Texto): Gera um relatório de vendas por produto (similar à Consulta 3).
     * Inclui descrição do produto para melhor leitura.
     */
    public String gerarRelatorioVendasPorProdutoTexto(LocalDate dataInicio, LocalDate dataFim) {
        List<OrcamentoModel> orcamentosEfetivados = orcamentoRepository.findEfetivadosByPeriodo(dataInicio, dataFim);
        
        Map<String, Integer> vendasPorDescricaoProduto = orcamentosEfetivados.stream()
                .flatMap(orcamento -> orcamento.getItens().stream())
                .collect(Collectors.groupingBy(
                        item -> item.getProduto().getDescricao(), 
                        Collectors.summingInt(item -> item.getQuantidade()) 
                ));

        StringBuilder relatorio = new StringBuilder();
        relatorio.append("Relatório de Vendas por Produto (");
        relatorio.append(dataInicio.toString()).append(" a ").append(dataFim.toString());
        relatorio.append(")\n");
        relatorio.append("--------------------------------------------------\n");
        relatorio.append(String.format("%-30s | %-10s\n", "Descrição Produto", "Qtd Vendida"));
        relatorio.append("--------------------------------------------------\n");

        if (vendasPorDescricaoProduto.isEmpty()) {
            relatorio.append("Nenhuma venda registrada no período.\n");
        } else {
            vendasPorDescricaoProduto.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()) 
                .forEach(entry -> 
                    relatorio.append(String.format("%-30s | %-10d\n", entry.getKey(), entry.getValue()))
                );
        }
        relatorio.append("--------------------------------------------------\n");
        
        return relatorio.toString();
    }
}
