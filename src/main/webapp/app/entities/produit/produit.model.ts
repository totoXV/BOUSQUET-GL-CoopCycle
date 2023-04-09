export interface IProduit {
  id: number;
}

export type NewProduit = Omit<IProduit, 'id'> & { id: null };
