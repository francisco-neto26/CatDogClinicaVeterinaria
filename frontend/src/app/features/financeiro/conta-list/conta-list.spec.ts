import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContaList } from './conta-list';

describe('ContaList', () => {
  let component: ContaList;
  let fixture: ComponentFixture<ContaList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ContaList]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ContaList);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
