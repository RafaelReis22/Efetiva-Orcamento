package com.bcopstein.sistvendas.dominio.regras.imposto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class ImpostoStrategyFactory {
    
    private final ImpostoRSStrategy impostoRSStrategy;
    private final ImpostoSPStrategy impostoSPStrategy;
    private final ImpostoPEStrategy impostoPEStrategy;

    @Autowired
    public ImpostoStrategyFactory(ImpostoRSStrategy impostoRSStrategy,
                                  ImpostoSPStrategy impostoSPStrategy,
                                  ImpostoPEStrategy impostoPEStrategy) {
        this.impostoRSStrategy = impostoRSStrategy;
        this.impostoSPStrategy = impostoSPStrategy;
        this.impostoPEStrategy = impostoPEStrategy;
    }

    public Optional<ImpostoEstadualStrategy> getStrategy(String estado) {
        if (estado == null) return Optional.empty();
        switch (estado.toUpperCase()) {
            case "RS":
                return Optional.of(impostoRSStrategy);
            case "SP":
                return Optional.of(impostoSPStrategy);
            case "PE":
                return Optional.of(impostoPEStrategy);
            default:
                return Optional.empty();
        }
    }
}
