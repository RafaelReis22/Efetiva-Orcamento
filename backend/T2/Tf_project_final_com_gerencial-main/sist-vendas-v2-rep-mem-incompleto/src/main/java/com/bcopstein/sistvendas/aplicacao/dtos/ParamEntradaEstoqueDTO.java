package com.bcopstein.sistvendas.aplicacao.dtos;

// DTO for receiving stock entry parameters
public class ParamEntradaEstoqueDTO {
    private long idProduto;
    private int quantidade;

 
    public ParamEntradaEstoqueDTO() {}

    
    public ParamEntradaEstoqueDTO(long idProduto, int quantidade) {
        this.idProduto = idProduto;
        this.quantidade = quantidade;
    }

   
    public long getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(long idProduto) {
        this.idProduto = idProduto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
}
