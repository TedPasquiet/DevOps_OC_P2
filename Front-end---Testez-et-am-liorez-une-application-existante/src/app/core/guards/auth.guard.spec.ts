import { TestBed } from '@angular/core/testing';
import { provideRouter, Router, UrlTree } from '@angular/router';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';

import { authGuard } from './auth.guard';
import { UserService } from '../service/user.service';

describe('authGuard', () => {
  let userServiceMock: jest.Mocked<Pick<UserService, 'isLoggedIn'>>;
  let router: Router;

  const runGuard = () => {
    return TestBed.runInInjectionContext(() =>
      authGuard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot)
    );
  };

  beforeEach(() => {
    userServiceMock = {
      isLoggedIn: jest.fn(),
    };

    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        { provide: UserService, useValue: userServiceMock },
      ]
    });

    router = TestBed.inject(Router);
  });

  it('should allow access when user is logged in', () => {
    userServiceMock.isLoggedIn.mockReturnValue(true);

    const result = runGuard();

    expect(result).toBe(true);
  });

  it('should redirect to / when user is not logged in', () => {
    userServiceMock.isLoggedIn.mockReturnValue(false);

    const result = runGuard() as UrlTree;

    expect(result).toBeInstanceOf(UrlTree);
    expect(result.toString()).toBe('/');
  });
});
