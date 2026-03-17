export interface AdminStats {
  ventesJour: number;
  produitsTotal: number;
  stockFaibleAlertes: number;
  ventesMensuelles: { mois: string; montant: number; benefice: number }[];
  alertesRecentes: string[];
}

export interface ChefStats {
  ordresEnCours: number;
  ordresTerminesAujourdhui: number;
  recettesActives: number;
  ingredientsStockBas: number;
  ordresProductionJour?: number;
  recettesPopulaires?: { nom: string; usage: number }[];
  alertesIngredients?: string[];
}

export interface MagasinierStats {
  ingredientsStockBas: number;
  ingredientsExpirantBientot: number;
  commandesAchatEnAttente: number;
  commandesARecevoir?: number;
}

export interface EmployeStats {
  ventesAujourdhui: number;
  ventesPersoJour?: number;
  tachesACompleter: number;
}
