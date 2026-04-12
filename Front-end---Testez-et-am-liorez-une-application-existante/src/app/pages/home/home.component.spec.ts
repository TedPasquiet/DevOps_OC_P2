import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { of, throwError } from 'rxjs';

import { HomeComponent } from './home.component';
import { UserService } from '../../core/service/user.service';

describe('HomeComponent', () => {
  let component: HomeComponent;
  let fixture: ComponentFixture<HomeComponent>;
  let userServiceMock: jest.Mocked<Pick<UserService, 'login'>>;
  let router: Router;

  beforeEach(async () => {
    userServiceMock = {
      login: jest.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [HomeComponent],
      providers: [
        provideRouter([]),
        { provide: UserService, useValue: userServiceMock },
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(HomeComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('form validation', () => {
    it('should be invalid when empty', () => {
      expect(component.loginForm.invalid).toBe(true);
    });

    it('should be valid when login and password are filled', () => {
      component.loginForm.setValue({ login: 'john', password: 'password' });
      expect(component.loginForm.valid).toBe(true);
    });
  });

  describe('onSubmit', () => {
    it('should not call userService when form is invalid', () => {
      component.onSubmit();
      expect(userServiceMock.login).not.toHaveBeenCalled();
    });

    it('should set submitted to true on submit', () => {
      component.onSubmit();
      expect(component.submitted).toBe(true);
    });

    it('should call userService.login and navigate to /protected on success', () => {
      userServiceMock.login.mockReturnValue(of('fake-jwt-token'));
      const navigateSpy = jest.spyOn(router, 'navigate');
      component.loginForm.setValue({ login: 'john', password: 'password' });

      component.onSubmit();

      expect(userServiceMock.login).toHaveBeenCalledWith({ login: 'john', password: 'password' });
      expect(navigateSpy).toHaveBeenCalledWith(['/protected']);
    });

    it('should set errorMessage on login failure', () => {
      userServiceMock.login.mockReturnValue(throwError(() => new Error('Unauthorized')));
      component.loginForm.setValue({ login: 'john', password: 'wrongpassword' });

      component.onSubmit();

      expect(component.errorMessage).toBe('Identifiants incorrects. Veuillez réessayer.');
    });
  });
});
