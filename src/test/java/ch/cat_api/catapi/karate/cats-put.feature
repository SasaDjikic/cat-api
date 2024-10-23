Feature: Cat PUT Endpoints

  Scenario: Update a existing cat with /cats/{_id} endpoint and verify response
    * def id = '670e4a5b0937f8de1a9d605e'

    Given url 'http://localhost:8400/cats'
    And request { "name": "Whiskers", "age": 3, "buyer": "PETER" }
    And path id
    And header Content-Type = 'application/json'
    When method put
    Then status 200
    And assert response != null
    And assert response.name == "Whiskers"
    And assert response.age == 3
    And assert response.buyer == "PETER"

  Scenario: Update a existing cat with bad request with /cats/{_id} endpoint and verify response
    * def id = '670e4a5b0937f8de1a9d605e'

    Given url 'http://localhost:8400/cats'
    And request { "name": "Whiskers", "age": 3, "buyer": 2 }
    And path id
    And header Content-Type = 'application/json'
    When method put
    Then status 400

  Scenario: Update a existing cat by invalid id with /cats/{_id} endpoint and verify response
    * def id = 'invalid-id'

    Given url 'http://localhost:8400/cats'
    And request { "name": "Whiskers", "age": 3, "buyer": "PETER" }
    And path id
    And header Content-Type = 'application/json'
    When method put
    Then status 400

  Scenario: Update a existing cat by id which does not exist with /cats/{_id} endpoint and verify response
    * def id = '670e4a5b0937f8de1a9d6051'

    Given url 'http://localhost:8400/cats'
    And request { "name": "Whiskers", "age": 3, "buyer": "PETER" }
    And path id
    And header Content-Type = 'application/json'
    When method put
    Then status 404
