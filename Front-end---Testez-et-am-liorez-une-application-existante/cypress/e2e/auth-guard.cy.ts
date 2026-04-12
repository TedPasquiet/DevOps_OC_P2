describe('Auth guard', () => {
  it('should redirect to / when visiting /protected without being logged in', () => {
    cy.visit('/protected');
    cy.url().should('eq', Cypress.config().baseUrl + '/');
  });

  it('should allow access to /protected when logged in', () => {
    localStorage.setItem('auth_token', 'fake-jwt-token');
    cy.visit('/protected');
    cy.url().should('include', '/protected');
  });
});
