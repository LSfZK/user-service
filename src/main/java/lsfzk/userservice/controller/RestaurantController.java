package lsfzk.userservice.controller;

import lsfzk.userservice.dto.RestaurantSummaryDTO;
import lsfzk.userservice.service.RestaurantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{id}/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping
    public ResponseEntity<?> getRestaurantsByUser(@PathVariable Long id) {
        List<RestaurantSummaryDTO> result = restaurantService.getRestaurantsByOwnerId(id);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<?> insertRestaurants(){
        return ResponseEntity.ok("사업장 등록 성공");
    }

    @PatchMapping
    public ResponseEntity<?> updateRestaurant(@PathVariable Long id){
        return ResponseEntity.ok("사업장 정보 수정");
    }

    @DeleteMapping
    public ResponseEntity<?> deleteRestaurant(@PathVariable Long id){
        return ResponseEntity.ok("사업장 삭제");
    }
}