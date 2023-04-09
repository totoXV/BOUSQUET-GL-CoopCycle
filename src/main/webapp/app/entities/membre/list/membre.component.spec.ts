import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { MembreService } from '../service/membre.service';

import { MembreComponent } from './membre.component';

describe('Membre Management Component', () => {
  let comp: MembreComponent;
  let fixture: ComponentFixture<MembreComponent>;
  let service: MembreService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'membre', component: MembreComponent }]), HttpClientTestingModule],
      declarations: [MembreComponent],
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
      .overrideTemplate(MembreComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MembreComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(MembreService);

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
    expect(comp.membres?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to membreService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getMembreIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getMembreIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
