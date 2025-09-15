import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import App from '../App';

beforeEach(() => {
  global.fetch = vi.fn(() =>
    Promise.resolve({ ok: true, json: () => Promise.resolve([]) })
  );
});

describe('App', () => {
  it('renderiza o título da aplicação', () => {
    render(<App />);
    expect(screen.getByText(/efetiva orçamento/i)).toBeInTheDocument();
  });

  it('exibe os três botões de navegação', () => {
    render(<App />);
    expect(screen.getByText(/catálogo/i)).toBeInTheDocument();
    expect(screen.getByText(/carrinho/i)).toBeInTheDocument();
    expect(screen.getByText(/gerencial/i)).toBeInTheDocument();
  });

  it('navega para área gerencial ao clicar', async () => {
    render(<App />);
    await userEvent.click(screen.getByText(/gerencial/i));
    expect(screen.getByText(/painel gerencial/i)).toBeInTheDocument();
  });

  it('navega para carrinho ao clicar', async () => {
    render(<App />);
    await userEvent.click(screen.getByText(/carrinho/i));
    expect(screen.getByText(/seu carrinho/i)).toBeInTheDocument();
  });
});
