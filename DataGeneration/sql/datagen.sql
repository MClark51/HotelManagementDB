--
--ROOM_TYPE DATA GENERATION
--
INSERT INTO room_type VALUES ('Single', 'Twin', 1, 1);
INSERT INTO room_type VALUES ('Double', 'Twin',2,2);
INSERT INTO room_type VALUES ('Triple', 'Full',3,3);
INSERT INTO room_type VALUES ('Double-High', 'Full',2,2);
INSERT INTO room_type VALUES ('Double-Deluxe', 'King',1,2);
INSERT INTO room_type VALUES ('Basic', 'Queen',2,4);
INSERT INTO room_type VALUES ('Deluxe', 'King',2,4);
INSERT INTO room_type VALUES ('Supreme', 'Queen',3,6);

--
--COST
-- this one is complicated
DECLARE
cursor rmt is SELECT rm_type FROM room_type;
cursor hot is SELECT h_id FROM hotel;
BEGIN
   for t in rmt
   loop
       for i in hot
       loop
           INSERT INTO cost VALUES (i, t, 5, 5000, DATE '2023-1-1', DATE '2023-3-31');
       end loop;
   end loop;
END;

--
--
--