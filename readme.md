# 🐾 CatDog Vet - Sistema de Gestão Veterinária

> **Projeto Final Fullstack** - +DEV2BLU 2025
> **Entrega:** 26/11

Plataforma fullstack completa para gerenciamento de clínica veterinária. O sistema resolve o problema de agendamentos descentralizados, permitindo que tutores marquem consultas online e a clínica gerencie prontuários e financeiro em um único lugar.

---

## 👥 Integrantes
* **Francisco Miguel Ludwig Neto**

---

## 🛠️ Tecnologias e Arquitetura

O projeto utiliza uma arquitetura moderna baseada em microsserviços simplificados, totalmente containerizada.



| Componente | Tecnologia | Função |
| :--- | :--- | :--- |
| **Frontend** | Angular 20+ | Interface do usuário (SPA) com PrimeNG. |
| **Backend API** | Spring Boot 3.3 | API REST principal, Regras de Negócio, Segurança. |
| **Notification Service** | Spring Boot 3.3 | Microsserviço consumidor de mensagens. |
| **Banco de Dados** | PostgreSQL 17 | Persistência relacional com versionamento (Flyway). |
| **Mensageria** | RabbitMQ | Comunicação assíncrona entre serviços. |
| **Storage** | MinIO (S3) | Armazenamento de objetos (fotos de perfil e pets). |
| **Cache** | Redis | Cache de dados para performance. |
| **Infraestrutura** | Docker Compose | Orquestração de todo o ambiente. |

---

## 📨 Fluxo de Mensageria (RabbitMQ)

O sistema implementa um fluxo assíncrono para garantir alta disponibilidade no cadastro:

1.  **Evento:** Novo usuário se cadastra no Frontend.
2.  **Produção:** A `backend-api` salva o usuário e envia uma mensagem para a fila `vet.registration.queue`.
3.  **Consumo:** O `notification-service` escuta a fila, processa a mensagem e simula o envio de um e-mail de boas-vindas (log).

---

## Como Executar o Projeto

### 1. Pré-requisitos
* Docker e Docker Compose instalados e rodando.
* Git.

---

### 2. Instalação

Clone o repositório e entre na pasta:

git clone [https://github.com/francisco-neto26/CatDogClinicaVeterinaria.git](https://github.com/francisco-neto26/CatDogClinicaVeterinaria.git)

---

### 3. Configuração (.env)

No Windows PowerShell, execute este comando na raiz para criar o arquivo de variáveis de ambiente (necessário para o Docker):

    Set-Content -Path ".env" -Value "
    # Banco de Dados
    DB_USER=admin
    DB_PASSWORD=admin
    DB_NAME=catdogvet

    # Minio (Storage)
    MINIO_USER=minioadmin
    MINIO_PASSWORD=minioadmin
    "
---

### 4. Subir o Ambiente

Execute o comando para construir as imagens e iniciar os containers:

Precisa estar na pasta onde consta o arquivo: docker-compose.yml

docker-compose up --build -d

Aguarde alguns minutos. O sistema irá configurar o banco, rodar as migrações e semear os dados iniciais.

---

### 5. Acessando o Sistema

Após os logs estabilizarem, acesse:

Aplicação URL Credenciais de Acesso Sistema Web

    URL http://localhost:4200

    User: admin@vet.com senha: admin

Swagger API

    URL http://localhost:8080/swagger-ui.html

RabbitMQ

    http://localhost:15672
    User: guest senha: guest

Minio Console

    URL http://localhost:9001
    User: Conforme arquivo .env senha: Conforme arquivo .env

Banco de dados PostgreSql

Para conectar ao Banco de Dados (Postgres) com um cliente (DBeaver, pgAdmin):

    Host: localhost
    Porta: 5432
    Banco: Conforme arquivo .env 
    Usuário: Conforme arquivo .env 
    Senha: Conforme arquivo .env 

---

### 6.Testes e Validação
O Backend possui testes unitários automatizados. Para rodar acesse a pasta "backend-api" via PowerShell e execute o comando abaixo

    ./gradlew clean test

---

### 7. Estrutura do Projeto (Monorepo)

O projeto organiza múltiplos serviços em um único repositório para facilitar o desenvolvimento e versionamento.

    /CatDogClinicaVeterinaria
    │
    ├── backend-api/               # API REST Principal (Spring Boot)
    │   ├── src/main/java          # Código Fonte (Controllers, Services)
    │   ├── src/main/resources     # Configurações e Migrations (Flyway)
    │   ├── src/test               # Testes Unitários (JUnit 5 + Mockito)
    │   └── Dockerfile             # Build da imagem Java
    │
    ├── notification-service/      # Microsserviço Worker (Spring Boot)
    │   ├── src/main/java          # Listener do RabbitMQ
    │   └── Dockerfile
    │
    ├── frontend/                  # Aplicação Web (Angular 18)
    │   ├── src/app/core           # Serviços, Guards, Interceptors
    │   ├── src/app/features       # Telas (Auth, Dashboard, Clinica, Financeiro)
    │   ├── src/app/shared         # Componentes reutilizáveis (Navbar, Sidebar)
    │   └── Dockerfile             # Build de produção com Nginx
    │
    ├── docker-compose.yml         # Orquestração dos 6 containers
    └── README.md                  # Documentação principal

### 8.  Detalhamento dos Serviços

## backend-api (Porta 8080)

Responsabilidade: Core do sistema. Gerencia usuários, animais, agenda e financeiro.

## Integrações: PostgreSQL (Dados), Redis (Cache), Minio (Arquivos), RabbitMQ (Produtor) notification-service (Porta 8081) 

Responsabilidade: Processamento assíncrono.Ação: Consome mensagens da fila vet.registration.queue para notificar novos usuários.

## frontend (Porta 4200)
Responsabilidade: Interface do Usuário.Tecnologia: Angular Standalone Components + PrimeNG.Estado: Gerenciado via NGXS.

