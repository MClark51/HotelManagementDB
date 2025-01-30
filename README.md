# Hotel California Final Project
#### Author: Marco Clark

### Project Overview
The project objective was to create a Java CLI program that interfaces with an Oracle SQL Database and serve as a management portal for a hotel system. Users can login to the program and choose to interact as either a customer, front desk agent, or housekeeping. 

#### Building and Running the Project
- You will need the following to run this project:
    - an Oracle Database
    - The latest version of Java
- On line 24 of `/src/Java/Test.java` you will need to enter the connection string for your Oracle DB
- Compile the java program using `javac /src/Java/Test.java`
- Generate the Jar file with `jar cfmv Test.jar /src/Build/Manifest.txt Test.class`
- Run the project with `java -jar test.jar`
    - Enter your Oracle username and password when prompted
- Have fun testing the functionality of the program!

### Description of Functionality

#### Data
For more information about the data used and how to generate it, please visit the README in `/DataGeneration`

#### Frequent Guest Program
At data generation, all customers were created with 2000 "frequent guest points". Customers can pay for rooms with points and points are accumulated at the end of the stay based on the total cost of the stay. The current accrual rate is 10% of the total cost rounded to the nearest integer. However, the cost to pay for a room with points is 75% of the dollar value per night.

#### Payments
At check-in time, the customer checking in is paying with their oldest card on file by default. At checkout they can choose to pay with points or change the card they are using.

#### Multiple Reservations
I made a decision for this business to allow one customer to have several active reservations at different hotels. This is something I could do in the real world and (there are shady people out there that do this I think)

#### Triggers
Two of the triggers I implemented automatically set the 'status' of a room when a customer checks in or checks out. When a customer checks in and is assigned a room by the front desk agent, the trigger takes the new row of the check_in table and sets the room number for the given hotel's status to be 'occupied' in the room table. Similarly, when a new row is added to check_out, that room number in the given hotel has its status set to 'needsClean'. You may notice some functions on my oracle account, but those should be ignored.

#### Interfaces

##### Customer Interface
This is one of the meatier interfaces in terms of code and functionality. Customers start by selecting the hotel they would like to stay at from a list of all available hotels. The hotel list is numbered and the user is prompted to enter the number corresponding to the hotel they would like. For all user inputs going forward, strict checking of (a) input type and (b) that the input is acceptable (i.e the hotel number entered is a valid hotel number). Customers are then prompted to enter a check-in date, which must be in the future, and a check-out date, which must be after the check-in date. Next, they are given options for room types available and are then prompted to select one of those types. The customer is then prompted to enter their customer id. If they are a new customer, they should enter 0. At this point, the new customer will be prompted for their name, phone number, address, and credit card information. Now, the system finds the oldest card on file for the customer and then creates the reservation entry. 

##### Front Desk Interface
This interface has two main functions, checking in customers who have reservations and checking out customers who are currently checked in.
 - Check-in: the program asks the front desk agent to first select the hotel they are checking customers in at. They are then presented with the reservations scheduled to begin over the next five days. Since I randomized the dates when creating data, the program may not actually have enough data to test on a given day so I display five days worth of reservations in order to give some testable data. The desk agent then must select a reservation, and then they are presented with a list of clean rooms in the hotel that are able to be filled nad match the reserved room type. They must then select a valid room number. Then the database is updated by entering the appropriate data into the 'check_in' table and the trigger that I created sets the status of the room to occupied.
 - Check-out: the front desk agent is presented with a list of currently checked in customers, when they checked in, when they're suppsoed to check out, and what room number they are in. Upon selecting a reservation to check out, a list of payments for the checking out customer is presented and the agent must select one payment to pay with. They are also given the option to pay with points, or to enter a new credit card and pay with that. The appropriate values are then entered into the 'check_out' table and the trigger for room status sets the checked-out room status to 'needClean'.

##### Business Manager Interface
This interface has a single option, to create a new rate based on the dates that they provide. The user is prompted to select a hotel to change the rates at, a room type to assign this rate to, and then a start and end date. I then add the rate to the database and adjust accordingly.

##### Housekeeping Interface
This interface is fairly straightforward. It first asks the user to select a hotel to clean rooms at. A list of rooms that are marked as 'needClean' in their status field are then printed and the user must select a room to clean. A query is assembled and executed to update the stats of the room to clean.