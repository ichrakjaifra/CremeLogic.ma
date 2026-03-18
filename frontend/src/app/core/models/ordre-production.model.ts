export interface OrdreProduction {
  id: number;
  numeroOrdre: string;
  produitId: number;
  produitNom: string;
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
  enRetard: boolean;
  dateCreation?: string | Date;
}
