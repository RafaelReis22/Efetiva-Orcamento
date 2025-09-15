import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import Managerial from '../components/Managerial';

describe('Managerial', () => {
  it('renderiza o painel gerencial', () => {
    render(<Managerial />);
    expect(screen.getByText(/painel gerencial/i)).toBeInTheDocument();
  });

  it('exibe campos de data início e fim', () => {
    render(<Managerial />);
    expect(screen.getByLabelText(/data início/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/data fim/i)).toBeInTheDocument();
  });

  it('exibe botão de gerar relatórios', () => {
    render(<Managerial />);
    expect(screen.getByText(/gerar relatórios/i)).toBeInTheDocument();
  });
});
