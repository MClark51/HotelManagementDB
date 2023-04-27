# Hotel California Final Project
#### Author: Marco Clark

### Overarching disclaimer about functionality
To be a tab bit more realistic with my system, I am creating the front desk interface in a way such that you can only check in customers who have a reservation starting on the current day. The current day is THE DAY IN WHICH YOU ARE EXECUTING THE PROGRAM. Of course from the time that I generated my data to the time that you will be testing/grading, some reservations that were booked upon data creation will have become abandoned and there is functionality to see all reservations that were essentially "no-call/no-show" reservations. As one may expect, you can't reserve a room for a previous date and the check-out must be at least 1 day after the check-in. 

### Sources of data
(almost) all data generated on the database was from a java program that I created. It is included in this directory in a specified folder. 

I got random names and addresses for customers and hotels from randomlists.com. I copied batches of data and manually put quotes and parentheses as needed to create String arrays in Java. This wasn't entirely efficient, but it worked and allowed me to then add those Strings into my INSERT queries later on. For the room types, I wrote out each of those queries and executed them in SQL Developer since there weren't too many of them. 

To ensure that I had a memory of which room number was a certain type, I made new objects for Rooms and made an ArrayList of rooms for each hotel as I created the rooms. I then used this list later on when creating reservations and checking customers in.

### Frequent Guest Program
At data generation, all customers were created with 2000 "frequent guest points". Customers can pay for rooms with points and points are accumulated at the end of the stay based on the total cost of the stay. The current accrual rate is 10% of the total cost rounded to the nearest integer. 

## Interfaces

### Customer Interface
This is one of the meatier interfaces in terms of code and functionality. Customers start by selecting the hotel they would like to stay at from a list of all available hotels. The hotel list is numbered and the user is prompted to enter the number corresponding to the hotel they would like. For all user inputs going forward, strict checking of (a) input type and (b) that the input is acceptable (i.e the hotel number entered is a valid hotel number). Customers are then prompted to enter a check-in date, which must be in the future, and a check-out date, which must be after the check-in date. Next, they are given options for room type and are then prompted to select one of those types. The customer is then prompted to enter their customer id. If they are a new customer, they should enter 0. At this point, the new customer will be prompted for their name, phone number, address, and credit card information. At this point, the system finds the oldest card on file for the cusotmer and then creates the reservation entry. 