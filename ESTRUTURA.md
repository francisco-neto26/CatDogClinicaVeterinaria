# 📂 Arquitetura do Projeto

Este documento detalha a estrutura interna do projeto `CatDogClinicaVet`, as responsabilidades de cada serviço e o fluxo de dados.

## Estrutura de Pastas (Monorepo)

O projeto utiliza uma abordagem de monorepo, onde múltiplos serviços (backends, frontend) residem no mesmo repositório:


/CatDogClinicaVeterinaria
│
├── backend-api/
│   ├── build.gradle
│   ├── Dockerfile
│   └── src/ (Spring Boot: API Principal, Produtor RabbitMQ)
│
├── notification-service/
│   ├── build.gradle
│   ├── Dockerfile
│   └── src/ (Spring Boot: Consumidor RabbitMQ)
│
├── frontend/
│   ├── angular.json
│   ├── Dockerfile
│   ├── nginx.conf
│   └── src/ (Angular: UI, Telas, Serviços HTTP)
│
├── .env                  (Credenciais locais - NÃO VERSIONADO)
├── docker-compose.yml    (Orquestrador de todos os serviços)
└── README.md             (Guia principal de setup)


---

## 🔬 Detalhamento dos Serviços

### 1. Serviços de Backend (Spring Boot)

Temos dois serviços de backend distintos para atender ao requisito de mensageria.

#### `backend-api` (Serviço Principal)

* **Porta Host:** `8080`
* **Descrição:** É o cérebro da aplicação. Expõe a API REST principal que o `frontend` consome.
* **Responsabilidades:**
    * Autenticação e Autorização (Spring Security + JWT).
    * Endpoints CRUD (Clientes, Pets, Agendamentos).
    * Conexão com `PostgreSQL` (via JPA) para dados principais.
    * Conexão com `Redis` para cache (ex: gerenciamento de tokens JWT).
    * Conexão com `Minio` para upload/download de fotos dos pets.
    * **Produtor RabbitMQ:** Publica mensagens em uma fila quando eventos de negócio ocorrem (ex: novo usuário cadastrado).

#### `notification-service` (Serviço de Notificação)

* **Porta Host:** `8081`
* **Descrição:** Serviço leve e desacoplado. Sua única função é ouvir eventos do RabbitMQ e reagir a eles.
* **Responsabilidades:**
    * **Consumidor RabbitMQ:** Ouve a fila de "cadastro de usuário".
    * Ao receber uma mensagem, ele (atualmente) simula o envio de um e-mail de boas-vindas, imprimindo um log no console.

### 2. Frontend (Angular)

* **Porta Host:** `4200`
* **Descrição:** A interface do usuário (UI) construída em Angular.
* **Responsabilidades:**
    * Renderizar as telas de login, registro, dashboard, etc.
    * Consumir a API REST do `backend-api` para buscar e enviar dados.
    * Gerenciar o estado da aplicação (ex: usuário logado) usando `NGXS`.
    * Utiliza `PrimeNG` para componentes de UI (tabelas, modais, calendários).
* **Container:** O `Dockerfile` do frontend realiza um build de produção (`npm run build`) e serve os arquivos estáticos resultantes (HTML, JS, CSS) usando um container `Nginx` leve.

---

## 📨 Fluxo de Mensageria (RabbitMQ) - Cadastro de Usuário

Este é o fluxo de negócio obrigatório que passa pela fila, garantindo o desacoplamento entre a API principal e o serviço de notificações.

1.  **Requisição:** O usuário preenche o formulário de registro no `frontend` (Angular).
2.  **API (Produção):** O `frontend` envia um `POST /auth/registrar` para o `backend-api`.
3.  **Processamento:** O `backend-api` valida os dados, criptografa a senha e salva o novo `Usuario` no banco `PostgreSQL`.
4.  **Publicação:** Imediatamente após salvar, o `backend-api` (Produtor) envia uma mensagem contendo dados básicos (ex: email, nome) para uma *exchange* específica no `RabbitMQ`.
5.  **Roteamento:** O `RabbitMQ` roteia essa mensagem da *exchange* para a fila `vet.registration.queue`.
6.  **Consumo:** O `notification-service` (Consumidor) está ouvindo permanentemente essa fila. Ele recebe a mensagem.
7.  **Ação:** O `notification-service` executa sua lógica de negócio: (Simulação) formata e "envia" um e-mail de boas-vindas.

**Vantagem:** Se o `notification-service` estiver fora do ar, o cadastro de usuário n