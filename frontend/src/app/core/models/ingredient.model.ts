export interface Ingredient {
  id: number;
  codeIngredient: string;
  nom: string;
  description?: string;
  uniteMesure: string;
  quantiteStock: number;
  quantiteMinimum: number;
  quantiteMaximum?: number;
  prixUnitaire: number;
  perissable: boolean;
  dateExpiration?: Date;
  fournisseurId?: number;
  fournisseurNom?: string; // Optional helper
  categorie?: string; // Optional helper if applicable
}

export interface MouvementStock {
  id: number;
  ingredientId: number;
  quantite: number;
  type: 'ENTREE' | 'SORTIE' | 'PERTE' | 'AJUSTEMENT';
  dateMouvement: Date;
  raison: string;
  utilisateurId: number;
}
