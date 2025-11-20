import { TestBed } from '@angular/core/testing';

import { ItemServico } from './item-servico';

describe('ItemServico', () => {
  let service: ItemServico;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ItemServico);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
