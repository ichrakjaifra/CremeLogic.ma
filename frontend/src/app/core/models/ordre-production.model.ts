export interface OrdreProduction {
  id: number;
  numeroOrdre: string;
  produitId: number;
  produitNom: string;
  recetteId?: number;
  quantite: number;
  quantiteProduite?: number; // Optional, might be in some versions
  dateDebutPrevue: string | Date;
  dateFinPrevue: string | Date | null;
  dateDebutReelle?: string | Date | null;
  dateFinReelle?: string | Date | null;
  statut: 'PLANIFIE' | 'EN_COURS' | 'TERMINEE' | 'ANNULEE' | 'PLANIFIEE'; // Adjusted to match backend "TERMINEE"
  coutTotal?: number;
  coutUnitaire?: number;
  notes?: string;
  createurNom?: string;
  responsableNom?: string;
  suivisEtapes?: SuiviEtape[];
  instructionsRecette?: string;
  enRetard: boolean;
  dateCreation?: string | Date;
}

export interface SuiviEtape {
  id: number;
  etapeRecetteId: number;
  descriptionEtape: string;
  ordreEtape: number;
  tempsEstimeEtape?: number;
  statut: 'A_FAIRE' | 'EN_COURS' | 'TERMINEE';
  dateDebut?: string | Date;
  dateFin?: string | Date;
}
