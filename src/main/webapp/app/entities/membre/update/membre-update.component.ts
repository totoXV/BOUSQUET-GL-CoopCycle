import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { MembreFormService, MembreFormGroup } from './membre-form.service';
import { IMembre } from '../membre.model';
import { MembreService } from '../service/membre.service';

@Component({
  selector: 'jhi-membre-update',
  templateUrl: './membre-update.component.html',
})
export class MembreUpdateComponent implements OnInit {
  isSaving = false;
  membre: IMembre | null = null;

  editForm: MembreFormGroup = this.membreFormService.createMembreFormGroup();

  constructor(
    protected membreService: MembreService,
    protected membreFormService: MembreFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ membre }) => {
      this.membre = membre;
      if (membre) {
        this.updateForm(membre);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const membre = this.membreFormService.getMembre(this.editForm);
    if (membre.id !== null) {
      this.subscribeToSaveResponse(this.membreService.update(membre));
    } else {
      this.subscribeToSaveResponse(this.membreService.create(membre));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMembre>>): void {
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

  protected updateForm(membre: IMembre): void {
    this.membre = membre;
    this.membreFormService.resetForm(this.editForm, membre);
  }
}
