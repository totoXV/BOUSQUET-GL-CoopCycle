import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IMembre } from '../membre.model';
import { MembreService } from '../service/membre.service';

@Injectable({ providedIn: 'root' })
export class MembreRoutingResolveService implements Resolve<IMembre | null> {
  constructor(protected service: MembreService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IMembre | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((membre: HttpResponse<IMembre>) => {
          if (membre.body) {
            return of(membre.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
