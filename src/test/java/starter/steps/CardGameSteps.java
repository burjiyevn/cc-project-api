package starter.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import net.serenitybdd.rest.SerenityRest;
import org.junit.Assert;

import java.util.List;
import java.util.Map;

public class CardGameSteps {

    private String deckId;
    private List<Map<String, String>> playerOneHand;
    private List<Map<String, String>> playerTwoHand;
    private static final String BASE_URL = "https://deckofcardsapi.com/api/deck";

    @Given("the deck of cards site is up and running")
    public void theDeckOfCardsSiteIsUpAndRunning() {
        Response response = SerenityRest
                .given()
                .when()
                .get(BASE_URL + "/new")
                .then()
                .statusCode(200)
                .extract()
                .response();

        Assert.assertEquals(200, response.statusCode());

    }

    @When("I request a new shuffled deck")
    public void iRequestANewShuffledDeck() {
        Response response = SerenityRest
                .given()
                .queryParam("deck_count", 1)
                .when()
                .get(BASE_URL + "/new/shuffle/")
                .then()
                .extract()
                .response();

        deckId = response.path("deck_id");
        System.out.println("Deck id is: " + deckId);
    }

    @And("I draw three cards for player one")
    public void iDrawThreeCardsForPlayerOne() {
        playerOneHand = drawCards(deckId, 3);
        System.out.println(playerOneHand);
    }

    @And("I draw three cards for player two")
    public void iDrawThreeCardsForPlayerTwo() {
        playerTwoHand = drawCards(deckId, 3);
        System.out.println(playerTwoHand);

    }

    @Then("I check if either player has blackjack")
    public void iCheckIfEitherPlayerHasBlackjack() {
        checkBlackjack(playerOneHand, "Player One");
        checkBlackjack(playerTwoHand, "Player Two");
    }

    private List<Map<String, String>> drawCards(String deckId, int count) {
        Response response = SerenityRest
                .given()
                .queryParam("count", count)
                .when()
                .get(BASE_URL + "/" + deckId + "/draw/")
                .then()
                .extract()
                .response();

        return response.jsonPath().getList("cards");
    }

    private void checkBlackjack(List<Map<String, String>> hand, String playerName) {
        int totalValue = 0;
        for (Map<String, String> card : hand) {
            String cardCode = card.get("code");
            // Assume the card code's first character represents the value
            totalValue += getCardValue(cardCode);
        }

        if (totalValue == 21) {
            System.out.println(playerName + " has blackjack!");
        } else {
            System.out.println(playerName + " does not have blackjack. Their hand's total value is: " + totalValue);
        }
    }

    private int getCardValue(String cardCode) {
        char valueChar = cardCode.charAt(0);
        switch (valueChar) {
            case 'A':
                return 11; // Ace can be 1 or 11, simplified rule
            case 'K':
            case 'Q':
            case 'J':
            case '0': // 10 is represented with a '0'
                return 10;
            default:
                // Assuming that the card code's first character is a digit representing the card's value
                return Character.getNumericValue(valueChar);
        }
    }

}
