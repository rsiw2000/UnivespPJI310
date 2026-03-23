import React, { useState, useEffect } from 'react';
import {
  Container,
  Typography,
  Box,
  Card,
  CardContent,
  TextField,
  Button,
  Alert,
  Grid,
  MenuItem,
  Paper,
  Divider,
} from '@mui/material';
import { useForm, Controller } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { UserProfile, Estado, Cidade } from '../types';
import { userService, locationService } from '../services/api';
import { useAuth } from '../contexts/AuthContext';

const schema = yup.object({
  nome: yup.string().required('Nome é obrigatório').min(2, 'Nome deve ter pelo menos 2 caracteres'),
  email: yup.string().required('Email é obrigatório').email('Email inválido'),
  telefone: yup.string().required('Telefone é obrigatório'),
  estado: yup.string().required('Estado é obrigatório'),
  cidade: yup.number().required('Cidade é obrigatória').min(1, 'Selecione uma cidade'),
  bairro: yup.string().required('Bairro é obrigatório'),
  cpfCnpj: yup.string().required('CPF/CNPJ é obrigatório'),
});

const Profile: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [loadingProfile, setLoadingProfile] = useState(true);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [estados, setEstados] = useState<Estado[]>([]);
  const [cidades, setCidades] = useState<Cidade[]>([]);
  const [loadingCidades, setLoadingCidades] = useState(false);
  const { user } = useAuth();

  const { control, handleSubmit, formState: { errors }, setValue, watch } = useForm<UserProfile>({
    resolver: yupResolver(schema),
    defaultValues: {
      nome: '',
      email: '',
      telefone: '',
      estado: 'SP',
      cidade: 0,
      bairro: '',
      cpfCnpj: '',
    },
  });

  const watchedEstado = watch('estado');

  useEffect(() => {
    const loadData = async () => {
      try {
        // Carregar estados
        const statesResponse = await locationService.getStates();
        if (statesResponse.success) {
          setEstados(statesResponse.data);
        }

        // Carregar perfil do usuário
        const profileResponse = await userService.getProfile();
        if (profileResponse.success) {
          const profile = profileResponse.user;
          setValue('nome', profile.nome);
          setValue('email', profile.email);
          setValue('telefone', profile.telefone);
          setValue('estado', profile.estado);
          setValue('cidade', profile.cidade);
          setValue('bairro', profile.bairro);
          setValue('cpfCnpj', profile.cpfCnpj);
        }
      } catch (error) {
        console.error('Erro ao carregar dados:', error);
        setError('Erro ao carregar dados do perfil');
      } finally {
        setLoadingProfile(false);
      }
    };

    loadData();
  }, [setValue]);

  useEffect(() => {
    const loadCidades = async () => {
      if (watchedEstado) {
        setLoadingCidades(true);
        try {
          const response = await locationService.getCities(watchedEstado);
          if (response.success) {
            setCidades(response.data);
          }
        } catch (error) {
          console.error('Erro ao carregar cidades:', error);
          setCidades([]);
        } finally {
          setLoadingCidades(false);
        }
      }
    };

    loadCidades();
  }, [watchedEstado]);

  const onSubmit = async (data: UserProfile) => {
    setLoading(true);
    setError('');
    setMessage('');

    try {
      const response = await userService.updateProfile(data);
      if (response.success) {
        setMessage('Perfil atualizado com sucesso!');
      } else {
        setError(response.message || 'Erro ao atualizar perfil');
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erro ao atualizar perfil');
    } finally {
      setLoading(false);
    }
  };

  if (loadingProfile) {
    return (
      <Container maxWidth="md">
        <Box sx={{ textAlign: 'center', mt: 4 }}>
          <Typography>Carregando perfil...</Typography>
        </Box>
      </Container>
    );
  }

  return (
    <Container maxWidth="md">
      <Typography variant="h4" component="h1" gutterBottom>
        Meu Perfil
      </Typography>

      <Grid container spacing={3}>
        {/* Informações da Conta */}
        <Grid item xs={12}>
          <Paper sx={{ p: 3, mb: 3 }}>
            <Typography variant="h6" gutterBottom color="primary">
              Informações da Conta
            </Typography>
            <Grid container spacing={2}>
              <Grid item xs={12} sm={6}>
                <Typography variant="body2" color="text.secondary">
                  Nome de usuário:
                </Typography>
                <Typography variant="body1" sx={{ fontWeight: 'medium' }}>
                  {user?.name}
                </Typography>
              </Grid>
              <Grid item xs={12} sm={6}>
                <Typography variant="body2" color="text.secondary">
                  Tipo de conta:
                </Typography>
                <Typography variant="body1" sx={{ fontWeight: 'medium' }}>
                  {user?.isAdmin ? 'Administrador' : 'Usuário'}
                </Typography>
              </Grid>
            </Grid>
          </Paper>
        </Grid>

        {/* Formulário de Edição */}
        <Grid item xs={12}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Dados Pessoais
              </Typography>

              {error && (
                <Alert severity="error" sx={{ mb: 2 }}>
                  {error}
                </Alert>
              )}

              {message && (
                <Alert severity="success" sx={{ mb: 2 }}>
                  {message}
                </Alert>
              )}

              <Box component="form" onSubmit={handleSubmit(onSubmit)} sx={{ mt: 2 }}>
                <Grid container spacing={2}>
                  <Grid item xs={12}>
                    <Controller
                      name="nome"
                      control={control}
                      render={({ field }) => (
                        <TextField
                          {...field}
                          required
                          fullWidth
                          label="Nome Completo"
                          error={!!errors.nome}
                          helperText={errors.nome?.message}
                        />
                      )}
                    />
                  </Grid>

                  <Grid item xs={12} sm={6}>
                    <Controller
                      name="email"
                      control={control}
                      render={({ field }) => (
                        <TextField
                          {...field}
                          fullWidth
                          label="Email"
                          type="email"
                          disabled
                          helperText="Email não pode ser alterado"
                        />
                      )}
                    />
                  </Grid>

                  <Grid item xs={12} sm={6}>
                    <Controller
                      name="telefone"
                      control={control}
                      render={({ field }) => (
                        <TextField
                          {...field}
                          required
                          fullWidth
                          label="Telefone"
                          error={!!errors.telefone}
                          helperText={errors.telefone?.message}
                        />
                      )}
                    />
                  </Grid>

                  <Grid item xs={12} sm={6}>
                    <Controller
                      name="cpfCnpj"
                      control={control}
                      render={({ field }) => (
                        <TextField
                          {...field}
                          fullWidth
                          label="CPF/CNPJ"
                          disabled
                          helperText="CPF/CNPJ não pode ser alterado"
                        />
                      )}
                    />
                  </Grid>

                  <Grid item xs={12} sm={6}>
                    <Controller
                      name="estado"
                      control={control}
                      render={({ field }) => (
                        <TextField
                          {...field}
                          required
                          fullWidth
                          select
                          label="Estado"
                          error={!!errors.estado}
                          helperText={errors.estado?.message}
                        >
                          {estados.map((estado) => (
                            <MenuItem key={estado.uf} value={estado.uf}>
                              {estado.nome}
                            </MenuItem>
                          ))}
                        </TextField>
                      )}
                    />
                  </Grid>

                  <Grid item xs={12} sm={6}>
                    <Controller
                      name="cidade"
                      control={control}
                      render={({ field }) => (
                        <TextField
                          {...field}
                          required
                          fullWidth
                          select
                          label="Cidade"
                          disabled={loadingCidades || cidades.length === 0}
                          error={!!errors.cidade}
                          helperText={
                            errors.cidade?.message || 
                            (loadingCidades ? 'Carregando cidades...' : '')
                          }
                        >
                          <MenuItem value={0}>Selecione uma cidade</MenuItem>
                          {cidades.map((cidade) => (
                            <MenuItem key={cidade.id} value={cidade.id}>
                              {cidade.nome}
                            </MenuItem>
                          ))}
                        </TextField>
                      )}
                    />
                  </Grid>

                  <Grid item xs={12} sm={6}>
                    <Controller
                      name="bairro"
                      control={control}
                      render={({ field }) => (
                        <TextField
                          {...field}
                          required
                          fullWidth
                          label="Bairro"
                          error={!!errors.bairro}
                          helperText={errors.bairro?.message}
                        />
                      )}
                    />
                  </Grid>
                </Grid>

                <Divider sx={{ my: 3 }} />

                <Box sx={{ display: 'flex', gap: 2, justifyContent: 'flex-end' }}>
                  <Button
                    variant="outlined"
                    onClick={() => window.history.back()}
                  >
                    Cancelar
                  </Button>
                  <Button
                    type="submit"
                    variant="contained"
                    disabled={loading}
                  >
                    {loading ? 'Salvando...' : 'Salvar Alterações'}
                  </Button>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Container>
  );
};

export default Profile;
