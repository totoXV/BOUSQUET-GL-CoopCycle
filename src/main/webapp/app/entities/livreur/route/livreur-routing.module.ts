import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { LivreurComponent } from '../list/livreur.component';
import { LivreurDetailComponent } from '../detail/livreur-detail.component';
import { LivreurUpdateComponent } from '../update/livreur-update.component';
import { LivreurRoutingResolveService } from './livreur-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const livreurRoute: Routes = [
  {
    path: '',
    component: LivreurComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: LivreurDetailComponent,
    resolve: {
      livreur: LivreurRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: LivreurUpdateComponent,
    resolve: {
      livreur: LivreurRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: LivreurUpdateComponent,
    resolve: {
      livreur: LivreurRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(livreurRoute)],
  exports: [RouterModule],
})
export class LivreurRoutingModule {}
