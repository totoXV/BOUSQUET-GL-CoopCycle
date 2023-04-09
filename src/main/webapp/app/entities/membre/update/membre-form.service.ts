import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IMembre, NewMembre } from '../membre.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IMembre for edit and NewMembreFormGroupInput for create.
 */
type MembreFormGroupInput = IMembre | PartialWithRequiredKeyOf<NewMembre>;

type MembreFormDefaults = Pick<NewMembre, 'id'>;

type MembreFormGroupContent = {
  id: FormControl<IMembre['id'] | NewMembre['id']>;
};

export type MembreFormGroup = FormGroup<MembreFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MembreFormService {
  createMembreFormGroup(membre: MembreFormGroupInput = { id: null }): MembreFormGroup {
    const membreRawValue = {
      ...this.getFormDefaults(),
      ...membre,
    };
    return new FormGroup<MembreFormGroupContent>({
      id: new FormControl(
        { value: membreRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
    });
  }

  getMembre(form: MembreFormGroup): IMembre | NewMembre {
    if (form.controls.id.disabled) {
      // form.value returns id with null value for FormGroup with only one FormControl
      return {};
    }
    return form.getRawValue() as IMembre | NewMembre;
  }

  resetForm(form: MembreFormGroup, membre: MembreFormGroupInput): void {
    const membreRawValue = { ...this.getFormDefaults(), ...membre };
    form.reset(
      {
        ...membreRawValue,
        id: { value: membreRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): MembreFormDefaults {
    return {
      id: null,
    };
  }
}
