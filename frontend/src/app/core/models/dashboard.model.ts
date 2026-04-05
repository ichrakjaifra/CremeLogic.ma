export interface AdminStats {
  totalProduits: number;
  totalIngredients: number;
  totalVentesJour: number;
  totalCommandesJour: number;
  chiffreAffairesJour: number;
  chiffreAffairesMois: number;
  depensesMois: number;
  beneficeMois: number;
  produitsStockFaible: number;
  ingredientsStockFaible: number;
  alertesNonResolues: number;
  ventesParMois: { [key: string]: number };
  beneficesParMois: { [key: string]: number };
  ventesParCategorie: { [key: string]: number };
  
  // New lists
  ventesRecent: any[];
  commandesRecent: any[];
  productionsRecent: any[];
  alertesRecent: any[];
  topFournisseurs: any[];
}

export interface ChefStats {
  totalProductionsJour: number;
  ingredientsStockFaible: number;
  totalProductionsTermineesJour: number;
  totalRecettesActives: number;
  recettesPopulaires: any[];
  tachesDuJour: any[];
  ingredientsCritiques: any[];
  productionsRecent: any[];
}

export interface MagasinierStats {
  ingredientsStockFaible: number;
  totalCommandesJour: number;
  ingredientsExpirant: number;
  valeurStockTotal: number;
}

export interface EmployeStats {
  chiffreAffairesJour?: number;
  totalVentesJour?: number;
  tachesDuJour?: any[];
  instructionDuChef?: string;
  tachesACompleter?: number;
}
