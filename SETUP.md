# 🚀 Guia de Configuração Completo

Este guia fornece instruções detalhadas para configurar o projeto do zero.

---

## 📑 Índice

- [Pré-requisitos](#pré-requisitos)
- [Instalação Local](#instalação-local)
- [Configuração do Banco de Dados](#configuração-do-banco-de-dados)
- [Configuração do Supabase](#configuração-do-supabase)
- [Configuração da Aplicação](#configuração-da-aplicação)
- [Primeiro Uso](#primeiro-uso)
- [Deploy](#deploy)
- [Troubleshooting](#troubleshooting)

---

## ✅ Pré-requisitos

### Software Necessário

#### 1. Java Development Kit (JDK)

**Versão mínima:** JDK 11

**Download:** https://www.oracle.com/java/technologies/downloads/

**Verificar instalação:**
```bash
java -version
# Deve mostrar: java version "11.0.x" ou superior
```

#### 2. Maven

**Versão mínima:** 3.6

**Download:** https://maven.apache.org/download.cgi

**Verificar instalação:**
```bash
mvn -version
# Deve mostrar: Apache Maven 3.6.x ou superior
```

#### 3. Git

**Download:** https://git-scm.com/downloads

**Verificar instalação:**
```bash
git --version
# Deve mostrar: git version 2.x.x
```

#### 4. IDE (Opcional, mas recomendado)

Escolha uma:
- **IntelliJ IDEA** (Recomendado): https://www.jetbrains.com/idea/download/
- **Eclipse**: https://www.eclipse.org/downloads/
- **VS Code** com Java Extension Pack: https://code.visualstudio.com/

---

## 💻 Instalação Local

### 1. Clone o Repositório

```bash
git clone https://github.com/seu-usuario/liberacao-projetos.git
cd liberacao-projetos
```

### 2. Estrutura do Projeto

Após clonar, você terá:

```
liberacao-projetos/
├── src/
│   ├── main/
│   │   ├── java/com/metrica/liberacao/
│   │   └── resources/
│   │       └── application.yml
│   └── test/
├── docs/
│   └── API.md
├── .env.example
├── .gitignore
├── CONTRIBUTING.md
├── LICENSE
├── README.md
├── SETUP.md (este arquivo)
└── pom.xml
```

---

## 🗄️ Configuração do Banco de Dados

### Opção 1: Neon.tech (Recomendado para Produção)

#### Passo 1: Criar Conta

1. Acesse https://neon.tech/
2. Clique em "Sign Up"
3. Faça login com GitHub (mais rápido)

#### Passo 2: Criar Projeto

1. No dashboard, clique em "New Project"
2. Configure:
   - **Project name:** liberacao-projetos
   - **Region:** US East (Ohio) - ou mais próximo de você
   - **Postgres version:** Última disponível (15 ou 16)
3. Clique em "Create Project"

#### Passo 3: Obter Credenciais

Após criar o projeto, você verá a **Connection String**:

```
postgres://usuario:senha@ep-xxxxx.us-east-2.aws.neon.tech/neondb?sslmode=require
```

**Extraia as informações:**
- **Host:** `ep-xxxxx.us-east-2.aws.neon.tech`
- **Port:** `5432`
- **Database:** `neondb`
- **User:** `usuario`
- **Password:** `senha`

#### Passo 4: Criar Banco de Dados (Opcional)

```sql
CREATE DATABASE liberacao_projetos;
```

### Opção 2: PostgreSQL Local (Para Desenvolvimento)

#### Instalação

**Windows:**
```bash
# Via Chocolatey
choco install postgresql

# Ou baixe o instalador
# https://www.postgresql.org/download/windows/
```

**macOS:**
```bash
brew install postgresql
brew services start postgresql
```

**Linux (Ubuntu/Debian):**
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
```

#### Criar Banco de Dados

```bash
# Acessar PostgreSQL
psql -U postgres

# Criar database
CREATE DATABASE liberacao_projetos;

# Criar usuário (opcional)
CREATE USER liberacao_user WITH PASSWORD 'senha_segura';
GRANT ALL PRIVILEGES ON DATABASE liberacao_projetos TO liberacao_user;

# Sair
\q
```

---

## ☁️ Configuração do Supabase

### Passo 1: Criar Conta

1. Acesse https://supabase.com/
2. Clique em "Start your project"
3. Faça login com GitHub

### Passo 2: Criar Projeto

1. No dashboard, clique em "New Project"
2. Configure:
   - **Organization:** Crie uma nova ou use existente
   - **Project name:** liberacao-projetos
   - **Database Password:** Senha forte (anote!)
   - **Region:** South America (São Paulo) - mais próximo do Brasil
   - **Pricing Plan:** Free (suficiente para desenvolvimento)
3. Clique em "Create new project"
4. Aguarde ~2 minutos enquanto provisiona

### Passo 3: Criar Bucket de Storage

1. No menu lateral, vá em **Storage**
2. Clique em "Create a new bucket"
3. Configure:
   - **Name:** `projetos-pdfs`
   - **Public bucket:** ❌ **NÃO** marcar (deve ser privado)
   - **File size limit:** 52428800 (50MB em bytes)
   - **Allowed MIME types:** `application/pdf`
4. Clique em "Create bucket"

### Passo 4: Configurar Políticas de Acesso (RLS)

```sql
-- Desabilitar RLS para acesso via API (backend controla acesso)
ALTER TABLE storage.objects DISABLE ROW LEVEL SECURITY;
```

**Ou via interface:**
1. Vá em **Storage** → `projetos-pdfs`
2. Clique em **Policies**
3. Adicione uma política customizada se necessário

### Passo 5: Obter Credenciais da API

1. Vá em **Settings** → **API**
2. Copie as seguintes informações:
   - **Project URL:** `https://abcdefgh.supabase.co`
   - **anon/public key:** `eyJhbGci...` (chave longa)

> ⚠️ **IMPORTANTE:** Nunca exponha a `service_role key` publicamente!

---

## ⚙️ Configuração da Aplicação

### 1. Criar Arquivo de Ambiente

```bash
cp .env.example .env
```

### 2. Editar `.env`

Abra o arquivo `.env` e preencha com suas credenciais:

```env
# Database (Neon.tech)
DB_HOST=ep-xxxxx.us-east-2.aws.neon.tech
DB_PORT=5432
DB_NAME=liberacao_projetos
DB_USER=seu_usuario
DB_PASSWORD=sua_senha_neon

# Supabase
SUPABASE_URL=https://abcdefgh.supabase.co
SUPABASE_KEY=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
SUPABASE_BUCKET=projetos-pdfs

# Server
SERVER_PORT=8080
```

### 3. Configurar `application.yml`

O arquivo `src/main/resources/application.yml` já está configurado para usar variáveis de ambiente:

```yaml
spring:
  application:
    name: liberacao-projetos
  
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:liberacao_projetos}?sslmode=require
    username: ${DB_USER:postgres}
    password: ${DB_PASSWORD:postgres}
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: update  # Cria tabelas automaticamente
    show-sql: true      # Mostra SQL no console (dev)
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true

  servlet:
    multipart:
      max-file-size: 50MB
      max-request-size: 50MB

supabase:
  url: ${SUPABASE_URL}
  key: ${SUPABASE_KEY}
  bucket: ${SUPABASE_BUCKET:projetos-pdfs}

server:
  port: ${SERVER_PORT:8080}
```

### 4. Instalar Dependências

```bash
mvn clean install
```

**Saída esperada:**
```
[INFO] BUILD SUCCESS
[INFO] Total time: 15.234 s
```

---

## 🎬 Primeiro Uso

### 1. Executar a Aplicação

```bash
mvn spring-boot:run
```

**Saída esperada:**
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.x.x)

[...] Started LiberacaoProjetosApplication in 3.456 seconds
```

### 2. Verificar se está Funcionando

Abra o navegador em: http://localhost:8080

Ou teste com cURL:
```bash
curl http://localhost:8080/actuator/health
```

**Resposta esperada:**
```json
{"status":"UP"}
```

### 3. Criar Primeiro Projeto (Teste)

```bash
curl -X POST http://localhost:8080/api/projetos \
  -H "Content-Type: application/json" \
  -d '{
    "codigoAcesso": "TEST001",
    "pinAcesso": "1234",
    "nomeCliente": "Cliente Teste",
    "precoAnteprojeto": 1000.00,
    "precoExecutivo": 5000.00
  }'
```

**Resposta esperada:**
```json
{
  "id": 1,
  "codigoAcesso": "TEST001",
  "nomeCliente": "Cliente Teste",
  "statusAnteprojeto": "AGUARDANDO_PAGAMENTO",
  "statusExecutivo": "AGUARDANDO_PAGAMENTO",
  ...
}
```

### 4. Validar Acesso

```bash
curl -X POST http://localhost:8080/api/projetos/validar \
  -H "Content-Type: application/json" \
  -d '{
    "codigoAcesso": "TEST001",
    "pinAcesso": "1234"
  }'
```

---

## 🌐 Deploy

### Deploy no Render

#### Passo 1: Criar Conta

1. Acesse https://render.com/
2. Clique em "Get Started"
3. Faça login com GitHub

#### Passo 2: Criar Web Service

1. No dashboard, clique em "New +"
2. Selecione "Web Service"
3. Conecte seu repositório GitHub
4. Configure:
   - **Name:** liberacao-projetos
   - **Region:** Oregon (US West)
   - **Branch:** main
   - **Runtime:** Java
   - **Build Command:** `mvn clean install`
   - **Start Command:** `java -jar target/liberacao-projetos-0.0.1-SNAPSHOT.jar`
   - **Plan:** Free

#### Passo 3: Adicionar Variáveis de Ambiente

Na seção **Environment**, adicione:

```
DB_HOST=seu-host.neon.tech
DB_PORT=5432
DB_NAME=liberacao_projetos
DB_USER=seu_usuario
DB_PASSWORD=sua_senha
SUPABASE_URL=https://seu-projeto.supabase.co
SUPABASE_KEY=sua_chave
SUPABASE_BUCKET=projetos-pdfs
```

#### Passo 4: Deploy

1. Clique em "Create Web Service"
2. Aguarde o build (~5-10 minutos)
3. Após sucesso, acesse a URL fornecida

**URL será algo como:**
```
https://liberacao-projetos-xxxx.onrender.com
```

---

## 🔧 Troubleshooting

### Problema: "Connection refused" ao Banco

**Causa:** Banco de dados não está acessível

**Solução:**
1. Verifique se as credenciais estão corretas no `.env`
2. Teste conexão manual:
   ```bash
   psql -h seu-host.neon.tech -U usuario -d database
   ```
3. Verifique firewall/VPN

### Problema: "Failed to upload to Supabase"

**Causa:** Credenciais do Supabase incorretas ou bucket não existe

**Solução:**
1. Verifique `SUPABASE_URL` e `SUPABASE_KEY` no `.env`
2. Confirme que o bucket `projetos-pdfs` existe no Supabase
3. Verifique se o bucket é **privado** (não público)

### Problema: Aplicação não inicia (Port already in use)

**Causa:** Porta 8080 já está em uso

**Solução:**
```bash
# Encontrar processo na porta 8080
# Windows
netstat -ano | findstr :8080

# macOS/Linux
lsof -i :8080

# Matar processo ou mudar porta no .env
SERVER_PORT=8081
```

### Problema: "File size exceeds maximum"

**Causa:** Arquivo maior que 50MB

**Solução:**
1. Reduzir tamanho do PDF
2. Ou aumentar limite no `application.yml`:
   ```yaml
   spring:
     servlet:
       multipart:
         max-file-size: 100MB
         max-request-size: 100MB
   ```

### Problema: Erro 401 ao fazer download

**Causa:** Código ou PIN incorretos

**Solução:**
1. Verificar código de acesso e PIN
2. Testar validação primeiro:
   ```bash
   curl -X POST http://localhost:8080/api/projetos/validar \
     -H "Content-Type: application/json" \
     -d '{"codigoAcesso":"PROJ001","pinAcesso":"1234"}'
   ```

### Problema: Erro 403 ao fazer download

**Causa:** Documento não pago (status != PAGO)

**Solução:**
1. Atualizar status para PAGO:
   ```bash
   curl -X PUT http://localhost:8080/api/projetos/1/status/anteprojeto \
     -H "Content-Type: application/json" \
     -d '{"status":"PAGO"}'
   ```

---

## 📚 Próximos Passos

Agora que o projeto está configurado:

1. ✅ Leia a [Documentação da API](docs/API.md)
2. ✅ Explore os endpoints com Postman
3. ✅ Contribua seguindo o [Guia de Contribuição](CONTRIBUTING.md)
4. ✅ Reporte bugs ou sugira features no GitHub Issues

---

## 🆘 Precisa de Ajuda?

- 📧 Email: seu-email@example.com
- 🐙 Issues: https://github.com/seu-usuario/liberacao-projetos/issues
- 💬 Discussions: https://github.com/seu-usuario/liberacao-projetos/discussions

---

<div align="center">

**Configuração completa! 🎉**

[⬆ Voltar ao topo](#-guia-de-configuração-completo)

</div>
