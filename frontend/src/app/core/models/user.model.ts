export interface User {
  code: number;
  nom: string;
  prenom: string;
  email: string;
  role: string;
  client?: string;
  adresse?: string;
  telephone?: string;
  contact?: string;
  secteur?: string;
  typeClient?: string;
  listeNoire?: boolean;
  motifListeNoire?: string;
}
