# Kafka Error Handling Patterns — Spring Kafka

Implementação dos 4 padrões de error handling descritos no artigo da Confluent,
usando Spring Kafka + Java 17.

---

## Pré-requisitos

- Java 17+
- Maven 3.8+
- Docker (para subir o Kafka localmente)

---

## 1. Subindo o Kafka localmente

```bash
docker run -d --name kafka \
  -p 9092:9092 \
  -e KAFKA_NODE_ID=1 \
  -e KAFKA_PROCESS_ROLES=broker,controller \
  -e KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9092,CONTROLLER://0.0.0.0:9093 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  -e KAFKA_CONTROLLER_LISTENER_NAMES=CONTROLLER \
  -e KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT \
  -e KAFKA_CONTROLLER_QUORUM_VOTERS=1@localhost:9093 \
  -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
  -e KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR=1 \
  -e KAFKA_TRANSACTION_STATE_LOG_MIN_ISR=1 \
  apache/kafka:3.7.0
```

---

## 2. Rodando o projeto

```bash
mvn spring-boot:run
```

Os tópicos são criados automaticamente pelo `TopicConfig.java` na inicialização.

---

## 3. Testando cada padrão via Kafka Console Producer

### Pattern 1 — Stop on Error

```bash
# Mensagem normal (vai para p1-target-topic)
kafka-console-producer.sh --bootstrap-server localhost:9092 --topic p1-source-topic
> ORDER:ITEM1:5

# Mensagem que causa falha (consumer para de consumir)
> ORDER:FAIL:5
```

**O que observar:** Após a mensagem com "FAIL", o consumer para de avançar no offset.
Todas as mensagens seguintes ficam bloqueadas.

---

### Pattern 2 — Dead Letter Queue

```bash
kafka-console-producer.sh --bootstrap-server localhost:9092 --topic p2-source-topic

# Mensagem válida → vai para p2-target-topic
> ORDER:ITEM1:5

# Mensagem inválida → vai para p2-error-topic (DLQ)
> INVALID_MESSAGE_WITHOUT_PREFIX

# O fluxo principal continua normalmente
> ORDER:ITEM2:3
```

**O que observar:** Mensagens inválidas vão para o DLQ, mas o consumer continua
processando as seguintes sem parar.

---

### Pattern 3 — Retry Topic (sem garantia de ordem)

```bash
kafka-console-producer.sh --bootstrap-server localhost:9092 --topic p3-source-topic

# Sem preço disponível para ITEM_A → vai para retry
> ORDER:ITEM_A:5

# Com preço disponível para ITEM_B → processado normalmente
> ORDER:ITEM_B:3
```

Para disponibilizar o preço de ITEM_A via código:
```java
// Injete PriceService e chame:
priceService.setPrice("ITEM_A", 29.99);
```

**O que observar:** ITEM_B é processado imediatamente enquanto ITEM_A fica no retry.
Quando o preço de ITEM_A for disponibilizado, ele vai para o target — possivelmente
*depois* de eventos que chegaram mais tarde (fora de ordem).

---

### Pattern 4 — Ordered Retry (com garantia de ordem)

```bash
kafka-console-producer.sh --bootstrap-server localhost:9092 --topic p4-source-topic

# Sem preço para ITEM_A → vai para retry
> ORDER:ITEM_A:5

# Também vai para retry (porque ITEM_A já tem retry em andamento)
> ORDER:ITEM_A:3

# ITEM_B não tem dependency missing → processado normalmente
> ORDER:ITEM_B:10
```

**O que observar:**
- Ambas as mensagens de ITEM_A ficam enfileiradas no retry, em ordem.
- Quando o preço de ITEM_A for disponibilizado, as mensagens são processadas
  na mesma sequência em que chegaram (Event 1, depois Event 2).
- ITEM_B continua sendo processado normalmente em paralelo.

---

## 4. Monitorando os tópicos

```bash
# Ver mensagens no target
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic p4-target-topic --from-beginning

# Ver DLQ
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic p4-error-topic --from-beginning

# Ver redirect topic (tombstones)
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic p4-redirect-topic --from-beginning
```

---

## 5. Estrutura do projeto

```
src/main/java/com/example/kafka/
├── KafkaPatternsApplication.java
├── config/
│   └── TopicConfig.java              # Cria todos os tópicos
├── pattern1/
│   ├── Pattern1Consumer.java         # Para no erro
│   └── Pattern1Producer.java
├── pattern2/
│   ├── Pattern2Consumer.java         # Dead Letter Queue
│   └── Pattern2Producer.java
├── pattern3/
│   ├── Pattern3Consumer.java         # Consumer principal
│   ├── Pattern3RetryConsumer.java    # Consumer do retry topic
│   ├── Pattern3Producer.java
│   └── PriceService.java
└── pattern4/
    ├── Pattern4Consumer.java         # Consumer principal + redirect listener
    ├── Pattern4RetryConsumer.java    # Consumer do retry com tombstone
    ├── Pattern4Producer.java
    ├── Pattern4PriceService.java
    └── OrderedRetryStore.java        # In-memory store de controle de ordem
```

---

## Resumo dos padrões

| Padrão | Continua após erro? | Retry? | Ordem garantida? |
|--------|---------------------|--------|-----------------|
| 1 - Stop on Error  | ❌ Para | ❌ | ✅ (total) |
| 2 - Dead Letter Queue | ✅ | ❌ | ✅ (eventos válidos) |
| 3 - Retry Topic    | ✅ | ✅ | ❌ |
| 4 - Ordered Retry  | ✅ | ✅ | ✅ |
