import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../livreur.test-samples';

import { LivreurFormService } from './livreur-form.service';

describe('Livreur Form Service', () => {
  let service: LivreurFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LivreurFormService);
  });

  describe('Service methods', () => {
    describe('createLivreurFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createLivreurFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
          })
        );
      });

      it('passing ILivreur should create a new form with FormGroup', () => {
        const formGroup = service.createLivreurFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
          })
        );
      });
    });

    describe('getLivreur', () => {
      it('should return NewLivreur for default Livreur initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createLivreurFormGroup(sampleWithNewData);

        const livreur = service.getLivreur(formGroup) as any;

        expect(livreur).toMatchObject(sampleWithNewData);
      });

      it('should return NewLivreur for empty Livreur initial value', () => {
        const formGroup = service.createLivreurFormGroup();

        const livreur = service.getLivreur(formGroup) as any;

        expect(livreur).toMatchObject({});
      });

      it('should return ILivreur', () => {
        const formGroup = service.createLivreurFormGroup(sampleWithRequiredData);

        const livreur = service.getLivreur(formGroup) as any;

        expect(livreur).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ILivreur should not enable id FormControl', () => {
        const formGroup = service.createLivreurFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewLivreur should disable id FormControl', () => {
        const formGroup = service.createLivreurFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
