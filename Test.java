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

    try (Connection con=DriverManager.getConnection("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241",user, pass); Statement s=con.createStatement();) {
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
                System.out.println("Here are all of the available hotels: \n");
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

                //new or returning customer??
                System.out.println("If a returning customer, enter your customer id now. Otherwise enter 0:");
                int cID = -1;
                int pID = -1;
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
                    System.out.println("Enter a card number:");
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
                    
                }

                
                //get customer's first payment on file
                q = "select unique p_id as pd from payment where cust_id = " + cID;
                




                //now build query to insert
                // q = "insert into reservation VALUES (" + newResNum + ","  + 

                break;
            case 2:
                System.out.println("Front desk not yet implemented!");
                break;
            case 3:
                System.out.println("Housekeeping not yet implemented!");
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
}