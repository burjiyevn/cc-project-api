Feature: Blackjack game operations

  Scenario: Play a simple game of Blackjack
    Given the deck of cards site is up and running
    When I request a new shuffled deck
    And I draw three cards for player one
    And I draw three cards for player two
    Then I check if either player has blackjack

