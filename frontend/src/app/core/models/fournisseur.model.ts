export interface Fournisseur {
  id: number;
  nom: string;
  telephone: string;
  email?: string;
  adresse: string;
  ville?: string;
  pays?: string;
  codePostal?: string;
  notes?: string;
  noteEvaluation?: number;
  actif: boolean;
  nombreCommandes?: number;
  montantTotalCommandes?: number;
}
