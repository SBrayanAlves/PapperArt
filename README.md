# 🎨 PapperArt

Sistema desktop da **PapperArt**, uma papelaria, com duas áreas: **compras**, pro cliente, e **gestão**, pra quem trabalha na loja.

Projeto N2 de **Programação Orientada a Objetos** (UniEuro, prof. Aldo Henrique).

📄 [Visão do projeto](docs/visao.md) · 📏 [Regras de negócio](docs/regras-de-negocio.md) · 🧩 [Modelo: diagramas e banco](docs/modelo.md)

## 👥 Integrantes
| Nome | Responsável por |
|---|---|
| Sebastião Brayan de Araújo Alves | Código |
| Brittany Morgana de Souza Marques | Documentação e design das telas |

## 🛠️ Tecnologias
- **Java 17+** e **Maven**
- **Swing** (interface)
- **PostgreSQL**, acessado via **JDBC**
- **JUnit 5** (testes)

## ▶️ Como executar
> 🚧 Em construção.

1. Criar um banco no PostgreSQL (ex: `papperart`) e rodar o `banco.sql` nele
2. Copiar `config.exemplo.properties` para `config.properties` e preencher a URL, o usuário e a senha (o `config.properties` não vai pro Git)
3. Abrir o projeto no IntelliJ e rodar a classe `Main` do pacote `br.com.papperart.app`

### 🔑 Contas de demonstração
Criadas pelo `banco.sql`, só existem no banco local.

| Área | E-mail | Senha |
|---|---|---|
| Gestão | `admin@papperart.com` | `Admin1234` |
| Compras | `ana.souza@email.com` | `Cliente123` |

## 📂 Organização
| Pacote / pasta | O que tem |
|---|---|
| `app` | `Main`, que abre a tela |
| `model` | Classes de domínio |
| `dao` | Acesso ao banco (**todo o SQL fica aqui**) |
| `view` | Telas Swing |
| `exception` | Exceções personalizadas |
| `seguranca` | Hash das senhas (PBKDF2) |
| `docs/` | Documentação |
| `banco.sql` | Criação do banco + dados de exemplo |

## 📌 Andamento
- [ ] **Checkpoint (11/11):** diagrama de classes, classes de domínio com herança, `banco.sql` e commits dos integrantes
- [ ] Teste no laboratório (18/11)
- [ ] **Entrega e apresentação (02/12)**

## 🤖 Uso de IA
A documentação, a estrutura do projeto, as classes de domínio e o `banco.sql` foram feitos com apoio do **Claude** (Anthropic) e revisados e testados pelos integrantes. O detalhamento fica na declaração de uso de IA do artigo.
