# CTruco - Truco game for didactic purpose

## Overview

This project was designed to serve as supporting material in classes involving object-oriented design and programming,
Java language, testing, and backend development. In the source code, students and practitioners will find implementations 
of concepts such as: 

- Modern Java programming (functional API, Streams, modules, switch expressions, etc.)
- Clean Code and Clean Architecture (SOLID);
- GRASP patterns;
- Design Patterns (Singleton, State, Strategy, DAO, etc.);
- Object Calisthenics;
- Testing Driven Development (TDD) using JUnit 5 and Mockito;
- Domain Driven Design (DDD);
- Conventional Commits;
- Semantic versioning - SemVer.

## Downloading and Running

**CTruco** `domain` module was implemented using vanilla Java to exemplify the features the language provides. 
It isolates the core business rules from libraries and third party plugins, thus enabling the creation of applications in
different formats, for multiple platforms. The following playing modes are already available: 

- Console game between a player and a bot (run `cli/GameCLI` class in `console` module);
- Console simulation of games between two bots (run `standalone/PlayWithBots` class in `console` module); and
- JavaFX game between a player and a bot (run `view/WindowGameTable` class in `desktop` module);
- Spring Boot backend for games between a player and a bot (run `WebApp` class in `web` module);

The project uses Java 17 language features. Therefore, JDK 17+ is required. Further requirements are declared as Maven
dependencies, so no additional config is needed.  

## Project Modules

CTruco is composed of the following modules: 

- `domain:` encompasses game business rules, including core entities and use cases. All application modules depend on this module;
- `persistence:` provides concrete implementations for persistence interfaces specified in `domain` module use cases;
- `bot-spi:` contains a [Service Provider Interface (SPI)](https://docs.oracle.com/javase/tutorial/sound/SPI-intro.html) named `BotServiceProvider`, which enables any future bot implementation to be seamlessly integrated into `domain` use cases;
- `bot-impl:` provides two default implementations of `BotServiceProvider` interface: `DummyBot` (silly one), `MineiroBot` (not so silly);
- `console:` contains console versions of truco game applications that enable playing against bots or between bots;
- `desktop:`provides a JavaFX/FXML version of the truco game for users to play against bots;
- `web:`provides Spring Boot backend for a web version of the truco game for users to play against bots;


## Configuring the Web Backend (`application.properties`)

The `web` module (Spring Boot backend) reads its configuration from
`web/src/main/resources/application.properties`. This file is **not** versioned
(it is listed in `.gitignore` and should be treated like a `.env`), so you need
to create it before running the backend. A template is provided at
`web/src/main/resources/application.properties.example`.

### Step by step

1. **Copy the template** into the real file:

   ```bash
   cp web/src/main/resources/application.properties.example web/src/main/resources/application.properties
   ```

2. **Fill in the values.** Each entry uses the format
   `property=${ENV_VAR:default}`: Spring will use the environment variable
   `ENV_VAR` when it is defined, otherwise it falls back to the value after the
   colon. For local development the defaults already work if you run the
   databases locally; you only need to replace the placeholders marked
   `CHANGE_ME`.

   | Property | Environment variable | Description | Local default |
   |----------|----------------------|-------------|---------------|
   | `spring.datasource.url` | `SPRING_DATASOURCE_URL` | PostgreSQL JDBC URL | `jdbc:postgresql://localhost:5432/ctruco` |
   | `spring.datasource.username` | `SPRING_DATASOURCE_USERNAME` | PostgreSQL user | `postgres` |
   | `spring.datasource.password` | `SPRING_DATASOURCE_PASSWORD` | PostgreSQL password | *(set your own)* |
   | `spring.data.mongodb.uri` | `SPRING_DATA_MONGODB_URI` | MongoDB connection URI | `mongodb://rootuser:rootpass@localhost:27017/ctruco?authSource=admin` |
   | `application.jwt.secretKey` | `APPLICATION_JWT_SECRETKEY` | Long, random secret used to sign JWTs | *(set your own)* |
   | `application.jwt.tokenExpirationAfterMinutes` | — | Access token lifetime (minutes) | `3` |
   | `application.jwt.refreshTokenExpirationAfterDays` | — | Refresh token lifetime (days) | `14` |
   | `cors.frontend-url` | `CORS_FRONTEND_URL` | URL of the frontend allowed by CORS | `http://localhost:3000` |

3. **Generate a strong JWT secret** (do not reuse one that has ever been
   committed/leaked). For example:

   ```bash
   openssl rand -base64 64
   ```

   Paste the result into `application.jwt.secretKey` (or export it as
   `APPLICATION_JWT_SECRETKEY`).

4. **Point CORS at your frontend.** During local development the default
   `http://localhost:3000` is used automatically. In production (e.g. Render),
   set the environment variable `CORS_FRONTEND_URL` to your deployed frontend
   URL — for example `https://ctruco-front.onrender.com`. If the variable is not
   set, the backend falls back to `http://localhost:3000`.

5. **(Production) Prefer environment variables.** Instead of writing secrets
   into `application.properties`, define the environment variables listed above
   in your hosting panel (Render, etc.). The template already wires every
   sensitive value to an environment variable, so no file changes are needed in
   production.

6. **Run the backend** with the `WebApp` class in the `web` module (it requires
   the PostgreSQL and MongoDB databases to be reachable).

## Testing

`Domain`, `bot-spi`, and `bot-impl` were developed using TDD and, therefore, are covered by several unit tests. In case of any change, 
please apply regression tests to assure proper code behaviour.

## Developing Your Own Bot Service

One of the ideas behind **CTruco** is to design a software flexible enough to receive new implementations of bot services provided by
the community without changing a single line of code. Implementing and integrating a bot into the game is straightforward:

1. Fork the project here in GitHub;
2. Create your own subpackage inside `com` package of `bot-impl` module;
3. Create a class to implement the interface `com.bueno.spi.service.BotServiceProvider` available in the `bot-spi` module;
4. Update the `module-info.java` file to export and provide your implementation as a service:
   ```
   module your.mod.name {
       ...
       exports name.of.the.package.where.your.bot.implementation.is; //package created in item 1
       provides com.bueno.spi.service.BotServiceProvider with YourBotClass, <other bots already available>;
   }
   ```
5. In `resources` folder, access the file `META-INF/services/com.bueno.spi.service.BotServiceProvider` and append 
the fully qualified name of your bot service implementation. For example: 

   ```
   ...
   name.of.the.package.where.your.bot.implementation.is.YourBotClass 
   ```
6. Grab a coffee.

Examples on how to implement a bot service can be found [here](https://github.com/lucas-ifsp/CTruco/tree/master/bot-impl). 

To check if you have configured it right, run the `standalone/PlayWithBots` class in `console` module. Your bot service 
implementation class should be available as a bot option.

Now that everything is set, you can develop the business logic of your bot service. The `BotServiceProvider` 
has four abstract methods to be implemented: 

- `int getRaiseResponse(GameIntel intel)`: answers a point raise request in a truco hand. Return value must be the following: -1 (quit), 0 (accept), 1 (re-raise/call);
- `getMaoDeOnzeResponse(GameIntel intel)`: choose if bot plays a *mão de onze*. Returning `false` means quit hand. Returning `true` means accept and play a three points hand;
- `boolean decideIfRaises(GameIntel intel)`: choose if bot starts a point raise request.  Returning `false` means do nothing. Returning `true` means requesting a point raise;
- `CardToPlay chooseCard(GameIntel intel)`: provided the card will be played or discarded in the current round.

There are only three model classes related to the service implementation:

- `GameIntel`: describes the current state of the game, including: bot cards, open cards in the table, vira, bot score, opponent score, etc.;
- `TrucoCard`: represents a valid card in the truco game, with fields such as CardRank and CardSuit;
- `CardToPlay`: wraps a TrucoCard as a card to be played in the round or discarded.


***THE FUNNY PART:  you can develop a bot to challenge other bots proposed by the community.
If your bot is good enough, please pull request it. Do not forget to add your own 
[GNU GPLv3](https://www.gnu.org/licenses/gpl-3.0.pt-br.html) notice in the header of your class.***


## License

This software was developed for non-commercial, didactic purposes. It is provided through [GNU GPLv3](https://www.gnu.org/licenses/gpl-3.0.pt-br.html).

## Contributors

**CTruco** is designed and developed with :heart: by **Prof. Lucas Oliveira** @ Federal Institute of São Paulo (IFSP) at São Carlos. 
The bots available in the project were proposed by different authors, which are described in the bot class headers.






