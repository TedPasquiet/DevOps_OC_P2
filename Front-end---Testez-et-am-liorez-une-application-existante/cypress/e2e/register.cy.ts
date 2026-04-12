describe('Register page', () => {
  beforeEach(() => {
    cy.visit('/register');
  });

  it('should display the register form', () => {
    cy.get('form').should('be.visible');
    cy.get('input[formControlName="firstName"]').should('exist');
    cy.get('input[formControlName="lastName"]').should('exist');
    cy.get('input[formControlName="login"]').should('exist');
    cy.get('input[formControlName="password"]').should('exist');
    cy.get('button[type="submit"]').should('exist');
  });

  it('should redirect to login page after successful registration', () => {
    cy.intercept('POST', '/api/register', {
      statusCode: 201,
      body: {},
    }).as('registerRequest');

    cy.get('input[formControlName="firstName"]').type('John');
    cy.get('input[formControlName="lastName"]').type('Doe');
    cy.get('input[formControlName="login"]').type('johndoe');
    cy.get('input[formControlName="password"]').type('password');
    cy.get('button[type="submit"]').click();

    cy.wait('@registerRequest');
    cy.url().should('eq', Cypress.config().baseUrl + '/');
  });
  it('should display validation message', () => {
    cy.get('input[formControlName="login"]').should('have.value', '');
    cy.get('input[formControlName="password"]').should('have.value', '');
    cy.get('input[formControlName="lastName"]').should('have.value', '');
    cy.get('input[formControlName="firstName"]').should('have.value', '');
    cy.get('button[type="submit"]').click();
    cy.contains('Le mot de passe est requis').should('be.visible');
    cy.contains('Le nom est requis').should('be.visible');
    cy.contains('Le prénom est requis').should('be.visible');
    cy.contains('Le login est requis').should('be.visible');
  })
  it('should display an error message when registration fails', () => {
    cy.intercept('POST', '/api/register', {
      statusCode: 400,
      body: {},
    }).as('registerRequest');

    cy.get('input[formControlName="firstName"]').type('John');
    cy.get('input[formControlName="lastName"]').type('Doe');
    cy.get('input[formControlName="login"]').type('johndoe');
    cy.get('input[formControlName="password"]').type('password');
    cy.get('button[type="submit"]').click();

    cy.wait('@registerRequest');
    cy.contains('Cet utilisateur existe déjà.').should('be.visible');
  })
});
