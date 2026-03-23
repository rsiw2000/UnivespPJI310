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
  MenuItem,
  Chip,
} from '@mui/material';
import { Add as AddIcon } from '@mui/icons-material';
import { useForm, Controller } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { Profissao, UserProfession } from '../types';
import { professionService, userService } from '../services/api';

const schema = yup.object({
  profissao: yup.number().required('Profissão é obrigatória').min(1, 'Selecione uma profissão'),
});

interface MyProfessionFormData {
  profissao: number;
}

const MyProfessions: React.FC = () => {
  const [minhasProfissoes, setMinhasProfissoes] = useState<UserProfession[]>([]);
  const [todasProfissoes, setTodasProfissoes] = useState<Profissao[]>([]);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const { control, handleSubmit, reset, formState: { errors } } = useForm<MyProfessionFormData>({
    resolver: yupResolver(schema),
    defaultValues: {
      profissao: 0,
    },
  });

  const loadMinhasProfissoes = async () => {
    try {
      const response = await userService.getUserProfessions();
      if (response.success) {
        setMinhasProfissoes(response.data);
      }
    } catch (error) {
      console.error('Erro ao carregar minhas profissões:', error);
      setError('Erro ao carregar suas profissões');
    }
  };

  const loadTodasProfissoes = async () => {
    try {
      const response = await professionService.getProfessions();
      if (response.success) {
        setTodasProfissoes(response.data);
      }
    } catch (error) {
      console.error('Erro ao carregar profissões:', error);
      setError('Erro ao carregar profissões');
    }
  };

  useEffect(() => {
    loadMinhasProfissoes();
    loadTodasProfissoes();
  }, []);

  const onSubmit = async (data: MyProfessionFormData) => {
    setLoading(true);
    setError('');
    setMessage('');

    try {
      const response = await userService.addUserProfession(data.profissao);
      if (response.success) {
        setMessage('Profissão adicionada com sucesso!');
        reset();
        await loadMinhasProfissoes(); // Recarregar lista
      } else {
        setError(response.message || 'Erro ao adicionar profissão');
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erro ao adicionar profissão');
    } finally {
      setLoading(false);
    }
  };

  const getSituacaoColor = (situacao: string) => {
    switch (situacao.toUpperCase()) {
      case 'A':
      case 'ATIVO':
        return 'success';
      case 'I':
      case 'INATIVO':
        return 'default';
      case 'P':
      case 'PENDENTE':
        return 'warning';
      default:
        return 'default';
    }
  };

  const getSituacaoLabel = (situacao: string) => {
    switch (situacao.toUpperCase()) {
      case 'A':
        return 'Ativo';
      case 'I':
        return 'Inativo';
      case 'P':
        return 'Pendente';
      default:
        return situacao;
    }
  };

  // Filtrar profissões já cadastradas pelo usuário
  const profissoesDisponiveis = todasProfissoes.filter(
    profissao => !minhasProfissoes.some(minha => minha.id === profissao.id)
  );

  return (
    <Container maxWidth="lg">
      <Typography variant="h4" component="h1" gutterBottom>
        Minhas Profissões
      </Typography>

      <Grid container spacing={4}>
        {/* Lista de Minhas Profissões */}
        <Grid item xs={12} md={8}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Profissões Cadastradas
              </Typography>
              
              {minhasProfissoes.length > 0 ? (
                <TableContainer component={Paper} variant="outlined">
                  <Table>
                    <TableHead>
                      <TableRow>
                        <TableCell>Profissão</TableCell>
                        <TableCell>Situação</TableCell>
                        <TableCell>Avaliação</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {minhasProfissoes.map((profissao) => (
                        <TableRow key={profissao.id}>
                          <TableCell>{profissao.nome}</TableCell>
                          <TableCell>
                            <Chip
                              label={getSituacaoLabel(profissao.situacao)}
                              color={getSituacaoColor(profissao.situacao) as any}
                              size="small"
                            />
                          </TableCell>
                          <TableCell>{profissao.avaliacao}</TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </TableContainer>
              ) : (
                <Box sx={{ textAlign: 'center', py: 4 }}>
                  <Typography color="text.secondary" gutterBottom>
                    Você ainda não tem profissões cadastradas.
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Adicione suas habilidades para que os clientes possam encontrá-lo!
                  </Typography>
                </Box>
              )}
            </CardContent>
          </Card>
        </Grid>

        {/* Formulário de Adição */}
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Adicionar Profissão
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
                  name="profissao"
                  control={control}
                  render={({ field }) => (
                    <TextField
                      {...field}
                      fullWidth
                      select
                      label="Selecione uma Profissão"
                      error={!!errors.profissao}
                      helperText={errors.profissao?.message}
                      sx={{ mb: 2 }}
                    >
                      <MenuItem value={0}>Selecione...</MenuItem>
                      {profissoesDisponiveis.map((profissao) => (
                        <MenuItem key={profissao.id} value={profissao.id}>
                          {profissao.nome}
                        </MenuItem>
                      ))}
                    </TextField>
                  )}
                />

                <Button
                  type="submit"
                  variant="contained"
                  fullWidth
                  disabled={loading || profissoesDisponiveis.length === 0}
                  startIcon={<AddIcon />}
                >
                  {loading ? 'Adicionando...' : 'Adicionar Profissão'}
                </Button>

                {profissoesDisponiveis.length === 0 && (
                  <Typography variant="body2" color="text.secondary" sx={{ mt: 1, textAlign: 'center' }}>
                    Todas as profissões disponíveis já foram adicionadas.
                  </Typography>
                )}
              </Box>
            </CardContent>
          </Card>

          {/* Informações */}
          <Card sx={{ mt: 2 }}>
            <CardContent>
              <Typography variant="h6" gutterBottom color="primary">
                Como funciona?
              </Typography>
              <Typography variant="body2" color="text.secondary" paragraph>
                Adicione as profissões em que você atua para que os clientes possam encontrá-lo quando buscarem por esses serviços.
              </Typography>
              <Typography variant="body2" color="text.secondary" paragraph>
                <strong>Situações:</strong>
              </Typography>
              <Box sx={{ ml: 1 }}>
                <Typography variant="body2" color="text.secondary">
                  • <strong>Ativo:</strong> Você está disponível para trabalhar
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  • <strong>Pendente:</strong> Aguardando aprovação
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  • <strong>Inativo:</strong> Temporariamente indisponível
                </Typography>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Container>
  );
};

export default MyProfessions;
