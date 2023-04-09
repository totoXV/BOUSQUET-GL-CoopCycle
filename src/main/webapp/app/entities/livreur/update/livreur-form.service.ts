import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ILivreur, NewLivreur } from '../livreur.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ILivreur for edit and NewLivreurFormGroupInput for create.
 */
type LivreurFormGroupInput = ILivreur | PartialWithRequiredKeyOf<NewLivreur>;

type LivreurFormDefaults = Pick<NewLivreur, 'id'>;

type LivreurFormGroupContent = {
  id: FormControl<ILivreur['id'] | NewLivreur['id']>;
};

export type LivreurFormGroup = FormGroup<LivreurFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class LivreurFormService {
  createLivreurFormGroup(livreur: LivreurFormGroupInput = { id: null }): LivreurFormGroup {
    const livreurRawValue = {
      ...this.getFormDefaults(),
      ...livreur,
    };
    return new FormGroup<LivreurFormGroupContent>({
      id: new FormControl(
        { value: livreurRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
    });
  }

  getLivreur(form: LivreurFormGroup): ILivreur | NewLivreur {
    if (form.controls.id.disabled) {
      // form.value returns id with null value for FormGroup with only one FormControl
      return {};
    }
    return form.getRawValue() as ILivreur | NewLivreur;
  }

  resetForm(form: LivreurFormGroup, livreur: LivreurFormGroupInput): void {
    const livreurRawValue = { ...this.getFormDefaults(), ...livreur };
    form.reset(
      {
        ...livreurRawValue,
        id: { value: livreurRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): LivreurFormDefaults {
    return {
      id: null,
    };
  }
}
