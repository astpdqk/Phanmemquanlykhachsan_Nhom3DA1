///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package Repository;
//
//import utilities.JdbcUntil;
//import ViewModel.Room_ItemViewModel;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.util.ArrayList;
//import DomainModel.roomItem;
//import ViewModel.ItemViewModel;
//import java.sql.SQLException;
//
//public class RoomItemRepo {
//
//    public ArrayList<Room_ItemViewModel> getList() {
//        ArrayList<Room_ItemViewModel> list = new ArrayList<>();
//        try {
//            Connection conn = jdbcUntil.getConnection();
//            String sql = "select status,amount from roomItem ";
//            PreparedStatement ps = conn.prepareStatement(sql);
//            ps.execute();
//
//            ResultSet rs = ps.getResultSet();
//            while (rs.next()) {
//                Room_ItemViewModel r = new Room_ItemViewModel();
//
//                r.setStatus(rs.getString(1));
//                r.setAmount(rs.getInt(2));
//                list.add(r);
//
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return list;
//    }
//
////    public Boolean save(Room_ItemViewModel r) {
////        int check = 0;
////        String sql = "insert into roomItem (itemId,roomId,status,amount) values (?, ? , ? ,? ) ";
////        try (Connection conn = jdbcUntil.getConnection();
////                PreparedStatement ps = conn.prepareStatement(sql)) {
////            ps.setObject(1, r.getStatus());
////            ps.setObject(2, r.getAmount());
////
////            return check > 0;
////        } catch (Exception e) {
////            e.getMessage();
////        }
////        return null;
////    }
//    
//    public Boolean delete(String tt) {
//        int check = 0;
//        try {
//            Connection conn =jdbcUntil.getConnection();
//            String sqlDelete = "delete from roomItem where status=? ";
//            PreparedStatement ps = conn.prepareStatement(sqlDelete);
//            ps.setString(1, tt);
//            check = ps.executeUpdate();
//            return check > 0;
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//    
//
//    public Boolean update(String tt, Room_ItemViewModel r) {
//        int check = 0;
//        try {
//            Connection conn = jdbcUntil.getConnection();
//            String sqlUpdate = "Update roomItem set amount=? where status=?  ";
//            PreparedStatement ps = conn.prepareStatement(sqlUpdate);
//            ps.setInt(1, r.getAmount());
//            ps.setString(2, tt);
//            check = ps.executeUpdate();
//            return check > 0;
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//}
