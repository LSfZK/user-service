package lsfzk.userservice.repository;

import lsfzk.userservice.model.BusinessRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BusinessRegistrationRepository extends JpaRepository<BusinessRegistration, Long> {

    List<BusinessRegistration> findByUserId(Long userId);
    Optional<BusinessRegistration> findBusinessRegistrationById(Long registrationId);
//    Optional<BusinessRegistration> updateBusinessRegistrationById(Long registrationId);
}