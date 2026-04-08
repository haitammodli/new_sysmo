export type TypeModification = 
  // --- ERREURS EN INTERNE ---
  | 'ERREUR_TAXATION'
  | 'ERREUR_POIDS'
  | 'ERREUR_CALCUL_PARAMETRAGE'
  // --- DEMANDES CLIENTS ---
  | 'ERREUR_DESTINATION_CLIENT'
  | 'MODIFICATION_DEMANDEE_CLIENT'
  | 'MODIFICATION_VALEUR_DECLARE'
  // --- FINANCIER & SERVICES ---
  | 'ANNULATION_ENCAISSEMENT'
  | 'REMISE_MONTANT'
  | 'SERVICE_GRATUIT';
export type StatutModificationAnn = 'EN_ATTENTE' | 'APPROUVEE' | 'REJETEE';

export interface Modification {
  id?: number;
  numeroExpedition: string;
  typeModification: TypeModification;
  ancienneValeur: string;
  nouvelleValeur: string;
  demandeurId: number;
  roleDemandeur: string;
  agentModificationId?: number | null;
  dateDemande?: string;
  dateTraitement?: string | null;
  statut?: StatutModificationAnn;
}