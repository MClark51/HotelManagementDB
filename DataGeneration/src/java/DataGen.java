package DataGeneration.src.java;
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import javax.sound.sampled.Line;

public class DataGen {
  public static void main(String[] args) {
    String user = "";
    String pass = "";
    Scanner kb = new Scanner(System.in);
    System.out.print("Enter Oracle user: ");
    user = kb.nextLine();
    System.out.print("Enter password for " + user + ": ");
    pass = kb.nextLine();

    try (
        Connection con=DriverManager.getConnection("YOUR DATABASE CONNECTION STRING HERE",user, pass);
        Statement s=con.createStatement();
        ) {
        String q;
        int i;
        //Init arrays to use for data generation
        String[] streets = new String[]{"46 Cherry Hill Drive", "50 Augusta Court", "652 West Dr.", "8308 Ketch Harbour Dr", "7231 East Kingston St", "9449 Indian Summer Drive"};
        String[] cities = new String[]{"Reidsville", "Milwaukee", "Brick", "Branford", "Lincoln", "Morganton"};
        String[] states = new String[]{"NJ", "WI", "NC", "CT", "NE", "PA"};
        int[] zip = new int[]{68506, 06405, 28655, 07723, 53204, 19438}; 
        int numhotels = streets.length;
        int numcustomers = 37;

        /** 
         * 
         * HOTEL CREATION
         * 
         */
        System.out.println("PRE HOTEL LOOP");
        for (int h=0; h < streets.length; h++){
            //generate a phone number with length
            long phone = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
            q = "insert into hotel values (" + h + ",'" + streets[h] + "','" + cities[h] + "','" + states[h] + "'," + zip[h] + "," + phone + ")";
            i = s.executeUpdate(q);
            //System.out.println("ONE HOTEL CREATED");
        }

        System.out.println("HOTELS  CREATED");
        /** 
         * 
         * AMENITIES
         * 
        */
        
        //to start, each hotel will have a pool, free coffee, free wifi
        //odd number hotels get a gym
        //even numebr hotels get electric car chargers
        int amen_id = 1;
        for (int p=0; p < streets.length; p++){
          q = "INSERT INTO amenity VALUES (" + amen_id + "," + p + ", 'pool', 50)";
          i = s.executeUpdate(q);
          amen_id ++;
          q = "INSERT INTO amenity VALUES (" + amen_id + "," + p + ", 'Free Wifi', 0)";
          i = s.executeUpdate(q);
          amen_id ++;
          q = "INSERT INTO amenity VALUES (" + amen_id + "," + p + ", 'Free Toiletries', 0)";
          i = s.executeUpdate(q);
          amen_id ++;
          q = "INSERT INTO amenity VALUES (" + amen_id + "," + p + ", 'Free Coffee/Water', 0)";
          i = s.executeUpdate(q);
          amen_id ++;
          if (p%2 != 0){
            q = "INSERT INTO amenity VALUES (" + amen_id + "," + p + ", 'Gym', 25)";
            i = s.executeUpdate(q);
            amen_id ++;
          }
        }

        System.out.println("AMENITIES CREATED");
        
        /**
         * ROOM
         * 31 rooms per floor for 6 floors per hotel
         * room type is randomly selected each time
         */
        ArrayList<ArrayList<ArrayList<Room>>> rooms = new ArrayList<>();
        //create the empty arrays
        
        String[] rm_type = new String[] {"Single", "Double", "Triple", "Double-High", "Double-Deluxe", "Basic", "Deluxe", "Supreme"};
        
        for (int x=0;x<numhotels;x++){
          rooms.add(new ArrayList<>());
          for (int z=0;z<rm_type.length;z++){
            rooms.get(x).add(new ArrayList<Room>());
          }
        }

        for (int h = 0; h < 6; h++){
          //ArrayList<ArrayList<Room>> thisHotel = new ArrayList<>();
          for (int f = 100; f < 700; f +=100){
            for (int r = 1; r < 32; r ++){
              int rnum = f + r;
              int rmin = ThreadLocalRandom.current().nextInt(0,8);
              String ranRoomType = rm_type[rmin];
              Room curRoom = new Room(rnum, "clean", h, ranRoomType);
              rooms.get(h).get(rmin).add(curRoom);
              if (r % 6 == 0){ //just to have data for housekeeping to test
                q = "INSERT INTO room VALUES (" + rnum + "," + "'needClean'" + "," + h + ", '" + ranRoomType + "')";
              }
              else
                q = "INSERT INTO room VALUES (" + rnum + "," + "'clean'" + "," + h + ", '" + ranRoomType + "')";
              i = s.executeUpdate(q);
            }
          }
          
        }
        System.out.println("ROOMS CREATED");

        /**
         * CUSTOMER
         */

        List<String> readNames = new ArrayList<String>();//create list to store names
        BufferedReader bf = new BufferedReader(new FileReader("../datafiles/names.txt"));
        String name = bf.readLine();
        while (name != null){
          readNames.add(name);
          name = bf.readLine();
        }
        bf.close();
        String[] cnames = readNames.toArray(new String[0]);

        List<String> readAddr = new ArrayList<String>();//create list to store names
        BufferedReader bf2 = new BufferedReader(new FileReader("../datafiles/address.txt"));
        String curAdd = bf2.readLine();
        while (curAdd != null){
          System.out.println(curAdd);
          readAddr.add(curAdd);
          curAdd = bf2.readLine();
        }
        bf.close();
        bf2.close();
        String[] addr = readAddr.toArray(new String[0]);

        for (int cus = 1; cus <= cnames.length; cus++){
          long phone = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
          //q = "INSERT INTO customer VALUES(" + (cus) + ",'" + cnames[cus-1] + "','" + addr[cus-1] + "'," + phone + ", 2000)";
          q = "UPDATE customer SET name = '" + cnames[cus-1] + "', address = '" + addr[cus-1] + "' WHERE cust_id = " +  cus;
          i = s.executeUpdate(q);
        }

        System.out.println("CUSTOMERS CREATED");
        /**
         * 
         * PAYMENTS
         * 
         * I will add one payment method per customer for now, and will circle back to adding more for some users
         */
        for (int p = 1; p <= cnames.length; p++){
          //make pID off of a formula? This way PID isn't the same as cust_id
          int p_ID = p * p + 4 * p;
          //cust_id IS p
          long cardNum = (long) Math.floor(Math.random() * 9000000000000000L) + 1000000000000000L;
          long sec = (long) Math.floor(Math.random() * 900L) + 100L;
          //need to build the date datatype
          int exp_yr = ThreadLocalRandom.current().nextInt(2024, 2031);
          int exp_month = ThreadLocalRandom.current().nextInt(1,13);
          //all cards expire the first of the month
          String exp_date = exp_yr + "" + String.format("%02d",exp_month) + "01";
          q = "INSERT INTO payment VALUES (" + p_ID + "," + p + "," + cardNum + "," + sec + ", TO_DATE('" + exp_date + "','YYYYMMDD'))";
          i = s.executeUpdate(q);
        }
        System.out.println("PAYMENTS CREATED");
        

        /** 
         * 
         * COST
         * 
         */
        String[] rmtypes = new String[]{"Single", "Double", "Triple", "Double-High","Double-Deluxe", "Basic", "Deluxe", "Supreme"};
        //every two integers is the min/max for a given room type
        int[] price_ranges = new int[]{150,280,180,315,225,350,225,300,325,400,200,330,345,450,385,500};
        int p_index = 0;
        int num_types = rmtypes.length;
        for (int c = 0; c < numhotels; c++){
          //just do quarterly costs randomized for each hotel
          //set a threshold for each room type
          for (int yr = 2023; yr < 2026; yr++){
            for (int month = 1; month < 13; month++){
              //build the dates
              String start_date = yr + "" + String.format("%02d",month) + "01";
              String end_date="";
              if (month==2 && yr %4==0){
                end_date = yr + "" + String.format("%02d",month) + "29";
              }
              else if (month==2){
                end_date=yr + "" + String.format("%02d",month) + "28";
              }
              else if (month ==4 || month == 6 || month == 9 || month ==11){
                end_date=yr + "" + String.format("%02d",month) + "30";
              }
              else {
                end_date=yr + "" + String.format("%02d",month) + "31";
              }
              
              for (int t=0; t < num_types; t++){
                int thisCost = ThreadLocalRandom.current().nextInt(price_ranges[p_index],price_ranges[p_index+1]);
                p_index += 2;
                int points = (int) (thisCost * 0.75);
                q = "INSERT INTO cost VALUES (" + c + ",'" + rmtypes[t] + "'," + thisCost + "," + points + ", TO_DATE('" + start_date + "','YYYYMMDD'), TO_DATE('" + end_date + "','YYYYMMDD'))";
                i = s.executeUpdate(q);
              }
              p_index = 0;
            }
            
          }
          
        }

        /** 
         * 
         * RESERVATIONS
         * through end of 2024
         * make about 100 reservations? randomly select customers and hotels
         * 
         */
        //variables for date stuff
        LocalDate startD = LocalDate.of(2023,1,1);
        LocalDate endD = LocalDate.of(2024,12,31);

        // LocalDate startD = LocalDate.of(2023,5,5);
        // LocalDate endD = LocalDate.of(2023,5,31);


        long day = ChronoUnit.DAYS.between(startD, endD);
        
        ArrayList<Reservation> resers = new ArrayList<>();

        for (int res = 1901; res <2000; res++ ){
          int ran_cust = ThreadLocalRandom.current().nextInt(1,37);
          int p_ID = ran_cust * ran_cust + 4 * ran_cust; //this is what I used when creating payment id based off customer number
          //generate checkin
          LocalDate checkIn = startD.plusDays(ThreadLocalRandom.current().nextLong(day+1));
          //generate a rnadom number between 3 and 7. Stays must be between 3 and 8 days
          Long stay = ThreadLocalRandom.current().nextLong(3,8);
          LocalDate out = checkIn.plusDays(stay);
          int hotel_Id = ThreadLocalRandom.current().nextInt(0,numhotels);
          int rm_typ_index = ThreadLocalRandom.current().nextInt(0,7);
          String rm_typ = rmtypes[rm_typ_index];

          Reservation newRes = new Reservation(res, ran_cust, checkIn, out, hotel_Id, rm_typ_index, p_ID);
          resers.add(newRes);
          q = "INSERT INTO reservation VALUES (" + res + "," + ran_cust + ", TO_DATE('" + checkIn.toString() + "','YYYY-MM-DD'), TO_DATE('" + out.toString() + "','YYYY-MM-DD')," + hotel_Id + ",'" + rm_typ + "'," + p_ID + ")"; 
          i = s.executeUpdate(q);
        }

        /**
         * 
         * CHECKIN/OUT
         * 
         */
        //current date. Let's checkin all reseervations before today (date of execution)
        LocalDate cur = LocalDate.now();

        for (int r = 0; r < resers.size(); r++){
          //int ci_id = r * 3 + 7; //hash the checkin id
          Reservation curRes = resers.get(r);
          if (curRes.in_date.isBefore(cur)){
            //generate the checkin time for given day
            String time = ThreadLocalRandom.current().nextInt(0,23) + ":" + ThreadLocalRandom.current().nextInt(0,59) + ":" + ThreadLocalRandom.current().nextInt(0,59);
            String datetime = curRes.in_date.toString() + "/" + time;
            //need to get a room number, mark it as occupied. Also need to execute an update on the room table to change status
            int assignRoom = -1;
            ArrayList<Room> curHotType = rooms.get(curRes.h_id).get(curRes.rm_type); //gets all rooms in the reserved hotel
            for (int rl=0; rl < curHotType.size(); rl++){
              if (curHotType.get(rl).state == "clean"){
                //this room can now be reserved
                assignRoom = curHotType.get(rl).rnum;
                break; //hopefully it only breaks inner loop...
              }
            }

            //shouldnt happen
            if (assignRoom == -1){
              throw new Exception("No avail rooms");
            }

            
            q = "INSERT INTO check_in VALUES (" + curRes.cust_id + ", TO_DATE('" + datetime + "','YYYY-MM-DD/HH24:MI:SS')," + curRes.res_id + "," + assignRoom + "," + curRes.h_id + "," + curRes.p_id + ")";
            i = s.executeUpdate(q);

            //IF checkout is before TODAY then also do that.
            if (curRes.out_date.isBefore(cur)){
              String outTime = ThreadLocalRandom.current().nextInt(0,23) + ":" + ThreadLocalRandom.current().nextInt(0,59) + ":" + ThreadLocalRandom.current().nextInt(0,59);
              String outDateTime = curRes.out_date.toString() + "/" + outTime;
              q = "INSERT INTO check_out VALUES (" + curRes.cust_id + ", TO_DATE('" + outDateTime + "','YYYY-MM-DD/HH24:MI:SS')," + curRes.res_id + "," + assignRoom + "," + curRes.h_id + "," + curRes.p_id + ")";
              i = s.executeUpdate(q);
              //no need to change the state of the room since it is an immediate checkin and checkout

            }
            else {
              //change room status
              q = "UPDATE room SET state='occupied' WHERE h_id=" + curRes.h_id + " AND r_num =" + assignRoom;
              i=s.executeUpdate(q);
            }
          }
        }


      } catch(Exception e){e.printStackTrace();}
        kb.close();
  }
}