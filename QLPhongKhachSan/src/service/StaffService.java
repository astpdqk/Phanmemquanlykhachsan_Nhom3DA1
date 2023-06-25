/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.util.List;
import respository.StaffRepo;

/**
 *
 * @author FPTSHOP
 */
public class StaffService {
    
    private StaffRepo repo;
    
    public StaffService() {
        repo = new StaffRepo();
    }
    
    public List<String> getByUser(String user, String pass) {
        if (user.equals("") || pass.equals("")) {
            return null;
        }
        return repo.selectByUser(user, pass);
    }
}
