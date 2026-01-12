package lsfzk.userservice.controller;

import lsfzk.userservice.model.Device;
import lsfzk.userservice.repository.DeviceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class DeviceController {

    private final DeviceRepository deviceRepository;
    private static final Logger userLogger = LoggerFactory.getLogger("userLogger");

    public DeviceController(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    /**
     * Endpoint for the client app to register its device token.
     */
    @PostMapping("/users/me/devices")
    public ResponseEntity<?> registerDevice(@RequestBody Map<String, String> payload, Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        String newToken = payload.get("deviceToken");
        userLogger.info("Registering device for userId: {}, token: {}", userId, newToken);

        if (newToken == null || newToken.isEmpty()) {
            return ResponseEntity.badRequest().body("Device token is required.");
        }

        // 1. Check if this token already exists in DB
        Optional<Device> existingToken = deviceRepository.findByDeviceToken(newToken);

        if (existingToken.isPresent()) {
            Device token = existingToken.get();

            // 2. VETERAN MOVE: Check for "Shared Device" Scenario
            if (!token.getUserId().equals(userId)) {
                // The token exists but belongs to SOMEONE ELSE.
                // This means a new user logged into an old device.
                // We must "Steal" the token so the old user stops getting notis here.
                userLogger.info("Reassigning token {} from User {} to User {}",
                        newToken, token.getUserId(), userId);
                token.setUserId(userId);
            }

            // 3. Update timestamp (Heartbeat)
            token.setLastUpdated(LocalDateTime.now());
            deviceRepository.save(token);

        } else {
            // 4. Brand new device for this app
            Device token = new Device();
            token.setDeviceToken(newToken);
            token.setUserId(userId);
            token.setLastUpdated(LocalDateTime.now());
            deviceRepository.save(token);
        }

//        // Avoid duplicate tokens
//        if (!deviceRepository.existsByUserIdAndDeviceToken(userId, token)) {
//            Device device = new Device();
//            device.setUserId(userId);
//            device.setDeviceToken(token);
//            userLogger.info("Saving new device: {}", device);
//            deviceRepository.save(device);
//        }

        return ResponseEntity.ok(Map.of("message", "Device registered successfully."));
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
     * Endpoint for delete FCM token upon logout
     **/
    @DeleteMapping("/users/me/devices")
    @Transactional
    public ResponseEntity<?> deleteDevice(@RequestBody Map<String, String> payload, Principal principal) {
        deviceRepository.deleteByDeviceToken(payload.get("deviceToken"));
        return ResponseEntity.ok(Map.of("message", "Token deleted successfully."));
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