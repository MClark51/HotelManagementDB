# Hotel California Final Project
#### Author: Marco Clark

### Sources of data
(almost) all data generated on the database was from a java program that I created. It is included in this directory in a specified folder. 

I got random names and addresses for customers and hotels from randomlists.com. I copied batches of data and manually put quotes and parentheses as needed to create String arrays in Java. This wasn't entirely efficient, but it worked and allowed me to then add those Strings into my INSERT queries later on. For the room types, I wrote out each of those queries and executed them in SQL Developer since there weren't too many of them. 

To ensure that I had a memory of which room number was a certain type, I made new objects for Rooms and made an ArrayList of rooms for each hotel as I created the rooms. I then used this list later on when creating reservations and checking customers in.

### Frequent Guest Program
At data generation, all customers were created with 2000 "frequent guest points". Customers can pay for rooms with points and points are accumulated at the end of the stay based on the total cost of the stay. The current accrual rate is 10% of the total cost rounded to the nearest integer. 

### Payments
At check-in time, the customer checking in is paying with their oldest card on file by default. At checkout they can choose to pay with points or change the card they are using.

## Triggers
Two of the triggers I implemented automatically set the 'status' of a room when a customer checks in or checks out. When a customer checks in and is assigned a room by the front desk agent, the trigger takes the new row of the check_in table and sets the room number for the given hotel's status to be 'occupied' in the room table. Similarly, when a new row is added to check_out, that room number in the given hotel has its status set to 'needsClean'.
## Interfaces

### Customer Interface
This is one of the meatier interfaces in terms of code and functionality. Customers start by selecting the hotel they would like to stay at from a list of all available hotels. The hotel list is numbered and the user is prompted to enter the number corresponding to the hotel they would like. For all user inputs going forward, strict checking of (a) input type and (b) that the input is acceptable (i.e the hotel number entered is a valid hotel number). Customers are then prompted to enter a check-in date, which must be in the future, and a check-out date, which must be after the check-in date. Next, they are given options for room type and are then prompted to select one of those types. The customer is then prompted to enter their customer id. If they are a new customer, they should enter 0. At this point, the new customer will be prompted for their name, phone number, address, and credit card information. At this point, the system finds the oldest card on file for the cusotmer and then creates the reservation entry. 

### Front Desk Interface
This interface has two main functions, checking in customers who have reservations and checking out customers who are currently checked in.
 - Check-in: the program asks the front desk agent to first select the hotel they are checking customers in at. They are then presented with the reservations scheduled to begin over the next five days. Since I randomized the dates when creating data, the program may not actually have enough data to test on a given day so I display five days worth of reservations in order to give some testable data. The desk agent then must select a reservation, and then they are presented with a list of clean rooms in the hotel that are able to be filled. They must then select a valid room number. Then the database is updated by entering the appropriate data into the 'check_in' table and the trigger that I created sets the status of the room to occupied.
 - Check-out: the front desk agent is presented with a list of currently checked in customers, when they checked in, when they're suppsoed to check out, and what room number they are in. Upon selecting a reservation to check out, a list of payments for the checking out customer is presented and the agent must select one payment to pay with. They are also given the option to pay with points, or to enter a new credit card and pay with that. The appropriate values are then entered into the 'check_out' table and the trigger for room status sets the checked-out room status to 'needClean'.


##### Converting Date to LocalDate
https://www.baeldung.com/java-date-to-localdate-and-localdatetime
https://mkyong.com/java/how-to-get-current-timestamps-in-java/  -- timestamp