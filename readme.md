<h3 align="center">♨️ <b>API-REST-CRUD</b></h3>
<div align="center">

![Status](https://img.shields.io/badge/Status-Em%20Desenvolvimento-orange?style=flat-square)

</div>

<p align="center">
    <!--Aqui vai ter uma descrição do projeto-->
    API-REST-CRUD de produtos com autenticação e permissão através de JWT e dockerização com Docker Compose
</p>

---

## 📝 Índice
- [Sobre](#-sobre)
- [Como Rodar](#-como-rodar)
- [Funcionalidades](#-funcionalidades)
- [Uso](#-uso)
- [Tecnologias](#-feito-com)
- [Aprendizados](#-aprendizados)
- [Autor](#-joão-v-santos-wohl)

## 🔎 Sobre
Sistema de gerenciamento de estoque de produtos com autenticação e controle de acesso por perfis.

Projeto desenvolvido para colocar em prática conhecimentos em desenvolvimento de APIs Rest usando conhecimentos de persistência e modelagem de dados, segurança e controle de acesso, produtividade e clean code, qualidade e testabilidade e infraestrutura e portabilidade.

## 🚀 Como Rodar

### Pré Requisitos
<!--Aqui é descrito o que precisa para rodar o projeto com Docker e com Maven e Java (Manualmente) -->
Para rodar este projeto, você precisará de ferramentas diferentes dependendo de como deseja executá-lo:

#### 🐳 Docker (Recomendado)
- [**Docker Desktop**](https://www.docker.com/products/docker-desktop/)
> `Nota para usuários Linux:` Certifique-se de ter o Docker Engine e o Docker Compose instalados e seu usuário adicionado ao grupo docker.

**OU**

#### ✍️ Manualmente
**[Maven](https://maven.apache.org/download.cgi) `v3.9+`**, **[Java](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html) `21+`** e **[PostgreSQL](https://www.postgresql.org/download/) `17+`**

---
### 📥 Instalação
Para começar a instalação do projeto clone o repositório com o código a seguir.
```bash
git clone https://github.com/JoaoWohl/API-REST-CRUD.git
```

Após clonar o repositório utilize uma das opções abaixo para rodar o seu projeto.

---
### 🐳 Docker
Para rodar a aplicação utilizando o docker basta copiar o `.env.example` como `.env` configurá-lo e rodar a aplicação com o docker compose.

**Clique no seu Sistema Operacional:**

<details>
  <br>
  <summary style="font-weight: bold;cursor:pointer;">🐧 Linux</summary>
  
  ```bash
  cp .env.example .env
  ``` 
  
  ```bash
  docker-compose up --build -d    
  ```
</details>

<details>
  <br>
  <summary style="font-weight: bold;cursor:pointer;">🪟 Windows</summary>
  
  ```bash
  copy .env.example .env
  ``` 
  
  ```bash
  docker-compose up --build -d    
  ```
</details>

---
### ✍️ Manualmente
Para rodar a aplicação manualmente precisamos configurar a `.env` através do terminal.

**Clique no seu Sistema Operacional:**

<details>
<br>
<summary style="font-weight: bold;cursor:pointer;">🐧 Linux</summary>
```bash
mvn clean compile
```

```bash
DB_NAME="postgre" DB_URL="jdbc:postgresql://localhost:5432/db_name" DB_USER="postgres" DB_PASSWORD="1234" API_PORT="8080" JWT_SECRET="Chave-Longa-Super-Secreta-Para-JWT" mvn spring-boot:run   
```
</details>

<details>
<br>
<summary style="font-weight: bold;cursor:pointer;">🪟 Windows</summary>

```bash
mvn clean compile
```

```bash
$env:DB_NAME="postgre";
$env:DB_URL="jdbc:postgresql://localhost:5432/db_name";
$env:DB_USER="postgres";
$env:DB_PASSWORD="1234";
$env:API_PORT="8080";
$env:JWT_SECRET="Chave-Longa-Super-Secreta-Para-JWT";
mvn spring-boot:run
```
</details>

---
### 📋 Descrição `.env.example`
| Variável      | Descrição                                                 | Exemplo                              |
| ------------- | --------------------------------------------------------- | ------------------------------------ |
| `DB_NAME`     | Nome do banco de dados (Utilizar apenas quando rodar com docker) | `my_db`                              |
| `DB_URL`      | URL do banco de dados (Utilizar apenas quando rodar manualmente) | `jdbc:postgresql://db:5432/db_name`  |
| `DB_USER`     | Nome usuário do banco de dados                            | `postgres`                           |
| `DB_PASSWORD` | Senha do banco de dados                                   | `1234`                               |
| `API_PORT`    | Porta que será exposta pela API                           | `8080`                               |
| `JWT_SECRET`  | Chave de criptografia do JWT                              | `Chave-Longa-Super-Secreta-Para-JWT` |
---
## 📄 Documentação
 
A API conta com documentação interativa gerada pelo **Swagger UI**, onde é possível visualizar todos os endpoints, os parâmetros esperados, os possíveis retornos e testá-los diretamente pelo navegador.
 
Com a aplicação rodando, acesse:
 
```
http://localhost:8080/swagger-ui.html
```

## 💡 Funcionalidades
<!-- Aqui será descrito as funcionalidades da aplicação. -->
- Cadastro e gerenciamento de usuários
- Autenticação stateless com JWT
- Controle de acesso por roles (ADMIN, USER)
- Proteção de endpoints públicos e privados
- Cadastro e gerenciamento de produtos

## 📱 Uso
<!-- Aqui será descrito como utilizar a aplicação, descrevendo quais são os endpoints e como eles funcionam além de quem tem acesso a cada um deles -->
### 🔐 Autenticação
|Método|Endpoint|Acesso|Descrição|
|---|---|---|---|
|`POST`|`/auth/register`|Público|Cadastra usuário|
|`POST`|`/auth/login`|Público|Retorna JWT|
|`POST`|`/auth/admin/register`|ADMIN|Cadastra usuário com Acesso específico|

#### `POST /auth/register` − Criar Usuário
```json
{
  "name":"ExamplaName",
  "login":"ExamplaLogin@Example.com",
  "password":"ExamplePassword"
}
```

#### `POST /auth/admin/register` − Criar Usuário com Acesso específico
```json
{    
  "name":"ExamplaName",
  "login":"ExamplaLogin@Example.com",
  "password":"ExamplePassword",
  "role":"ADMIN"
}
```

#### `POST /auth/login` − Fazer Login
```json
{
  "login":"ExamplaLogin@Example.com",
  "password":"ExamplePassword"
}
```

### 📦 Produtos
|Método|Endpoint|Acesso|Descrição|
|---|---|---|---|
|`GET`|`/products`|USER, ADMIN|Lista todos os Produtos|
|`POST`|`/products`|ADMIN|Cria Produto|
|`GET`|`/products/{id}`|USER, ADMIN|Lista Produto específico|
|`DELETE`|`/products/{id}`|ADMIN|Deleta Produto|
|`PATCH`|`/products/{id}`|USER, ADMIN|Atualiza Produto|
|`PATCH`|`/products/{id}/put`|USER, ADMIN|Adiciona Quantidade de produto no estoque|
|`PATCH`|`/products/{id}/withdraw`|USER, ADMIN|Retira Quantidade de produto no estoque|

#### `POST /products` − Adicionar Produto
```json
{
  "name":"Bala",
  "price":1.99,
  "quantity": 1
}
```

#### `PATCH /products/{id}` − Atualizar Produto
```json
{
  "name":"Bala",
  "price":1.99,
  "quantity": 1
}
```

#### `PATCH /products/{id}/put` − Colocar Produto
```json
{
  "quantity": 1
}
```

#### `PATCH /products/{id}/withdraw` − Retirar Produto
```json
{
  "quantity": 1
}
```

## 🧰 Feito com
<!-- Aqui será descrito as técnologias e bibliotecas que foram utilizadas para fazer o projeto como Java, Srping Boot, Spring Security, JWT, PostgreSQL, etc. -->
![Java](https://img.shields.io/badge/Java-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat-square&logo=spring&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=flat-square&logo=springsecurity&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=flat-square&logo=jsonwebtokens&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=flat-square&logo=postgresql&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=flat-square&logo=spring&logoColor=white)
![JUnit](https://img.shields.io/badge/JUnit5-25A162?style=flat-square&logo=junit5&logoColor=white)
![Mockito](https://img.shields.io/badge/Mockito-78A641?style=flat-square&logo=mockito&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=flat-square&logo=apachemaven&logoColor=white)


## 📚 Aprendizados
<!-- Aqui será descrito cada aprendizado adquirido a cada Versão de atualização -->
### V1 - Criação da API ⚙️

- **Criação e nomeação de Endpoints** — seguindo as convenções REST, como uso
de substantivos no plural e hierarquia de recursos
- **Tratamento de Erros** — retornando códigos HTTP apropriados (404, 400, 409)
com mensagens padronizadas
- **Uso correto dos métodos HTTP** — entendendo a semântica de GET, POST,
PATCH e DELETE

### V2 - Dockerização 🐳

- **Build Multi-stage** — separando etapas de build e runtime para gerar
uma imagem final menor e mais eficiente
- **Orquestração com Docker Compose** — subindo múltiplos containers
(aplicação e banco) com um único comando
- **Externalização de Configurações** — usando variáveis de ambiente para
não expor dados sensíveis como senhas e URLs no código
- **Networking entre Containers** — configurando a comunicação interna
entre os serviços sem expor portas desnecessárias
- **Persistência de Dados** — utilizando volumes para garantir que os dados
do banco não sejam perdidos ao recriar os containers
- **Padronização de Ambiente** — garantindo que a aplicação rode da mesma
forma em qualquer máquina

### V3 - Autenticação e Segurança 🔒

- **Autenticação stateless com JWT** — geração e validação de tokens utilizando
a biblioteca java-jwt da Auth0
- **Configuração do Spring Security** — criação de filtros para interceptar
requisições e validar o token antes de chegar nos endpoints
- **Controle de acesso por roles** — protegendo endpoints de acordo com o
perfil do usuário (ex: ADMIN, USER)
- **Proteção de endpoints** — configuração de rotas públicas e privadas no SecurityFilterChain

### V4 - Documentação com Swagger 📄

- **Documentação de APIs com OpenAPI/Swagger** — descrevendo endpoints, parâmetros e respostas de forma padronizada
- **Boas práticas de documentação** — entendendo o que vale a pena documentar e o que é excesso, focando no que agrega valor para quem consome a API

## 👨‍💻 João V. Santos Wohl
💻 Portfolio | [🔗 Linkedin](https://www.linkedin.com/in/joao-wohl) | [✉️ Email](mailto:joaovitorsantoswohl9@gmail.com)