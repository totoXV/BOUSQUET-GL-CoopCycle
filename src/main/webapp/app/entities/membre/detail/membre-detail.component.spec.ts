import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { MembreDetailComponent } from './membre-detail.component';

describe('Membre Management Detail Component', () => {
  let comp: MembreDetailComponent;
  let fixture: ComponentFixture<MembreDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MembreDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ membre: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(MembreDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(MembreDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load membre on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.membre).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
