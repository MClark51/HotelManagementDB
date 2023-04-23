import java.sql.*;
import java.text.SimpleDateFormat;
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
                    System.out.println("NEW RES NUM::: " + newResNum);
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
                System.out.println("If a returning customer, enter your customer id now. Otherwise enter 0:");
                int cID = -1;
                //int pID = -1;
                boolean newcus = false;
                while (true){
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
                                System.out.println("1Invalid date. Enter as MM-YYYY");
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
                System.out.println("Front desk not yet implemented!");
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
    static int printHotels(String user, String pass){ //prints hotels and returns VALID user choice
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
}

