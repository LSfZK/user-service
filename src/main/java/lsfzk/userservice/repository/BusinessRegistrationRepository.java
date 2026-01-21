package lsfzk.userservice.repository;

import lsfzk.userservice.enums.BusinessRegistrationStatus;
import lsfzk.userservice.model.BusinessRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BusinessRegistrationRepository extends JpaRepository<BusinessRegistration, Long> {

    List<BusinessRegistration> findByUserId(Long userId);
    List<BusinessRegistration> findByStatus(BusinessRegistrationStatus status);
    @Query("SELECT br from BusinessRegistration br JOIN FETCH br.user WHERE br.status = :status")
    List<BusinessRegistration> findByStatusWithUser(BusinessRegistrationStatus status);
    Optional<BusinessRegistration> findBusinessRegistrationById(Long registrationId);
//    Optional<BusinessRegistration> updateBusinessRegistrationById(Long registrationId);
}