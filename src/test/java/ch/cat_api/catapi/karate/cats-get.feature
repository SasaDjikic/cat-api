Feature: Cat GET Endpoints

  Scenario: Retrieve all cats from /cats endpoint and verify response
    Given url 'http://localhost:8400/cats'
    When method get
    Then status 200
    And assert response.length >= 1

  Scenario: Retrieve all cats from /cats endpoint and verify response
    * def id = '670e4a2e8213ed68adccd199'

    Given url 'http://localhost:8400/cats'
    And path id
    When method get
    Then status 200
    And assert response != null

  Scenario: Retrieve a cat with a invalid id with /cats/{_id} endpoint and verify response
    * def id = 'invalid-id'

    Given url 'http://localhost:8400/cats'
    And path id
    When method get
    Then status 400

  Scenario: Retrieve a cat by id which does not exist with /cats/{_id} endpoint and verify response
    * def id = '670e4a2e8213ed68adccd191'

    Given url 'http://localhost:8400/cats'
    And path id
    When method get
    Then status 404
