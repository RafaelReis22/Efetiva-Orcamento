package com.bcopstein.sistvendas.persistencia;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.bcopstein.sistvendas.dominio.modelos.OrcamentoModel;
import com.bcopstein.sistvendas.dominio.persistencia.IOrcamentoRepository;
public class OrcamentoRepMem implements IOrcamentoRepository {
    private static long idCount = 1;
    private List<OrcamentoModel> orcamentos;

    public OrcamentoRepMem(){
        this.orcamentos = new LinkedList<>();
        System.out.println("OrcamentoRepMem initialized (In-Memory - Likely Inactive with JPA)");
    }

    public List<OrcamentoModel> todos() {
        return new LinkedList<>(orcamentos); 
    }

    
    public OrcamentoModel cadastra(OrcamentoModel orcamento) {
        if (orcamento.getId() == 0){
            orcamento.setId(idCount++);
        }
        
        orcamentos.removeIf(o -> o.getId() == orcamento.getId());
        orcamentos.add(orcamento);
        System.out.println("OrcamentoRepMem: Orcamento cadastrado/atualizado (ID: " + orcamento.getId() + ")");
        return orcamento;
    }

    
    public OrcamentoModel recuperaPorId(long id) {
        return orcamentos.stream()
            .filter(or->or.getId() == id)
            .findAny()
            .orElse(null);
    }

    public void marcaComoEfetivado(long id) {
        OrcamentoModel orcamento = recuperaPorId(id);
        if (orcamento == null){
            throw new IllegalArgumentException("Orcamento não encontrado: " + id);
        }
        orcamento.setStatus("EFETIVADO"); 
        System.out.println("OrcamentoRepMem: Orcamento marcado como efetivado (ID: " + id + ")");
        cadastra(orcamento); 
    }

    
    @Override
    public List<OrcamentoModel> findByStatus(String status) {
         return orcamentos.stream()
                .filter(o -> status.equalsIgnoreCase(o.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public List<OrcamentoModel> findByDataBetween(LocalDate startDate, LocalDate endDate) {
        return orcamentos.stream()
                .filter(o -> !o.getData().isBefore(startDate) && !o.getData().isAfter(endDate))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<OrcamentoModel> findByNomeClienteContainingIgnoreCase(String nomeCliente) {
         return orcamentos.stream()
                .filter(o -> o.getNomeCliente() != null && o.getNomeCliente().toLowerCase().contains(nomeCliente.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<OrcamentoModel> findValidos(LocalDate currentDate) {
        return orcamentos.stream()
                .filter(o -> !o.getDataValidade().isBefore(currentDate))
                .collect(Collectors.toList());
    }

    @Override
    public List<OrcamentoModel> findEfetivadosByPeriodo(LocalDate startDate, LocalDate endDate) {
        return orcamentos.stream()
                .filter(o -> "EFETIVADO".equalsIgnoreCase(o.getStatus()))
                .filter(o -> !o.getData().isBefore(startDate) && !o.getData().isAfter(endDate))
                .collect(Collectors.toList());
    }

    @Override
    public <S extends OrcamentoModel> S save(S entity) {
        return (S) cadastra(entity); 
    }

    @Override
    public <S extends OrcamentoModel> List<S> saveAll(Iterable<S> entities) {
        List<S> result = new LinkedList<>();
        entities.forEach(entity -> result.add(save(entity)));
        return result;
    }

    @Override
    public Optional<OrcamentoModel> findById(Long id) {
        return Optional.ofNullable(recuperaPorId(id));
    }

    @Override
    public boolean existsById(Long id) {
        return recuperaPorId(id) != null;
    }

    @Override
    public List<OrcamentoModel> findAll() {
        return todos(); 
    }

    @Override
    public List<OrcamentoModel> findAllById(Iterable<Long> ids) {
        List<OrcamentoModel> result = new LinkedList<>();
        ids.forEach(id -> {
            OrcamentoModel orc = recuperaPorId(id);
            if (orc != null) {
                result.add(orc);
            }
        });
        return result;
    }

    @Override
    public long count() {
        return orcamentos.size();
    }

    @Override
    public void deleteById(Long id) {
        orcamentos.removeIf(o -> o.getId() == id);
    }

    @Override
    public void delete(OrcamentoModel entity) {
        deleteById(entity.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> ids) {
         ids.forEach(this::deleteById);
    }

    @Override
    public void deleteAll(Iterable<? extends OrcamentoModel> entities) {
        entities.forEach(this::delete);
    }

    @Override
    public void deleteAll() {
        orcamentos.clear();
        idCount = 1;
    }

    
    @Override
    public void flush() {
      
    }

    @Override
    public <S extends OrcamentoModel> S saveAndFlush(S entity) {
        return save(entity); 
    }

    @Override
    public <S extends OrcamentoModel> List<S> saveAllAndFlush(Iterable<S> entities) {
        return saveAll(entities);
    }

    @Override
    public void deleteAllInBatch(Iterable<OrcamentoModel> entities) {
        deleteAll(entities); 
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> ids) {
        deleteAllById(ids); 
    }

    @Override
    public void deleteAllInBatch() {
        deleteAll(); 
    }

    @Override
    public OrcamentoModel getOne(Long id) {
        return findById(id).orElseThrow(() -> new RuntimeException("Orcamento not found: " + id));
    }

    @Override
    public OrcamentoModel getById(Long id) {
         return findById(id).orElseThrow(() -> new RuntimeException("Orcamento not found: " + id));
    }

    @Override
    public OrcamentoModel getReferenceById(Long id) {
        return getById(id);
    }

   
    @Override
    public List<OrcamentoModel> findAll(org.springframework.data.domain.Sort sort) {
        System.out.println("OrcamentoRepMem: findAll(Sort) called - sorting not implemented.");
        return findAll(); 
    }

    @Override
    public org.springframework.data.domain.Page<OrcamentoModel> findAll(org.springframework.data.domain.Pageable pageable) {
        System.out.println("OrcamentoRepMem: findAll(Pageable) called - pagination not implemented.");
        return new org.springframework.data.domain.PageImpl<>(findAll(), pageable, count());
    }

    @Override
    public <S extends OrcamentoModel> Optional<S> findOne(org.springframework.data.domain.Example<S> example) {
        throw new UnsupportedOperationException("Query by Example not supported in OrcamentoRepMem");
    }

    @Override
    public <S extends OrcamentoModel> List<S> findAll(org.springframework.data.domain.Example<S> example) {
        throw new UnsupportedOperationException("Query by Example not supported in OrcamentoRepMem");
    }

    @Override
    public <S extends OrcamentoModel> List<S> findAll(org.springframework.data.domain.Example<S> example, org.springframework.data.domain.Sort sort) {
         throw new UnsupportedOperationException("Query by Example not supported in OrcamentoRepMem");
    }

    @Override
    public <S extends OrcamentoModel> org.springframework.data.domain.Page<S> findAll(org.springframework.data.domain.Example<S> example, org.springframework.data.domain.Pageable pageable) {
         throw new UnsupportedOperationException("Query by Example not supported in OrcamentoRepMem");
    }

    @Override
    public <S extends OrcamentoModel> long count(org.springframework.data.domain.Example<S> example) {
         throw new UnsupportedOperationException("Query by Example not supported in OrcamentoRepMem");
    }

    @Override
    public <S extends OrcamentoModel> boolean exists(org.springframework.data.domain.Example<S> example) {
         throw new UnsupportedOperationException("Query by Example not supported in OrcamentoRepMem");
    }

    
    @Override
    public <S extends OrcamentoModel, R> R findBy(org.springframework.data.domain.Example<S> example, java.util.function.Function<org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
         throw new UnsupportedOperationException("Query by Example with FluentQuery not supported in OrcamentoRepMem");
    }
}
