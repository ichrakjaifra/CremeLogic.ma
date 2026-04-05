export interface Tache {
  id: number;
  titre: string;
  description?: string;
  statut: 'A_FAIRE' | 'EN_COURS' | 'TERMINE' | 'ANNULE';
  priorite: 'BASSE' | 'MOYENNE' | 'HAUTE';
  dateEcheance?: Date;
  assigneAId?: number;
  creeParId?: number;
  dateCreation: Date;
}
