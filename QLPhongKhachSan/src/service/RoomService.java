package service;

import java.util.List;
import model.Room;
import respository.RoomRepo;

public class RoomService implements IService<Room, String> {

    private RoomRepo repo;

    public RoomService() {
        repo = new RoomRepo();
    }

    public List<Room> getRoomByNumber(String number) {
        if (number.equals("") || number == null || repo.getRoomByNumber(number).isEmpty()) {
            return null;
        }
        return repo.getRoomByNumber(number);
    }

    @Override
    public String insert(Room entity) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void update(Room entity, String roomNumber) {
        repo.update(entity.getStatus(), roomNumber);
    }

    @Override
    public void delete(String id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<Room> getAll() {
        return repo.getAll();
    }

}
