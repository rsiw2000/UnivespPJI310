import React, { useState, useEffect } from 'react';
import {
  Container,
  Paper,
  TextField,
  Button,
  Typography,
  Box,
  Alert,
  Link,
  Grid,
  MenuItem,
} from '@mui/material';
import { useForm, Controller } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { useNavigate, Link as RouterLink } from 'react-router-dom';
import { RegisterData, Estado, Cidade } from '../types';
import { userService, locationService } from '../services/api';

const schema = yup.object({
  nome: yup.string().required('Nome é obrigatório').min(2, 'Nome deve ter pelo menos 2 caracteres'),
  email: yup.string().required('Email é obrigatório').email('Email inválido'),
  telefone: yup.string().required('Telefone é obrigatório'),
  cpfCnpj: yup.string().required('CPF/CNPJ é obrigatório'),
  estado: yup.string().required('Estado é obrigatório'),
  cidade: yup.number().required('Cidade é obrigatória').min(1, 'Selecione uma cidade'),
  bairro: yup.string().required('Bairro é obrigatório'),
  password: yup.string().required('Senha é obrigatória').min(8, 'Senha deve ter pelo menos 8 caracteres'),
  confirmPassword: yup
    .string()
    .required('Confirmação de senha é obrigatória')
    .oneOf([yup.ref('password')], 'Senhas não conferem'),
});

const Register: React.FC = () => {
  const [error, setError] = useState<string>('');
  const [success, setSuccess] = useState<string>('');
  const [loading, setLoading] = useState(false);
  const [estados, setEstados] = useState<Estado[]>([]);
  const [cidades, setCidades] = useState<Cidade[]>([]);
  const [loadingCidades, setLoadingCidades] = useState(false);
  const navigate = useNavigate();

  const { control, handleSubmit, watch, formState: { errors }, setValue } = useForm<RegisterData>({
    resolver: yupResolver(schema),
    defaultValues: {
      nome: '',
      email: '',
      telefone: '',
      cpfCnpj: '',
      estado: 'SP',
      cidade: 0,
      bairro: '',
      password: '',
      confirmPassword: '',
    },
  });

  const watchedEstado = watch('estado');

  useEffect(() => {
    const loadEstados = async () => {
      try {
        const response = await locationService.getStates();
        if (response.success) {
          setEstados(response.data);
        }
      } catch (error) {
        console.error('Erro ao carregar estados:', error);
      }
    };

    loadEstados();
  }, []);

  useEffect(() => {
    const loadCidades = async () => {
      if (watchedEstado) {
        setLoadingCidades(true);
        try {
          const response = await locationService.getCities(watchedEstado);
          if (response.success) {
            setCidades(response.data);
            setValue('cidade', 0); // Reset cidade quando estado muda
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
  }, [watchedEstado, setValue]);

  const onSubmit = async (data: RegisterData) => {
    setLoading(true);
    setError('');
    setSuccess('');

    try {
      const response = await userService.register(data);
      if (response.success) {
        setSuccess('Cadastro realizado com sucesso! Você pode fazer login agora.');
        setTimeout(() => {
          navigate('/login');
        }, 2000);
      } else {
        setError(response.message || 'Erro ao criar conta');
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erro ao criar conta. Tente novamente.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container component="main" maxWidth="md">
      <Box
        sx={{
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          mt: 2,
        }}
      >
        <Paper elevation={3} sx={{ padding: 4, width: '100%' }}>
          <Typography component="h1" variant="h4" align="center" gutterBottom>
            Cadastrar
          </Typography>
          <Typography variant="body1" align="center" color="text.secondary" sx={{ mb: 3 }}>
            Crie sua conta para começar a usar nossa plataforma
          </Typography>

          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}

          {success && (
            <Alert severity="success" sx={{ mb: 2 }}>
              {success}
            </Alert>
          )}

          <Box component="form" onSubmit={handleSubmit(onSubmit)} sx={{ mt: 1 }}>
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
                      required
                      fullWidth
                      label="Email"
                      type="email"
                      error={!!errors.email}
                      helperText={errors.email?.message}
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
                      required
                      fullWidth
                      label="CPF/CNPJ"
                      error={!!errors.cpfCnpj}
                      helperText={errors.cpfCnpj?.message}
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
                      helperText={errors.cidade?.message || (loadingCidades ? 'Carregando cidades...' : '')}
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

              <Grid item xs={12} sm={6}>
                <Controller
                  name="password"
                  control={control}
                  render={({ field }) => (
                    <TextField
                      {...field}
                      required
                      fullWidth
                      label="Senha"
                      type="password"
                      error={!!errors.password}
                      helperText={errors.password?.message}
                    />
                  )}
                />
              </Grid>

              <Grid item xs={12}>
                <Controller
                  name="confirmPassword"
                  control={control}
                  render={({ field }) => (
                    <TextField
                      {...field}
                      required
                      fullWidth
                      label="Confirmar Senha"
                      type="password"
                      error={!!errors.confirmPassword}
                      helperText={errors.confirmPassword?.message}
                    />
                  )}
                />
              </Grid>
            </Grid>

            <Button
              type="submit"
              fullWidth
              variant="contained"
              size="large"
              disabled={loading}
              sx={{ mt: 3, mb: 2 }}
            >
              {loading ? 'Criando conta...' : 'Criar Conta'}
            </Button>

            <Box sx={{ textAlign: 'center' }}>
              <Link component={RouterLink} to="/login" variant="body2">
                Já tem uma conta? Faça login
              </Link>
            </Box>
          </Box>
        </Paper>
      </Box>
    </Container>
  );
};

export default Register;
