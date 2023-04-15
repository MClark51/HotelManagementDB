import java.sql.*;
import java.io.*;
import java.util.Scanner;

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

                //prompt for dates in format YYYYMMDD
                System.out.println("Enter check-in date in format YYYYMMDD");
                String in_date  = kb.nextLine();
                System.out.println("Enter check-out date in format YYYYMMDD");
                String out_date = kb.nextLine();
                
                //COME BACK AND ADD CHECKS THAT DATES ARE A) FUTURE and b) CHECK OUT IS AFTER CHECK IN
                
                //get the most recent reservation id so we can add next num
                q = "select max(res_id) as mr from reservation";
                result = s.executeQuery(q);
                int newResNum = 0;
                if (!result.next()) System.out.println ("Empty result.");
                else {  
                    newResNum = result.getInt("mr") + 1;
                }



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