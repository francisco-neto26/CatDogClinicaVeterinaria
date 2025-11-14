# 🐾 Projeto CatDogClinicaVet

Plataforma fullstack para gerenciamento de uma clínica veterinária, desenvolvida como projeto final +DEV2BLU 2025.

O sistema permite que clientes agendem consultas e visualizem seus pets, enquanto funcionários (administrativo/veterinários) gerenciam os agendamentos e o cadastro de clientes.

## 🚀 Integrantes do projeto

* Francisco Miguel Ludwig Neto

---

## 🛠️ Tecnologias Utilizadas

Este projeto é 100% containerizado com Docker, garantindo um ambiente de desenvolvimento e produção consistente.

| Categoria | Tecnologia | Propósito |
| :--- | :--- | :--- |
| **Backend** | Java 21 | Linguagem principal |
| | Spring Boot 3.x | Framework principal (API REST) |
| | Spring Security (JWT) | Autenticação e autorização |
| | Spring Data JPA | Persistência de dados |
| | MapStruct | Mapeamento de DTOs/Entidades |
| **Frontend** | Angular (v20+) | Framework principal (UI) |
| | TypeScript | Linguagem principal |
| | PrimeNG / PrimeFlex | Biblioteca de componentes de UI |
| | NGXS | Gerenciamento de estado |
| **Mensageria** | Spring RabbitMQ | Produção e consumo de mensagens |
| **Infra & DB** | Docker & Docker Compose | Orquestração de containers |
| | PostgreSQL 17 | Banco de dados relacional |
| | RabbitMQ | Broker de mensageria assíncrona |
| | Redis 7 | Cache (ex: sessões, tokens) |
| | Minio (S3) | Armazenamento de objetos (fotos dos pets) |
| | Nginx | Servidor web para o frontend Angular |

---

## ⚡ Como Executar o Projeto (Ambiente Completo)

Siga os passos abaixo para subir toda a aplicação (Frontend, Backends, Banco de Dados e todos os serviços de infra).

### 1. Pré-requisitos

* [Git](https://git-scm.com/downloads)
* [Docker e Docker Compose](https://www.docker.com/products/docker-desktop/) (Certifique-se de que o Docker Desktop esteja em execução).

### 2. Clonar o Repositório

git clone [https://github.com/francisco-neto26/CatDogClinicaVeterinaria.git](https://github.com/francisco-neto26/CatDogClinicaVeterinaria.git)
cd CatDogClinicaVeterinaria

---

### 3. Criar o Arquivo de Ambiente (.env)
Este arquivo é obrigatório e armazena as senhas e configurações que o docker-compose.yml utiliza. Ele não é enviado para o Git.

    Windows PowerShell

    Execute o comando abaixo na raiz do projeto para criar o arquivo .env com as variáveis necessárias.

        Set-Content -Path ".env" -Value "
        # Senhas do Banco de Dados PostgreSQL
        DB_USER=admin
        DB_PASSWORD=admin
        DB_NAME=clinicavetdb

        # Credenciais do Minio (Storage S3)
        MINIO_USER=minioadmin
        MINIO_PASSWORD=minioadmin
        "

### 4. Subir os Containers

Na raiz do projeto (onde está o docker-compose.yml), execute:

docker-compose up --build
--build: Força o Docker a (re)construir as imagens dos seus serviços (backend-api, notification-service, frontend) na primeira vez ou se você fizer alterações no código.

Aguarde alguns minutos até que todos os containers estejam de pé e saudáveis (healthy).


---


### 🖥️ Acessando os Serviços

Após a execução, os seguintes serviços estarão disponíveis no seu localhost:

Aplicação (Frontend): ➡️ http://localhost:4200
Backend API (Swagger): ➡️ http://localhost:8080/swagger-ui.html
Minio (Storage UI): ➡️ http://localhost:9001
    Login: minioadmin / Senha: minioadmin (ou o que você definiu no .env)
RabbitMQ (Management): ➡️ http://localhost:15672
    Login: guest / Senha: guest

Para conectar ao Banco de Dados (Postgres) com um cliente (DBeaver, pgAdmin):

Host: localhost
Porta: 5432
Banco: clinicavetdb
Usuário: admin
Senha: admin

### 📂 Arquitetura e Fluxo de Mensageria

Para uma explicação detalhada sobre a estrutura de pastas, as responsabilidades de cada serviço e o fluxo de dados completo do RabbitMQ, consulte nosso guia de arquitetura:

➡️ Ver Arquivo de Estrutura (ESTRUTURA.md)

### 🐳 Comandos Úteis do Docker Compose

**Parar e remover os containers:**
docker-compose down

**Parar e remover containers E volumes (use isso para "resetar" seu banco de dados):**
docker-compose down -v

**Ver os logs de todos os serviços:**
docker-compose logs -f

**Ver os logs de um serviço específico (ex: backend-api):**
docker-compose logs -f backend-api

**Reconstruir as imagens sem cache (útil se o build estiver "preso"):**
docker-compose build --no-cache

