package DataGeneration.src.java;
import java.time.LocalDate;

public class Reservation {
    int res_id;
    int cust_id;
    LocalDate in_date;
    LocalDate out_date;
    int h_id;
    int rm_type; //this is an integer so we can easier access the correct set of rooms when assigning a room at checkin!!!
    int p_id;
    int assignRoom;
    public Reservation(int res_id, int cust_id, LocalDate in_date, LocalDate out_date, int h_id, int rm_type, int p_id){
        this.res_id = res_id;
        this.cust_id = cust_id;
        this.in_date=in_date;
        this.out_date=out_date;
        this.h_id=h_id;
        this.rm_type=rm_type;
        this.p_id=p_id;
    }
}