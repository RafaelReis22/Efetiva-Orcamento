package com.bcopstein.sistvendas.aplicacao.dtos;

import com.bcopstein.sistvendas.dominio.modelos.ItemDeEstoqueModel;

// DTO for representing stock items in API responses
public class ItemEstoqueDTO {
    private long idProduto;
    private String descricaoProduto;
    private int quantidadeDisponivel;
    private int estoqueMin;
    private int estoqueMax;

    public ItemEstoqueDTO(long idProduto, String descricaoProduto, int quantidadeDisponivel, int estoqueMin, int estoqueMax) {
        this.idProduto = idProduto;
        this.descricaoProduto = descricaoProduto;
        this.quantidadeDisponivel = quantidadeDisponivel;
        this.estoqueMin = estoqueMin;
        this.estoqueMax = estoqueMax;
    }

   
    public static ItemEstoqueDTO fromModel(ItemDeEstoqueModel model) {
        return new ItemEstoqueDTO(
            model.getProduto().getId(),
            model.getProduto().getDescricao(),
            model.getQuantidade(),
            model.getEstoqueMin(),
            model.getEstoqueMax()
        );
    }


    public long getIdProduto() {
        return idProduto;
    }

    public String getDescricaoProduto() {
        return descricaoProduto;
    }

    public int getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }

    public int getEstoqueMin() {
        return estoqueMin;
    }

    public int getEstoqueMax() {
        return estoqueMax;
    }
}
