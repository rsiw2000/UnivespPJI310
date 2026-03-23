# Guia de Configuração - Eu Indico

## 🚀 Execução Rápida

### 1. Backend (Terminal 1)
```bash
cd backend
pip install -r requirements.txt
python app.py
```

### 2. Frontend (Terminal 2)
```bash
cd frontend
npm install
npm start
```

## 📋 Pré-requisitos

- Python 3.8+
- Node.js 16+
- PostgreSQL (configurado)
- pip e npm

## 🔧 Configuração Detalhada

### Backend

1. **Instalar dependências Python:**
   ```bash
   cd backend
   pip install -r requirements.txt
   ```

2. **Configurar banco de dados:**
   - Configure PostgreSQL
   - Ajuste a string de conexão em `utils/database.py`
   - Execute os scripts SQL em `sql_scripts/`

3. **Executar servidor:**
   ```bash
   python app.py
   ```
   Servidor rodará em: `http://localhost:5050`

### Frontend

1. **Instalar dependências Node:**
   ```bash
   cd frontend
   npm install
   ```

2. **Configurar ambiente (opcional):**
   Crie `.env` na pasta frontend:
   ```
   REACT_APP_API_URL=http://localhost:5050/api
   ```

3. **Executar aplicação:**
   ```bash
   npm start
   ```
   Aplicação rodará em: `http://localhost:3000`

## 🧪 Dados de Teste

O projeto inclui dados mock em `backend/mockdata/` para facilitar o desenvolvimento.

## 🐛 Solução de Problemas

### Erro de CORS
- Verifique se o Flask-CORS está instalado
- Confirme que a configuração CORS está ativa no backend

### Erro de Conexão com API
- Verifique se o backend está rodando na porta 5050
- Confirme a URL da API no frontend

### Erro de Banco de Dados
- Verifique a conexão PostgreSQL
- Execute os scripts SQL necessários

## 📱 Funcionalidades Implementadas

✅ Sistema de autenticação completo  
✅ Cadastro de usuários com validação  
✅ Busca de profissionais por categoria  
✅ Sistema de avaliação  
✅ Gerenciamento de profissões pessoais  
✅ Interface responsiva e moderna  
✅ Painel administrativo  

## 🔐 Usuários de Teste

Após popular o banco com os scripts SQL, você pode usar:

**Admin:**
- Email: admin@exemplo.com
- Senha: admin123

**Usuário comum:**
- Email: usuario@exemplo.com  
- Senha: usuario123

## 📞 Suporte

Em caso de dúvidas, verifique:
1. Se todas as dependências foram instaladas
2. Se o banco de dados está configurado
3. Se as portas 3000 e 5050 estão livres
4. Os logs de erro no console
