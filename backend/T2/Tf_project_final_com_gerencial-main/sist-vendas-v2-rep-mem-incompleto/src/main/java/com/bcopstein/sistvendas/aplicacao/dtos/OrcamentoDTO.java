package com.bcopstein.sistvendas.aplicacao.dtos;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.bcopstein.sistvendas.dominio.modelos.ItemPedidoModel;
import com.bcopstein.sistvendas.dominio.modelos.OrcamentoModel;

public class OrcamentoDTO {
    private long id;
    private List<ItemPedidoDTO> itens;
    private double custoItens;
    private double imposto;
    private double desconto;
    private double custoConsumidor;
    private String status; 

    
    public OrcamentoDTO(long id, List<ItemPedidoDTO> itens, double custoItens, double imposto, double desconto, double custoConsumidor, String status) {
        this.id = id;
        this.itens = itens;
        this.custoItens = custoItens;
        this.imposto = imposto;
        this.desconto = desconto;
        this.custoConsumidor = custoConsumidor;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public List<ItemPedidoDTO> getItens(){
        return itens;
    }

    public double getCustoItens() {
        return custoItens;
    }

    public double getImposto() {
        return imposto;
    }

    public double getDesconto() {
        return desconto;
    }

    public double getCustoConsumidor() {
        return custoConsumidor;
    }

    
    public String getStatus() {
        return status;
    }

 
    public static OrcamentoDTO fromModel(OrcamentoModel orcamento){
        List<ItemPedidoDTO> itens = new ArrayList<>(orcamento.getItens().size());
        for(ItemPedidoModel ip:orcamento.getItens()){
            itens.add(ItemPedidoDTO.fromModel(ip));
        }
        return new OrcamentoDTO(orcamento.getId(), itens, orcamento.getCustoItens(),
                                orcamento.getImposto(), orcamento.getDesconto(), orcamento.getCustoConsumidor(), orcamento.getStatus()); 
    }
}

