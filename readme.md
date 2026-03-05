# ♨️ API REST CRUD de produtos com Java, Spring Boot JPA e PostgreSQL

![Status](https://img.shields.io/badge/Status-Em%20Desenvolvimento-orange?style=flat-square)

Projeto desenvolvido para colocar em prática conhecimentos em desenvolvimento de APIs Rest, Banco de dados

Este projeto mostra de forma prática, como funciona uma API REST CRUD, com aplicação em Java e banco de dados PostgreSQL.

---

## 🧰 Tecnologias e Ferramentas

- **Linguagem:** Java 21 (LTS)
- **Framework:** Spring Boot 3
- **Persistência:** Spring Data JPA / Hibernate
- **Banco de Dados:** PostgreSQL
- **Mapeamento:** MapStruct (Conversão DTO/Entity)
- **Produtividade:** Lombok

## 📂 Estrutura do projeto

```
api/
│
├── src/main/java/com/produto/api/
│   ├── controller/             # EndPoints da API
│   ├── dto/                    # Objetos de transferência de dados
│   ├── exception/              # Exceções do projeto
│   ├── mapper/                 # Mapeamento de objetos
│   ├── model/                  # Entidades
│   ├── repository/             # Interface de acesso a dados
│   ├── service/                # Lógica de negócio
│   └── ApiApplication.java     # Classe principal
│
├── resources/
│   └── application.properties  # Configurações do Spring Boot
│
├── pom.xml
└── readme.md
```

---

## 🚀 Executando localmente

### 1. Clone o repositório

```bash
git clone https://github.com/JoaoWohl/API-Produtos.git
```

### 2. Configure o ambiente

Certifique-se de que o banco de dados especificado na DB_URL já foi criado no seu PostgreSQL antes de rodar a aplicação.

Crie o arquivo `.env` na raiz com base no `.env.example`:

**No Linux:**

```bash
cp .env.example .env
```

**No Windows:**

```bash
copy .env.example .env
```

Abra o .env e preencha com as credenciais do seu banco de dados local.

### 3. Rodando a aplicação

```bash
mvn spring-boot:run
```

### 4. Acesse a aplicação utilizando

Acesse utilizando um software de teste de API (Postman/Insomnia)

http://localhost:8080

| Método   | Endpoint         | Descrição                         |
| -------- | ---------------- | --------------------------------- |
| `GET`    | `/products`      | Lista todos os produtos           |
| `GET`    | `/products/{id}` | Lista produto com ID informado    |
| `POST`   | `/products`      | Cria um novo produto              |
| `DELETE` | `/products/{id}` | Deleta produto com ID informado   |
| `PATCH`  | `/products/{id}` | Atualiza produto com ID informado |

**Exemplo de JSON para `POST`:**

```json
{
  "name": "Teclado Mecânico",
  "price": 250.0,
  "quantity": 10
}
```

---

## 🔍 Funcionalidades

- Cadastro de produtos com validação de campos
- Listagem de todos os produtos e busca por ID
- Atualização completa ou parcial dos dados do produto via PATCH
- Remoção lógica ou física de registros

---

## 📚 Aprendizados

- Criação e nomeação de Endpoints
- Tratamento de Erros com código HTTP apropriado
- Uso correto dos metodos HTTP (GET, POST, PATCH e DELETE)

---

### 👨‍💻 João V. Santos Wohl

💻 Portfolio | [🔗 Linkedin](https://www.linkedin.com/in/joao-wohl) | [✉️ Email](mailto:joaovitorsantoswohl9@gmail.com)
