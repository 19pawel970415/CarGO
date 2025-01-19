The project of CarGo, a car rental app being a part of the University of Lodz "Programming Database Applications" course.

# CarGo - clients' rentals, manager's vehicles and admin's users management system in one providing statistics and report for the company runners

**Author:** Paweł Żurawski and Jakub Lutomski

## 1. Project Objectives, technologies used and USER GUIDE

### 1.1 Objectives

The goal of this project is to create a car rental web application enabling users to, most importantly, rent cars, manage their rentals and have control over their account. The system also gives the manager the opportunity to manage the rentals and vehicles. Additionally, the admin can manage users. Finally, both the manager and the admin have access to the statistics and the admin has the access to the report.

In summary, the purpose of this project is to create a fully functioning car rental web app for both clients and the manager / admin.

### 1.2 Technologies

1. **Java**: Java is used as the primary programming language for developing the backend logic of the application. It handles server-side operations, business logic, and interacts with the database to process user requests and responses. The .java files of the project are:
   - the contoller (GeneralController.java)
   - the model classes (e.g. Car.java)
   - the db handling files (CarRepository.java)
   - the service classes (e.g. UserService.java)
   - the start-up classes (StartupTask.java)
   - the app class (CarGoApplication.java).
     
3. **Spring**: The Spring Framework is utilized for building the backend of the application. It provides features such as dependency injection, transaction management, and web application development through Spring MVC. It helps in organizing code into well-structured components and simplifies the development of enterprise applications. The .java files of the project in which the Spring is used are:
   - the contollers (GeneralController.java)
   - the service classes (e.g. UserService.java)
   - the app class (CarGoApplication.java).

5. **Hibernate and HQL**: Hibernate is used as the Object-Relational Mapping (ORM) tool to manage database interactions. It maps Java objects to database tables, simplifying CRUD operations and database queries. HQL (Hibernate Query Language) is used for querying and manipulating the database. It is employed to define and manage data structures, execute queries, and perform operations. Hibernate handles database transactions and helps in managing the persistence layer of the application efficiently. The .java files of the project in which the Hibernate is used are:
   - the model classes (e.g. Manager.java)
   - the db handling files (CarRepository.java)
   - the service classes (e.g. UserService.java)
   
6. **HTML**: HTML (HyperText Markup Language) is used to create the structure and content of the web pages. It forms the foundation of the frontend by defining elements such as forms, tables, headers, and paragraphs that make up the user interface of the application. The frontend files of the project are the files in src/main/resources/templates directory such as:
   - the main page (index.html)
   - the main tabs (e.g. about.html)
   - the subtabs (e.g. stats.html)
   - the feature tabs (e.g. gallery.html)
     
7. **CSS**: CSS (Cascading Style Sheets) is used to style and layout the HTML content. It controls the visual presentation of web pages, including colors, fonts, spacing, and positioning of elements. CSS ensures that the application has a consistent and visually appealing design across different devices. The frontend .css files of the project are the files in src/main/resources/static/css directory such as:
   - the main .css file (styles.css)
   - the bootstrap .css files (e.g. bootstrap.css)
   - the jquery .css files (e.g. jquery.fancybox.min.css)
   - the other .css files (e.g. login-style.css)
   
8. **JavaScript**: JavaScript is used to enhance the interactivity and functionality of the web pages. It handles client-side scripting for form validation, dynamic content updates, and user interactions. JavaScript improves the user experience by allowing for real-time feedback and updates without requiring a page reload. The .js files of the project are the files in src/main/resources/static/js directory such as:
   - the password validation .js file (script.js)
   - the bootstrap .js files (e.g. bootstrap.bundle.js)
   - the jquery .js files (e.g. jquery.validate.js)
   - the other .js files (e.g. modernizer.js)

## Before Running the Application

Ensure that your environment is set up correctly.

Check your Java version (21 or higher), if you have no java downloaded get it from here: https://www.oracle.com/java/technologies/downloads/

```bash
$ java -version
openjdk version "21.0.4" 2024-07-16 LTS
OpenJDK Runtime Environment Temurin-21.0.4+7 (build 21.0.4+7-LTS)
OpenJDK 64-Bit Server VM Temurin-21.0.4+7 (build 21.0.4+7-LTS, mixed mode, sharing)
```

Verify Maven installation (3.9.9 or higher), if you have no maven downloaded get it from here: https://maven.apache.org/download.cgi

```bash
$ mvn -v
Apache Maven 3.9.9 (8e8579a9e76f7d015ee5ec7bfcdc97d260186937)
Maven home: C:\maven\apache-maven-3.9.9
Java version: 21.0.4, vendor: Eclipse Adoptium, runtime: C:\Program Files\Eclipse Adoptium\jdk-21.0.4.7-hotspot
Default locale: pl_PL, platform encoding: UTF-8
OS name: "windows 11", version: "10.0", arch: "amd64", family: "windows"
```

To ensure Maven and Java are properly configured on your system, follow these steps:

1. Right-click on "This PC" (or "Computer") and select "Properties."
2. Go to "Advanced system settings."
3. In "System Properties," click on "Environment Variables."
4. Under the "Path" variable, make sure the following paths or alike are present:
   - `C:\maven\apache-maven-3.9.9\bin`
   - `%JAVA_HOME%\bin` (make sure `%JAVA_HOME%` is correctly set)

5. The `JAVA_HOME` variable should point to the correct JDK path, for example:
   - `C:\Program Files\Eclipse Adoptium\jdk-11.0.18+7-hotspot`

Ensure these paths are set correctly for the project to run without issues.

Verify MySQL Workbench version (8.0 or higher), if you don't have it installed, get it from here: https://dev.mysql.com/downloads/workbench/

## How to Run the Application

1. **Clone the repository**:
   ```bash
   git clone https://github.com/19pawel970415/PrimeNumbersCalculator.git
   ```

2. **Navigate to the project directory**:
   ```bash
   cd your_path_to_dir_with_project/PrimeNumbersCalculator
   ```

3. **Run the application** (one command):
   ```bash
   mvn spring-boot:run
   ```

4. **Access the application**:
    - Open your web browser and navigate to `http://localhost:8080/`.

## License
This project is open-source and available under the MIT License.

## Acknowledgments
- Thanks to the Spring Boot community for providing a robust framework for developing web applications.
