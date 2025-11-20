import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ItemServicoList } from './item-servico-list';

describe('ItemServicoList', () => {
  let component: ItemServicoList;
  let fixture: ComponentFixture<ItemServicoList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ItemServicoList]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ItemServicoList);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
