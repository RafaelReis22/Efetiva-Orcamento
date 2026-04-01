import React, { useState } from 'react'
import Catalog from './components/Catalog'
import Cart from './components/Cart'
import Managerial from './components/Managerial'
import { ShoppingBag, LayoutDashboard, Store } from 'lucide-react'

class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true, error };
  }

  componentDidCatch(error, errorInfo) {
    console.error("ErrorBoundary capturou:", error, errorInfo);
  }

  render() {
    if (this.state.hasError) {
      return (
        <div style={{ padding: '2rem', color: 'red', background: '#222', borderRadius: '8px', margin: '2rem' }}>
          <h2>Algo quebrou a tela!</h2>
          <pre style={{ whiteSpace: 'pre-wrap' }}>{this.state.error?.toString()}</pre>
          <pre style={{ whiteSpace: 'pre-wrap', marginTop: '1rem' }}>{this.state.error?.stack}</pre>
        </div>
      );
    }
    return this.props.children; 
  }
}

function App() {
  const [activeTab, setActiveTab] = useState('catalog')
  const [cartItems, setCartItems] = useState([])

  const addToCart = (product, quantity) => {
    setCartItems(prev => {
      const existing = prev.find(item => item.produto.id === product.id)
      if (existing) {
        return prev.map(item => 
          item.produto.id === product.id 
            ? { ...item, quantidade: item.quantidade + quantity }
            : item
        )
      }
      return [...prev, { produto: product, quantidade: quantity }]
    })
  }

  const clearCart = () => setCartItems([])

  return (
    <div className="app-container">
      <header className="animate-fade-in">
        <h1>Efetiva Orçamento</h1>
        <nav>
          <button 
            className={`btn ${activeTab === 'catalog' ? '' : 'btn-secondary'}`}
            onClick={() => setActiveTab('catalog')}
          >
            <Store size={18} style={{ display: 'inline', marginRight: '8px', verticalAlign: 'middle' }}/>
            Catálogo
          </button>
          <button 
            className={`btn ${activeTab === 'cart' ? '' : 'btn-secondary'}`}
            onClick={() => setActiveTab('cart')}
          >
            <ShoppingBag size={18} style={{ display: 'inline', marginRight: '8px', verticalAlign: 'middle' }}/>
            Carrinho / Orçamentos ({cartItems.reduce((acc, it) => acc + it.quantidade, 0)})
          </button>
          <button 
            className={`btn ${activeTab === 'managerial' ? '' : 'btn-secondary'}`}
            onClick={() => setActiveTab('managerial')}
          >
            <LayoutDashboard size={18} style={{ display: 'inline', marginRight: '8px', verticalAlign: 'middle' }}/>
            Área Gerencial
          </button>
        </nav>
      </header>

      <main className="animate-fade-in" key={activeTab}>
        <ErrorBoundary>
          {activeTab === 'catalog' && <Catalog onAdd={addToCart} />}
          {activeTab === 'cart' && <Cart items={cartItems} onClear={clearCart} setItems={setCartItems} />}
          {activeTab === 'managerial' && <Managerial />}
        </ErrorBoundary>
      </main>
    </div>
  )
}

export default App
