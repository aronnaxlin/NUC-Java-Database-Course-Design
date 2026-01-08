package site.aronnax.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import site.aronnax.common.Result;
import site.aronnax.service.OwnerService;

@RestController
@RequestMapping("/api/owner")
public class OwnerController {

    private final OwnerService ownerService;

    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @GetMapping("/search")
    public Result<List<Map<String, Object>>> search(
            @RequestParam(value = "keyword", defaultValue = "") String keyword) {
        return Result.success(ownerService.searchOwners(keyword));
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> getOwnerDetail(@PathVariable("id") Long id) {
        return Result.success(ownerService.getOwnerWithProperties(id));
    }

    @PostMapping("/property/transfer")
    public Result<String> transferProperty(@RequestParam("propertyId") Long propertyId,
            @RequestParam("newOwnerId") Long newOwnerId) {
        boolean success = ownerService.updatePropertyOwner(propertyId, newOwnerId);
        if (success) {
            return Result.success("产权变更成功 | Property Transfer Successful");
        }
        return Result.error("产权变更失败，请检查ID是否有效 | Transfer Failed");
    }
}
