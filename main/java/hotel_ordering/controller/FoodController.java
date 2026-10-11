package hotel_ordering.controller;

import hotel_ordering.entity.Food;
import hotel_ordering.service.FoodService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/foods")
@CrossOrigin(origins = {
    "http://localhost:5173",
    "http://localhost:5174",
    "http://localhost:5175"
})
public class FoodController {

    private final FoodService foodService;

    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    // CREATE
    @PostMapping
    public Food createFood(@RequestBody Food food) {
        return foodService.createFood(food);
    }

    // READ - all foods
    @GetMapping
    public List<Food> getAllFoods() {
        return foodService.getAllFoods();
    }

    // READ - one food
    @GetMapping("/{id}")
    public ResponseEntity<Food> getFoodById(@PathVariable Long id) {

        return foodService.getFoodById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Food> updateFood(
            @PathVariable Long id,
            @RequestBody Food food) {

        try {
            return ResponseEntity.ok(
                    foodService.updateFood(id, food)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFood(@PathVariable Long id) {

        try {
            foodService.deleteFood(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}