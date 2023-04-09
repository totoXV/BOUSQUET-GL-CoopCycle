import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IMembre, NewMembre } from '../membre.model';

export type PartialUpdateMembre = Partial<IMembre> & Pick<IMembre, 'id'>;

export type EntityResponseType = HttpResponse<IMembre>;
export type EntityArrayResponseType = HttpResponse<IMembre[]>;

@Injectable({ providedIn: 'root' })
export class MembreService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/membres');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(membre: NewMembre): Observable<EntityResponseType> {
    return this.http.post<IMembre>(this.resourceUrl, membre, { observe: 'response' });
  }

  update(membre: IMembre): Observable<EntityResponseType> {
    return this.http.put<IMembre>(`${this.resourceUrl}/${this.getMembreIdentifier(membre)}`, membre, { observe: 'response' });
  }

  partialUpdate(membre: PartialUpdateMembre): Observable<EntityResponseType> {
    return this.http.patch<IMembre>(`${this.resourceUrl}/${this.getMembreIdentifier(membre)}`, membre, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IMembre>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IMembre[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getMembreIdentifier(membre: Pick<IMembre, 'id'>): number {
    return membre.id;
  }

  compareMembre(o1: Pick<IMembre, 'id'> | null, o2: Pick<IMembre, 'id'> | null): boolean {
    return o1 && o2 ? this.getMembreIdentifier(o1) === this.getMembreIdentifier(o2) : o1 === o2;
  }

  addMembreToCollectionIfMissing<Type extends Pick<IMembre, 'id'>>(
    membreCollection: Type[],
    ...membresToCheck: (Type | null | undefined)[]
  ): Type[] {
    const membres: Type[] = membresToCheck.filter(isPresent);
    if (membres.length > 0) {
      const membreCollectionIdentifiers = membreCollection.map(membreItem => this.getMembreIdentifier(membreItem)!);
      const membresToAdd = membres.filter(membreItem => {
        const membreIdentifier = this.getMembreIdentifier(membreItem);
        if (membreCollectionIdentifiers.includes(membreIdentifier)) {
          return false;
        }
        membreCollectionIdentifiers.push(membreIdentifier);
        return true;
      });
      return [...membresToAdd, ...membreCollection];
    }
    return membreCollection;
  }
}
