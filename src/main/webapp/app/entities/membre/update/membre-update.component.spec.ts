import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { MembreFormService } from './membre-form.service';
import { MembreService } from '../service/membre.service';
import { IMembre } from '../membre.model';

import { MembreUpdateComponent } from './membre-update.component';

describe('Membre Management Update Component', () => {
  let comp: MembreUpdateComponent;
  let fixture: ComponentFixture<MembreUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let membreFormService: MembreFormService;
  let membreService: MembreService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [MembreUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(MembreUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MembreUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    membreFormService = TestBed.inject(MembreFormService);
    membreService = TestBed.inject(MembreService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const membre: IMembre = { id: 456 };

      activatedRoute.data = of({ membre });
      comp.ngOnInit();

      expect(comp.membre).toEqual(membre);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMembre>>();
      const membre = { id: 123 };
      jest.spyOn(membreFormService, 'getMembre').mockReturnValue(membre);
      jest.spyOn(membreService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ membre });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: membre }));
      saveSubject.complete();

      // THEN
      expect(membreFormService.getMembre).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(membreService.update).toHaveBeenCalledWith(expect.objectContaining(membre));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMembre>>();
      const membre = { id: 123 };
      jest.spyOn(membreFormService, 'getMembre').mockReturnValue({ id: null });
      jest.spyOn(membreService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ membre: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: membre }));
      saveSubject.complete();

      // THEN
      expect(membreFormService.getMembre).toHaveBeenCalled();
      expect(membreService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMembre>>();
      const membre = { id: 123 };
      jest.spyOn(membreService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ membre });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(membreService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
