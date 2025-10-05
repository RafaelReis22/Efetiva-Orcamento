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
    expect(screen.getAllByText(/catálogo/i).length).toBeGreaterThan(0);
    expect(screen.getAllByText(/carrinho/i).length).toBeGreaterThan(0);
    expect(screen.getAllByText(/gerencial/i).length).toBeGreaterThan(0);
  });

  it('navega para área gerencial ao clicar', async () => {
    render(<App />);
    await userEvent.click(screen.getAllByText(/gerencial/i)[0]);
    expect(screen.getByText(/painel gerencial/i)).toBeInTheDocument();
  });

  it('navega para carrinho ao clicar', async () => {
    render(<App />);
    await userEvent.click(screen.getAllByText(/carrinho/i)[0]);
    expect(screen.getAllByText(/seu carrinho/i).length).toBeGreaterThan(0);
  });
});
