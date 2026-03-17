export interface CommandeAchat {
  id: number;
  fournisseurId: number;
  nomFournisseur: string;
  dateCommande: Date;
  dateReceptionPrevue?: Date;
  montantTotal: number;
  statut: 'EN_ATTENTE' | 'VALIDEE' | 'EN_COURS' | 'RECUE' | 'ANNULEE';
  lignesCommande: LigneCommandeAchat[];
  notes?: string;
}

export interface LigneCommandeAchat {
  ingredientId: number;
  nomIngredient: string;
  quantite: number;
  prixUnitaire: number;
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
