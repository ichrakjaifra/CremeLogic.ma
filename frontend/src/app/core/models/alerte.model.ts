import { TypeAlerte } from '../enums/type-alerte.enum';

export interface Alerte {
    id: number;
    type: TypeAlerte;
    titre: string;
    description: string;
    dateCreation: string;
    dateResolution?: string;
    resolue: boolean;
    priorite: string;
    ingredientId?: number;
    produitId?: number;
    commandeId?: number;
    ordreProductionId?: number;
    utilisateurId?: number;
    venteId?: number;
}
