package respository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Bill;
import model.Room;
import utilities.JdbcUntil;

public class RoomRepo {

    Connection conn = JdbcUntil.getConnection();

    public List<Room> getRoomByNumber(String number) {
        String sql = "select * from Room where roomNUmber = ?";
        List<Room> list = new ArrayList<>();
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, number);
            ps.execute();
            ResultSet rs = ps.getResultSet();
            while (rs.next()) {
                Room room = new Room();
                room.setId(rs.getString("id"));
                room.setStatus(rs.getString("Status"));
                room.setKindOfRoom(rs.getString("KindOfRoom"));
                room.setIdPromotion(rs.getString("idpromotion"));
                room.setCode(rs.getString("Code"));
                room.setArea(rs.getString("area"));
                room.setRoomNumber(rs.getString("RoomNumber"));
                room.setLocation(rs.getString("Location"));
                room.setPrice(rs.getString("Price"));
                list.add(room);
            }
        } catch (SQLException ex) {
        }
        return list;
    }

    public List<Room> getAll() {
        String sql = "  select * from Room order by roomNumber asc";
        List<Room> list = new ArrayList<>();

        PreparedStatement ps;
        try {
            ps = conn.prepareStatement(sql);

            ps.execute();
            ResultSet rs = ps.getResultSet();
            while (rs.next()) {
                Room room = new Room();
                room.setId(rs.getString("id"));
                room.setStatus(rs.getString("Status"));
                room.setKindOfRoom(rs.getString("KindOfRoom"));
                room.setIdPromotion(rs.getString("idpromotion"));
                room.setCode(rs.getString("Code"));
                room.setArea(rs.getString("area"));
                room.setRoomNumber(rs.getString("RoomNumber"));
                room.setLocation(rs.getString("Location"));
                room.setPrice(rs.getString("Price"));
                list.add(room);
            }
        } catch (SQLException ex) {
            Logger.getLogger(RoomRepo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public void update(String status, String numberRoom) {
        String sql = "update room set [status]=? where roomNumber =? ";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(status));
            ps.setString(2, numberRoom);
            ps.execute();
            ResultSet rs = ps.getResultSet();
        } catch (SQLException ex) {
            Logger.getLogger(RoomRepo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
