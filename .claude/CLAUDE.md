# code-dojo-kotlin

Sistema interno de repasses a médicos credenciados.

## Convenções

- **Feature packages**: cada entidade (`earning`, `paymentsplit`, `practitioner`, `bankaccount`) em seu próprio package com Controller / Service / Repository / Table / DTOs / data class.
- **Camadas estritas**:
  - **Controller** — HTTP only. Recebe request, chama Service, responde com `*Response` (DTO). Nunca expõe entidade direto.
  - **Service** — lógica de negócio. Sem HTTP, sem Exposed.
  - **Repository** — acesso a banco via Exposed (`transaction {}`).
- **Auth**: header `X-Operator-Id` obrigatório. Extraído via `call.requireAuth()`.
- **Exceções**: `NotFoundException`, `BadRequestException` → `StatusPages` traduz para HTTP.
- **Testes**: Postgres real via compose (serviço `postgres-test`). Sem H2.
- **Money**: `BigDecimal` (serializado como `String` nos DTOs).

## Rodando local

Pré-requisito: Docker (e Git). O JDK roda dentro do container.

```bash
make app    # sobe Postgres + app em :8080
```

## Testes

```bash
make test   # roda a suíte contra o postgres-test
```
