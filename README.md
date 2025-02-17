# Orquestração de Sagas com Spring Boot, Kafka e Docker

Este projeto demonstra o padrão de **Orquestração de Sagas** implementado com **Spring Boot**, **Apache Kafka** e **Docker** para gerenciar transações distribuídas entre múltiplos microsserviços.

## Visão Geral

O padrão Saga garante consistência de dados em sistemas distribuídos, dividindo uma transação em subtransações menores, cada uma gerenciada por microsserviços individuais. Aqui, a abordagem de orquestração é utilizada, com um **Orquestrador** central que coordena todo o fluxo da saga.

### Arquitetura

![img.png](img.png)

# Serviços

## 1. Order-Service
Microsserviço responsável apenas por gerar um pedido inicial e receber uma notificação. Aqui teremos endpoints REST para iniciar o processo e recuperar os dados dos eventos.

- **Banco de Dados**: MongoDB

## 2. Orchestrator-Service
Microsserviço responsável por orquestrar todo o fluxo de execução da Saga. Ele saberá qual microsserviço foi executado, em qual estado e qual será o próximo serviço a ser chamado. Além disso, este serviço salvará o processo dos eventos.

- **Banco de Dados**: Não possui

## 3. Product-Validation-Service
Microsserviço responsável por validar se o produto informado no pedido existe e está válido. Ele armazenará a validação de um produto para o ID de um pedido.

- **Banco de Dados**: PostgreSQL

## 4. Payment-Service
Microsserviço responsável por realizar um pagamento com base nos valores unitários e quantidades informadas no pedido. Ele armazenará a informação de pagamento de um pedido.

- **Banco de Dados**: PostgreSQL

## 5. Inventory-Service
Microsserviço responsável por realizar a baixa do estoque dos produtos de um pedido. Ele armazenará a informação da baixa de um produto para o ID de um pedido.

- **Banco de Dados**: PostgreSQL

### 🚀 Deploy
Todos os serviços da arquitetura serão inicializados através do arquivo `docker-compose.yml`.


### Componentes

1. **Order Service (Serviço de Pedidos)**:
    - Inicia e finaliza a saga.
    - Utiliza **MongoDB** para armazenar os dados dos pedidos.

2. **Orchestrator (Orquestrador)**:
    - Gerencia o fluxo da saga, garantindo que todos os passos sejam executados com sucesso ou revertidos em caso de falha.

3. **Product Validation Service (Serviço de Validação de Produtos)**:
    - Valida se o produto solicitado está disponível.
    - Armazena os dados dos produtos em um banco de dados PostgreSQL dedicado.

4. **Payment Service (Serviço de Pagamento)**:
    - Processa os pagamentos relacionados aos pedidos.
    - Utiliza um banco de dados PostgreSQL para os registros de transações.

5. **Inventory Service (Serviço de Inventário)**:
    - Atualiza o inventário com base nos pedidos realizados.
    - Armazena os dados de inventário em PostgreSQL.

### Fluxo de Trabalho

1. O **Order Service** envia uma solicitação ao **Orquestrador** para iniciar uma nova saga.
2. O **Orquestrador** se comunica com os seguintes serviços, de forma sequencial:
    - **Product Validation Service**: Valida a disponibilidade do produto.
    - **Payment Service**: Processa o pagamento.
    - **Inventory Service**: Atualiza o inventário.
3. O **Orquestrador** escuta eventos de sucesso ou falha de cada serviço:
    - Em caso de sucesso, o fluxo continua para o próximo serviço.
    - Em caso de falha, um rollback é acionado para desfazer os passos já realizados.
4. O **Order Service** é notificado sobre o resultado final da saga.

### Tecnologias Utilizadas

- **Spring Boot**: Para construção de microsserviços RESTful.
- **Apache Kafka**: Para comunicação assíncrona entre os serviços.
- **MongoDB**: Para armazenamento de dados relacionados a pedidos.
- **PostgreSQL**: Para gerenciar dados específicos dos serviços (produtos, pagamentos, inventário).
- **Docker**: Para conteinerização de todos os serviços.

## Instruções de Configuração

### Pré-requisitos

- Docker e Docker Compose instalados
- Java 17 ou superior
- Maven instalado

### Executando o Projeto

1. Clone o repositório:
   ```bash
   git clone https://github.com/jcsalerno/microsservicos-padrao-saga-orquestrado.git
   cd saga-orchestration
   ```

2. Compile o projeto:
   ```bash
   mvn clean install
   ```

3. Inicie os serviços usando Docker Compose:
   ```bash
   docker compose up --build
   ```

4. Acesse os serviços:
    - **Order Service**: `http://localhost:8080`
    - **Interface do Kafka (opcional)**: `http://localhost:9092`

### Executando Sem Docker

Se preferir executar os serviços manualmente:
1. Inicie o MongoDB e os bancos PostgreSQL.
2. Atualize os arquivos `application.properties` em cada serviço com as configurações locais do banco de dados.
3. Inicie cada serviço usando:
   ```bash
   mvn spring-boot:run
   ```

## Execução do projeto
1. Execução geral via automação com script em Python
Basta executar o arquivo `build.py`. Para isto, é necessário ter o Python 3 instalado.
Para executar, basta apenas executar o seguinte comando no diretório raiz do repositório:
`python build.py`
Será realizado o build de todas as aplicações, removidos todos os containers e em sequência, será rodado o `docker compose.`

# Serviços e Portas

## Microsserviços

- **Order-Service:** `3000`
- **Orchestrator-Service:** `8080`
- **Product-Validation-Service:** `8090`
- **Payment-Service:** `8091`
- **Inventory-Service:** `8092`

## Infraestrutura

- **Apache Kafka:** `9092`
- **Redpanda Console:** `8081`

## Bancos de Dados

- **PostgreSQL (Product-DB):** `5433`
- **PostgreSQL (Payment-DB):** `5434`
- **PostgreSQL (Inventory-DB):** `5435`
- **MongoDB (Order-DB):** `27017`


# 📌 Endpoints da Saga

## 🔹 Iniciar a Saga

### 🟢 **Requisição**
**Método:** `POST`  
**URL:** `http://localhost:3000/api/order`

### 📥 **Payload**
```json
{
  "products": [
    {
      "product": {
        "code": "COMIC_BOOKS",
        "unitValue": 15.50
      },
      "quantity": 3
    },
    {
      "product": {
        "code": "BOOKS",
        "unitValue": 9.90
      },
      "quantity": 1
    }
  ]
}
```
### 📤  **Resposta**

```json
{
"id": "64429e987a8b646915b3735f",
"products": [
{
"product": {
"code": "COMIC_BOOKS",
"unitValue": 15.5
},
"quantity": 3
},
{
"product": {
"code": "BOOKS",
"unitValue": 9.9
},
"quantity": 1
}
],
"createdAt": "2023-04-21T14:32:56.335943085",
"transactionId": "1682087576536_99d2ca6c-f074-41a6-92e0-21700148b519"
}

```

### 🔹 **Visualizar a Saga**

É possível recuperar os dados da saga pelo orderId ou pelo transactionId. O resultado será o mesmo.
### **🟢 Requisição**

Método: GET
URL com orderId:

http://localhost:3000/api/event?orderId=64429e987a8b646915b3735f

URL com transactionId:

http://localhost:3000/api/event?transactionId=1682087576536_99d2ca6c-f074-41a6-92e0-21700148b519

### **📤 Resposta**
```json
{
"id": "64429e9a7a8b646915b37360",
"transactionId": "1682087576536_99d2ca6c-f074-41a6-92e0-21700148b519",
"orderId": "64429e987a8b646915b3735f",
"payload": {
"id": "64429e987a8b646915b3735f",
"products": [
{
"product": {
"code": "COMIC_BOOKS",
"unitValue": 15.5
},
"quantity": 3
},
{
"product": {
"code": "BOOKS",
"unitValue": 9.9
},
"quantity": 1
}
],
"totalAmount": 56.40,
"totalItems": 4,
"createdAt": "2023-04-21T14:32:56.335943085",
"transactionId": "1682087576536_99d2ca6c-f074-41a6-92e0-21700148b519"
},
"source": "ORCHESTRATOR",
"status": "SUCCESS",
"eventHistory": [
{
"source": "ORCHESTRATOR",
"status": "SUCCESS",
"message": "Saga started!",
"createdAt": "2023-04-21T14:32:56.78770516"
},
{
"source": "PRODUCT_VALIDATION_SERVICE",
"status": "SUCCESS",
"message": "Products are validated successfully!",
"createdAt": "2023-04-21T14:32:57.169378616"
},
{
"source": "PAYMENT_SERVICE",
"status": "SUCCESS",
"message": "Payment realized successfully!",
"createdAt": "2023-04-21T14:32:57.617624655"
},
{
"source": "INVENTORY_SERVICE",
"status": "SUCCESS",
"message": "Inventory updated successfully!",
"createdAt": "2023-04-21T14:32:58.139176809"
},
{
"source": "ORCHESTRATOR",
"status": "SUCCESS",
"message": "Saga finished successfully!",
"createdAt": "2023-04-21T14:32:58.248630293"
}
],
"createdAt": "2023-04-21T14:32:58.28"
}

```

## Estrutura de Pastas

```
|-- order-service
|-- orchestrator-service
|-- product-service
|-- payment-service
|-- inventory-service
|-- docker-compose.yml
|-- README.md
```


## Licença

Este projeto foi desenvolvido pelo curso da Udemy: https://www.udemy.com/course/arquitetura-de-microsservicos-padrao-saga-orquestrado/?couponCode=KEEPLEARNINGBR
Junto ao professor Victor Hugo Negrisoli


