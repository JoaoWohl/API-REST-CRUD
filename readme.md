# ♨️ API REST CRUD de produtos com Java, Spring Boot JPA e PostgreSQL

![Status](https://img.shields.io/badge/Status-Em%20Desenvolvimento-orange?style=flat-square)

Projeto desenvolvido para colocar em prática conhecimentos em desenvolvimento de APIs Rest, Banco de dados

Este projeto mostra de forma prática, como funciona uma API REST CRUD, com aplicação em Java e banco de dados PostgreSQL

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
├── .dockerignore               # Configuração de arquivos para Dockerfile
├── .env.example                # Exemplo de .env
├── docker-compose.yml          # Arquivo de configuração do Docker Compose
├── Dockerfile                  # Arquivo de construção da Imagem da API
├── pom.xml                     # Gerenciador de Dependências
└── readme.md
```

---

## 🛠️ Pré-Requisitos

Para rodar este projeto, você precisará de ferramentas diferentes dependendo de como deseja executá-lo:

### 🐳 Via Docker (Recomendado):

- [**Docker Desktop**](https://www.docker.com/products/docker-desktop/)

> `Nota para usuários Linux:` Certifique-se de ter o Docker Engine e o Docker Compose instalados e seu usuário adicionado ao grupo docker.

---

## 🚀 Executando localmente

### 1. Clone o repositório

```bash
git clone https://github.com/JoaoWohl/API-REST-CRUD.git
```

### 2. Configure o ambiente

Preencha o arquivo .env com as configurações desejadas. O Docker Compose utilizará essas variáveis para criar o banco de dados automaticamente para você

Crie o arquivo `.env` na raiz com base no `.env.example`:

**🐧 No Linux:**

```bash
cp .env.example .env
```

**🪟 No Windows:**

```bash
copy .env.example .env
```

#### Explicação da `.env`

| Variável      | Descrição                                            | Exemplo          |
| ------------- | ---------------------------------------------------- | ---------------- |
| `DB_NAME`     | Nome do banco de dados que o Spring tentará conectar | produtos         |
| `DB_USER`     | Usuário do banco de dados                            | `postgres`       |
| `DB_PASSWORD` | Senha do banco de dados                              | `sua_senha_aqui` |
| `API_PORT`    | Porta onde a aplicação será exposta                  | `8080`           |

Abra o .env e preencha com as credenciais que serão utilizadas pelos containers

### 3. Rodando a aplicação

Para iniciar a aplicação rode

```bash
docker compose up --build
```

Verifique se subiu com

```bash
docker compose logs -f
```

Para verificar o status e as portas ativas dos containers

```bash
docker compose ps
```

### 4. Acesse a aplicação utilizando

Acesse utilizando um software de teste de API (Postman/Insomnia)

Utilizando a porta configurada no seu `.env` (Padrão: http://localhost:8080)

| Método   | Endpoint                  | Descrição                                                |
| -------- | ------------------------- | -------------------------------------------------------- |
| `GET`    | `/products`               | Lista todos os produtos                                  |
| `GET`    | `/products/{id}`          | Lista produto com ID informado                           |
| `POST`   | `/products`               | Cria um novo produto                                     |
| `DELETE` | `/products/{id}`          | Deleta produto com ID informado                          |
| `PATCH`  | `/products/{id}`          | Atualização parcial ou completa do produto por ID        |
| `PATCH`  | `/products/{id}/withdraw` | Retira quantidade definida de produto de acordo com ID   |
| `PATCH`  | `/products/{id}/put`      | Adiciona quantidade definida de produto de acordo com ID |

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

### V1 - Criação da API ⚙️

- Criação e nomeação de Endpoints
- Tratamento de Erros com código HTTP apropriado
- Uso correto dos metodos HTTP (GET, POST, PATCH e DELETE)

### V2 - Dockeirização 🐳

- Build Multi-stage
- Orquestração com Docker Compose
- Externalização de Configurações
- Networking entre Containers
- Persistência de Dados
- Padronização de Ambiente

---

### 👨‍💻 João V. Santos Wohl

💻 Portfolio | [🔗 Linkedin](https://www.linkedin.com/in/joao-wohl) | [✉️ Email](mailto:joaovitorsantoswohl9@gmail.com)
