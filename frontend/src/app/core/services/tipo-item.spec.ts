import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TipoItemService } from './tipo-item';

describe('TipoItemService', () => {
  let service: TipoItemService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(TipoItemService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});