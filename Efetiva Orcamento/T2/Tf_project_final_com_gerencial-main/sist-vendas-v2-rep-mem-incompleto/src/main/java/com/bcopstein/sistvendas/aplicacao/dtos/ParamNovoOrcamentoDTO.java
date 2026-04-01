package com.bcopstein.sistvendas.aplicacao.dtos;

import java.util.List;

// DTO to encapsulate parameters for creating a new budget
public class ParamNovoOrcamentoDTO {
    private List<ItemPedidoDTO> itens;
    private String nomeCliente;
    private String estadoEntrega;
    private String paisEntrega;

    
    public ParamNovoOrcamentoDTO() {}

    
    public ParamNovoOrcamentoDTO(List<ItemPedidoDTO> itens, String nomeCliente, String estadoEntrega, String paisEntrega) {
        this.itens = itens;
        this.nomeCliente = nomeCliente;
        this.estadoEntrega = estadoEntrega;
        this.paisEntrega = paisEntrega;
    }

    
    public List<ItemPedidoDTO> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedidoDTO> itens) {
        this.itens = itens;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getEstadoEntrega() {
        return estadoEntrega;
    }

    public void setEstadoEntrega(String estadoEntrega) {
        this.estadoEntrega = estadoEntrega;
    }

    public String getPaisEntrega() {
        return paisEntrega;
    }

    public void setPaisEntrega(String paisEntrega) {
        this.paisEntrega = paisEntrega;
    }
}
