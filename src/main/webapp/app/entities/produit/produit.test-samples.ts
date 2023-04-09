import { IProduit, NewProduit } from './produit.model';

export const sampleWithRequiredData: IProduit = {
  id: 91013,
};

export const sampleWithPartialData: IProduit = {
  id: 16294,
};

export const sampleWithFullData: IProduit = {
  id: 1691,
};

export const sampleWithNewData: NewProduit = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
