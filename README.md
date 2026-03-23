# Eu Indico - Plataforma de Prestadores de Serviços

Uma plataforma completa para conectar prestadores de serviços com clientes, desenvolvida com Flask (backend) e React (frontend).

## 📁 Estrutura do Projeto

```
Univesp-PJI240/
├── backend/                 # API Flask
│   ├── app.py              # Aplicação principal da API
│   ├── model/              # Modelos de dados
│   ├── utils/              # Utilitários e configurações
│   ├── mockdata/           # Dados de teste
│   ├── sql_scripts/        # Scripts SQL
│   └── requirements.txt    # Dependências Python
├── frontend/               # Aplicação React
│   ├── src/
│   │   ├── components/     # Componentes reutilizáveis
│   │   ├── pages/         # Páginas da aplicação
│   │   ├── contexts/      # Contextos React
│   │   ├── services/      # Serviços de API
│   │   └── types/         # Tipos TypeScript
│   ├── public/
│   └── package.json
├── static/                 # Recursos estáticos (logos, etc.)
└── templates/             # Templates HTML originais (referência)
```

## 🚀 Como Executar

### Backend (API Flask)

1. Navegue até a pasta backend:
   ```bash
   cd backend
   ```

2. Instale as dependências:
   ```bash
   pip install -r requirements.txt
   ```

3. Execute a aplicação:
   ```bash
   python app.py
   ```

A API estará disponível em `http://localhost:5050`

### Frontend (React)

1. Navegue até a pasta frontend:
   ```bash
   cd frontend
   ```

2. Instale as dependências:
   ```bash
   npm install
   ```

3. Execute a aplicação:
   ```bash
   npm start
   ```

A aplicação estará disponível em `http://localhost:3000`

## 🎯 Funcionalidades

### Para Usuários
- ✅ Cadastro e autenticação
- ✅ Gerenciamento de perfil
- ✅ Busca de profissionais por categoria
- ✅ Sistema de avaliação de profissionais
- ✅ Cadastro de profissões pessoais

### Para Administradores
- ✅ Gerenciamento de profissões no sistema
- ✅ Acesso completo a todas as funcionalidades

## 🛠 Tecnologias Utilizadas

### Backend
- **Flask** - Framework web Python
- **SQLAlchemy** - ORM para banco de dados
- **PostgreSQL** - Banco de dados
- **Flask-CORS** - Habilitação de CORS

### Frontend
- **React 18** - Biblioteca JavaScript
- **TypeScript** - Tipagem estática
- **Material-UI (MUI)** - Biblioteca de componentes
- **React Router** - Roteamento
- **React Hook Form** - Gerenciamento de formulários
- **Axios** - Cliente HTTP

## 📱 Design e UX

O frontend foi completamente redesenhado com:
- ✨ Interface moderna e responsiva
- 🎨 Design system consistente com Material Design
- 📱 Experiência otimizada para mobile e desktop
- ♿ Acessibilidade aprimorada
- 🚀 Performance otimizada

## 🔐 Autenticação

O sistema implementa autenticação baseada em token com:
- Login/logout seguro
- Proteção de rotas
- Controle de acesso por nível de usuário
- Persistência de sessão

## 🗄 Banco de Dados

O projeto utiliza PostgreSQL com as seguintes tabelas principais:
- `usuarios` - Dados dos usuários
- `profissoes` - Categorias de profissões
- `usuarios_profissoes` - Relação usuário-profissão
- `servicos_prestados` - Avaliações e comentários
- `estados` e `municipios` - Dados geográficos

## 📋 API Endpoints

### Autenticação
- `POST /api/auth/login` - Login
- `POST /api/auth/logout` - Logout

### Usuários
- `POST /api/user/register` - Cadastro
- `GET /api/user/profile` - Buscar perfil
- `PUT /api/user/profile` - Atualizar perfil
- `GET /api/user/professions` - Profissões do usuário
- `POST /api/user/professions` - Adicionar profissão

### Localizações
- `GET /api/locations/states` - Estados
- `GET /api/locations/cities/{uf}` - Cidades por estado

### Profissões
- `GET /api/professions` - Listar profissões
- `POST /api/professions` - Criar profissão (admin)

### Busca
- `GET /api/search/professionals` - Buscar profissionais

### Avaliações
- `POST /api/reviews` - Criar avaliação

## 🔧 Configuração de Desenvolvimento

### Variáveis de Ambiente

Crie um arquivo `.env` na pasta frontend:
```
REACT_APP_API_URL=http://localhost:5050/api
```

### Banco de Dados

Configure a conexão do banco no arquivo `backend/utils/database.py`

## 📝 Contribuição

1. Faça um fork do projeto
2. Crie uma branch para sua feature
3. Commit suas mudanças
4. Push para a branch
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo LICENSE para mais detalhes.

## 👥 Equipe

Desenvolvido como projeto acadêmico para a Univesp - PJI240

---

**Nota**: Este é um projeto educacional desenvolvido para demonstrar conceitos de desenvolvimento full-stack com separação clara entre frontend e backend.