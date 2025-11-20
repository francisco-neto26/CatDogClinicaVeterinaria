import { TestBed } from '@angular/core/testing';

import { Titulo } from './titulo';

describe('Titulo', () => {
  let service: Titulo;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Titulo);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
