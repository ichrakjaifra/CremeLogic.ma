export interface Recette {
  id: number;
  nom: string;
  description?: string;
  instructions?: string;
  tempsPreparation?: number;
  tempsCuisson?: number;
  nombrePortions: number;
  coutTotal: number;
  lignesRecette: LigneRecette[];
  createurId?: number;
  dateCreation?: Date;
  dateModification?: Date;
  photoUrl?: string; // Optional for frontend display
}

export interface LigneRecette {
  id?: number;
  ingredientId: number;
  nomIngredient?: string;
  quantite: number;
  uniteMesure?: string;
  prixUnitaire?: number;
}
