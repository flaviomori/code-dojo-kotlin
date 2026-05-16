# code-dojo-kotlin

Sistema de repasses a médicos credenciados — Kotlin/Ktor + Postgres.

## Setup

1. `git clone` este repo.
2. `docker compose up -d` (sobe Postgres na porta 5432).
3. `./gradlew run`.
4. Smoke: `curl -H "X-Operator-Id: 99999999-9999-9999-9999-999999999999" http://localhost:8080/practitioners/11111111-1111-1111-1111-111111111111`
5. Rodar os testes: `./gradlew test`.

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
