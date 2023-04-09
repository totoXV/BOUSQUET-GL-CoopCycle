import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { MembreComponent } from './list/membre.component';
import { MembreDetailComponent } from './detail/membre-detail.component';
import { MembreUpdateComponent } from './update/membre-update.component';
import { MembreDeleteDialogComponent } from './delete/membre-delete-dialog.component';
import { MembreRoutingModule } from './route/membre-routing.module';

@NgModule({
  imports: [SharedModule, MembreRoutingModule],
  declarations: [MembreComponent, MembreDetailComponent, MembreUpdateComponent, MembreDeleteDialogComponent],
})
export class MembreModule {}
