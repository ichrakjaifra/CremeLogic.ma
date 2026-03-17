export interface OrdreProduction {
  id: number;
  produitId: number;
  nomProduit: string;
  quantitePrevue: number;
  quantiteProduite: number;
  dateDebut: Date;
  dateFinPrevue: Date;
  dateFinReelle?: Date;
  statut: 'PLANIFIE' | 'EN_COURS' | 'TERMINE' | 'ANNULE';
  chefId: number;
  notes?: string;
}
