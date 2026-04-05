export interface User {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  telephone: string;
  role: 'ADMIN' | 'CHEF' | 'MAGASINIER' | 'EMPLOYE';
  actif: boolean;
  dateCreation: string; // From LocalDateTime
  dateModification?: string; // From LocalDateTime
  photoUrl?: string;
}

export interface AuthResponse extends User {
  token: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}
