# Orquestração de Sagas com Spring Boot, Kafka e Docker

Este projeto demonstra o padrão de **Orquestração de Sagas** implementado com **Spring Boot**, **Apache Kafka** e **Docker** para gerenciar transações distribuídas entre múltiplos microsserviços.

## Visão Geral

O padrão Saga garante consistência de dados em sistemas distribuídos, dividindo uma transação em subtransações menores, cada uma gerenciada por microsserviços individuais. Aqui, a abordagem de orquestração é utilizada, com um **Orquestrador** central que coordena todo o fluxo da saga.

### Arquitetura

![img.png](img.png)

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
   docker-compose up --build
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

## Testando o Fluxo da Saga

1. Crie um novo pedido usando o Order Service:
   ```bash
   curl -X POST http://localhost:8080/api/orders \
        -H "Content-Type: application/json" \
        -d '{"productId": "123", "quantity": 2, "paymentDetails": {...}}'
   ```

2. Monitore o progresso da saga via logs ou tópicos do Kafka.
3. Verifique o status final do pedido no Order Service.

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

## Melhorias Futuras

- Adicionar logs e monitoramento mais detalhados.
- Implementar um mecanismo de retry para falhas transitórias.
- Suportar fluxos adicionais com definições configuráveis de saga.

## Licença

Este projeto está licenciado sob a Licença MIT. Consulte o arquivo LICENSE para mais detalhes.


