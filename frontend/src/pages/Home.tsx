import React from 'react';
import {
  Container,
  Typography,
  Box,
  Card,
  CardContent,
  Grid,
  Button,
} from '@mui/material';
import {
  Search as SearchIcon,
  Work as WorkIcon,
  PersonAdd as PersonAddIcon,
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

const Home: React.FC = () => {
  const { isAuthenticated, user } = useAuth();
  const navigate = useNavigate();

  const features = [
    {
      title: 'Buscar Profissionais',
      description: 'Encontre prestadores de serviços qualificados na sua região',
      icon: <SearchIcon sx={{ fontSize: 40 }} />,
      action: () => navigate('/buscar'),
      requireAuth: true,
    },
    {
      title: 'Cadastrar Profissões',
      description: 'Adicione suas habilidades e seja encontrado por clientes',
      icon: <WorkIcon sx={{ fontSize: 40 }} />,
      action: () => navigate('/minhas-profissoes'),
      requireAuth: true,
    },
    {
      title: 'Criar Conta',
      description: 'Junte-se à nossa comunidade de profissionais',
      icon: <PersonAddIcon sx={{ fontSize: 40 }} />,
      action: () => navigate('/cadastro'),
      requireAuth: false,
    },
  ];

  return (
    <Container maxWidth="lg">
      <Box sx={{ textAlign: 'center', mb: 6 }}>
        <Typography variant="h2" component="h1" gutterBottom color="primary">
          Eu Indico
        </Typography>
        <Typography variant="h5" component="h2" gutterBottom color="text.secondary">
          Conectando você aos melhores prestadores de serviços
        </Typography>
        {isAuthenticated ? (
          <Typography variant="h6" sx={{ mt: 2 }}>
            Olá, {user?.name}! {user?.isAdmin && '(ADMIN)'}
          </Typography>
        ) : (
          <Typography variant="body1" sx={{ mt: 2 }}>
            Faça login para começar a usar nossa plataforma
          </Typography>
        )}
      </Box>

      <Grid container spacing={4} justifyContent="center">
        {features
          .filter(feature => !feature.requireAuth || isAuthenticated)
          .map((feature, index) => (
            <Grid item key={index} xs={12} sm={6} md={4}>
              <Card 
                sx={{ 
                  height: '100%', 
                  display: 'flex', 
                  flexDirection: 'column',
                  transition: 'transform 0.2s',
                  '&:hover': {
                    transform: 'translateY(-4px)',
                    boxShadow: 4,
                  },
                }}
              >
                <CardContent sx={{ flexGrow: 1, textAlign: 'center', p: 3 }}>
                  <Box sx={{ color: 'primary.main', mb: 2 }}>
                    {feature.icon}
                  </Box>
                  <Typography variant="h6" component="h3" gutterBottom>
                    {feature.title}
                  </Typography>
                  <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                    {feature.description}
                  </Typography>
                  <Button
                    variant="contained"
                    onClick={feature.action}
                    fullWidth
                  >
                    Acessar
                  </Button>
                </CardContent>
              </Card>
            </Grid>
          ))}
      </Grid>

      {!isAuthenticated && (
        <Box sx={{ textAlign: 'center', mt: 6 }}>
          <Typography variant="h6" gutterBottom>
            Pronto para começar?
          </Typography>
          <Box sx={{ display: 'flex', gap: 2, justifyContent: 'center', flexWrap: 'wrap' }}>
            <Button
              variant="contained"
              size="large"
              onClick={() => navigate('/login')}
            >
              Fazer Login
            </Button>
            <Button
              variant="outlined"
              size="large"
              onClick={() => navigate('/cadastro')}
            >
              Criar Conta
            </Button>
          </Box>
        </Box>
      )}

      <Box sx={{ mt: 8, textAlign: 'center', py: 4, borderTop: 1, borderColor: 'divider' }}>
        <Typography variant="body2" color="text.secondary">
          © 2024 Eu Indico - Plataforma de Prestadores de Serviços
        </Typography>
      </Box>
    </Container>
  );
};

export default Home;
