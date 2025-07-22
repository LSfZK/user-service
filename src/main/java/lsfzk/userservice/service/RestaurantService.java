package lsfzk.userservice.service;

import lombok.RequiredArgsConstructor;
import lsfzk.userservice.dto.RestaurantSummaryDTO;
import lsfzk.userservice.model.Restaurant;
import lsfzk.userservice.repository.RestaurantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public List<RestaurantSummaryDTO> getRestaurantsByOwnerId(Long id) {
        List<Restaurant> restaurants = restaurantRepository.findByOwnerId(id);
        return restaurants.stream()
                .map(r -> new RestaurantSummaryDTO(
                        r.getId(),
                        r.getName(),
                        r.getAddress(),
                        r.getPhone(),
                        r.getCategory(),
                        r.getAvgRating()
                ))
                .toList();
    }


}
