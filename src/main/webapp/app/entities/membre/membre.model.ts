export interface IMembre {
  id: number;
}

export type NewMembre = Omit<IMembre, 'id'> & { id: null };
