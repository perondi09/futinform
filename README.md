# FutInform

Plataforma gratuita para quem vive futebol: tabela do Brasileirão, próximos jogos e notificações do seu time (início, fim e resultado da partida).

Feito como uma ferramenta real, com foco em ser simples, rápida e sem custo para quem usa.

<!-- ![Interface](docs/interface.png) -->

## Arquitetura

```
        ┌──────────────┐   HTTPS   ┌───────────────────┐        ┌────────────┐
        │  React (SPA) │ ────────▶ │  Spring Boot API  │ ─────▶ │ PostgreSQL │
        │  Vite+nginx  │ ◀──────── │  (8080, JWT)      │ ◀───── │     17     │
        └──────────────┘           └─────────┬─────────┘        └────────────┘
                                             │
                          ┌──────────────────┼──────────────────┐
                          ▼                  ▼                  ▼
                   ┌─────────────┐   ┌──────────────┐   ┌──────────────┐
                   │  Scheduler  │   │ Notificações │   │ Google OAuth │
                   │ (sync jobs) │   │   (Twilio)   │   │              │
                   └──────┬──────┘   └──────────────┘   └──────────────┘
                          ▼
                 ┌──────────────────┐
                 │ football-data.org│  (plano gratuito, competição BSA)
                 └──────────────────┘
```

O front nunca chama a API externa. Jobs agendados sincronizam os dados para o banco, e a API lê sempre do banco. Isso respeita o limite de 10 req/min da API gratuita e permite disparar notificações.

## Tech Stack

**Backend**
- Java 21 + Spring Boot
- Spring Security com JWT (sessão stateless) e login com Google
- Spring Data JPA (Hibernate) + Flyway para migrations
- PostgreSQL 17

**Frontend**
- React + TypeScript + Vite
- Servido via nginx

**Infraestrutura**
- Docker com multi-stage builds (backend e frontend)
- Docker Compose orquestrando backend, frontend e PostgreSQL
- GitHub Actions para CI/CD
- Deploy na AWS

## Modelo de dados

| Tabela | Função |
|---|---|
| `app_user` | Contas de usuário (e-mail/senha ou Google) e time escolhido |
| `team` | Times do campeonato, sincronizados da API externa |
| `match` | Partidas com status e placar |
| `standing` | Classificação atual de cada time |
| `notification` | Registro de cada aviso enviado |

Decisões principais:

- `team`, `match` e `standing` usam o **id da API externa** como chave, o que torna a sincronização idempotente (upsert).
- `notification` tem `UNIQUE(user_id, match_id, type)`: o usuário nunca recebe o mesmo aviso duas vezes, mesmo se o job rodar de novo.
- `favorite_team_id` é definido no cadastro e é o que dispara os avisos do time.
- Senhas com BCrypt; `password_hash` é nulo para quem entra só com Google.

## Notificações

Enviadas para o time escolhido no cadastro:

| Evento | Quando |
|---|---|
| `MATCH_START` | Jogo começou |
| `MATCH_END` | Jogo terminou |
| `RESULT` | Placar final e posição na tabela |

## Como rodar

```bash
git clone https://github.com/<seu-usuario>/futinform.git
cd futinform
cp .env.example .env    # preencha as chaves
docker compose up -d --build
```

| Serviço | URL |
|---|---|
| Frontend | http://localhost:3000 |
| Backend | http://localhost:8080 |

Variáveis principais do `.env`: `POSTGRES_*`, `JWT_SECRET`, `FOOTBALL_DATA_API_KEY`, `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`, `TWILIO_*`. Nunca commite o `.env`.

## API Endpoints

### Auth
| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/api/auth/register` | Cadastro com time escolhido |
| POST | `/api/auth/login` | Login e recebe JWT |
| POST | `/api/auth/google` | Login/cadastro com Google |

### Usuário
| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/api/users/me` | Dados do usuário logado |
| PATCH | `/api/users/me/team` | Trocar time |

### Campeonato
| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/api/teams` | Lista de times |
| GET | `/api/standings` | Tabela do Brasileirão |
| GET | `/api/matches/today` | Jogos do dia |
| GET | `/api/matches/upcoming` | Próximos jogos |
| GET | `/api/matches/my-team` | Jogos do time do usuário |

## Roadmap

- [x] Integração com football-data.org
- [x] Endpoints de times, partidas e tabela
- [ ] Autenticação JWT + Google
- [ ] Cadastro com escolha de time
- [ ] Sincronização por job agendado
- [ ] Notificações de início, fim e resultado
- [ ] Front React (Início, Tabela, Jogos, Perfil)
- [ ] CI/CD e deploy na AWS
- [ ] Preferências de notificação por tipo de evento
- [ ] Insights (artilheiros, finalizações)
