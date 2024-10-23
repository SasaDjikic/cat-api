Feature: Cat POST Endpoints

  Scenario: Create a new cats with /cats endpoint and verify response
    Given url 'http://localhost:8400/cats'
    And request { "name": "TEST", "age": 12, "buyer": "PETER" }
    And header Content-Type = 'application/json'
    When method post
    Then status 200
    And assert response != null
    And assert response.name == "TEST"
    And assert response.age == 12
    And assert response.buyer == "PETER"

  Scenario: Create a new cats with nullable buyer with /cats endpoint and verify response
    Given url 'http://localhost:8400/cats'
    And request { "name": "TEST", "age": 12 }
    And header Content-Type = 'application/json'
    When method post
    Then status 200
    And assert response != null
    And assert response.name == "TEST"
    And assert response.age == 12
    And assert response.buyer == null

  Scenario: Create a new cats bad request with /cats endpoint and verify response
    Given url 'http://localhost:8400/cats'
    And request { "name": 123, "age": 12, "buyer": "PETER" }
    And header Content-Type = 'application/json'
    When method post
    Then status 400
    And assert response != null

