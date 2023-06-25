package service;

import model.PromotionR;
import respository.PromotionRRepo;

public class PromotionRService {

    private PromotionRRepo repo;

    public PromotionRService() {
        repo = new PromotionRRepo();
    }

    public PromotionR searchPromotionR(String id, String dateEnd) {
        return repo.searchPromotionR(id,dateEnd);
    }

}
