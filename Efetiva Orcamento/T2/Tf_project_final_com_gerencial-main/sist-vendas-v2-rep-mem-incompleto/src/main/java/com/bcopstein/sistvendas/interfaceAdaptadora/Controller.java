package com.bcopstein.sistvendas.interfaceAdaptadora;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bcopstein.sistvendas.aplicacao.casosDeUso.CriaOrcamentoUC;
import com.bcopstein.sistvendas.aplicacao.casosDeUso.EfetivaOrcamentoUC;
import com.bcopstein.sistvendas.aplicacao.casosDeUso.ProdutosDisponiveisUC;
import com.bcopstein.sistvendas.aplicacao.dtos.ItemEstoqueDTO;
import com.bcopstein.sistvendas.aplicacao.dtos.ItemPedidoDTO;
import com.bcopstein.sistvendas.aplicacao.dtos.OrcamentoDTO;
import com.bcopstein.sistvendas.aplicacao.dtos.ParamEntradaEstoqueDTO;
import com.bcopstein.sistvendas.aplicacao.dtos.ParamNovoOrcamentoDTO;
import com.bcopstein.sistvendas.aplicacao.dtos.ProdutoDTO;
import com.bcopstein.sistvendas.dominio.modelos.ItemDeEstoqueModel;
import com.bcopstein.sistvendas.dominio.modelos.OrcamentoModel;
import com.bcopstein.sistvendas.dominio.servicos.ServicoDeEstoque;
import com.bcopstein.sistvendas.dominio.servicos.ServicoDeVendas;
import com.bcopstein.sistvendas.dominio.servicos.ServicoGerencial; 

@RestController
@CrossOrigin(origins = "*")
public class Controller {
    private ProdutosDisponiveisUC produtosDisponiveisUC;
    private CriaOrcamentoUC criaOrcamentoUC;
    private EfetivaOrcamentoUC efetivaOrcamentoUC;
    private ServicoDeEstoque servicoDeEstoque;
    private ServicoDeVendas servicoDeVendas;
    private ServicoGerencial servicoGerencial;

    @Autowired
    public Controller(ProdutosDisponiveisUC produtosDisponiveisUC,
                      CriaOrcamentoUC criaOrcamentoUC,
                      EfetivaOrcamentoUC efetivaOrcamentoUC,
                      ServicoDeEstoque servicoDeEstoque,
                      ServicoDeVendas servicoDeVendas,
                      ServicoGerencial servicoGerencial) { 
        this.produtosDisponiveisUC = produtosDisponiveisUC;
        this.criaOrcamentoUC = criaOrcamentoUC;
        this.efetivaOrcamentoUC = efetivaOrcamentoUC;
        this.servicoDeEstoque = servicoDeEstoque;
        this.servicoDeVendas = servicoDeVendas;
        this.servicoGerencial = servicoGerencial;
    }

    @GetMapping("/")
    public String welcomeMessage(){
        return("Bem vindo as lojas ACME");
    }

    
    @GetMapping("/produtos")
    public List<ProdutoDTO> produtosDisponiveis(){
        return produtosDisponiveisUC.run();
    }    

    @PostMapping("/orcamentos")
    public ResponseEntity<OrcamentoDTO> novoOrcamento(@RequestBody ParamNovoOrcamentoDTO param){
        try {
            OrcamentoDTO orcamento = criaOrcamentoUC.run(param);
            if ("RECUSADO_LOCAL".equals(orcamento.getStatus())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(orcamento);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(orcamento);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Endpoint: Efetivar orçamento indicado
    @PostMapping("/orcamentos/efetivar/{id}")
    public ResponseEntity<OrcamentoDTO> efetivaOrcamento(@PathVariable(value="id") long idOrcamento){
        try {
            OrcamentoDTO orcamento = efetivaOrcamentoUC.run(idOrcamento);
            if ("EXPIRADO".equals(orcamento.getStatus())) {
                return ResponseEntity.status(HttpStatus.GONE).body(orcamento);
            }
            if ("RECUSADO_ESTOQUE".equals(orcamento.getStatus())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(orcamento);
            }
             if (!"EFETIVADO".equals(orcamento.getStatus())) {
                 return ResponseEntity.ok(orcamento);
            }
            return ResponseEntity.ok(orcamento);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Endpoint: Informar a chegada de produtos no estoque
    @PostMapping("/estoque/entrada")
    public ResponseEntity<Void> entradaEstoque(@RequestBody ParamEntradaEstoqueDTO param) {
        boolean success = servicoDeEstoque.entradaEstoque(param.getIdProduto(), param.getQuantidade());
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); 
        }
    }

    // Endpoint: Informar a saída de produtos do estoque (baixa direta)
    @PostMapping("/estoque/saida")
    public ResponseEntity<Void> baixaEstoque(@RequestBody ParamEntradaEstoqueDTO param) {
        boolean success = servicoDeEstoque.baixaEstoque(param.getIdProduto(), param.getQuantidade());
        if (success) {
            return ResponseEntity.ok().build(); // Retorna 200 OK se a baixa for bem-sucedida
        } else {
            // Retorna 400 Bad Request se a quantidade for insuficiente ou o produto não for encontrado
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); 
        }
    }

    // Endpoint: Retornar a quantidade disponível no estoque para todos os itens do catálogo
    @GetMapping("/estoque")
    public List<ItemEstoqueDTO> estoqueDisponivelTodos() {
        List<ItemDeEstoqueModel> itens = servicoDeEstoque.todosItensEstoque();
        return itens.stream().map(ItemEstoqueDTO::fromModel).collect(Collectors.toList());
    }

    // Endpoint: Retornar a quantidade disponível no estoque para uma lista de produtos informados
    @GetMapping("/estoque/consulta")
    public List<ItemEstoqueDTO> estoqueDisponivelLista(@RequestParam List<Long> pids) {
        List<ItemDeEstoqueModel> itens = servicoDeEstoque.consultaEstoquePorProdutos(pids);
        return itens.stream().map(ItemEstoqueDTO::fromModel).collect(Collectors.toList());
    } 

    // Endpoint: Retornar a lista de orçamentos efetivados em um determinado período
    @GetMapping("/orcamentos/efetivados")
    public List<OrcamentoDTO> orcamentosEfetivadosPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        List<OrcamentoModel> orcamentos = servicoDeVendas.orcamentosEfetivadosPorPeriodo(dataInicio, dataFim);
        return orcamentos.stream().map(OrcamentoDTO::fromModel).collect(Collectors.toList());
    }


    // Consulta 1: Volume total de vendas por período
    @GetMapping("/gerencial/vendas/volume")
    public ResponseEntity<Double> volumeVendasPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        double volume = servicoGerencial.calcularVolumeVendasPeriodo(dataInicio, dataFim);
        return ResponseEntity.ok(volume);
    }

    // Consulta 2: Perfil de compras de um cliente (lista de orçamentos efetivados)
    @GetMapping("/gerencial/clientes/perfil")
    public ResponseEntity<List<OrcamentoDTO>> perfilComprasCliente(@RequestParam String nomeCliente) {
        List<OrcamentoModel> orcamentos = servicoGerencial.obterPerfilComprasCliente(nomeCliente);
        List<OrcamentoDTO> dtos = orcamentos.stream().map(OrcamentoDTO::fromModel).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Consulta 3: Total de vendas (unidades) por produto
    @GetMapping("/gerencial/produtos/vendas")
    public ResponseEntity<Map<Long, Integer>> totalVendasPorProduto(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        Map<Long, Integer> vendas = servicoGerencial.calcularTotalVendasPorProduto(dataInicio, dataFim);
        return ResponseEntity.ok(vendas);
    }

    // Consulta 4: Taxa de conversão de orçamentos
    @GetMapping("/gerencial/orcamentos/conversao")
    public ResponseEntity<Double> taxaConversaoOrcamentos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        double taxa = servicoGerencial.calcularTaxaConversao(dataInicio, dataFim);
        return ResponseEntity.ok(taxa);
    }

    // Relatório Texto (Consulta 5)
    @GetMapping("/gerencial/relatorios/vendasProdutoTexto")
    public ResponseEntity<String> relatorioTextoVendasPorProduto(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        String relatorio = servicoGerencial.gerarRelatorioVendasPorProdutoTexto(dataInicio, dataFim);
        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(relatorio);
    }
}
