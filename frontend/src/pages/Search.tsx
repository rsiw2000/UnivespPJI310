import React, { useState, useEffect } from 'react';
import {
  Container,
  Typography,
  Box,
  Card,
  CardContent,
  Grid,
  TextField,
  Button,
  MenuItem,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Rating,
  Alert,
} from '@mui/material';
import { useForm, Controller } from 'react-hook-form';
import { Profissao, Professional, ReviewData } from '../types';
import { professionService, searchService, reviewService } from '../services/api';

interface SearchFormData {
  profissao: number;
}

const Search: React.FC = () => {
  const [profissoes, setProfissoes] = useState<Profissao[]>([]);
  const [professionals, setProfessionals] = useState<Professional[]>([]);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('Selecione uma profissão!');
  const [reviewDialog, setReviewDialog] = useState(false);
  const [selectedProfessional, setSelectedProfessional] = useState<Professional | null>(null);
  const [reviewData, setReviewData] = useState<ReviewData>({
    idProfissao: 0,
    idPrestador: 0,
    avaliacao: 0,
    comentario: '',
  });
  const [reviewSuccess, setReviewSuccess] = useState('');

  // const avaliacoes = ['Nenhuma', 'Péssimo', 'Ruim', 'Regular', 'Bom', 'Ótimo'];

  const { control, handleSubmit, watch } = useForm<SearchFormData>({
    defaultValues: {
      profissao: 0,
    },
  });

  const watchedProfissao = watch('profissao');

  useEffect(() => {
    const loadProfissoes = async () => {
      try {
        const response = await professionService.getProfessions();
        if (response.success) {
          setProfissoes(response.data);
        }
      } catch (error) {
        console.error('Erro ao carregar profissões:', error);
      }
    };

    loadProfissoes();
  }, []);

  const onSubmit = async (data: SearchFormData) => {
    if (data.profissao === 0) {
      setMessage('Selecione uma profissão!');
      setProfessionals([]);
      return;
    }

    setLoading(true);
    try {
      const response = await searchService.searchProfessionals(data.profissao);
      if (response.success) {
        setProfessionals(response.data);
        setMessage(response.data.length > 0 ? '' : 'Nenhum profissional encontrado');
      } else {
        setMessage('Erro ao buscar profissionais');
        setProfessionals([]);
      }
    } catch (error) {
      console.error('Erro ao buscar profissionais:', error);
      setMessage('Erro ao buscar profissionais');
      setProfessionals([]);
    } finally {
      setLoading(false);
    }
  };

  // Auto-buscar quando profissão muda
  useEffect(() => {
    if (watchedProfissao > 0) {
      onSubmit({ profissao: watchedProfissao });
    }
  }, [watchedProfissao]);

  const handleReview = (professional: Professional) => {
    setSelectedProfessional(professional);
    setReviewData({
      idProfissao: professional.idProfissao,
      idPrestador: professional.id,
      avaliacao: 0,
      comentario: '',
    });
    setReviewDialog(true);
  };

  const submitReview = async () => {
    try {
      const response = await reviewService.createReview(reviewData);
      if (response.success) {
        setReviewSuccess('Avaliação enviada com sucesso!');
        setReviewDialog(false);
        // Recarregar profissionais para atualizar avaliações
        if (watchedProfissao > 0) {
          onSubmit({ profissao: watchedProfissao });
        }
      }
    } catch (error) {
      console.error('Erro ao enviar avaliação:', error);
    }
  };

  return (
    <Container maxWidth="lg">
      <Typography variant="h4" component="h1" gutterBottom>
        Busca de Profissionais
      </Typography>

      <Card sx={{ mb: 4 }}>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            Selecione a profissão
          </Typography>
          <Box component="form" onSubmit={handleSubmit(onSubmit)}>
            <Grid container spacing={2} alignItems="center">
              <Grid item xs={12} md={8}>
                <Controller
                  name="profissao"
                  control={control}
                  render={({ field }) => (
                    <TextField
                      {...field}
                      fullWidth
                      select
                      label="Profissão"
                    >
                      <MenuItem value={0}>Selecione...</MenuItem>
                      {profissoes.map((profissao) => (
                        <MenuItem key={profissao.id} value={profissao.id}>
                          {profissao.nome}
                        </MenuItem>
                      ))}
                    </TextField>
                  )}
                />
              </Grid>
              <Grid item xs={12} md={4}>
                <Button
                  type="submit"
                  variant="contained"
                  fullWidth
                  disabled={loading}
                >
                  {loading ? 'Buscando...' : 'Buscar'}
                </Button>
              </Grid>
            </Grid>
          </Box>
        </CardContent>
      </Card>

      {reviewSuccess && (
        <Alert severity="success" sx={{ mb: 2 }} onClose={() => setReviewSuccess('')}>
          {reviewSuccess}
        </Alert>
      )}

      <Card>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            Profissionais localizados
          </Typography>
          
          {message && (
            <Typography color="text.secondary" sx={{ mb: 2 }}>
              {message}
            </Typography>
          )}

          {professionals.length > 0 && (
            <TableContainer component={Paper} variant="outlined">
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell>Nome</TableCell>
                    <TableCell>Avaliação</TableCell>
                    <TableCell>Contato</TableCell>
                    <TableCell align="center">Ações</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {professionals.map((professional) => (
                    <TableRow key={professional.id}>
                      <TableCell>{professional.nome}</TableCell>
                      <TableCell>{professional.nota}</TableCell>
                      <TableCell>{professional.contato}</TableCell>
                      <TableCell align="center">
                        <Button
                          variant="outlined"
                          size="small"
                          onClick={() => handleReview(professional)}
                        >
                          Avaliar
                        </Button>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          )}
        </CardContent>
      </Card>

      {/* Dialog de Avaliação */}
      <Dialog open={reviewDialog} onClose={() => setReviewDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle>
          Avaliar Profissional: {selectedProfessional?.nome}
        </DialogTitle>
        <DialogContent>
          <Box sx={{ mt: 2 }}>
            <Typography component="legend">Avaliação</Typography>
            <Rating
              value={reviewData.avaliacao}
              onChange={(_, newValue) => {
                setReviewData({ ...reviewData, avaliacao: newValue || 0 });
              }}
              max={5}
              size="large"
            />
            <TextField
              fullWidth
              multiline
              rows={4}
              label="Comentário"
              value={reviewData.comentario}
              onChange={(e) => setReviewData({ ...reviewData, comentario: e.target.value })}
              sx={{ mt: 2 }}
              placeholder="Conte sobre sua experiência com este profissional..."
            />
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setReviewDialog(false)}>
            Cancelar
          </Button>
          <Button 
            onClick={submitReview} 
            variant="contained"
            disabled={reviewData.avaliacao === 0}
          >
            Confirmar Avaliação
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default Search;
