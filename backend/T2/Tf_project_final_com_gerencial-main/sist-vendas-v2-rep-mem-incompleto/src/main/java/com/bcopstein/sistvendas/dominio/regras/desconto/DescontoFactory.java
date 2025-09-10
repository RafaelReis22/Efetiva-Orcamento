package com.bcopstein.sistvendas.dominio.regras.desconto;

import org.springframework.stereotype.Component;

@Component
public class DescontoFactory {
    
    public DescontoChain getDescontoChain() {
        // Cria a cadeia de responsabilidade para os descontos cumulativos
        DescontoChain descontoVolume = new DescontoPorVolume(null);
        DescontoChain descontoItem = new DescontoPorItem(descontoVolume);
        return descontoItem;
    }
}
