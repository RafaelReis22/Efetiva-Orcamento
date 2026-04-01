import { useState, useEffect } from 'react'

export default function Catalog({ onAdd }) {
  const [products, setProducts] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    fetch('/api/produtos')
      .then(res => res.json())
      .then(data => {
        setProducts(data)
        setLoading(false)
      })
      .catch(err => {
        console.error(err)
        setLoading(false)
      })
  }, [])

  if (loading) return <div className="glass-panel text-center">Carregando catálogo premium...</div>

  return (
    <div>
      <h2 className="mb-4">Produtos Disponíveis</h2>
      <div className="grid">
        {products.map((p, i) => (
          <ProductCard key={p.id} product={p} onAdd={onAdd} idx={i} />
        ))}
      </div>
    </div>
  )
}

function ProductCard({ product, onAdd, idx }) {
  const [qtd, setQtd] = useState(1)

  return (
    <div className="glass-panel product-card animate-fade-in" style={{ animationDelay: `${idx * 0.1}s` }}>
      <div>
        <h3>{product.descricao}</h3>
        {product.descricao.endsWith('*') && (
          <span className="badge badge-warning mb-4">Produto Essencial</span>
        )}
        <div className="price">
          R$ {product.precoUnitario.toFixed(2)}
        </div>
      </div>
      
      <div className="cart-controls">
        <input 
          type="number" 
          min="1" 
          value={qtd} 
          onChange={(e) => setQtd(parseInt(e.target.value) || 1)} 
        />
        <button 
          className="btn" 
          style={{flex: 1}}
          onClick={() => {
            onAdd(product, qtd)
            setQtd(1)
          }}
        >
          Adicionar
        </button>
      </div>
    </div>
  )
}
