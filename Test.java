import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.*;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.ArrayList;
import java.util.Date;

public class Test {
    public static void main(String[] args) {
    String user = "";
    String pass = "";
    Scanner kb = new Scanner(System.in);
    System.out.print("Enter Oracle user: ");
    user = kb.nextLine();
    System.out.print("Enter password for " + user + ": ");
    pass = kb.nextLine();

    try (Connection con=DriverManager.getConnection("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241",user, pass); Statement s=con.createStatement()) {
        String q;
        ResultSet result;
        int i;
        /** 
         * INTERFACE LIST:
         * 1) Customer reservation access
         *    - Specify city and dates
         *    - then display room types available on that date
         *    - check if customer is new or returning
         *        --assign ID if new
         *        --take other essential data
         * 
         * 2) Front-Desk Agent
         * 
         * 
         * 3) Housekeeping
         * 
         * 
         * 4) Business Manager
         */

        System.out.println("Welcome to THE Hotel California! Select which user type you would like to continue as:");
        System.out.println("1. Customer\n2. Front-Desk\n3. Housekeeping\n4. Business Manager");
        int loginChoice = 0;

        while (loginChoice > 4 || loginChoice < 1){
            System.out.print("Enter the number corresponding to your user type: ");
            loginChoice = Integer.parseInt(kb.nextLine());
        }
         
        switch (loginChoice){
            case 1:
                //start by displaying the hotels
                // System.out.println("Here are all of the available hotels: \n");
                // q = "SELECT h_id, city, street, state, zip FROM hotel";
                // result = s.executeQuery(q);
                // int hnum = 1;
                // if (!result.next()) System.out.println ("Empty result."); //need to throw exception and exit
                // else {
                //     do {
                //         System.out.println(hnum + ") " + result.getString("street") + " " + result.getString("city") + " " + result.getString("state") + " " + result.getInt("zip"));
                //         hnum += 1;
                //     }while(result.next());
                // }
                
                // //get max hotel number
                // q = "select max(h_id) as mh from hotel";
                // result = s.executeQuery(q);
                // int max_hid=0;
                // if (!result.next()) System.out.println ("Empty result.");
                // else {max_hid = result.getInt("mh");}
                
                // //ask user to select a hotel
                // System.out.print("Select a hotel number above :");
                // int userHNum = Integer.parseInt(kb.nextLine()) - 1; //subtract 1 because DB indexes hotels from 0
                // System.out.println();
                // while (userHNum > max_hid){
                //     System.out.print("Select a valid hotel number above :");
                //     userHNum = Integer.parseInt(kb.nextLine()) - 1;
                // }
                int userHNum = printHotels(user,pass);

                //CHECKIN DATE
                String inDate = "";
                String outDate = "";
                Date in = null;
                Date out = null;
                System.out.println("Enter check-in date in format MM-dd-yyyy");
                SimpleDateFormat sdfrmt = new SimpleDateFormat("MM-dd-yyyy");
                sdfrmt.setLenient(false);
                while (true){
                    if (kb.hasNext("[0-9][0-9]-[0-9][0-9]-[0-9][0-9][0-9][0-9]")){ //pattern for date
                        inDate = kb.nextLine();
                        try {
                            in = sdfrmt.parse(inDate);
                            //if we get here, it is a valid date
                            //want to check if it is before TODAY
                            if (in.before(new Date())){
                                System.out.println("Check-in cannot be today or earlier. Enter check-in date in format MM-dd-yyyy");
                                continue;
                            }
                            break;
                        }
                        catch (Exception e){
                            System.out.println("Invalid. Enter check-in date in format MM-dd-yyyy");
                        }
                    }
                    else {
                        System.out.println("Invalid format. Enter check-in date in format MM-dd-yyyy");
                        kb.nextLine(); //consume input
                    }
                }
                //CHECKOUT DATE
                System.out.println("Enter check-out date in format MM-dd-yyyy");
                while (true){
                    if (kb.hasNext("[0-9][0-9]-[0-9][0-9]-[0-9][0-9][0-9][0-9]")){ //pattern for date
                        outDate = kb.nextLine();
                        try {
                            out = sdfrmt.parse(outDate);
                            //if we get here, it is a valid date
                            //want to check if it is before checkin
                            if (out.before(in) || out.equals(in)){
                                System.out.println("Check-out cannot be before check-in. Enter check-in date in format MM-dd-yyyy");
                                continue;
                            }
                            break;
                        }
                        catch (Exception e){
                            System.out.println("Invalid. Enter check-out date in format MM-dd-yyyy");
                        }
                    }
                    else {
                        System.out.println("Invalid format. Enter check-out date in format MM-dd-yyyy");
                        kb.nextLine(); //consume input
                    }
                }
                
                //get the most recent reservation id so we can add next num
                q = "select max(res_id) as mr from reservation";
                result = s.executeQuery(q);
                int newResNum = 0;
                if (!result.next()) System.out.println ("Empty result.");
                else {  
                    newResNum = result.getInt("mr") + 1;
                }

                //display available room types

                q = "select unique rm_type as rt from room where h_id = " + userHNum;
                result = s.executeQuery(q);
                ArrayList<String> rmTypes = new ArrayList<>();
                String userRoom = "";
                if (!result.next()) System.out.println ("Empty result.");
                else {  
                    do {
                        String curString = result.getString("rt");
                        rmTypes.add(curString);
                        System.out.println(curString);
                    }while (result.next());
                    
                    while (!rmTypes.contains(userRoom)){
                        System.out.println("Enter a room type to reserve:");
                        userRoom = kb.nextLine();
                    }
                    
                }
                System.out.println("ROOM TYPE:::" + userRoom);

                //new or returning customer??
                int cID = -1;
                //int pID = -1;
                boolean newcus = false;
                while (true){
                    System.out.println("If a returning customer, enter your customer id now. Otherwise enter 0:");
                    if (!kb.hasNextInt()){
                        System.out.println("Customer id must be a number");
                        kb.nextLine();
                    }
                    else {
                        cID = kb.nextInt();
                        if (cID == 0){
                            //generate new c_ID
                            q = "select max(cust_id) as mcid from customer";
                            result = s.executeQuery(q);
                            result.next();
                            cID = result.getInt("mcid") + 1;
                            System.out.println(cID + "\n\n");
                            newcus = true;
                            
                        }
                        //check for an exisitng customer!!
                        else {
                            //need to check that customer ID exists
                            q = "SELECT * FROM customer WHERE cust_id = ?";
                            PreparedStatement st = con.prepareStatement(q);
                            st.setInt(1, cID);
                            result = st.executeQuery();
                            if (!result.next()){
                                System.out.println("Invalid customer ID. Try again.");
                                continue;
                            }
                        }
                        break;
                    }
                }
                //collect new customer data
                kb.nextLine();
                if (newcus){
                    System.out.print("Welcome new customer. Enter your name: ");
                    String newName = kb.nextLine();
                    System.out.print("Enter your address:");
                    String newAddr = kb.nextLine();
                    System.out.println("Enter phone number in form ########## with no dashes or spaces: ");
                    int pNum = -1;
                    while (true){
                        if (!kb.hasNextInt()){
                            System.out.println("Phone number must consist of numbers only");
                            kb.nextLine();
                        }
                        else {
                            pNum = kb.nextInt();
                            //COULD USE MORE VALIDATION
                            if (pNum < 1000000000){ //i.e not long enough to be valid
                                System.out.println("Not enough numbers in phone number. Enter phone number in form ########## with no dashes or spaces:");
                                continue;
                            }
                            break; 
                        }
                    }
                    q = "INSERT INTO customer VALUES(" + cID + ",'" + newName + "','" + newAddr + "'," + pNum + ", 2000)";
                    i = s.executeUpdate(q);
                    System.out.println("\nFor future reference, your customer ID is " + cID + ". KEEP TRACK OF THIS NUMBER\n");
                    //also need a payment
                    System.out.println("New customers must have 1 card on file.\nEnter a card number:");
                    long cnum = -1;
                    while (true){
                        if (!kb.hasNextLong()){
                            System.out.println("Card number must be a number of form ################");
                            kb.nextLine();
                        }
                        else {
                            cnum = kb.nextLong();
                            if (cnum < 1000000000000000L){
                                System.out.println("Invalid. Card number must be a number of form ################");
                                continue;
                            }
                            break;
                        }
                    }

                    //ASK FOR EXP
                    System.out.println("Enter expiration date:");
                    SimpleDateFormat expfrmt = new SimpleDateFormat("dd-MM-yyyy");
                    expfrmt.setLenient(false);
                    String expDate = "";
                    while (true){
                        if (kb.hasNext("[0-9][0-9]-[0-9][0-9][0-9][0-9]")){ //pattern for date
                            expDate = kb.nextLine();
                            expDate = "01-" + expDate; //first of month for expiration
                            System.out.println("Exp is ++ " + expDate);
                            try {
                                in = sdfrmt.parse(expDate);
                                //if we get here, it is a valid date
                                //want to check if it is before TODAY
                                if (in.before(new Date())){
                                    System.out.println("Date is not in the future");
                                    continue;
                                }
                                else 
                                    break;
                            }
                            catch (Exception e){
                                System.out.println("Invalid date. Enter as MM-YYYY");
                            }
                        }
                        else {
                            System.out.println("Invalid date. Enter as MM-YYYY");
                            kb.nextLine(); //consume input
                        }
                    }

                    //now need pin
                    System.out.println("Enter security PIN:");
                    int pin = -1;
                    while (true){
                        if (kb.hasNextInt()){
                            pin = kb.nextInt();
                            if (pin > 999 || pin < 0){ //cannot be more than 3 digits
                                System.out.println("PIN must be 3 digits and cannot be negative");
                                continue;
                            }
                            break;
                        }
                        else {
                            System.out.println("PIN must be a 3 digit integer");
                        }
                    }

                    //p_ID = c_ID * c_ID + 4 * c_ID (this is the formula I used when generating data)
                    int p_ID = cID * cID + 4 * cID;
                    //now insert into payment table
                    q = "INSERT INTO payment VALUES (" + p_ID + "," + cID + "," + cnum + "," + pin + ", TO_DATE('" + expDate.toString() + "','DD-MM-YYYY'))";
                    i = s.executeUpdate(q);
                }

                
                //get customer's first payment on file
                q = "select unique p_id as pd from payment where cust_id = " + cID;
                result = s.executeQuery(q);
                result.next();
                int cust_p_id = result.getInt("pd");

                //q = "INSERT INTO reservation VALUES (" + res + "," + ran_cust + ", TO_DATE('" + checkIn.toString() + "','YYYY-MM-DD'), TO_DATE('" + out.toString() + "','YYYY-MM-DD')," + hotel_Id + ",'" + rm_typ + "'," + p_ID + ")"; 
                q = "insert into reservation VALUES (" + newResNum + ","  + cID + ",TO_DATE('" + inDate + "','MM-dd-yyyy')" + ",TO_DATE('" + outDate + "','MM-dd-yyyy')," + userHNum + ",'" + userRoom + "'," + cust_p_id + ")";
                i = s.executeUpdate(q);
                System.out.println("Successfully booked reservation..");

                break;
            case 2:
                System.out.println("Welcome front desk agent!\n");
                int choice = 0;
                System.out.println("Select a hotel to begin.");
                int hNum = printHotels(user, pass);
                A:
                while (true){
                    System.out.println("Would you like to\n1. Check-in a customer\n2. Check-out a customer\n4. Exit");
                    if (kb.hasNextInt()){
                        choice = kb.nextInt();
                    }
                    else {
                        System.out.println("You must enter an integer between 1 and 4.");
                        kb.next();
                        continue A;
                    }
                    SC: //label for switch case
                    switch (choice){
                        //check in a customer
                        case 1:
                            //this is inefficient
                            LocalDate cur = LocalDate.now();
                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("YYYY-MM-dd");
                            String curDate = cur.format(dtf);
                            //!!!!!!!!!!!!!!!!!
                            //DISCLAIMER THIS IS ALL BEORE TODAY
                            //!!!!!!!!!!!!!!!!!!!

                            //select all reservations before today that haven't been checked in
                            q = "SELECT * FROM reservation WHERE in_date >= to_date('" + curDate  +"','YYYY-mm-DD') AND h_id = " + hNum + " AND res_id not in (select res_id from check_in)";
                            result = s.executeQuery(q);
                            ArrayList<Integer> resNums = new ArrayList<>();
                            if (!result.next()){
                                System.out.println("No reservations scheduled for today");
                                break SC; //break the switch case
                            }
                            else {
                                //assemble a list of available reservations
                                int cResNum;
                                do { 
                                    cResNum = result.getInt("res_id");
                                    resNums.add(cResNum);
                                    System.out.println(cResNum + " " + result.getInt("cust_id") + " " + result.getDate("in_date") + " " + result.getDate("out_date") + " " + result.getString("h_id"));
                                }while(result.next());
                            }
                            //call func to prompt user to select a reservation number
                            System.out.println("Displayed below are all scheduled reservations before and including today.");
                            int resNumSel = selectInt("Enter a reservation number to start the check-in process", resNums, kb);

                            //need to make list of rooms avail, then ask which room to assign
                            ArrayList<Integer> availRooms = new ArrayList<>();
                            q = "SELECT r_num FROM room WHERE h_id = ? and rm_type = (SELECT rm_type FROM reservation WHERE res_id = ?)";
                            PreparedStatement stat = con.prepareStatement(q);
                            stat.setInt(1, hNum);
                            stat.setInt(2, resNumSel);
                            result = stat.executeQuery();
                            if (!result.next()){
                                System.out.println("There are no rooms available today.");
                                break SC;
                            }
                            else {
                                int curRNum;
                                do {
                                    curRNum = result.getInt("r_num");
                                    availRooms.add(curRNum);
                                    System.out.println(curRNum);
                                }while (result.next());
                            }

                            //select a room using custom method
                            int roomSelect = selectInt("Select a room to check-in", availRooms, kb);

                            //need to get the customer's payment ID and customer ID. Take the earliest on file by DEFAULT
                            q = "select cust_id,p_id from payment where cust_id = (select cust_id from reservation where res_id = ?)";
                            stat = con.prepareStatement(q);
                            stat.setInt(1, resNumSel);
                            result = stat.executeQuery();
                            result.next(); //dummy line
                            int pID = result.getInt("p_id");
                            int custId = result.getInt("cust_id");
                            
                            //get current date
                            Date currDate  = new Date();

                            //get new ci_id num
                            q = "SELECT max(ci_id) as mcid from check_in";
                            result = s.executeQuery(q);
                            result.next();
                            int newCheckIn = result.getInt("mcid") + 1;

                            //now insert the values
                            q = "INSERT INTO check_in (cust_id, in_time, res_id, r_num, h_id, p_id) VALUES (?,?,?,?,?,?)";
                            stat = con.prepareStatement(q);
                            stat.setInt(1, custId); stat.setTimestamp(2, new java.sql.Timestamp(currDate.getTime())); 
                            stat.setInt(3,resNumSel); stat.setInt(4,roomSelect); stat.setInt(5,hNum); stat.setInt(6, pID);
                            stat.executeUpdate();

                            System.out.println("Successfully checked in");
                            break;
                        case 2:
                            //check out a customer.
                            //start by displaying all checkins and have the user select an id to start the checkout process
                            q = "SELECT * from reservation join check_in using (res_id) where res_id not in (select res_id from check_out) and reservation.h_id = ?"; //select reservations that were checked in, but NOT checked out.
                            stat = con.prepareStatement(q);
                            stat.setInt(1, hNum);
                            result = stat.executeQuery();
                            ArrayList<Integer> checkInIds = new ArrayList<>(); // list to hold all available checkin reservation ids
                            if (!result.next()){
                                System.out.println("No available checkins to checkout");
                            }
                            else {
                                //build a list of checkins
                                int checkInCur = -1;//this will be changed each iteration and added to a list
                                System.out.format("%-14s\t%-14s\t%-14s\t%-14s\t%-14s%n", "Reservation ID", "Customer ID", "Room Number", "Check-in", "Check-out");
                                do {
                                    checkInCur = result.getInt("res_id");
                                    System.out.format("%-14d\t%-14d\t%-14d\t%-14s\t%-14s%n", checkInCur, result.getInt("cust_id") , result.getInt("r_num") , result.getDate("in_date").toString() , result.getDate("out_date").toString());
                                    checkInIds.add(checkInCur);
                                }while (result.next());
                                System.out.println();
                            }
                            int checkOutSelect = selectInt("Select a reservation id to check out", checkInIds, kb);

                            //now prompt user to select payment
                            q = "SELECT p_id , card_num, secur, exp FROM payment WHERE cust_id in (select cust_id from reservation where res_id = ?)";
                            stat = con.prepareStatement(q);
                            stat.setInt(1, checkOutSelect);
                            result = stat.executeQuery();

                            ArrayList<Integer> paymentIDs = new ArrayList<>();
                            if (!result.next()){
                                System.out.println("No available payments for this customer");
                                //literally impossible but a good check
                            }
                            else {
                                //build list of payment ids for this customer
                                System.out.format("%-15s\t%-15s\t%-15s\t%-15s\n", "Payment ID", "Card Number", "Security", "EXP");
                                do{
                                    int thisPID = result.getInt("p_id");
                                    paymentIDs.add(thisPID);
                                    System.out.format("%-15d\t%-15d\t%-15d\t%-15s\n", thisPID, result.getInt("card_num"), result.getInt("secur"), result.getDate("exp").toString());
                                }while(result.next());
                            }

                            int pIDSelect = selectInt("Select a payment ID for this customer. Enter -1 if using rewards points", paymentIDs, kb);


                            //now calculate the cost of the stay.
                            //this requries a few queries since the rate could change during the stay
                            q = "SELECT IN_TIME from check_in where res_id = ?";
                            stat = con.prepareStatement(q);
                            stat.setInt(1, checkOutSelect);
                            result = stat.executeQuery();

                            LocalDate currentDate = LocalDate.now();
                            LocalDate checkInDate = null;
                            if (!result.next()){
                                System.out.println("this is literally impossible");
                            }
                            else{

                            }


                            break;
                        case 3: //some extra fucntionality
                            System.out.println("No\n\n");


                            break;
                        case 4:
                            System.out.println("exiting...");
                            break A;
                        default:
                            System.out.println("Invalid integer option.");
                            break;
                    }
                }


//TO_DATE('" + outDateTime + "','YYYY-MM-DD/HH24:MI:SS')




                break;
            case 3:
                System.out.println("\nSelect a hotel to perform housekeeping:");
                int houseHotelNum = printHotels(user, pass);
                //now display all rooms that are in need of cleaning for the given hotel
                q = "SELECT r_num FROM room WHERE h_id = ? AND state = 'needClean'";
                PreparedStatement stat = con.prepareStatement(q);
                stat.setInt(1, houseHotelNum);
                result = stat.executeQuery();
                ArrayList<Integer> intRooms = new ArrayList<>();
                result.next(); //dummy line methinks
                do {
                    int rn = result.getInt("r_num");
                    intRooms.add(rn);
                    System.out.println(rn);
                }while (result.next());
                

                for (Integer rm : intRooms){
                    System.out.println(rm.intValue());
                }
                int rmClean = -1;
                while (true){
                    System.out.println("\nSelect a room to clean");
                    if (kb.hasNextInt()){
                        int temp = kb.nextInt();
                        if (intRooms.contains(temp)){
                            rmClean = temp;
                            break;
                        }
                        else {
                            System.out.println("Room number is not available to be cleaned.");
                        }
                    }
                    else {
                        System.out.println("Please enter integer room numbers.");
                    }
                }
                //ok we good to do the update
                q = "UPDATE room SET state = 'clean' where r_num = ? and h_id = ?";
                stat = con.prepareStatement(q);
                stat.setInt(1, rmClean);
                stat.setInt(2, houseHotelNum);
                i = stat.executeUpdate();
                System.out.println("Room successfully marked as clean");


                break;
            case 4:
                System.out.println("Business Manager not yet implemented!");
                break;
            default:
                System.out.println("No case for associated integer");
                break;
        }
    }
    catch (SQLException e){
        //throws in the event of bad usernames/passwords
        System.out.println("Bad connection");
    }
    kb.close();
  }
    public static int printHotels(String user, String pass){ //prints hotels and returns VALID user choice
        try (Connection con=DriverManager.getConnection("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241",user, pass); Statement s=con.createStatement();) {
            String q;
            ResultSet result;
            int i;
            Scanner kb = new Scanner(System.in);

            q = "SELECT h_id, city, street, state, zip FROM hotel";
            result = s.executeQuery(q);
            int hnum = 1;
            if (!result.next()) System.out.println ("Empty result."); //need to throw exception and exit
            else {
                do {
                    System.out.println(hnum + ") " + result.getString("street") + " " + result.getString("city") + " " + result.getString("state") + " " + result.getInt("zip"));
                    hnum += 1;
                }while(result.next());
            }
            
            //get max hotel number
            q = "select max(h_id) as mh from hotel";
            result = s.executeQuery(q);
            int max_hid=0;
            if (!result.next()) System.out.println ("Empty result.");
            else {max_hid = result.getInt("mh");}
            
            //ask user to select a hotel
            System.out.print("Select a hotel number above :");
            int userHNum = Integer.parseInt(kb.nextLine()) - 1; //subtract 1 because DB indexes hotels from 0
            System.out.println();
            while (userHNum > max_hid){
                System.out.print("Select a valid hotel number above :");
                userHNum = Integer.parseInt(kb.nextLine()) - 1;
            }
            return userHNum;
        }
        catch (SQLException e){
            //throws in the event of bad usernames/passwords
            System.out.println("Bad connection");
            return -1;
        }

    }
    public static int selectInt(String message, ArrayList<Integer> list, Scanner kb){
        int selInt=-1;
        while (true){
            System.out.println(message);
            if (kb.hasNextInt()){
                selInt = kb.nextInt();
                if (!list.contains(selInt)){
                    System.out.println("Invalid selection");
                }
                else 
                    break;
            }
            else {
                System.out.println("Selected value must be an integer...");
                kb.next();
            }
        }
        return selInt;
    }
}

