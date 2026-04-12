describe('Navigation', () => {
  it('should navigate to /register when clicking "Créer un compte" from login page', () => {
    cy.visit('/');
    cy.contains('Créer un compte').click();
    cy.url().should('include', '/register');
  });

  it('should navigate to / when clicking "Se connecter" from register page', () => {
    cy.visit('/register');
    cy.contains('Se connecter').click();
    cy.url().should('eq', Cypress.config().baseUrl + '/');
  });

  it('should navigate to / and clear token when clicking "Se déconnecter"', () => {
    localStorage.setItem('auth_token', 'fake-jwt-token');
    cy.visit('/protected');

    cy.contains('Se déconnecter').click();

    cy.url().should('eq', Cypress.config().baseUrl + '/');
    cy.window().then(win => {
      expect(win.localStorage.getItem('auth_token')).to.be.null;
    });
  });
});