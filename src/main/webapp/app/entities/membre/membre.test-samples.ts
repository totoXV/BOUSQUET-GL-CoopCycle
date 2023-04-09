import { IMembre, NewMembre } from './membre.model';

export const sampleWithRequiredData: IMembre = {
  id: 7744,
};

export const sampleWithPartialData: IMembre = {
  id: 43287,
};

export const sampleWithFullData: IMembre = {
  id: 22135,
};

export const sampleWithNewData: NewMembre = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
