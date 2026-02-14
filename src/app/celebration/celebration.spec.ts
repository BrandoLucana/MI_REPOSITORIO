import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Celebration } from './celebration';

describe('Celebration', () => {
  let component: Celebration;
  let fixture: ComponentFixture<Celebration>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Celebration]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Celebration);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
