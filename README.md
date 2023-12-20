# **Nexus**

It is a social network similar to Twitter, but with a different implementation, using elements of Web3. The interface will visually be slightly similar to that of Discord.

<a href="https://github.com/Dariagan/webapp13a/network">Our GIT history</a>


* [Phase 0](#Phase-0) 

* [Phase 1](#Phase-1) 

* [Phase 2](#Phase-2)

* [Phase 3](#Phase-3)


## Phase 0

## Entitites 
* Users 
* Tweets
* All the sub-entities generated from all of the one-to-many and many-to-many relationships

## Functionalities 
* Registration and login: The user can register and log in.
* Posting tweets: The user can post tweets, which may contain an image.
* Following: The user can follow other users, whose tweets will appear in said user's feed.
* Blocking: The user can block other users, whose tweets will be excluded from appearing in his feed.
* Post liking: The user can like posts they enjoy.
* Search for posts: The user can search for tweets via tags. 


## User permissions 
* Anon user: browse the web and search by tags.
* Logged users: all anon users can do, plus liking and reporting tweets, following users, updating own profile picture.
* Administrator: all logged users can do, plus deleting other users' posts and banning users.


## Images
* Profile picture
* Media posts 

## Graphics 

A bar chart will be used to represent: 

* Top 5 most liked tweets

## Complementary technology
* Blockchain
* Bar chart

## Algorithm or advanced query

* Query for searching by multiple tags
* Query for finding most reported tweets
* Query for finding most liked tweets

All these queries take care of excluding banned or blocked users' tweets.

# Phase 1 

## Pages designed in phase 1
Three different types of user interfaces (unauthenticated, registered and moderator) have been prototoyped per page, to make it easy to see the minor differences between them.
* Homepage: This is a static page that is responsible for presenting the application. There is a "launch app" button with which we will launch the application. 
![image](https://user-images.githubusercontent.com/80198176/218340455-5d6fe757-cc35-450a-955e-4b19e57bfdd7.png)
![image](https://user-images.githubusercontent.com/80198176/218340466-e6e287e6-8fa2-4bc4-86de-587445c83635.png)
![image](https://user-images.githubusercontent.com/80198176/218340478-aee24d6c-ca63-4219-ae84-32b11054cd43.png)
 
* Signup: This is the page where users will register. They will have to fill in the corresponding fields and register or, if they already have an account, they can head to the login page. 
![image](https://user-images.githubusercontent.com/80198176/218340784-5e431798-736b-4dbe-aacf-97eade571e74.png)

* Login: This is the page where users will log in. They will have to fill in the fields with valid credentials and log in. Or, if they do not have an account, they can access the sign up page. 
![image](https://user-images.githubusercontent.com/80198176/218340675-859a9dd2-48f2-43bb-b4e1-f6444924d425.png)

* Thread (unauthenticated): This is the view which unauthenticated users will see when clicking on a thread. 
![image](https://user-images.githubusercontent.com/80198176/218341250-2160f6ad-1267-4946-a57b-0eff54df0104.png)

* Feed (moderator): This is the feed view which users with moderator role will see. 
![image](https://user-images.githubusercontent.com/80198176/218340991-dc763338-5769-48aa-a436-5f5b9aaf870e.png)

* Profile (authenticated): This is the profile view for authenticated users.
![image](https://user-images.githubusercontent.com/80198176/218341314-80b96318-5d9d-44b6-8796-f7141a5115d1.png)


## Navigation diagram
![Navigation diagram](https://user-images.githubusercontent.com/80198176/218341414-53fe26b0-4dee-4f52-aae6-27d9028d141c.png)

[REDACTED_NAME] made 100% of the beautiful designs in Figma (fixed css too), Stefano took care of fixing all the badly exported css and html, and making the html generic with Mustache templating.

# Phase 2 

## CONFIGURATION

Follow these steps to use our application:

1. Download the [source code](https://github.com/Dariagan/webapp13a/releases/tag/Phase-2-fixed). Extract it, and open the project with an IDE adequate for Maven.
2. Download MySQL Community Server, and MySQL Community from https://dev.mysql.com/downloads/, specifically install version 8.0.32 for both products.
3. Don't alter the default configuration settings during the installation process of the products. When setting up the server configuration, leave the DB port to the default (3306), and set credentials to: "root"; "password" (no quotes).
4. Launch MySQL Workbench and launch your local DB server by clicking on the named box under MySQL Connections.
5. If asked to, enter again the previously mentioned credentials.
6. If not done already, create a new schema for the DB named "nexus" (no quotes).
7. In nexus_app/src/main/resources/application.properties, set spring.jpa.hibernate.ddl-auto=create on the first run. After successfully running the server, change the previously mentioned line to spring.jpa.hibernate.ddl-auto=update to keep your DB persistent.


## Database diagram: 
![Database diagram](https://user-images.githubusercontent.com/38651496/224676556-298982de-9e4a-442c-87a4-f234f6fa6334.jpg)

## UML class diagram:
![UML class diagram](https://user-images.githubusercontent.com/38651496/224727082-53f56eda-680a-497c-ac5f-d45678b3a308.png)


### Team members' five most representative commits: 

#### Stefano: 

|  Description | Link | 
| ------ | ------ | 
| Replaced HttpSession("user") attribute by DB query due to asynchronous user-state bugs | [Commit 1](https://github.com/Dariagan/webapp13/commit/5a225131d9172d07e898527818e381dc699be28e) | 
| Post tweets functionality | [Commit 2](https://github.com/Dariagan/webapp13/commit/3e0b526bef8502f05473b704009d373a3791d8b0) | 
| Profile pic upload | [Commit 3](https://github.com/Dariagan/webapp13/commit/41cb81cb7738a169a7c75e46331ddbf597709fdb) | 
| Tweet.Builder and User.Builder improvements | [Commit 4](https://github.com/Dariagan/webapp13/commit/25e540053a7b6103975055fa9cc88f351ab990d6) | 
| Improved my User.Builder class | [Commit 5](https://github.com/Dariagan/webapp13/commit/bbdbbfcc0ee74d675843035dc9272bd6dace0262) |
| Bar chart feature | [Commit 6](https://github.com/Dariagan/webapp13a/commit/64bbc4868a71f367292b528cd1b7708111743381) |
| Exclude banned users' tweets | [Commit 7](https://github.com/Dariagan/webapp13a/commit/cb7872e652e0848767c4a1fbfaac579d553de7f0) |
| Block users feature | [Commit 8](https://github.com/Dariagan/webapp13a/commit/2fa2df6d7113ed10aab0e46a83ee9f5046f8e779) |
| Ajax call gets tweets from users you follow if logged in | [Commit 9](https://github.com/Dariagan/webapp13a/commit/c5c21a8447cbac05b7c14e35a8100751c5f110da) |


#### [REDACTED_NAME]: 

|  Description | Link | 
| ------ | ------ | 
| Post deletion logic | [Commit 1](https://github.com/Dariagan/webapp13/commit/6d1345b29daa22bc49d680acb9bdb50c59ac217b) | 
| Did ResourcesBuilder and default set profile pic | [Commit 2](https://github.com/Dariagan/webapp13/commit/daa4cbf68ae9ec758b71c96635d825b08449d8ef) | 
| Tweet reporting endpoint and minor refactors | [Commit 3](https://github.com/Dariagan/webapp13/commit/0f4b89fa17e07a166e81077d8d47718fee749e9d) | 
| Paginated tweets endpoint and json ignore annotations  | [Commit 4](https://github.com/Dariagan/webapp13/commit/6151ddf756fd042dd61b817c2394db1079de31bc) 
| Paginated tweets front-end integration with Ajax | [Commit 5](https://github.com/Dariagan/webapp13/commit/c9cc77e58bfd5f3f0b18b2068001205c1c5bc800) | 

### Team members' top five most involved-in files: 

#### Stefano: 

I implemented all the normal controllers, and all of the fuctionality and features for users and admins (some parts were done by REDACTED_NAME too). Did a lot of refactors, implemented the great majority of methods and classes, worked on the DB model and persistence. Took care of the interactive functionality. Made all the current advanced JPQL queries. Implemented the bar chart. Made all mustache templates in the html.

Click on "(Browse History)" to see pre-rename history of files.

* [ProfileController.java](https://github.com/Dariagan/webapp13a/blob/dev/nexus_app/src/main/java/es/codeurjc/backend/controller/ProfileController.java) 
* [User.java](https://github.com/Dariagan/webapp13a/blob/dev/nexus_app/src/main/java/es/codeurjc/backend/model/User.java) 
* [UserService.java](https://github.com/Dariagan/webapp13a/blob/dev/nexus_app/src/main/java/es/codeurjc/backend/service/UserService.java) 
* [Tweet.java](https://github.com/Dariagan/webapp13a/blob/dev/nexus_app/src/main/java/es/codeurjc/backend/model/Tweet.java) 
* [SignupController.java](https://github.com/Dariagan/webapp13a/blob/dev/nexus_app/src/main/java/es/codeurjc/backend/controller/SignUpController.java) 
* [LoginController.java](https://github.com/Dariagan/webapp13a/blob/dev/nexus_app/src/main/java/es/codeurjc/backend/controller/LoginController.java) 
* [TweetInteractionController.java](https://github.com/Dariagan/webapp13a/blob/dev/nexus_app/src/main/java/es/codeurjc/backend/controller/TweetInteractionController.java) 
* [UserInteractionController.java](https://github.com/Dariagan/webapp13a/blob/dev/nexus_app/src/main/java/es/codeurjc/backend/controller/UserInteractionController.java) 
* [ChartController.java](https://github.com/Dariagan/webapp13a/blob/dev/nexus_app/src/main/java/es/codeurjc/backend/controller/ChartController.java) 

#### [REDACTED_NAME]: 

I have worked on the [underlying decentralized moderation protocol](https://files.catbox.moe/ziu756.pdf)
along with a [microservice as middleware](https://github.com/Dariagan/webapp13/blob/dev/nexus_app/src/main/java/es/codeurjc/backend/controller/TweetDeleterController.java)
for its integration. In addition, I have worked on several
changes to the system, such as the feed querying system for moderators, users and anons, the
reporting and post deletion system, the pagination prototype and extensive refactorings and fixes
to the overall system. I have achieved this by using stateless functional programming techniques
to ensure greater program safety. Also took care of null-safety sanitation.

* [FeedController.java](https://github.com/Dariagan/webapp13/blob/dev/nexus_app/src/main/java/es/codeurjc/backend/controller/FeedController.java) 
* [OptTwo.java](https://github.com/Dariagan/webapp13/blob/dev/nexus_app/src/main/java/es/codeurjc/backend/utilities/OptTwo.java)
* [TweetService.java](https://github.com/Dariagan/webapp13/blob/dev/nexus_app/src/main/java/es/codeurjc/backend/service/TweetService.java)
* [TweetDeleterController.java](https://github.com/Dariagan/webapp13/blob/8cf9cdc52ed468e2bca7c797721a10fd0ee1e129/FASE2/src/main/java/es/codeurjc/backend/controller/TweetDeleterController.java)
* [TweetReportController.java](https://github.com/Dariagan/webapp13/blob/8cf9cdc52ed468e2bca7c797721a10fd0ee1e129/FASE2/src/main/java/es/codeurjc/backend/controller/TweetReportController.java)
* [ProfileController.java](https://github.com/Dariagan/webapp13/blob/dev/nexus_app/src/main/java/es/codeurjc/backend/controller/ProfileController.java) 

# Phase 3 

## CONFIGURATION

1. Download and install Docker in your computer.
2. Execute $ docker-compose up
3. Create an account in DockerHub and execute: $ docker login
4. Then run the Docker using: $ docker run --network=host tomy014/nexus

## UPDATED UML CLASS DIAGRAM

![Captura de pantalla 2023-03-27 052257](https://user-images.githubusercontent.com/38651496/227835123-500b30e0-8b34-49c3-94c7-4087b6649945.png)

## Team members participation

### Team members' five most representative commits:

#### Stefano: 

Added banning functionality for moderators, refactored the normal Tweet and User controllers, heavily refactored and expanded the REST controller, and fixed it.
Now you can make a custom query via parameters in the url of the specific REST API call.

|  Description | Link | 
| ------ | ------ | 
| Working upload image to tweet | [Commit 1](https://github.com/Dariagan/webapp13/commit/eafa357657176ab9eda5bef323fc6b29aada4090) | 
| REST controllers refactor and fixes | [Commit 2](https://github.com/Dariagan/webapp13/commit/1131e03d64f79f94a90ccc32bec729d74ad54a9e) | 
| Added ban functionality| [Commit 3](https://github.com/Dariagan/webapp13/commit/bfe2ee41301e020230bf658fbe5102667e1522ab) | 
| Display ban in HTML | [Commit 4](https://github.com/Dariagan/webapp13/commit/626763a0ca0a657f2a6bcb68eb66ccb092e9af5e) | 
| Refactored interaction controllers | [Commit 5](https://github.com/Dariagan/webapp13/commit/ef835c237dcca1cd0cc3759d6304ca8665c0e5e0) | 
| Fixed advanced JPQL query | [Commit 6](https://github.com/Dariagan/webapp13/commit/e9a57e0780aea229e2959d5e63984d16bfe51265) | 
| Made 2 advanced JQPL queries and big FeedController refactor| [Commit 7](https://github.com/Dariagan/webapp13/commit/f38ff4b6a4a07b1ea90eade60aebc23929cb690c) | 
| Ajax call gets tweets from users you follow if logged in | [Commit 7](https://github.com/Dariagan/webapp13a/commit/c5c21a8447cbac05b7c14e35a8100751c5f110da) |

#### Gabriel: 

I've mainly worked on the creation of the docker image, its operation.
|  Description |
| ------ | 
| Created docker-compse.yml |
| Added plugins and dependencies | 


### Team members' top five most involved-in files: 

#### Stefano: 

* [UserRestController.java](https://github.com/Dariagan/webapp13/blob/dev/nexus_app/src/main/java/es/codeurjc/backend/controller/UserRestController.java)
* [TweetRestController.java](https://github.com/Dariagan/webapp13/blob/dev/nexus_app/src/main/java/es/codeurjc/backend/controller/TweetRestController.java) 
* [FeedController.java](https://github.com/Dariagan/webapp13/blob/dev/nexus_app/src/main/java/es/codeurjc/backend/controller/FeedController.java) 
* [ProfileController.java](https://github.com/Dariagan/webapp13/blob/dev/nexus_app/src/main/java/es/codeurjc/backend/controller/ProfileController.java) 
* [TweetInteractionController.java](https://github.com/Dariagan/webapp13/blob/dev/nexus_app/src/main/java/es/codeurjc/backend/controller/TweetInteractionController.java) 
* [UserInteractionController.java](https://github.com/Dariagan/webapp13/blob/dev/nexus_app/src/main/java/es/codeurjc/backend/controller/UserInteractionController.java) 

#### Gabriel: 

* [dockerfile](https://github.com/Dariagan/webapp13/blob/dev/docker/dockerfile)

# Phase 4 

video:
https://www.youtube.com/watch?v=BtNx_bVydYA&feature=youtu.be
postman:
https://documenter.getpostman.com/view/26487599/2s93zE3LFV

### Angular Frontend Diagram
![Diagrama sin título drawio](https://github.com/Dariagan/webapp13a/assets/103439723/17238a51-789d-44a7-802e-c903a3485a32)

#### Pasos para subir la aplicacion a la maquina virtual de la URJC usando Ubuntu 22.04

1. Descargar la clave privada asignada por el profesor con el nombre appWeb-13a y colocarla en la raiz del sistema (además, deberas cambiarle los permisos con el comando chmod 600 appWeb-13a)
2. Utilizar la instruccion ssh -i appWeb-13a vmuser@10.100.139.60 para conectarse a la MV mediante SSH (pedira la contraseña antes mencionada)
3. Descargar Docker en la MV siguiendo estos pasos https://docs.docker.com/engine/install/ubuntu/
4. Clonar este repositorio mediante el comando git clone https://github.com/Dariagan/webapp13a
5. Pedira usuario y contraseña, pero a la hora de introducir la contraseña, esta deberá ser un token de uso personal y no la propia contraseña de la cuenta.
6. Mediante cd, deberas colocarte en la carpeta docker.
7. Una vez en la ruta correcta, ejecuta el comando sudo docker compose up -d (de vital importancia el sudo ya que si no, no contará con permisos de lectura/escritura)
8. Una vez que ponga que la imagen se esta ejecutando, puedes acceder a la pagina mediante los enlaces https://10.100.139.60:8443/ o https://10.100.139.60:8443/new/index.html (version SPA)


