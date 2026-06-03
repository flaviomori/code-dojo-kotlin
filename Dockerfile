# Imagem de dev: JDK dentro do container para que o único pré-requisito local seja Docker.
# Eclipse Temurin 25 (LTS) — multi-arch (amd64 + arm64), satisfaz jvmToolchain(25) sem download.
# Pin: tag legível + digest do índice multi-arch para builds reprodutíveis ao longo do tempo.
FROM eclipse-temurin:25.0.3_9-jdk@sha256:c2b7ea21649875fb9052237ac4e3cd4ef63968a2a389a0a1b1a72a5e53e5c93f

# curl para o smoke test do app.
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# O código é bind-mountado em runtime (ver docker-compose.yml); o gradlew baixa o
# Gradle 9.5.1 e as dependências para volumes nomeados na primeira execução.
