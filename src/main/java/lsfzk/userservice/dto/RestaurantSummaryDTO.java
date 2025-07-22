package lsfzk.userservice.dto;

public record RestaurantSummaryDTO(
        Long id,
        String name,
        String address,
        String phone,
        String category,
        double avgRating
) {}