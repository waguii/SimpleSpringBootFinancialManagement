# Financial Management System
This project is a financial management system built with Java, Spring Boot, and Maven. It allows you to manage financial accounts and transactions through a command-line interface.
## Prerequisites  
- Java 11 or higher - Maven - An IDE (e.g., IntelliJ IDEA)
## Setup
*Clone the repository:*  
```sh git clone https://github.com/yourusername/financial-management-system.git cd financial-management-system ```  
*Build the project:*  
```sh mvn clean install ```  
*Run the application:*  
```sh mvn spring-boot:run ```
## Usage  
Once the application is running, you can use the following commands to interact with the system:  
### Initialize the Database  
```sh init-bd ```  
### Add Financial Entries  
```sh add-financial-entry "CAIXA" "OUT" "02/10/2024 15:40:00" "50" add-financial-entry "CAIXA" "IN" "02/10/2024 15:41:00" "150" balance-financial-account "CAIXA" "02/10/2024 15:42:00" "500" del-financial-entry "3" add-financial-entry "CAIXA" "OUT" "03/10/2024 15:20:00" "50" add-financial-entry "CAIXA" "IN" "03/10/2024 15:21:00" "25" balance-financial-account "CAIXA" "03/10/2024 15:43:00" "400" balance-financial-account "CAIXA" "03/10/2024 15:44:00" "200" add-financial-entry "CAIXA" "OUT" "03/10/2024 15:45:00" "50" add-financial-entry "CAIXA" "IN" "03/10/2024 15:46:00" "25" add-financial-entry "CAIXA" "OUT" "02/10/2024 15:47:00" "50" balance-financial-account "CAIXA" "04/10/2024 15:48:00" "500" add-financial-entry "CAIXA" "OUT" "03/10/2024 15:49:00" "50" balance-financial-account "CAIXA" "03/10/2024 15:50:00" "400" add-financial-entry "CAIXA" "OUT" "03/10/2024 15:51:00" "40" add-financial-entry "CAIXA" "OUT" "03/10/2024 15:52:00" "30" balance-financial-account "CAIXA" "03/10/2024 15:53:00" "100" add-financial-entry "CAIXA" "OUT" "04/10/2024 15:54:00" "50" add-financial-entry "CAIXA" "IN" "04/10/2024 15:55:00" "25" add-financial-entry "CAIXA" "IN" "04/10/2024 15:56:00" "25" add-financial-entry "CAIXA" "IN" "04/10/2024 15:57:00" "25" add-financial-entry "CAIXA" "OUT" "04/10/2024 15:58:00" "50" del-financial-entry "10" balance-financial-account "CAIXA" "04/10/2024 15:59:00" "800" ``` 
## License  This project is licensed under the MIT License.