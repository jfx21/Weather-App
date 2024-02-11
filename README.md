# Introduction
UserService wrtten in Java 18 with Spring and JPA. In that service we can register user, login to his account and delete it with jwt tokenization.

# Build and test
To run the app you need Java 17 with maven. Clone the repo and then 'load maven project' when everything loads you may proceed to run the main applcation.

# Database setup
In this app we're using MySQL. Locally you'll need to set up your own server p.ex. with help of MySQL Workbench. Enter the needed environmental variables for db:
DB_URl, DB_USER, DB_PASS the way you configured it in the MySQL server.

# JWT Tokenization
You'll need to provide a secret-key in your run configuration: p.ex: secret-key=204E632266556A586E3272357538782F413F4428472B4B6250645367566B5990242422211124224242242424242

