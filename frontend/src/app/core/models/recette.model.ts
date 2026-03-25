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
  etapes?: EtapeRecette[];
  createurId?: number;
  dateCreation?: Date;
  dateModification?: Date;
  photoUrl?: string;
}

export interface EtapeRecette {
  id: number;
  description: string;
  ordre: number;
  tempsEstime?: number;
}

export interface LigneRecette {
  id?: number;
  ingredientId: number;
  nomIngredient?: string;
  quantite: number;
  uniteMesure?: string;
  prixUnitaire?: number;
}
