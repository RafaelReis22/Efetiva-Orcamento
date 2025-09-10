-- Insere dados na tabela produto_model
INSERT INTO produto_model (id, descricao, preco_unitario) VALUES
 (10, 'Televisor', 2000.0);
INSERT INTO produto_model (id, descricao, preco_unitario) VALUES
 (20, 'Geladeira', 3500.0);
INSERT INTO produto_model (id, descricao, preco_unitario) VALUES
 (30, 'Fogao', 1200.0);
INSERT INTO produto_model (id, descricao, preco_unitario) VALUES
 (40, 'Lava-louça', 1800.0);
INSERT INTO produto_model (id, descricao, preco_unitario) VALUES
 (50, 'Lava-roupas', 2870.0); -- Corrigi a capitalização para consistência

-- Adiciona produtos essenciais para teste de imposto de PE
INSERT INTO produto_model (id, descricao, preco_unitario) VALUES
 (60, 'Arroz*', 10.50); -- Essencial, para PE
INSERT INTO produto_model (id, descricao, preco_unitario) VALUES
 (70, 'Farinha*', 5.25); -- Essencial, para PE
INSERT INTO produto_model (id, descricao, preco_unitario) VALUES
 (80, 'Oleo', 12.00); -- Não essencial, para PE

-- Insere dados na tabela item_de_estoque_model
-- Garantir que todos os produtos essenciais ou usados em testes tenham estoque
INSERT INTO item_de_estoque_model (id, produto_id, quantidade, estoque_min, estoque_max) VALUES
 (100, 10, 20, 5, 50); -- Televisor
INSERT INTO item_de_estoque_model (id, produto_id, quantidade, estoque_min, estoque_max) VALUES
 (200, 20, 10, 5, 30); -- Geladeira
INSERT INTO item_de_estoque_model (id, produto_id, quantidade, estoque_min, estoque_max) VALUES
 (300, 40, 8, 5, 50); -- Lava-louça
INSERT INTO item_de_estoque_model (id, produto_id, quantidade, estoque_min, estoque_max) VALUES
 (400, 50, 12, 5, 40); -- Lava-roupas
INSERT INTO item_de_estoque_model (id, produto_id, quantidade, estoque_min, estoque_max) VALUES
 (500, 60, 50, 10, 100); -- Arroz* (essencial)
INSERT INTO item_de_estoque_model (id, produto_id, quantidade, estoque_min, estoque_max) VALUES
 (600, 70, 75, 10, 150); -- Farinha* (essencial)
INSERT INTO item_de_estoque_model (id, produto_id, quantidade, estoque_min, estoque_max) VALUES
 (700, 80, 30, 5, 60); -- Oleo (não essencial)