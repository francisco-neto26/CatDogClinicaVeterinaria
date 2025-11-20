import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TituloList } from './titulo-list';

describe('TituloList', () => {
  let component: TituloList;
  let fixture: ComponentFixture<TituloList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TituloList]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TituloList);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
