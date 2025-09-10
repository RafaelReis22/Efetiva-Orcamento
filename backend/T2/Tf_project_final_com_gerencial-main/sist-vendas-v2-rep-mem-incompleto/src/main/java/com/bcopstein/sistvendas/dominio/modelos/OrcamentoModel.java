package com.bcopstein.sistvendas.dominio.modelos;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@Entity
public class OrcamentoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private LocalDate data;
    private String nomeCliente;
    private String estadoEntrega;
    private String paisEntrega;

    @OneToMany(mappedBy = "orcamento", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ItemPedidoModel> itens;
    
    private double custoItens; // Somatório dos custos dos itens
    private double impostoEstadual; // Imposto estadual/regional
    private double impostoFederal; // Imposto federal/nacional
    private double desconto;
    private double custoConsumidor; // Valor final
    private LocalDate dataValidade;
    private String status; // Ex: PENDENTE, EFETIVADO, EXPIRADO, RECUSADO_LOCAL, RECUSADO_ESTOQUE

   
    protected OrcamentoModel() {
        this.itens = new LinkedList<>();
        this.data = LocalDate.now();
        this.dataValidade = this.data.plusDays(21);
        this.status = "PENDENTE"; 
    }

    
    public OrcamentoModel(String nomeCliente, String estadoEntrega, String paisEntrega) {
        this(); 
        this.nomeCliente = nomeCliente;
        this.estadoEntrega = estadoEntrega;
        this.paisEntrega = paisEntrega;
    }

    public void addItensPedido(List<ItemPedidoModel> itensPedido){
        for(ItemPedidoModel item : itensPedido){
            item.setOrcamento(this); 
            this.itens.add(item);
        }
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
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

    public List<ItemPedidoModel> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedidoModel> itens) {
        this.itens = itens;
        for(ItemPedidoModel item : itens) {
            item.setOrcamento(this);
        }
    }

    public double getCustoItens() {
        return custoItens;
    }

    public void setCustoItens(double custoItens) {
        this.custoItens = custoItens;
    }

    public double getImpostoEstadual() {
        return impostoEstadual;
    }

    public void setImpostoEstadual(double impostoEstadual) {
        this.impostoEstadual = impostoEstadual;
    }

    public double getImpostoFederal() {
        return impostoFederal;
    }

    public void setImpostoFederal(double impostoFederal) {
        this.impostoFederal = impostoFederal;
    }

    public double getImposto() {
        return this.impostoEstadual + this.impostoFederal;
    }

    public double getDesconto() {
        return desconto;
    }

    public void setDesconto(double desconto) {
        this.desconto = desconto;
    }

    public double getCustoConsumidor() {
        return custoConsumidor;
    }

    public void setCustoConsumidor(double custoConsumidor) {
        this.custoConsumidor = custoConsumidor;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(LocalDate dataValidade) {
        this.dataValidade = dataValidade;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

  
    public boolean isValido() {
        return LocalDate.now().isBefore(this.dataValidade.plusDays(1)); 
    }

    
    public boolean isEfetivado() {
        return "EFETIVADO".equalsIgnoreCase(this.status);
    }

    public void efetiva() {
        if (isValido()) { 
            this.status = "EFETIVADO";
        } else {
            this.status = "EXPIRADO";
        }
    }
}
