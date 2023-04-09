import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { MembreComponent } from '../list/membre.component';
import { MembreDetailComponent } from '../detail/membre-detail.component';
import { MembreUpdateComponent } from '../update/membre-update.component';
import { MembreRoutingResolveService } from './membre-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const membreRoute: Routes = [
  {
    path: '',
    component: MembreComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: MembreDetailComponent,
    resolve: {
      membre: MembreRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: MembreUpdateComponent,
    resolve: {
      membre: MembreRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: MembreUpdateComponent,
    resolve: {
      membre: MembreRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(membreRoute)],
  exports: [RouterModule],
})
export class MembreRoutingModule {}
