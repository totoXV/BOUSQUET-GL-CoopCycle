import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ILivreur, NewLivreur } from '../livreur.model';

export type PartialUpdateLivreur = Partial<ILivreur> & Pick<ILivreur, 'id'>;

export type EntityResponseType = HttpResponse<ILivreur>;
export type EntityArrayResponseType = HttpResponse<ILivreur[]>;

@Injectable({ providedIn: 'root' })
export class LivreurService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/livreurs');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(livreur: NewLivreur): Observable<EntityResponseType> {
    return this.http.post<ILivreur>(this.resourceUrl, livreur, { observe: 'response' });
  }

  update(livreur: ILivreur): Observable<EntityResponseType> {
    return this.http.put<ILivreur>(`${this.resourceUrl}/${this.getLivreurIdentifier(livreur)}`, livreur, { observe: 'response' });
  }

  partialUpdate(livreur: PartialUpdateLivreur): Observable<EntityResponseType> {
    return this.http.patch<ILivreur>(`${this.resourceUrl}/${this.getLivreurIdentifier(livreur)}`, livreur, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ILivreur>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ILivreur[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getLivreurIdentifier(livreur: Pick<ILivreur, 'id'>): number {
    return livreur.id;
  }

  compareLivreur(o1: Pick<ILivreur, 'id'> | null, o2: Pick<ILivreur, 'id'> | null): boolean {
    return o1 && o2 ? this.getLivreurIdentifier(o1) === this.getLivreurIdentifier(o2) : o1 === o2;
  }

  addLivreurToCollectionIfMissing<Type extends Pick<ILivreur, 'id'>>(
    livreurCollection: Type[],
    ...livreursToCheck: (Type | null | undefined)[]
  ): Type[] {
    const livreurs: Type[] = livreursToCheck.filter(isPresent);
    if (livreurs.length > 0) {
      const livreurCollectionIdentifiers = livreurCollection.map(livreurItem => this.getLivreurIdentifier(livreurItem)!);
      const livreursToAdd = livreurs.filter(livreurItem => {
        const livreurIdentifier = this.getLivreurIdentifier(livreurItem);
        if (livreurCollectionIdentifiers.includes(livreurIdentifier)) {
          return false;
        }
        livreurCollectionIdentifiers.push(livreurIdentifier);
        return true;
      });
      return [...livreursToAdd, ...livreurCollection];
    }
    return livreurCollection;
  }
}
