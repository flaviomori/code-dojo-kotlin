# Superfície Docker-only. Único pré-requisito local: Docker (e Git). Sem JDK.
.PHONY: app test sh smoke logs down

OPERATOR := 99999999-9999-9999-9999-999999999999
PRACTITIONER := 11111111-1111-1111-1111-111111111111

# Sobe o app (e o Postgres) em :8080. 1ª vez baixa Gradle + deps (~5-10 min).
app:
	docker compose up --build app

# Roda a suíte contra o postgres-test (sobe como dependência).
test:
	docker compose run --rm --build test

# Shell na imagem de dev para Gradle ad-hoc (ex.: ./gradlew test --tests ...).
sh:
	docker compose run --rm app bash

# Smoke test: busca o Dr. Carlos. Requer o app de pé (make app em outro terminal).
smoke:
	curl -s -H "X-Operator-Id: $(OPERATOR)" http://localhost:8080/practitioners/$(PRACTITIONER)

logs:
	docker compose logs -f app

# Derruba tudo e remove volumes (Postgres + caches do Gradle).
down:
	docker compose down -v
