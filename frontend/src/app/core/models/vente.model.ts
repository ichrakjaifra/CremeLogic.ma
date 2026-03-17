export interface Vente {
  id: number;
  numeroVente: string;
  dateVente: Date;
  caissierId?: number;
  nomCaissier?: string;
  montantTotal: number;
  montantPaye?: number;
  montantRendu?: number;
  modePaiement: 'ESPECES' | 'CARTE' | 'AUTRE';
  lignesVente: LigneVente[];
  nomClient?: string;
  notes?: string;
}

export interface LigneVente {
  id?: number;
  produitId: number;
  nomProduit: string;
  quantite: number;
  prixUnitaire: number;
  montantTotal?: number;
}
