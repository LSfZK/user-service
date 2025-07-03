package lsfzk.userservice.repository;

import lsfzk.userservice.model.BusinessRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BusinessRegistrationRepository extends JpaRepository<BusinessRegistration, Long> {

    List<BusinessRegistration> findByUserId(Long userId);
}