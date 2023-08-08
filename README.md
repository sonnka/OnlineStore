# Online Store
OnlineStore is a web application developed to support online shopping. 
The project focuses on providing a user-friendly interface that allows customers to explore products, make payments, create orders, and manage them effortlessly.

## Key Features
- Products

   OnlineStore has an extensive collection of products. 
   Customers can easily browse through the product catalog, search/filter by name and other fields, and effortlessly sort products based on price or name.
   With CSV import feature, adding multiple products to the store becomes a breeze, streamlining the process for extensive product lists. 
   To ensure optimal performance, service has  backend caching for the product list. 

- User Management

  Creating an account on OnlineStore is simple with a help of email confirmation.
  Once registered, customers can easily manage their personal information, view order history, and track payment details.
  Service also has flexible switching between English and Ukrainian languages.

- Administrative Control

  An initial main admin account is automatically created during service launch. This main admin can add other administrators, granting them the necessary privileges.
  Admins have the authority to manage user accounts, resend registration confirmation emails, delete users if needed, and oversee product-related activities.
  Additionally, they can create, edit, and archive subscriptions.

- Order Management

  The orders page allows customers to easily monitor their purchases. Sorting orders by creation date, price, or payment status allows customers to stay organized.
  They can add new orders, edit existing ones, make payments securely using the Stripe system, and delete orders.
  The order page offers a comprehensive summary of the selected items, total cost calculation, and includes additional information like order descriptions and delivery addresses.
  
## Technology Stack

The OnlineStore project incorporates a range of technologies to deliver its functionality:

- Front-end: HTML, CSS, JavaScript, JQuery, Bootstrap;

- Back-end: Java 17, Spring Boot (3.0.6), JPA/Hibernate;

- Database: MySQL, MongoDB;

- Documentation: Open API

- Payments : Stripe

- Caching: Spring Cache

- Others: AJAX, Liquibase, Thymeleaf , ElasticSearch, Docker, Docker Compose, Apache Server, OpenCSV, SonarLint.
