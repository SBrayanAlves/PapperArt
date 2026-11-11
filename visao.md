# 🧭 Visão do Projeto
← [README](../README.md) · [Regras de negócio](regras-de-negocio.md)

## 🎨 A PapperArt
Papelaria completa, criada há cerca de 3 anos e em crescimento.

## 🎯 Objetivo
Um sistema desktop com duas áreas:
- **Compras**, onde o cliente vê os produtos, monta o carrinho e paga
- **Gestão**, onde quem trabalha na loja controla os **produtos** e o estoque, os **clientes**, os **funcionários** e os **pedidos**

## 👤 Quem usa
| Quem | Área | Como entra |
|---|---|---|
| Cliente | Compras | E-mail e senha |
| Funcionário | Gestão | E-mail e senha |

## 📦 Escopo
### ✅ O sistema faz
- **Login** com e-mail e senha, levando cada usuário pra sua área
- **Área de compras:** ver os produtos, montar o pedido com vários itens (o carrinho) e pagar com Pix, cartão de crédito ou débito, ou dinheiro com troco, inclusive dividindo em mais de uma forma
- **Área de gestão:** cadastrar, listar, alterar e excluir produtos, clientes e funcionários; acompanhar o status dos pedidos (aberto, pago, entregue ou cancelado); desativar o acesso de quem saiu da loja

### ❌ Fora do escopo
- Recuperação de senha por e-mail
- Emissão de nota fiscal
- Controle de caixa (abertura e fechamento do dia)

## 🛠️ Tecnologias
Java 17+ e Maven · Swing · PostgreSQL com JDBC · JUnit 5

## 👥 Equipe
| Pessoa | Papel |
|---|---|
| Brayan | Código |
| Brittany | Dona da PapperArt · documentação e design das telas |
