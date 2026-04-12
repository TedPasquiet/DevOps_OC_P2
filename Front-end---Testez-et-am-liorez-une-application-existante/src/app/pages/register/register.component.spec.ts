import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';

import { RegisterComponent } from './register.component';
import { UserService } from '../../core/service/user.service';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let userServiceMock: jest.Mocked<Pick<UserService, 'register'>>;

  beforeEach(async () => {
    userServiceMock = {
      register: jest.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [RegisterComponent],
      providers: [
        provideRouter([]),
        { provide: UserService, useValue: userServiceMock },
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('form', () => {
    it('should expose form controls via the form getter', () => {
      expect(component.form).toBe(component.registerForm.controls);
    });
  });

  describe('form validation', () => {
    it('should be invalid when empty', () => {
      expect(component.registerForm.invalid).toBe(true);
    });

    it('should be valid when all fields are filled', () => {
      component.registerForm.setValue({
        firstName: 'John',
        lastName: 'Doe',
        login: 'john',
        password: 'password'
      });
      expect(component.registerForm.valid).toBe(true);
    });

    it('should be invalid when only firstName is missing', () => {
      component.registerForm.setValue({
        firstName: '',
        lastName: 'Doe',
        login: 'john',
        password: 'password'
      });
      expect(component.registerForm.invalid).toBe(true);
    });
  });

  describe('onSubmit', () => {
    it('should not call userService when form is invalid', () => {
      component.onSubmit();
      expect(userServiceMock.register).not.toHaveBeenCalled();
    });

    it('should call userService.register when form is valid', () => {
      userServiceMock.register.mockReturnValue(of({}));
      component.registerForm.setValue({
        firstName: 'John',
        lastName: 'Doe',
        login: 'john',
        password: 'password'
      });

      component.onSubmit();

      expect(userServiceMock.register).toHaveBeenCalledWith({
        firstName: 'John',
        lastName: 'Doe',
        login: 'john',
        password: 'password'
      });
    });

    it('should set submitted to true on submit', () => {
      component.onSubmit();
      expect(component.submitted).toBe(true);
    });

    it('should set errorMessage when registration fails', () => {
      userServiceMock.register.mockReturnValue(throwError(() => new Error('Conflict')));
      component.registerForm.setValue({
        firstName: 'John',
        lastName: 'Doe',
        login: 'john',
        password: 'password'
      });

      component.onSubmit();

      expect(component.errorMessage).toBe('Cet utilisateur existe déjà.');
    });
  });

  describe('onReset', () => {
    it('should reset the form and set submitted to false', () => {
      component.submitted = true;
      component.registerForm.setValue({
        firstName: 'John',
        lastName: 'Doe',
        login: 'john',
        password: 'password'
      });

      component.onReset();

      expect(component.submitted).toBe(false);
      expect(component.registerForm.value).toEqual({
        firstName: null,
        lastName: null,
        login: null,
        password: null
      });
    });
  });
});
