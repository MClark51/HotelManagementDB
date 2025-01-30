-- after chcking in a room, set the state of the room to 'occupied'
create or replace 
trigger roomSet after insert on check_in
referencing new as n
for each row
begin
update room set state = 'occupied' where h_id =: n.h_id and r_num =: n.r_num;
end;

-- after checking a customer out of a room, set the property of the room to 'needClean'
create or replace
trigger roomEmpty after insert on check_out
referencing new as n
for each row
begin
update room set state = 'needClean' where h_id =: n.h_id and r_num =: n.r_num;
end;

-- check if there are too many rooms of a type booked for a given range of dates
create or replace
function roomTypeOverflow(sD IN date, oD IN Date, rmtyp IN room_type.rm_type%type, hid IN number)
return varchar2 is tester varchar2(5);
startD number;
endD number;
testD date;
begin
    startD := to_number(sD);
    endD := to_number(to_char(oD));
    for cur in startD..endD loop
        testD := to_date(cur,'j');
        dbms_output.put_line(to_char(testD, 'yyyy-MM-dd'));
    end loop;
end;

