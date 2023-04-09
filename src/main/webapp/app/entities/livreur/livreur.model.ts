export interface ILivreur {
  id: number;
}

export type NewLivreur = Omit<ILivreur, 'id'> & { id: null };
