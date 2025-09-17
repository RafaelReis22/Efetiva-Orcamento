import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import Managerial from '../components/Managerial';

describe('Managerial', () => {
  it('renderiza o painel gerencial', () => {
    render(<Managerial />);
    expect(screen.getByText(/painel gerencial/i)).toBeInTheDocument();
  });

  it('exibe labels de data início e fim', () => {
    render(<Managerial />);
    expect(screen.getByText(/data início/i)).toBeInTheDocument();
    expect(screen.getByText(/data fim/i)).toBeInTheDocument();
  });

  it('exibe dois campos de data', () => {
    const { container } = render(<Managerial />);
    const dateInputs = container.querySelectorAll('input[type="date"]');
    expect(dateInputs).toHaveLength(2);
  });

  it('exibe botão de gerar relatórios', () => {
    render(<Managerial />);
    expect(screen.getByText(/gerar relatórios/i)).toBeInTheDocument();
  });
});
