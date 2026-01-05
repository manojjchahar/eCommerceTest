@eCommFull @Authentication @UI
#User authentication: Registration, login, logout, account deletion

  Feature: Authentication functionality
    As a user
    I want to register a new account to AutomationExercise,
    login to account, logout, and delete account
    So that I can explore authentication features

@firstTest
    Scenario Outline: User registration via UI
      Given user is on the signup page
      When user signs up with "<User>"
      Then user should be on signup details page with "<SignupPageMessage>"
      Then signup should proceed to account creation step
      And user completes account creation with valid "<profile>" type
      Then user should be registered with "<signUpSuccessMessage>"
      And user should be logged in after continue button
      Then user perform "<Action>" successfully
        Examples:
            | SignupPageMessage           | profile |signUpSuccessMessage    |Action |User|
            | Enter Account Information   | default |Account Created!        |Delete Account|John Doe|


    @negative @duplicate
    Scenario Outline: Duplicate user registration is rejected
      Given user is on the signup page
      When user attempts to sign up with existing user "<existingUser>"
      Then duplicate signup should be rejected with "<duplicateMessage>"
        Examples:
            | existingUser | duplicateMessage                |
            | mxk     | Email Address already exist!    |


    @headerNavigation
    Scenario: Successful UI login
      Given user is on the login page
      When user logs in with default user credentials
      Then user should be logged in successfully


    @headerNavigation
    Scenario Outline: Account management via UI
      Given user is on the login page
      When user logs in with "<User>"
      Then user should be logged in successfully
      And user perform "<Action>" successfully

      Examples:
        | Action         |User|
        | Logout         |mxk |