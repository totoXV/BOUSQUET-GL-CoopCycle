import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { LivreurService } from '../service/livreur.service';

import { LivreurComponent } from './livreur.component';

describe('Livreur Management Component', () => {
  let comp: LivreurComponent;
  let fixture: ComponentFixture<LivreurComponent>;
  let service: LivreurService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'livreur', component: LivreurComponent }]), HttpClientTestingModule],
      declarations: [LivreurComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
              })
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(LivreurComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(LivreurComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(LivreurService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.livreurs?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to livreurService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getLivreurIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getLivreurIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
