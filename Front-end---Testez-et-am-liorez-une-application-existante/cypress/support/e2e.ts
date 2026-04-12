// This file is loaded before every E2E test.
// You can add global hooks, custom commands, or imports here.

// Clear localStorage before each test to ensure a clean state
beforeEach(() => {
  cy.clearLocalStorage();
});
