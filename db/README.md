# Banco de dados: proteção dos dados dos clientes

## O que muda

| Dado | Antes | Agora | Técnica |
|---|---|---|---|
| Senha | texto puro | `senha_hash` | PBKDF2-HMAC-SHA256, 600 000 iterações, salt aleatório por senha (irreversível) |
| CPF | texto puro | `cpf_cifrado` + `cpf_indice` | AES-256-GCM (reversível só com a chave) + HMAC-SHA256 para garantir CPF único |
| Telefone | texto puro | `telefone_cifrado` | AES-256-GCM |
| Tokens de ativação/recuperação | texto puro | `token_*_hash` + `token_*_expira` | SHA-256: o token só existe no link do e-mail |
| E-mail | diferenciava maiúsculas | sempre minúsculo, único sem diferenciar maiúsculas | índice `lower(email)` |
| Tentativas de login | sem limite | 5 erros seguidos bloqueiam a conta por 15 min | `tentativas_login_falhas`, `bloqueado_ate` |

As chaves ficam **fora do banco**, em variáveis de ambiente. Assim, quem conseguir uma cópia do banco não consegue ler CPFs nem telefones.

| Variável | Para quê |
|---|---|
| `AGNELLO_CHAVE_DADOS` | Chave AES-256 que cifra o CPF e o telefone |
| `AGNELLO_CHAVE_INDICE` | Chave HMAC do índice do CPF |
| `AGNELLO_URL_BASE` | Endereço público do site, usado nos links dos e-mails (ex.: `https://vinheria-agnello.onrender.com`). No Eclipse pode ficar vazia. |

> ⚠️ **Se perder a `AGNELLO_CHAVE_DADOS`, os CPFs e telefones cifrados não podem ser recuperados.** Guarde as duas chaves num gestor de senhas (Bitwarden, 1Password…). Nunca as coloque no código, no Git nem em prints de tela.

---

## Passo a passo da migração (banco que já existe no Neon)

A migração segue o padrão **expandir → migrar → contrair**: as colunas antigas só são apagadas no fim, depois de tudo estar conferido. Até ao passo 6 dá para voltar atrás sem perder nada.

### 0. Backup
Neon → **Branches** → **Create branch** (ex.: `antes-da-criptografia`). É uma cópia instantânea do banco.

### 1. Gerar as chaves (uma vez)
Eclipse → `src/main/java/com/agnello/migracao/GerarChaves.java` → botão direito → **Run As → Java Application**.
O Console mostra duas linhas `AGNELLO_CHAVE_DADOS=…` e `AGNELLO_CHAVE_INDICE=…`. Guarde-as no gestor de senhas.

### 2. Configurar as variáveis no Tomcat do Eclipse
Separador **Servers** → duplo clique no Tomcat → **Open launch configuration** → separador **Environment** → **Add…**:
`NEON_DB_URL`, `NEON_DB_USER`, `NEON_DB_PASSWORD` (já existentes) + `AGNELLO_CHAVE_DADOS` + `AGNELLO_CHAVE_INDICE`.

### 3. Rodar o V2 no Neon
Neon → **SQL Editor** → cole `migracoes/V2__clientes_colunas_seguras.sql` → **Run**.
Só acrescenta colunas; o site atual continua funcionando.

### 4. Migrar os dados existentes
Eclipse → **Run → Run Configurations… → Java Application → New** → Main class: `com.agnello.migracao.MigrarDadosSensiveis`.
- Separador **Environment**: as mesmas 5 variáveis do passo 2.
- Separador **Arguments**: `--simular` → **Run**. Confira o resumo (nada é gravado).
- Apague `--simular` e rode de novo para gravar.

### 5. Publicar a versão nova
Render → **Environment**: adicione `AGNELLO_CHAVE_DADOS`, `AGNELLO_CHAVE_INDICE` (**as mesmas** do Eclipse) e `AGNELLO_URL_BASE`.
Depois faça o merge do Pull Request na `main`; o Render publica sozinho.
No log do Render deve aparecer `Chaves de criptografia carregadas.`

### 6. Rodar a migração de novo
Mesmo comando do passo 4 (sem `--simular`). Pega quem se cadastrou pela versão antiga entre os passos 4 e 5. Se mostrar `Clientes migrados: 0`, está tudo em dia.

### 7. Rodar o V3 no Neon
`migracoes/V3__clientes_remover_texto_puro.sql` → apaga as colunas em texto puro e cria as regras de integridade.
Se ainda houver algum cliente por migrar, o script **para sem alterar nada** e diz quantos faltam.

### 8. (Opcional, recomendado) Rodar o V4
`migracoes/V4__produtos_integridade.sql` → preços em `NUMERIC(10,2)` (dinheiro não deve usar `double`) e proíbe preço e estoque negativos.

### Conferir
```sql
-- Não deve existir nenhuma coluna senha, cpf, telefone ou token_* sem _hash
SELECT column_name FROM information_schema.columns WHERE table_name = 'clientes' ORDER BY 1;

-- Todos os valores cifrados começam com v1: e todos os hashes com pbkdf2_sha256$600000$
SELECT id, left(senha_hash, 22), left(cpf_cifrado, 3), left(telefone_cifrado, 3) FROM clientes;
```

### Voltar atrás
- **Antes do passo 7:** publique de novo a versão anterior da aplicação. As colunas antigas continuam lá.
- **Depois do passo 7:** restaure o branch criado no passo 0.

---

## Banco novo do zero
Para criar um ambiente de testes vazio, rode `schema.sql`. Ele já cria as tabelas no formato final, sem passar pelas migrações.

## Trocar uma chave no futuro
Os valores cifrados começam com `v1:`. Para trocar a chave de dados: crie a versão `v2` em `CifraDados`, decifre com a chave antiga e cifre de novo com a nova, usando um programa igual ao `MigrarDadosSensiveis`.
