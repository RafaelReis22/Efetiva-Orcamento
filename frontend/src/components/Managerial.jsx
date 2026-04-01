import { useState } from 'react'

export default function Managerial() {
  const [dataInicio, setDataInicio] = useState('2023-01-01')
  // Usar data atual para fim, como YYYY-MM-DD
  const [dataFim, setDataFim] = useState(new Date().toISOString().split('T')[0])
  const [resultados, setResultados] = useState(null)
  const [loading, setLoading] = useState(false)

  const carregarRelatorios = async () => {
    setLoading(true)
    try {
      const q = `?dataInicio=${dataInicio}&dataFim=${dataFim}`
      const [volRes, prodRes, calcRes, txtRes] = await Promise.all([
        fetch(`/api/gerencial/vendas/volume${q}`).then(r => r.ok ? r.json() : 0),
        fetch(`/api/gerencial/produtos/vendas${q}`).then(r => r.ok ? r.json() : {}),
        fetch(`/api/gerencial/orcamentos/conversao${q}`).then(r => r.ok ? r.json() : 0),
        fetch(`/api/gerencial/relatorios/vendasProdutoTexto${q}`).then(r => r.ok ? r.text() : 'Erro ou sem dados para o relatorio'),
      ])

      setResultados({
        volume: Number(volRes),
        produtos: prodRes,
        conversao: Number(calcRes),
        texto: txtRes
      })
    } catch(e) {
      console.error(e)
    }
    setLoading(false)
  }

  return (
    <div className="animate-fade-in mx-auto" style={{maxWidth: '1000px'}}>
      <div className="glass-panel mb-4">
        <h2 className="mb-4">Painel Gerencial</h2>
        <div style={{display: 'flex', gap: '1rem', alignItems: 'flex-end'}}>
           <div style={{flex: 1}}>
             <label>Data Início</label>
             <input type="date" value={dataInicio} onChange={e => setDataInicio(e.target.value)} />
           </div>
           <div style={{flex: 1}}>
             <label>Data Fim</label>
             <input type="date" value={dataFim} onChange={e => setDataFim(e.target.value)} />
           </div>
           <div>
             <button className="btn mb-4" onClick={carregarRelatorios} disabled={loading}>
               {loading ? 'Carregando...' : 'Gerar Relatórios'}
             </button>
           </div>
        </div>
      </div>

      {resultados && (
        <div className="grid">
          <div className="glass-panel">
             <h3>Volume Total de Vendas</h3>
             <div style={{fontSize: '2.5rem', fontWeight: 'bold', color: 'var(--success-color)', marginTop: '1rem'}}>
               R$ {(resultados.volume || 0).toFixed(2)}
             </div>
             <p className="text-secondary mt-2">No período selecionado</p>
          </div>

          <div className="glass-panel">
             <h3>Taxa de Conversão</h3>
             <div style={{fontSize: '2.5rem', fontWeight: 'bold', color: 'var(--accent-color)', marginTop: '1rem'}}>
               {((resultados.conversao || 0) * 100).toFixed(1)}%
             </div>
             <p className="text-secondary mt-2">Orçamentos efetivados / totais</p>
          </div>

          <div className="glass-panel" style={{gridColumn: '1 / -1'}}>
             <h3>Relatório Consolidado (Texto)</h3>
             <pre style={{
                background: 'rgba(0,0,0,0.3)', 
                padding: '1rem', 
                borderRadius: '8px',
                border: '1px solid var(--glass-border)',
                marginTop: '1rem',
                overflowX: 'auto',
                fontFamily: 'monospace'
             }}>
{resultados.texto}
             </pre>
          </div>
        </div>
      )}
    </div>
  )
}
