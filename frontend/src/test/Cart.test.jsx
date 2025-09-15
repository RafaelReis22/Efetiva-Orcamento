import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import Cart from '../components/Cart';

describe('Cart', () => {
  it('exibe mensagem quando carrinho está vazio', () => {
    render(<Cart items={[]} onClear={() => {}} setItems={() => {}} />);
    expect(screen.getByText(/carrinho está vazio/i)).toBeInTheDocument();
  });

  it('exibe itens do carrinho quando preenchido', () => {
    const items = [{
      produto: { id: 1, descricao: 'Produto Teste', precoUnitario: 10.0 },
      quantidade: 2
    }];
    render(<Cart items={items} onClear={() => {}} setItems={() => {}} />);
    expect(screen.getByText('Produto Teste')).toBeInTheDocument();
  });

  it('exibe botão de limpar carrinho com itens', () => {
    const items = [{
      produto: { id: 1, descricao: 'Item A', precoUnitario: 5.0 },
      quantidade: 1
    }];
    render(<Cart items={items} onClear={() => {}} setItems={() => {}} />);
    expect(screen.getByText(/limpar carrinho/i)).toBeInTheDocument();
  });

  it('exibe botão gerar orçamento com itens', () => {
    const items = [{
      produto: { id: 1, descricao: 'Item B', precoUnitario: 20.0 },
      quantidade: 3
    }];
    render(<Cart items={items} onClear={() => {}} setItems={() => {}} />);
    expect(screen.getByText(/gerar orçamento/i)).toBeInTheDocument();
  });
});
