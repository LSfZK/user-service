package lsfzk.userservice.repository;

import lsfzk.userservice.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    List<Device> findByUserId(Long userId);
    boolean existsByUserIdAndDeviceToken(Long userId, String deviceToken);
}
