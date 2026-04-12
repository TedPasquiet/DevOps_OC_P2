import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { UserService } from './user.service';
import { Register } from '../models/Register';
import { Login } from '../models/Login';

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
      ]
    });
    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('register', () => {
    it('should POST to /api/register with user data', () => {
      const user: Register = { firstName: 'John', lastName: 'Doe', login: 'john', password: 'pass' };

      service.register(user).subscribe();

      const req = httpMock.expectOne('/api/register');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(user);
      req.flush({});
    });
  });

  describe('login', () => {
    it('should POST to /api/login and store the token in localStorage', () => {
      const credentials: Login = { login: 'john', password: 'pass' };
      const fakeToken = 'fake-jwt-token';

      service.login(credentials).subscribe(token => {
        expect(token).toBe(fakeToken);
      });

      const req = httpMock.expectOne('/api/login');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(credentials);
      req.flush(fakeToken);

      expect(localStorage.getItem('auth_token')).toBe(fakeToken);
    });
  });

  describe('getToken', () => {
    it('should return the token stored in localStorage', () => {
      localStorage.setItem('auth_token', 'my-token');
      expect(service.getToken()).toBe('my-token');
    });

    it('should return null when no token is stored', () => {
      expect(service.getToken()).toBeNull();
    });
  });

  describe('isLoggedIn', () => {
    it('should return true when a token exists', () => {
      localStorage.setItem('auth_token', 'my-token');
      expect(service.isLoggedIn()).toBe(true);
    });

    it('should return false when no token exists', () => {
      expect(service.isLoggedIn()).toBe(false);
    });
  });

  describe('logout', () => {
    it('should remove the token from localStorage', () => {
      localStorage.setItem('auth_token', 'my-token');
      service.logout();
      expect(localStorage.getItem('auth_token')).toBeNull();
    });
  });
});
