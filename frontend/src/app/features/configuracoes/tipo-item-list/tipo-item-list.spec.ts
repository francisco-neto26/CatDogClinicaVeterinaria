import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TipoItemList } from './tipo-item-list';

describe('TipoItemList', () => {
  let component: TipoItemList;
  let fixture: ComponentFixture<TipoItemList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TipoItemList]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TipoItemList);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
