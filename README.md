The project of CarGo, a car rental app being a part of the University of Lodz "Programming Database Applications" course.

# CarGo - clients' rentals, manager's vehicles and admin's users management system in one providing statistics and report for the company runners

**Author:** Paweł Żurawski and Jakub Lutomski

The goal of this project is to create a car rental web application enabling users to, most importantly, rent cars, manage their rentals and have control over their account. The system also gives the manager the opportunity to manage the rentals and vehicles. Additionally, the admin can manage users. Finally, both the manager and the admin have access to the statistics and the admin has the access to the report.

In summary, the purpose of this project is to create a fully functioning car rental web app for both clients and the manager / admin.

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
cd sciezkaDoFolderuapache-maven-3.9.9
nano ~/.zshrc
source ~/.zshrc
```

Rerun your Command Line or Terminal

```bash
mvn -v
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
