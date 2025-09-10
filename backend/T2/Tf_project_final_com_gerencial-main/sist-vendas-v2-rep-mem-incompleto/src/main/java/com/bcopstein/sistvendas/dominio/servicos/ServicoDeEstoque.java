package com.bcopstein.sistvendas.dominio.servicos;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bcopstein.sistvendas.dominio.persistencia.IEstoqueRepository;
import com.bcopstein.sistvendas.dominio.persistencia.IProdutoRepository;
import com.bcopstein.sistvendas.dominio.modelos.ItemDeEstoqueModel;
import com.bcopstein.sistvendas.dominio.modelos.ProdutoModel;

@Service
public class ServicoDeEstoque{
    private IEstoqueRepository estoqueRep;
    private IProdutoRepository produtosRep;
    
    @Autowired
    public ServicoDeEstoque(IProdutoRepository produtosRep, IEstoqueRepository estoqueRep){
        this.produtosRep = produtosRep;
        this.estoqueRep = estoqueRep;
    }
 
    // Retorna todos os produtos que tem alguma quantidade em estoque
    public List<ProdutoModel> produtosDisponiveis(){
        // Usa a query customizada no repositório para buscar apenas os produtos
        return estoqueRep.findProdutosComEstoque(); 
    }

    // Retorna um produto específico pelo ID
    public ProdutoModel produtoPorCodigo(long id){
        // Usa o método findById do JpaRepository, que retorna Optional
        Optional<ProdutoModel> produtoOpt = this.produtosRep.findById(id);
        return produtoOpt.orElse(null); // Retorna null se não encontrado
    }

    // Retorna a quantidade em estoque de um produto específico
    public int qtdadeEmEstoque(long idProduto){
        Optional<ItemDeEstoqueModel> itemOpt = estoqueRep.findByProdutoId(idProduto);
        return itemOpt.map(ItemDeEstoqueModel::getQuantidade).orElse(0); // Retorna 0 se não encontrado
    }

    // Dá baixa na quantidade de um item no estoque
    public boolean baixaEstoque(long idProduto, int quantidade){
        Optional<ItemDeEstoqueModel> itemOpt = estoqueRep.findByProdutoId(idProduto);
        if (itemOpt.isPresent()) {
            ItemDeEstoqueModel item = itemOpt.get();
            if (item.getQuantidade() >= quantidade) {
                item.setQuantidade(item.getQuantidade() - quantidade);
                estoqueRep.save(item); // Salva a alteração no banco
                return true;
            } else {
                return false; // Quantidade insuficiente
            }
        } else {
            return false; // Produto não encontrado no estoque
        }
    }  

    // Adiciona quantidade a um item no estoque (novo método para endpoint)
    public boolean entradaEstoque(long idProduto, int quantidade) {
        Optional<ItemDeEstoqueModel> itemOpt = estoqueRep.findByProdutoId(idProduto);
        if (itemOpt.isPresent()) {
            ItemDeEstoqueModel item = itemOpt.get();
            item.setQuantidade(item.getQuantidade() + quantidade);
            estoqueRep.save(item);
            return true;
        } else {
            // Se o produto existe mas não está no estoque, podemos adicioná-lo
            Optional<ProdutoModel> produtoOpt = produtosRep.findById(idProduto);
            if (produtoOpt.isPresent()) {
                // Valores padrão para min/max podem ser definidos aqui ou vir de outro lugar
                ItemDeEstoqueModel novoItem = new ItemDeEstoqueModel(produtoOpt.get(), quantidade, 10, 100); 
                estoqueRep.save(novoItem);
                return true;
            } else {
                return false; // Produto não existe
            }
        }
    }

    // Retorna todos os itens de estoque (para endpoint de consulta geral)
    public List<ItemDeEstoqueModel> todosItensEstoque() {
        return estoqueRep.findAll();
    }

    // Retorna itens de estoque para uma lista de IDs de produto (para endpoint de consulta específica)
    public List<ItemDeEstoqueModel> consultaEstoquePorProdutos(List<Long> idsProdutos) {
        return estoqueRep.findByProdutoIdIn(idsProdutos);
    }
}
