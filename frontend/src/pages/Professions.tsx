import React, { useState, useEffect } from 'react';
import {
  Container,
  Typography,
  Box,
  Card,
  CardContent,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  TextField,
  Button,
  Alert,
  Grid,
} from '@mui/material';
import { Add as AddIcon } from '@mui/icons-material';
import { useForm, Controller } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { Profissao } from '../types';
import { professionService } from '../services/api';

const schema = yup.object({
  nome: yup.string().required('Nome da profissão é obrigatório').min(2, 'Nome deve ter pelo menos 2 caracteres'),
});

interface ProfessionFormData {
  nome: string;
}

const Professions: React.FC = () => {
  const [profissoes, setProfissoes] = useState<Profissao[]>([]);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const { control, handleSubmit, reset, formState: { errors } } = useForm<ProfessionFormData>({
    resolver: yupResolver(schema),
    defaultValues: {
      nome: '',
    },
  });

  const loadProfissoes = async () => {
    try {
      const response = await professionService.getProfessions();
      if (response.success) {
        setProfissoes(response.data);
      }
    } catch (error) {
      console.error('Erro ao carregar profissões:', error);
      setError('Erro ao carregar profissões');
    }
  };

  useEffect(() => {
    loadProfissoes();
  }, []);

  const onSubmit = async (data: ProfessionFormData) => {
    setLoading(true);
    setError('');
    setMessage('');

    try {
      const response = await professionService.createProfession(data.nome);
      if (response.success) {
        setMessage('Profissão cadastrada com sucesso!');
        reset();
        await loadProfissoes(); // Recarregar lista
      } else {
        setError(response.message || 'Erro ao cadastrar profissão');
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erro ao cadastrar profissão');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container maxWidth="lg">
      <Typography variant="h4" component="h1" gutterBottom>
        Cadastro de Profissões
      </Typography>

      <Grid container spacing={4}>
        {/* Lista de Profissões */}
        <Grid item xs={12} md={8}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Profissões Cadastradas
              </Typography>
              
              {profissoes.length > 0 ? (
                <TableContainer component={Paper} variant="outlined">
                  <Table>
                    <TableHead>
                      <TableRow>
                        <TableCell>ID</TableCell>
                        <TableCell>Nome da Profissão</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {profissoes.map((profissao) => (
                        <TableRow key={profissao.id}>
                          <TableCell>{profissao.id}</TableCell>
                          <TableCell>{profissao.nome}</TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </TableContainer>
              ) : (
                <Typography color="text.secondary">
                  Nenhuma profissão cadastrada ainda.
                </Typography>
              )}
            </CardContent>
          </Card>
        </Grid>

        {/* Formulário de Cadastro */}
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Nova Profissão
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

              <Box component="form" onSubmit={handleSubmit(onSubmit)}>
                <Controller
                  name="nome"
                  control={control}
                  render={({ field }) => (
                    <TextField
                      {...field}
                      fullWidth
                      label="Nome da Profissão"
                      error={!!errors.nome}
                      helperText={errors.nome?.message}
                      sx={{ mb: 2 }}
                    />
                  )}
                />

                <Button
                  type="submit"
                  variant="contained"
                  fullWidth
                  disabled={loading}
                  startIcon={<AddIcon />}
                >
                  {loading ? 'Cadastrando...' : 'Cadastrar Profissão'}
                </Button>
              </Box>
            </CardContent>
          </Card>

          {/* Informações Adicionais */}
          <Card sx={{ mt: 2 }}>
            <CardContent>
              <Typography variant="h6" gutterBottom color="primary">
                Sobre o Cadastro de Profissões
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Esta seção é exclusiva para administradores. Aqui você pode:
              </Typography>
              <Box component="ul" sx={{ mt: 1, pl: 2 }}>
                <Typography component="li" variant="body2" color="text.secondary">
                  Cadastrar novas profissões no sistema
                </Typography>
                <Typography component="li" variant="body2" color="text.secondary">
                  Visualizar todas as profissões existentes
                </Typography>
                <Typography component="li" variant="body2" color="text.secondary">
                  Permitir que usuários se cadastrem nessas profissões
                </Typography>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Container>
  );
};

export default Professions;
