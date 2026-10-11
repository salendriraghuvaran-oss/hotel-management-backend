package hotel_ordering.service;

import hotel_ordering.entity.Food;
import hotel_ordering.repository.FoodRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FoodService {

    private final FoodRepository foodRepository;

    public FoodService(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
    }

    // CREATE
    public Food createFood(Food food) {
        return foodRepository.save(food);
    }

    // READ - all foods
    public List<Food> getAllFoods() {
        return foodRepository.findAll();
    }

    // READ - one food
    public Optional<Food> getFoodById(Long id) {
        return foodRepository.findById(id);
    }

    // UPDATE
    public Food updateFood(Long id, Food foodDetails) {

        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Food not found"));

        food.setHotelId(foodDetails.getHotelId());
        food.setName(foodDetails.getName());
        food.setDescription(foodDetails.getDescription());
        food.setPrice(foodDetails.getPrice());
        food.setCategory(foodDetails.getCategory());
        food.setImageUrl(foodDetails.getImageUrl());
        food.setAvailable(foodDetails.getAvailable());

        return foodRepository.save(food);
    }

    // DELETE
    public void deleteFood(Long id) {
        foodRepository.deleteById(id);
    }
}