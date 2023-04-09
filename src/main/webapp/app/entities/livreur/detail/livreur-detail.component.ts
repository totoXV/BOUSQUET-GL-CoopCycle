import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ILivreur } from '../livreur.model';

@Component({
  selector: 'jhi-livreur-detail',
  templateUrl: './livreur-detail.component.html',
})
export class LivreurDetailComponent implements OnInit {
  livreur: ILivreur | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ livreur }) => {
      this.livreur = livreur;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
