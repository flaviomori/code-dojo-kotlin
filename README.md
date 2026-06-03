# code-dojo-kotlin

Sistema de repasses a médicos credenciados — Kotlin/Ktor + Postgres.

## Setup

Pré-requisitos: **Docker** e **Git**. Nada mais — o JDK roda dentro do container.

1. `git clone` este repo.
2. `make app` — sobe Postgres + app em `http://localhost:8080`. A 1ª execução baixa Gradle e dependências (~5-10 min); as seguintes são rápidas (cache em volume).
3. Smoke (em outro terminal, com o app de pé): `make smoke`.
4. Testes: `make test`.

Comandos: `make app`, `make test`, `make smoke`, `make sh` (shell para Gradle ad-hoc), `make logs`, `make down`.

Loop de edição: edite `src/` no host, `Ctrl-C` no `make app` e rode `make app` de novo (sem hot-reload).

## Endpoints disponíveis

- `GET /practitioners/{id}` — busca médico por ID
- `GET /practitioners` — lista médicos
- `GET /earnings?practitionerId={id}` — lista repasses de um médico
- `GET /earnings/{id}` — busca repasse por ID
- `POST /earnings` — registra repasse
- `PUT /earnings/{id}` — atualiza repasse
- `GET /payment-splits?practitionerId={id}` — lista splits de pagamento de um médico
- `GET /payment-splits/{id}` — busca split por ID
- `POST /payment-splits` — registra split
- `PUT /payment-splits/{id}` — atualiza split

Autenticação fake: passe header `X-Operator-Id: <uuid>` em todas as requests.

## Dados de seed

Após primeira subida, dois médicos, duas contas bancárias, um repasse e um split ficam disponíveis. Veja `V1__init.sql` e `V2__payment_splits.sql`.
