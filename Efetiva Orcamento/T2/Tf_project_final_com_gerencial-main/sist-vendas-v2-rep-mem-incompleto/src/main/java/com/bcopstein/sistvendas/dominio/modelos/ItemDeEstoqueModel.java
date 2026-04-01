package com.bcopstein.sistvendas.dominio.modelos;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
public class ItemDeEstoqueModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "produto_id") 
    private ProdutoModel produto;
    
    private int quantidade;
    private int estoqueMin;
    private int estoqueMax;

  
    protected ItemDeEstoqueModel() {}

    public ItemDeEstoqueModel(long id, ProdutoModel produto, int quantidade, int estoqueMin, int estoqueMax) {
        this.id = id;
        this.produto = produto;
        this.quantidade = quantidade;
        this.estoqueMin = estoqueMin;
        this.estoqueMax = estoqueMax;
    }

   
    public ItemDeEstoqueModel(ProdutoModel produto, int quantidade, int estoqueMin, int estoqueMax) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.estoqueMin = estoqueMin;
        this.estoqueMax = estoqueMax;
    }

    public long getId() {
        return id;
    }

    public ProdutoModel getProduto() {
        return produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public int getEstoqueMin() {
        return estoqueMin;
    }

    public int getEstoqueMax() {
        return estoqueMax;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public void setEstoqueMin(int estoqueMin) {
        this.estoqueMin = estoqueMin;
    }

    public void setEstoqueMax(int estoqueMax) {
        this.estoqueMax = estoqueMax;
    }

    @Override
    public String toString() {
        return "ItemDeEstoque [id=" + id + ", produto=" + (produto != null ? produto.getId() : null) + ", quantidade=" + quantidade + ", estoqueMin="
                + estoqueMin + ", estoqueMax=" + estoqueMax + "]";
    }
   
}
