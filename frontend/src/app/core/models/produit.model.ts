export interface Produit {
  id: number;
  nom: string;
  categorie: string;
  prixVente: number;
  stockDisponible: number;
  stockSeuil: number;
  uniteMesure: string;
  description?: string;
  photoUrl?: string;
  recetteId?: number;
  dateCreation: Date;
  actif: boolean;
}

export interface CategorieProduit {
  id: number;
  nom: string;
}
