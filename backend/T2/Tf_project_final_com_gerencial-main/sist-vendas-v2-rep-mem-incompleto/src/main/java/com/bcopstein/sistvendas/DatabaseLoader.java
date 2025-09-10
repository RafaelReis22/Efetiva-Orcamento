package com.bcopstein.sistvendas;

import com.bcopstein.sistvendas.dominio.modelos.ItemDeEstoqueModel;
import com.bcopstein.sistvendas.dominio.modelos.ProdutoModel;
import com.bcopstein.sistvendas.dominio.persistencia.IEstoqueRepository;
import com.bcopstein.sistvendas.dominio.persistencia.IProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseLoader implements CommandLineRunner {

    private final IProdutoRepository produtoRepository;
    private final IEstoqueRepository estoqueRepository;

    @Autowired
    public DatabaseLoader(IProdutoRepository produtoRepository, IEstoqueRepository estoqueRepository) {
        this.produtoRepository = produtoRepository;
        this.estoqueRepository = estoqueRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (produtoRepository.count() == 0) {
            System.out.println("Iniciando seed do banco de dados...");
          
            ProdutoModel p1 = produtoRepository.save(new ProdutoModel("Produto A", 10.0));
            ProdutoModel p2 = produtoRepository.save(new ProdutoModel("Produto B Essencial*", 25.5));
            ProdutoModel p3 = produtoRepository.save(new ProdutoModel("Produto C", 5.0));
            ProdutoModel p4 = produtoRepository.save(new ProdutoModel("Produto D", 150.0));
            ProdutoModel p5 = produtoRepository.save(new ProdutoModel("Produto E Sem Estoque", 30.0));
            ProdutoModel p6 = produtoRepository.save(new ProdutoModel("Produto F", 80.0)); 

            
            estoqueRepository.save(new ItemDeEstoqueModel(p1, 100, 10, 200));
            estoqueRepository.save(new ItemDeEstoqueModel(p2, 50, 5, 100));
            estoqueRepository.save(new ItemDeEstoqueModel(p3, 200, 20, 300));
            estoqueRepository.save(new ItemDeEstoqueModel(p4, 20, 2, 50));
            estoqueRepository.save(new ItemDeEstoqueModel(p6, 5, 1, 10));
           

            System.out.println("Banco de dados inicializado com dados de seed.");
        } else {
            System.out.println("Banco de dados já contém dados. Seed não executado.");
        }
    }
}
