import axios from 'axios';
import { RegisterData, ReviewData } from '../types';
import { config } from '../config';

const API_BASE_URL = config.API_BASE_URL;

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor para adicionar token de autenticação
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  const username = localStorage.getItem('username');
  
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  
  if (username) {
    config.headers['X-Username'] = username;
  }
  
  return config;
});

export const authService = {
  login: async (username: string, password: string) => {
    const response = await api.post('/auth/login', { username, password });
    return response.data;
  },
  
  logout: async () => {
    const response = await api.post('/auth/logout');
    return response.data;
  },
};

export const userService = {
  register: async (data: RegisterData) => {
    const response = await api.post('/user/register', data);
    return response.data;
  },
  
  getProfile: async () => {
    const response = await api.get('/user/profile');
    return response.data;
  },
  
  updateProfile: async (data: Partial<RegisterData>) => {
    const response = await api.put('/user/profile', data);
    return response.data;
  },
  
  getUserProfessions: async () => {
    const response = await api.get('/user/professions');
    return response.data;
  },
  
  addUserProfession: async (id: number) => {
    const response = await api.post('/user/professions', { id });
    return response.data;
  },
};

export const locationService = {
  getStates: async () => {
    const response = await api.get('/locations/states');
    return response.data;
  },
  
  getCities: async (uf: string) => {
    const response = await api.get(`/locations/cities/${uf}`);
    return response.data;
  },
};

export const professionService = {
  getProfessions: async () => {
    const response = await api.get('/professions');
    return response.data;
  },
  
  createProfession: async (nome: string) => {
    const response = await api.post('/professions', { nome });
    return response.data;
  },
};

export const searchService = {
  searchProfessionals: async (idProfissao: number, idMunicipio?: number) => {
    const params = new URLSearchParams();
    params.append('idProfissao', idProfissao.toString());
    if (idMunicipio) {
      params.append('idMunicipio', idMunicipio.toString());
    }
    
    const response = await api.get(`/search/professionals?${params.toString()}`);
    return response.data;
  },
};

export const reviewService = {
  createReview: async (data: ReviewData) => {
    const response = await api.post('/reviews', data);
    return response.data;
  },
};

export default api;
