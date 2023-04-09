import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { LivreurComponent } from './list/livreur.component';
import { LivreurDetailComponent } from './detail/livreur-detail.component';
import { LivreurUpdateComponent } from './update/livreur-update.component';
import { LivreurDeleteDialogComponent } from './delete/livreur-delete-dialog.component';
import { LivreurRoutingModule } from './route/livreur-routing.module';

@NgModule({
  imports: [SharedModule, LivreurRoutingModule],
  declarations: [LivreurComponent, LivreurDetailComponent, LivreurUpdateComponent, LivreurDeleteDialogComponent],
})
export class LivreurModule {}
