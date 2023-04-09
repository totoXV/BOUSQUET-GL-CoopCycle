export interface ICommande {
  id: number;
}

export type NewCommande = Omit<ICommande, 'id'> & { id: null };
