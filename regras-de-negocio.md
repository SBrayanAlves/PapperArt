# 📏 Regras de Negócio
← [README](../README.md) · [Visão do projeto](visao.md)

> Como a loja funciona, do ponto de vista do sistema. Cada regra tem um código (RN01, RN02...), que também aparece no código e no modelo.

## 👤 Clientes e funcionários
| # | Regra |
|---|---|
| RN02 | E-mail válido e sem repetir. O cliente também precisa informar o telefone, com DDD |
| RN11 | CPF válido e sem repetir. Na tela, ele aparece escondido: `***.982.247-**` |
| RN12 | Data de nascimento obrigatória e nunca no futuro |
| RN13 | Senha com pelo menos 8 caracteres, com letras e números. O sistema não guarda a senha, só um código gerado a partir dela (hash) |
| RN14 | Funcionário que saiu da loja é desativado e não entra mais no sistema |
| RN15 | Login errado mostra sempre a mesma mensagem ("E-mail ou senha inválidos"), sem dizer o que estava errado |

## 📦 Produtos
| # | Regra |
|---|---|
| RN01 | Preço maior que zero e estoque nunca negativo |

**Categorias:** Escolar, Escritório, Arte, Presentes e Outros.

## 🛒 Pedidos
| # | Regra |
|---|---|
| RN03 | Cada cliente tem no máximo 1 pedido aberto: esse pedido é o **carrinho** |
| RN04 | Não dá pra pedir mais unidades do que tem em estoque |
| RN05 | O preço do item fica **congelado** quando ele entra no pedido: se o produto mudar de preço depois, o pedido não muda |
| RN06 | Só o pedido aberto recebe itens e pagamentos |
| RN07 | O pedido só é pago com pelo menos 1 item e com os pagamentos somando o total. O estoque baixa nesse momento |
| RN10 | Só o pedido aberto pode ser cancelado |

## 💳 Pagamentos
Formas aceitas: **Pix**, **cartão de crédito**, **cartão de débito** e **dinheiro**. Um pedido pode ser pago com mais de uma forma (ex: parte em dinheiro, parte no cartão).

| # | Regra |
|---|---|
| RN08 | Débito é sempre à vista; crédito pode ser parcelado de 1 até 12 vezes |
| RN09 | No dinheiro, o valor recebido tem que cobrir o pagamento, e o sistema calcula o troco |

## 🔄 Vida de um pedido
```
ABERTO (carrinho) ──pagou──▶ PAGO ──entregou──▶ ENTREGUE
      └──desistiu──▶ CANCELADO
```

| Situação | Como fica |
|---|---|
| Comprou uma lapiseira e pagou | Pedido 1: pago |
| Separou folhas pra pagar quando voltar | Pedido 2: aberto, salvo no sistema |
| Voltou e pagou as folhas | Pedido 2: pago |
| Escolheu vários itens pra pagar de uma vez | Todos no mesmo pedido aberto |
| Pagou | O pedido vira pago; a próxima compra abre um pedido novo |
