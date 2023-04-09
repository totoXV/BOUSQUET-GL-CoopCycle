import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IMembre } from '../membre.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../membre.test-samples';

import { MembreService } from './membre.service';

const requireRestSample: IMembre = {
  ...sampleWithRequiredData,
};

describe('Membre Service', () => {
  let service: MembreService;
  let httpMock: HttpTestingController;
  let expectedResult: IMembre | IMembre[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(MembreService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Membre', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const membre = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(membre).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Membre', () => {
      const membre = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(membre).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Membre', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Membre', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Membre', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addMembreToCollectionIfMissing', () => {
      it('should add a Membre to an empty array', () => {
        const membre: IMembre = sampleWithRequiredData;
        expectedResult = service.addMembreToCollectionIfMissing([], membre);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(membre);
      });

      it('should not add a Membre to an array that contains it', () => {
        const membre: IMembre = sampleWithRequiredData;
        const membreCollection: IMembre[] = [
          {
            ...membre,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addMembreToCollectionIfMissing(membreCollection, membre);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Membre to an array that doesn't contain it", () => {
        const membre: IMembre = sampleWithRequiredData;
        const membreCollection: IMembre[] = [sampleWithPartialData];
        expectedResult = service.addMembreToCollectionIfMissing(membreCollection, membre);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(membre);
      });

      it('should add only unique Membre to an array', () => {
        const membreArray: IMembre[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const membreCollection: IMembre[] = [sampleWithRequiredData];
        expectedResult = service.addMembreToCollectionIfMissing(membreCollection, ...membreArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const membre: IMembre = sampleWithRequiredData;
        const membre2: IMembre = sampleWithPartialData;
        expectedResult = service.addMembreToCollectionIfMissing([], membre, membre2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(membre);
        expect(expectedResult).toContain(membre2);
      });

      it('should accept null and undefined values', () => {
        const membre: IMembre = sampleWithRequiredData;
        expectedResult = service.addMembreToCollectionIfMissing([], null, membre, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(membre);
      });

      it('should return initial array if no Membre is added', () => {
        const membreCollection: IMembre[] = [sampleWithRequiredData];
        expectedResult = service.addMembreToCollectionIfMissing(membreCollection, undefined, null);
        expect(expectedResult).toEqual(membreCollection);
      });
    });

    describe('compareMembre', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareMembre(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareMembre(entity1, entity2);
        const compareResult2 = service.compareMembre(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareMembre(entity1, entity2);
        const compareResult2 = service.compareMembre(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareMembre(entity1, entity2);
        const compareResult2 = service.compareMembre(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
