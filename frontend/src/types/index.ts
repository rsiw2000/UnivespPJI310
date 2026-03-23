export interface User {
  username: string;
  name: string;
  isAdmin: boolean;
}

export interface AuthContextType {
  user: User | null;
  token: string | null;
  login: (username: string, password: string) => Promise<boolean>;
  logout: () => void;
  isAuthenticated: boolean;
}

export interface Estado {
  uf: string;
  nome: string;
}

export interface Cidade {
  id: number;
  nome: string;
}

export interface Profissao {
  id: number;
  nome: string;
}

export interface UserProfession {
  id: number;
  nome: string;
  situacao: string;
  avaliacao: string;
}

export interface Professional {
  id: number;
  idProfissao: number;
  contato: string;
  nome: string;
  nota: string;
}

export interface UserProfile {
  nome: string;
  email: string;
  telefone: string;
  estado: string;
  cidade: number;
  bairro: string;
  cpfCnpj: string;
}

export interface RegisterData {
  nome: string;
  email: string;
  telefone: string;
  estado: string;
  cidade: number;
  bairro: string;
  cpfCnpj: string;
  password: string;
  confirmPassword: string;
}

export interface ReviewData {
  idProfissao: number;
  idPrestador: number;
  avaliacao: number;
  comentario: string;
}
