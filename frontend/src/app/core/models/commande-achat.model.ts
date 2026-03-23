export interface CommandeAchat {
  id: number;
  numeroCommande: string;
  fournisseurId: number;
  fournisseurNom: string;
  dateCommande: string;
  dateLivraisonPrevue?: string;
  dateLivraisonReelle?: string;
  montantTotal: number;
  statut: 'EN_ATTENTE' | 'VALIDEE' | 'EN_COURS' | 'LIVREE' | 'ANNULEE';
  lignesCommande: LigneCommandeAchat[];
  notes?: string;
  enRetard: boolean;
  createurNom?: string;
}

export interface LigneCommandeAchat {
  id?: number;
  ingredientId: number;
  ingredientNom: string;
  ingredientCode: string;
  quantiteCommandee: number;
  quantiteRecue: number;
  prixUnitaire: number;
  montantTotal: number;
  uniteMesure: string;
}

export interface Fournisseur {
  id: number;
  nom: string;
  email: string;
  telephone: string;
  adresse: string;
  ville: string;
  actif: boolean;
  notes?: string;
}
