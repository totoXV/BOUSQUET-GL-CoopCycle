import { ICommande, NewCommande } from './commande.model';

export const sampleWithRequiredData: ICommande = {
  id: 5189,
};

export const sampleWithPartialData: ICommande = {
  id: 57744,
};

export const sampleWithFullData: ICommande = {
  id: 12595,
};

export const sampleWithNewData: NewCommande = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
