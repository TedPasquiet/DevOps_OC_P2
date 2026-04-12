describe('Login page', () => {
  beforeEach(() => {
    cy.visit('/');
  });

  it('should display the login form', () => {
    cy.get('form').should('be.visible');
    cy.get('input[formControlName="login"]').should('exist');
    cy.get('input[formControlName="password"]').should('exist');
    cy.get('button[type="submit"]').should('exist');
  });

  it('should navigate to /protected after a successful login', () => {
    cy.intercept('POST', '/api/login', {
      statusCode: 200,
      body: 'fake-jwt-token',
    }).as('loginRequest');

    cy.get('input[formControlName="login"]').type('john');
    cy.get('input[formControlName="password"]').type('password');
    cy.get('button[type="submit"]').click();

    cy.wait('@loginRequest');
    cy.url().should('include', '/protected');
  });

  it('should display an error message on failed login', () => {
    cy.intercept('POST', '/api/login', {
      statusCode: 400,
      body: { message: 'Bad credentials' },
    }).as('loginRequest');

    cy.get('input[formControlName="login"]').type('wronguser');
    cy.get('input[formControlName="password"]').type('wrongpassword');
    cy.get('button[type="submit"]').click();

    cy.wait('@loginRequest');
    cy.contains('Identifiants incorrects').should('be.visible');
  });
  it('should display validation message', () => {
    cy.get('input[formControlName="login"]').should('have.value', '');
    cy.get('input[formControlName="password"]').should('have.value', '');
    cy.get('button[type="submit"]').click();
    cy.contains('Le login est requis').should('be.visible');
    cy.contains('Le mot de passe est requis').should('be.visible');
  });
});
