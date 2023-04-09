import { ILivreur, NewLivreur } from './livreur.model';

export const sampleWithRequiredData: ILivreur = {
  id: 71552,
};

export const sampleWithPartialData: ILivreur = {
  id: 21383,
};

export const sampleWithFullData: ILivreur = {
  id: 11693,
};

export const sampleWithNewData: NewLivreur = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
