package lsfzk.userservice.repository;

import lsfzk.userservice.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    List<Device> findByUserId(Long userId);
    boolean existsByUserIdAndDeviceToken(Long userId, String deviceToken);
    Optional<Device> findByDeviceToken(String deviceToken);
    void deleteByDeviceToken(String deviceToken);
}
