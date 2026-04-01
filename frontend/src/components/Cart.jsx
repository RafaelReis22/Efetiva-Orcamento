import { useState } from 'react'

export default function Cart({ items, onClear, setItems }) {
  const [nomeCliente, setNome] = useState('')
  const [estado, setEstado] = useState('SP')
  const [pais, setPais] = useState('Brasil')
  const [orcamento, setOrcamento] = useState(null)
  const [erro, setErro] = useState('')

  const gerarOrcamento = async () => {
    if (!nomeCliente) {
      setErro('Por favor, informe seu nome.')
      return
    }
    setErro('')
    
    const payload = {
      nomeCliente,
      estadoEntrega: estado,
      paisEntrega: pais,
      itens: items.map(it => ({
        idProduto: it.produto.id,
        qtdade: it.quantidade
      }))
    }

    try {
      const res = await fetch('/api/orcamentos', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      })
      const data = await res.json()
      
      if (!res.ok) {
        setErro(`Erro: ${data.status || 'Falha ao criar orçamento'}`)
        if(data.status === 'RECUSADO_LOCAL') setErro('Orçamento recusado: Local de entrega não atendido.')
      } else {
        setOrcamento(data)
        onClear()
      }
    } catch(e) {
      setErro('Erro de conexão: ' + e.message)
    }
  }

  const efetivarOrcamento = async () => {
    try {
      const res = await fetch(`/api/orcamentos/efetivar/${orcamento.id}`, {
        method: 'POST'
      })
      const data = await res.json()
      setOrcamento(data)
    } catch(e) {
      setErro('Erro ao efetivar.')
    }
  }

  if (orcamento) {
    try {
      return (
        <div className="glass-panel animate-fade-in text-center mx-auto" style={{maxWidth: '600px'}}>
        <h2>Orçamento Gerado: #{orcamento.id}</h2>
        <div className="summary-card text-left mt-4">
          <p><strong>Cliente:</strong> {orcamento.nomeCliente}</p>
          <p><strong>Entrega:</strong> {orcamento.estadoEntrega}, {orcamento.paisEntrega}</p>
          <p><strong>Validade:</strong> {orcamento.dataValidade}</p>
          <hr style={{borderColor: 'var(--glass-border)', margin: '1rem 0'}}/>
          
          <div className="flex-between">
            <span>Custo dos Itens: </span>
            <span>R$ {Number(orcamento.custoItens || 0).toFixed(2)}</span>
          </div>
          <div className="flex-between text-secondary">
            <span>Imposto Total: </span>
            <span>R$ {Number(orcamento.imposto || 0).toFixed(2)}</span>
          </div>
          <div className="flex-between" style={{color: 'var(--success-color)'}}>
            <span>Descontos: </span>
            <span>- R$ {Number(orcamento.desconto || 0).toFixed(2)}</span>
          </div>
          <hr style={{borderColor: 'var(--glass-border)', margin: '1rem 0'}}/>
          <div className="flex-between" style={{fontSize: '1.5rem', fontWeight: 'bold'}}>
            <span>Total: </span>
            <span>R$ {Number(orcamento.custoConsumidor || 0).toFixed(2)}</span>
          </div>
        </div>

        <div className="mt-4">
           <h4>Status: <span className={`badge ${orcamento.status === 'EFETIVADO' ? 'badge-success' : 'badge-warning'}`}>{orcamento.status}</span></h4>
        </div>

        {orcamento.status === 'PENDENTE' && (
          <button className="btn mt-4 w-full" onClick={efetivarOrcamento}>
            Efetivar Compra
          </button>
        )}

          <button className="btn btn-secondary mt-4 w-full" onClick={() => setOrcamento(null)}>
            Voltar
          </button>
        </div>
      )
    } catch(err) {
      return <div className="glass-panel text-center text-danger">Erro de renderização: {err.toString()} <br/><button onClick={() => setOrcamento(null)}>Voltar</button></div>
    }
  }

  return (
    <div className="glass-panel animate-fade-in mx-auto" style={{maxWidth: '800px'}}>
      <h2 className="mb-4">Seu Carrinho</h2>
      {erro && <div className="badge badge-danger mb-4 w-full text-center" style={{padding: '1rem'}}>{erro}</div>}
      
      {items.length === 0 ? (
        <p className="text-center text-secondary py-4">Seu carrinho está vazio.</p>
      ) : (
        <>
          <table>
            <thead>
              <tr>
                <th>Produto</th>
                <th>Preço Un.</th>
                <th>Qtd</th>
                <th>Subtotal</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {items.map((it, idx) => (
                <tr key={idx}>
                  <td>{it.produto.descricao}</td>
                  <td>R$ {it.produto.precoUnitario.toFixed(2)}</td>
                  <td>{it.quantidade}</td>
                  <td>R$ {(it.produto.precoUnitario * it.quantidade).toFixed(2)}</td>
                  <td>
                     <button className="btn btn-secondary" style={{padding: '0.25rem 0.5rem'}} onClick={() => {
                        setItems(items.filter((_, i) => i !== idx))
                     }}>X</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>

          <div className="mt-4" style={{display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '1rem'}}>
             <div>
                <label>Nome do Cliente</label>
                <input value={nomeCliente} onChange={e => setNome(e.target.value)} placeholder="João da Silva" />
             </div>
             <div>
                <label>Estado</label>
                <select value={estado} onChange={e => setEstado(e.target.value)}>
                   <option value="SP">São Paulo</option>
                   <option value="RS">Rio Grande do Sul</option>
                   <option value="PE">Pernambuco</option>
                   <option value="RJ">Rio de Janeiro (Não Atendido)</option>
                </select>
             </div>
             <div>
                <label>País</label>
                <select value={pais} onChange={e => setPais(e.target.value)}>
                   <option value="Brasil">Brasil</option>
                   <option value="Argentina">Argentina</option>
                </select>
             </div>
          </div>

          <div className="flex-between mt-4">
             <button className="btn btn-danger" onClick={onClear}>Limpar Carrinho</button>
             <button className="btn" onClick={gerarOrcamento}>Gerar Orçamento</button>
          </div>
        </>
      )}
    </div>
  )
}
