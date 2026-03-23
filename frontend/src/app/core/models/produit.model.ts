export interface Produit {
  id: number;
  codeProduit?: string;
  nom: string;
  categorie: string;
  prixVente: number;
  coutProduction?: number;
  marge?: number;
  stockDisponible: number;
  stockMinimum: number;
  stockMaximum: number;
  uniteMesure: string;
  description?: string;
  imageUrl?: string;
  recetteId?: number;
  dateCreation: Date;
  statut: 'ACTIF' | 'INACTIF' | string;
  stockFaible?: boolean;
  enRupture?: boolean;
  valeurStock?: number;
}

export const PRODUCT_CATEGORIES = [
  'GATEAUX',
  'PATISSERIES',
  'VIENNOISERIES',
  'BOULANGERIE',
  'DESSERTS',
  'BOISSONS',
  'SANDWICHS',
  'SALADES'
];

export interface CategorieProduit {
  id: number;
  nom: string;
}
