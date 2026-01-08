package site.aronnax.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import site.aronnax.common.Result;
import site.aronnax.dao.FeeDAO;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final FeeDAO feeDAO;

    public DashboardController(FeeDAO feeDAO) {
        this.feeDAO = feeDAO;
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("collectionRate", feeDAO.getCollectionRate());
        stats.put("incomeDistribution", feeDAO.getIncomeDistribution());
        stats.put("arrearsByBuilding", feeDAO.getArrearsByBuilding());

        return Result.success(stats);
    }
}
