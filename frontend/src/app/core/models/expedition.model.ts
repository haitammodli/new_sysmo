export interface Expedition {
  numeroexpedition?: string;
  numerodeclaration?: number;
  dateCreation?: string;
  dateLivraison?: string;
  adresseLivraison?: string;
  statut?: string;
  
  expiditeurId?: number;
  expiditeurNom?: string;
  distinataireId?: number;
  distinataireNom?: string;
  ramasseurId?: number;
  ramasseurNom?: string;
  
  agenceId?: number;
  agenceNom?: string;
  natureId?: number;
  typeId?: number;

  colis?: number;
  poid?: number;
  volume?: number;
  etiquette?: number;
  encombrement?: number;
  valeurDeclaree?: number;
  fond?: number;
  ht?: number;
  tva?: number;
  ttc?: number;
  bl?: boolean;
  numerobl?: string;
  facture?: boolean;
  numerofacture?: string;
  comment?: string;
  ref_regl?: number;
  ps?: number;

  modeReglId?: number;
  portId?: number;
  catprodId?: number;
  creditId?: number;
  unitId?: number;
  livraisonId?: number;
  taxationId?: number;
}
