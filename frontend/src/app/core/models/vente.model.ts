export interface Vente {
  id: number;
  numeroVente: string;
  dateVente: string | Date;
  caissierId?: number;
  caissierNom?: string;
  montantTotal: number;
  montantPaye?: number;
  montantRendu?: number;
  modePaiement: 'ESPECES' | 'CARTE' | 'AUTRE';
  lignesVente: LigneVente[];
  nomClient?: string;
  telephoneClient?: string;
  emailClient?: string;
  notes?: string;
  dateCreation?: string;
}

export interface LigneVente {
  id?: number;
  produitId: number;
  produitNom: string;
  quantite: number;
  prixUnitaire: number;
  montantTotal?: number;
}
