import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { LivreurDetailComponent } from './livreur-detail.component';

describe('Livreur Management Detail Component', () => {
  let comp: LivreurDetailComponent;
  let fixture: ComponentFixture<LivreurDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LivreurDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ livreur: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(LivreurDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(LivreurDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load livreur on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.livreur).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
