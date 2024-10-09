Feature: Cat Endpoints

  Scenario: Retrieve all cats from /cats endpoint and verify response
    Given url 'http://localhost:8400/cats'
    When method get
    Then status 200
    And assert response.length > 1

  Scenario: Delete a cat with a invalid id with /cats/{_id} endpoint and verify response
    * def id = 'invalid-id'

    Given url 'http://localhost:8400/cats'
    And path id
    When method delete
    Then status 400

  Scenario: Delete a cat by id which does not exist with /cats/{_id} endpoint and verify response
    * def id = '66fe57b467e04f76689a1243'

    Given url 'http://localhost:8400/cats'
    And path id
    When method delete
    Then status 404

  Scenario: Delete a cat by id which does not exist with /cats/{_id} endpoint and verify response
    * def id = '66fe57b467e04f76689a1241'

    Given url 'http://localhost:8400/cats'
    And path id
    When method delete
    Then status 204
