import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { LivreurFormService, LivreurFormGroup } from './livreur-form.service';
import { ILivreur } from '../livreur.model';
import { LivreurService } from '../service/livreur.service';

@Component({
  selector: 'jhi-livreur-update',
  templateUrl: './livreur-update.component.html',
})
export class LivreurUpdateComponent implements OnInit {
  isSaving = false;
  livreur: ILivreur | null = null;

  editForm: LivreurFormGroup = this.livreurFormService.createLivreurFormGroup();

  constructor(
    protected livreurService: LivreurService,
    protected livreurFormService: LivreurFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ livreur }) => {
      this.livreur = livreur;
      if (livreur) {
        this.updateForm(livreur);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const livreur = this.livreurFormService.getLivreur(this.editForm);
    if (livreur.id !== null) {
      this.subscribeToSaveResponse(this.livreurService.update(livreur));
    } else {
      this.subscribeToSaveResponse(this.livreurService.create(livreur));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ILivreur>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(livreur: ILivreur): void {
    this.livreur = livreur;
    this.livreurFormService.resetForm(this.editForm, livreur);
  }
}
