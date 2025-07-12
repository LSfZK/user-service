package lsfzk.userservice.controller;

import lsfzk.userservice.model.Device;
import lsfzk.userservice.repository.DeviceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class DeviceController {

    private final DeviceRepository deviceRepository;

    public DeviceController(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    /**
     * Endpoint for the client app to register its device token.
     */
    @PostMapping("/users/me/devices")
    public ResponseEntity<String> registerDevice(@RequestBody Map<String, String> payload, Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        String token = payload.get("deviceToken");

        if (token == null || token.isEmpty()) {
            return ResponseEntity.badRequest().body("Device token is required.");
        }

        // Avoid duplicate tokens
        if (!deviceRepository.existsByUserIdAndDeviceToken(userId, token)) {
            Device device = new Device();
            device.setUserId(userId);
            device.setDeviceToken(token);
            deviceRepository.save(device);
        }

        return ResponseEntity.ok("Device registered successfully.");
    }

    /**
     * An INTERNAL endpoint for other microservices to fetch device tokens.
     * This should NOT be exposed through the API Gateway.
     */
    @GetMapping("/internal/users/{userId}/devices")
    public ResponseEntity<List<String>> getDeviceTokensForUser(@PathVariable Long userId) {
        List<String> tokens = deviceRepository.findByUserId(userId)
                .stream()
                .map(Device::getDeviceToken)
                .collect(Collectors.toList());
        return ResponseEntity.ok(tokens);
    }

    /**
     * --- ADD THIS NEW ENDPOINT ---
     * Endpoint for the client app to de-register its device token upon logout.
     */
//    @PostMapping("/users/me/devices/logout")
//    public ResponseEntity<String> logoutDevice(@RequestBody Map<String, String> payload, Principal principal) {
//        Long userId = Long.parseLong(principal.getName());
//        String token = payload.get("deviceToken");
//
//        if (token == null || token.isEmpty()) {
//            return ResponseEntity.badRequest().body("Device token is required.");
//        }
//
//        // Delete the specific device token for the authenticated user
//        deviceRepository.deleteByUserIdAndDeviceToken(userId, token);
//
//        return ResponseEntity.ok("Device logged out successfully.");
//    }
}