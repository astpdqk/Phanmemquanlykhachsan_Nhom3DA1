
package service;
import java.util.ArrayList;
import respository.CheckoutRepo;
import viewModel.ThongtinCheckout;
import viewModel.DVcheckout;

public class CheckoutService {
    CheckoutRepo cr = new CheckoutRepo();
    public ArrayList<DVcheckout> GetDVcheckout(String id){
        return this.cr.GetDVcheckout(id);
    }
    public ArrayList<ThongtinCheckout> Getthongtincheckout(String id){
        return this.cr.GetthongtinCheckout(id);
    }
}
