import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../membre.test-samples';

import { MembreFormService } from './membre-form.service';

describe('Membre Form Service', () => {
  let service: MembreFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MembreFormService);
  });

  describe('Service methods', () => {
    describe('createMembreFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createMembreFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
          })
        );
      });

      it('passing IMembre should create a new form with FormGroup', () => {
        const formGroup = service.createMembreFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
          })
        );
      });
    });

    describe('getMembre', () => {
      it('should return NewMembre for default Membre initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createMembreFormGroup(sampleWithNewData);

        const membre = service.getMembre(formGroup) as any;

        expect(membre).toMatchObject(sampleWithNewData);
      });

      it('should return NewMembre for empty Membre initial value', () => {
        const formGroup = service.createMembreFormGroup();

        const membre = service.getMembre(formGroup) as any;

        expect(membre).toMatchObject({});
      });

      it('should return IMembre', () => {
        const formGroup = service.createMembreFormGroup(sampleWithRequiredData);

        const membre = service.getMembre(formGroup) as any;

        expect(membre).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IMembre should not enable id FormControl', () => {
        const formGroup = service.createMembreFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewMembre should disable id FormControl', () => {
        const formGroup = service.createMembreFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
