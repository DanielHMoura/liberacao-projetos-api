# 🤝 Guia de Contribuição

Obrigado por considerar contribuir com o **Sistema de Liberação de Projetos**! 

Este documento fornece diretrizes para contribuir com o projeto.

---

## 📋 Índice

- [Código de Conduta](#código-de-conduta)
- [Como Posso Contribuir?](#como-posso-contribuir)
- [Processo de Desenvolvimento](#processo-de-desenvolvimento)
- [Padrões de Código](#padrões-de-código)
- [Padrões de Commit](#padrões-de-commit)
- [Pull Request](#pull-request)
- [Reportando Bugs](#reportando-bugs)
- [Sugerindo Melhorias](#sugerindo-melhorias)

---

## 📜 Código de Conduta

Este projeto adere a um código de conduta. Ao participar, espera-se que você mantenha esse código. Por favor:

- ✅ Seja respeitoso e inclusivo
- ✅ Use linguagem acolhedora
- ✅ Aceite críticas construtivas
- ❌ Não use linguagem ou imagens inadequadas
- ❌ Não faça ataques pessoais ou políticos

---

## 🎯 Como Posso Contribuir?

### 1. Reportar Bugs
Encontrou um bug? Abra uma [issue](https://github.com/seu-usuario/liberacao-projetos/issues) com:
- Descrição clara do problema
- Passos para reproduzir
- Comportamento esperado vs observado
- Screenshots (se aplicável)
- Versão do Java e Spring Boot

### 2. Sugerir Funcionalidades
Tem uma ideia? Abra uma [issue](https://github.com/seu-usuario/liberacao-projetos/issues) com:
- Descrição da funcionalidade
- Justificativa (por que é útil?)
- Exemplos de uso

### 3. Melhorar Documentação
- Corrigir erros de digitação
- Adicionar exemplos
- Traduzir documentação
- Melhorar explicações

### 4. Contribuir com Código
- Corrigir bugs
- Implementar novas funcionalidades
- Melhorar performance
- Adicionar testes

---

## 🔄 Processo de Desenvolvimento

### 1. Fork do Projeto

```bash
# Via GitHub interface
# Clique em "Fork" no canto superior direito
```

### 2. Clone seu Fork

```bash
git clone https://github.com/seu-usuario/liberacao-projetos.git
cd liberacao-projetos
```

### 3. Configure o Upstream

```bash
git remote add upstream https://github.com/DanielHMoura/liberacao-projetos.git
```

### 4. Crie uma Branch

```bash
# Para nova funcionalidade
git checkout -b feature/nome-da-funcionalidade

# Para correção de bug
git checkout -b fix/nome-do-bug

# Para documentação
git checkout -b docs/descricao
```

### 5. Faça suas Alterações

```bash
# Edite os arquivos necessários
# Adicione testes se aplicável
# Atualize documentação se necessário
```

### 6. Teste suas Alterações

```bash
# Execute os testes
mvn test

# Execute a aplicação
mvn spring-boot:run

# Verifique se tudo funciona
```

### 7. Commit suas Alterações

```bash
git add .
git commit -m "Add: descrição clara do que foi feito"
```

### 8. Push para seu Fork

```bash
git push origin feature/nome-da-funcionalidade
```

### 9. Abra um Pull Request

- Vá até seu fork no GitHub
- Clique em "Compare & pull request"
- Preencha o template de PR
- Aguarde o review

---

## 💻 Padrões de Código

### Java

#### Nomenclatura

```java
// Classes: PascalCase
public class ProjetoService { }

// Métodos: camelCase
public void validarAcesso() { }

// Constantes: UPPER_SNAKE_CASE
private static final String STATUS_PAGO = "PAGO";

// Variáveis: camelCase
String nomeCliente = "Cliente ABC";
```

#### Organização

```java
// Ordem dos membros da classe:
1. Constantes
2. Atributos
3. Construtores
4. Métodos públicos
5. Métodos privados
```

#### Exemplo de Classe

```java
@Service
@RequiredArgsConstructor
public class ProjetoService {
    
    // 1. Constantes
    private static final String MSG_PROJETO_NAO_ENCONTRADO = "Projeto não encontrado";
    
    // 2. Atributos
    private final ProjetoRepository repository;
    private final SupabaseService supabaseService;
    
    // 3. Métodos públicos
    public Projeto criarProjeto(ProjetoRequestDTO dto) {
        validarCodigoUnico(dto.getCodigoAcesso());
        Projeto projeto = new Projeto();
        // ... lógica
        return repository.save(projeto);
    }
    
    // 4. Métodos privados
    private void validarCodigoUnico(String codigo) {
        if (repository.existsByCodigoAcesso(codigo)) {
            throw new IllegalArgumentException("Código já existe");
        }
    }
}
```

### Comentários

```java
// ❌ Evite comentários óbvios
int idade = 25; // define a idade

// ✅ Use comentários para explicar "por quê", não "o quê"
// Timeout de 3600s (1h) para URLs pré-assinadas do Supabase
private static final int URL_EXPIRATION_SECONDS = 3600;
```

### Tratamento de Exceções

```java
// ✅ Sempre use exceções específicas
throw new ProjetoNotFoundException("Projeto não encontrado: " + id);

// ❌ Evite exceções genéricas
throw new Exception("Erro");

// ✅ Log antes de lançar exceção
log.error("Erro ao buscar projeto com ID: {}", id, e);
throw new ProjetoNotFoundException("Projeto não encontrado", e);
```

---

## 📝 Padrões de Commit

### Formato

```
<tipo>: <descrição curta>

<corpo opcional - explicação detalhada>

<footer opcional - issues relacionadas>
```

### Tipos de Commit

| Tipo | Descrição | Exemplo |
|------|-----------|---------|
| `Add` | Nova funcionalidade | `Add: endpoint de listagem de projetos` |
| `Fix` | Correção de bug | `Fix: validação de PIN com 4 dígitos` |
| `Update` | Atualização de código existente | `Update: mensagens de erro mais claras` |
| `Refactor` | Refatoração de código | `Refactor: extrair validação para método separado` |
| `Docs` | Alteração em documentação | `Docs: adicionar exemplos de uso da API` |
| `Style` | Formatação de código | `Style: aplicar formatação Java Google Style` |
| `Test` | Adição ou correção de testes | `Test: adicionar testes para ProjetoService` |
| `Perf` | Melhoria de performance | `Perf: otimizar query de busca de projetos` |
| `Chore` | Tarefas de manutenção | `Chore: atualizar dependências do Maven` |

### Exemplos de Bons Commits

```bash
# ✅ Commit simples
Add: validação de tamanho de arquivo PDF

# ✅ Commit com corpo
Fix: erro ao fazer upload de arquivos grandes

O limite de 50MB não estava sendo respeitado devido a
configuração incorreta do Spring Boot. Ajustado o 
application.yml para aceitar uploads até 50MB.

Fixes #42

# ✅ Commit quebrando mudança
Update!: alterar estrutura de resposta da API

BREAKING CHANGE: O campo 'url' foi renomeado para 'urlDocumento'
em todas as respostas da API.

# ✅ Múltiplas mudanças relacionadas
Refactor: melhorar estrutura de validações

- Extrair validações para classe separada
- Adicionar testes unitários
- Atualizar documentação
```

### Exemplos de Commits Ruins

```bash
# ❌ Muito vago
Update: correções

# ❌ Múltiplas responsabilidades não relacionadas
Add: nova feature, fix bug, update docs

# ❌ Sem contexto
Fix: bug
```

---

## 🔀 Pull Request

### Checklist Antes de Abrir PR

- [ ] Código compila sem erros
- [ ] Testes passam (`mvn test`)
- [ ] Código segue os padrões do projeto
- [ ] Documentação atualizada (se necessário)
- [ ] Commits seguem o padrão estabelecido
- [ ] Branch está atualizada com `main`

### Template de Pull Request

```markdown
## 📋 Descrição

Breve descrição das mudanças.

## 🎯 Tipo de Mudança

- [ ] 🐛 Bug fix
- [ ] ✨ Nova funcionalidade
- [ ] 💥 Breaking change
- [ ] 📝 Documentação
- [ ] 🎨 Estilo/formatação
- [ ] ♻️ Refatoração
- [ ] ✅ Testes

## 🧪 Como Testar?

Passos para testar as mudanças:
1. ...
2. ...

## 📸 Screenshots (se aplicável)

[Adicione screenshots se relevante]

## ✅ Checklist

- [ ] Código compila sem erros
- [ ] Testes passam
- [ ] Documentação atualizada
- [ ] Commits seguem padrão

## 🔗 Issues Relacionadas

Fixes #[número da issue]
```

---

## 🐛 Reportando Bugs

### Template de Issue para Bugs

```markdown
## 🐛 Descrição do Bug

Descrição clara e concisa do bug.

## 📋 Para Reproduzir

Passos para reproduzir o comportamento:
1. Vá para '...'
2. Clique em '...'
3. Veja o erro

## ✅ Comportamento Esperado

Descrição do que deveria acontecer.

## 📸 Screenshots

Se aplicável, adicione screenshots.

## 🖥️ Ambiente

- OS: [ex: Windows 11, macOS 14, Ubuntu 22.04]
- Java: [ex: OpenJDK 17]
- Spring Boot: [ex: 3.2.0]
- Navegador (se aplicável): [ex: Chrome 120]

## 📝 Informações Adicionais

Qualquer outro contexto sobre o problema.
```

---

## 💡 Sugerindo Melhorias

### Template de Issue para Features

```markdown
## 💡 Descrição da Feature

Descrição clara da funcionalidade desejada.

## 🎯 Problema que Resolve

Por que essa feature é útil?

## 📝 Solução Proposta

Como você imagina que funcione?

## 🔄 Alternativas Consideradas

Outras formas de resolver o problema.

## 📋 Tarefas

- [ ] Tarefa 1
- [ ] Tarefa 2
- [ ] Tarefa 3

## 📸 Mockups (opcional)

Se tiver ideias visuais, adicione aqui.
```

---

## 🔍 Code Review

### Para Revisores

Ao revisar um PR, verifique:

- ✅ Código está limpo e legível
- ✅ Testes adequados foram adicionados
- ✅ Documentação foi atualizada
- ✅ Não há código duplicado
- ✅ Tratamento de erros apropriado
- ✅ Performance é aceitável
- ✅ Segurança não foi comprometida

### Para Contribuidores

- ✅ Responda aos comentários construtivamente
- ✅ Faça as alterações solicitadas
- ✅ Marque conversas como resolvidas quando aplicável
- ✅ Agradeça pelos reviews

---

## 🎓 Recursos Úteis

### Documentação

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)

### Ferramentas

- [IntelliJ IDEA](https://www.jetbrains.com/idea/)
- [Postman](https://www.postman.com/)
- [Git](https://git-scm.com/doc)

### Style Guides

- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- [Conventional Commits](https://www.conventionalcommits.org/)

---

## ❓ Dúvidas?

Se tiver qualquer dúvida sobre como contribuir:

- 📧 Abra uma [issue](https://github.com/seu-usuario/liberacao-projetos/issues) com a tag `question`
- 💬 Entre em contato via email: seu-email@example.com

---

## 🙏 Agradecimentos

Obrigado por dedicar seu tempo para melhorar este projeto! 

Toda contribuição, grande ou pequena, é muito apreciada. ❤️

---

<div align="center">

**Feito com ❤️ pela comunidade**

[⬆ Voltar ao topo](#-guia-de-contribuição)

</div>
