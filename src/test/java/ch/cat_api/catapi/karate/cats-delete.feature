Feature: Cat EDELETE ndpoints

  Scenario: Delete a cat by id which does not exist with /cats/{_id} endpoint and verify response
    * def id = '670e4a5f7899964d2f7f3d4c'

    Given url 'http://localhost:8400/cats'
    And path id
    When method delete
    Then status 204

  Scenario: Delete a cat with a invalid id with /cats/{_id} endpoint and verify response
    * def id = 'invalid-id'

    Given url 'http://localhost:8400/cats'
    And path id
    When method delete
    Then status 400

  Scenario: Delete a cat by id which does not exist with /cats/{_id} endpoint and verify response
    * def id = '670e4a5f7899964d2f7f3d41'

    Given url 'http://localhost:8400/cats'
    And path id
    When method delete
    Then status 404
