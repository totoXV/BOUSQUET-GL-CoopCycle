import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IMembre } from '../membre.model';

@Component({
  selector: 'jhi-membre-detail',
  templateUrl: './membre-detail.component.html',
})
export class MembreDetailComponent implements OnInit {
  membre: IMembre | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ membre }) => {
      this.membre = membre;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
