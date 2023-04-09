import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'produit',
        data: { pageTitle: 'coopCycleApp.produit.home.title' },
        loadChildren: () => import('./produit/produit.module').then(m => m.ProduitModule),
      },
      {
        path: 'membre',
        data: { pageTitle: 'coopCycleApp.membre.home.title' },
        loadChildren: () => import('./membre/membre.module').then(m => m.MembreModule),
      },
      {
        path: 'commande',
        data: { pageTitle: 'coopCycleApp.commande.home.title' },
        loadChildren: () => import('./commande/commande.module').then(m => m.CommandeModule),
      },
      {
        path: 'livreur',
        data: { pageTitle: 'coopCycleApp.livreur.home.title' },
        loadChildren: () => import('./livreur/livreur.module').then(m => m.LivreurModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
