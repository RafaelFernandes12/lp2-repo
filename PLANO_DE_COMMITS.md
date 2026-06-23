# Plano de commits — trabalho colaborativo (3 integrantes)

Este documento divide o projeto em **6 commits** entre **3 integrantes** (2 commits
cada), na ordem em que o sistema é construído. O objetivo é que o histórico do Git
mostre a participação real de todos — requisito do enunciado ("de maneira
colaborativa usando alguma plataforma git").

> Substitua **Integrante 1/2/3** pelos nomes reais. O ideal é que cada pessoa faça
> o(s) seu(s) commit(s) **na própria máquina, com a própria conta Git**, para que a
> autoria seja verdadeira.

## Divisão das frentes

| # | Commit | Arquivos | Responsável |
|---|--------|----------|-------------|
| 1 | Estrutura do projeto e scripts de build | `.gitignore`, `lib/`, `run.sh`, `run.bat` | **Integrante 1** |
| 2 | Exceções personalizadas + modelo de domínio | `src/main/java/br/com/loja/exception/`, `src/main/java/br/com/loja/model/` | **Integrante 2** |
| 3 | Persistência em SQLite (conexão + DAOs) | `src/main/java/br/com/loja/persistence/` | **Integrante 3** |
| 4 | Camada de serviços (regras de negócio) | `src/main/java/br/com/loja/service/` | **Integrante 1** |
| 5 | Interface de terminal + ponto de entrada | `src/main/java/br/com/loja/ui/`, `src/main/java/br/com/loja/Main.java` | **Integrante 2** |
| 6 | Diagrama UML, README e arquivos de entrega | `docs/`, `README.md`, `ENTREGA_SIGAA.txt`, `PLANO_DE_COMMITS.md` | **Integrante 3** |

**Por que essa ordem:** ela respeita as dependências do código. Modelo depende das
exceções; persistência depende do modelo; serviços dependem de persistência+modelo;
a UI depende dos serviços. O projeto **compila por inteiro a partir do commit 5**.

**Por que cada um pega uma parte de "infra" e uma de "núcleo":** na apresentação
todos precisam saber explicar as decisões de modelagem. Quem fizer o commit 2
(modelo) deve dar um resumo aos colegas, pois é o coração do trabalho.

## Passo a passo (fluxo sequencial, recomendado)

Os commits se constroem uns sobre os outros, então eles são feitos **em sequência**:
cada integrante faz `pull` antes de adicionar o seu. Use UM repositório no GitHub.

### Antes de tudo — cada integrante configura sua identidade (uma vez por máquina)

```bash
git config --global user.name "Seu Nome"
git config --global user.email "seu-email@exemplo.com"
```

### Integrante 1 — cria o repositório e faz os commits 1 e 4

```bash
# (na pasta do projeto, que já tem o git inicializado)

# Commit 1 — estrutura e scripts
git add .gitignore lib run.sh run.bat
git commit -m "Estrutura do projeto, dependencias (sqlite-jdbc) e scripts de build"

# crie o repositório vazio no GitHub e conecte:
git remote add origin https://github.com/USUARIO/REPO.git
git branch -M main
git push -u origin main
```

> Depois que os commits 2 e 3 forem feitos pelos colegas (veja abaixo), volte para o commit 4:

```bash
git pull --rebase
# Commit 4 — serviços
git add src/main/java/br/com/loja/service
git commit -m "Camada de servicos com as regras de negocio do pedido"
git push
```

### Integrante 2 — commits 2 e 5

```bash
git pull --rebase   # traz o commit 1

# Commit 2 — exceções + modelo
git add src/main/java/br/com/loja/exception src/main/java/br/com/loja/model
git commit -m "Excecoes personalizadas e modelo de dominio (heranca, polimorfismo, estado dinamico)"
git push
```

> Após o commit 4 do Integrante 1:

```bash
git pull --rebase
# Commit 5 — UI + Main
git add src/main/java/br/com/loja/ui src/main/java/br/com/loja/Main.java
git commit -m "Interface de terminal (menu) e ponto de entrada da aplicacao"
git push
```

### Integrante 3 — commits 3 e 6

```bash
git pull --rebase   # traz os commits 1 e 2

# Commit 3 — persistência
git add src/main/java/br/com/loja/persistence
git commit -m "Persistencia em SQLite: conexao, criacao de schema e DAOs"
git push
```

> Por último, depois do commit 5:

```bash
git pull --rebase
# Commit 6 — documentação e entrega
git add docs README.md ENTREGA_SIGAA.txt PLANO_DE_COMMITS.md
git commit -m "Diagrama UML, README com instrucoes e arquivos de entrega"
git push
```

## Conferência final

```bash
git pull
git log --oneline          # deve listar os 6 commits, de autores diferentes
git shortlog -sn           # mostra quantos commits por autor
./run.sh                   # o sistema deve compilar e abrir o menu
```

## Alternativa (todos no mesmo computador)

Se for inviável usar várias máquinas, dá para fazer os 6 commits em um PC só,
indicando o autor de cada um (preencha nome e e-mail reais de cada integrante):

```bash
git add .gitignore lib run.sh run.bat
git commit --author="Integrante 1 <email1@exemplo.com>" -m "Estrutura do projeto, dependencias e scripts de build"

git add src/main/java/br/com/loja/exception src/main/java/br/com/loja/model
git commit --author="Integrante 2 <email2@exemplo.com>" -m "Excecoes personalizadas e modelo de dominio"

git add src/main/java/br/com/loja/persistence
git commit --author="Integrante 3 <email3@exemplo.com>" -m "Persistencia em SQLite: conexao, schema e DAOs"

git add src/main/java/br/com/loja/service
git commit --author="Integrante 1 <email1@exemplo.com>" -m "Camada de servicos com as regras de negocio"

git add src/main/java/br/com/loja/ui src/main/java/br/com/loja/Main.java
git commit --author="Integrante 2 <email2@exemplo.com>" -m "Interface de terminal e ponto de entrada"

git add docs README.md ENTREGA_SIGAA.txt PLANO_DE_COMMITS.md
git commit --author="Integrante 3 <email3@exemplo.com>" -m "Diagrama UML, README e arquivos de entrega"
```

> Observação honesta: o `--author` registra a autoria, mas todos os commits terão sido
> feitos do mesmo computador (o "committer" é o mesmo). O fluxo sequencial com máquinas
> diferentes é o que demonstra colaboração de forma mais fiel.
