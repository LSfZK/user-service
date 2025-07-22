package lsfzk.userservice.service;

import lombok.RequiredArgsConstructor;
import lsfzk.userservice.dto.BusinessRegistrationResult;
import lsfzk.userservice.enums.BusinessRegistrationStatus;
//import lsfzk.userservice.event.BusinessRegistrationEvent;
import lsfzk.events.BusinessRegistrationEvent;
import lsfzk.userservice.model.BusinessRegistration;
import lsfzk.userservice.dto.BusinessRegistrationRequestDTO;
import lsfzk.userservice.model.User;
import lsfzk.userservice.repository.BusinessRegistrationRepository;
import lsfzk.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BusinessRegistrationService {

    private final BusinessRegistrationRepository registrationRepo;
    private final UserRepository userRepo;
    private final KafkaProducerService kafkaProducerService;

    @Transactional
    public BusinessRegistrationResult register(Long userId, BusinessRegistrationRequestDTO dto, String fileName, String fileUri) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        BusinessRegistration registration = new BusinessRegistration();
        registration.setUser(user);
        registration.setBusinessName(dto.getBusinessName());
        registration.setAddress(dto.getAddress());
        registration.setStatus(BusinessRegistrationStatus.PENDING);
        registration.setImage(fileName);
        registration.setDirectory(fileUri);

        BusinessRegistration result = registrationRepo.save(registration);

        BusinessRegistrationEvent event = new BusinessRegistrationEvent(
                userId,
                result.getId(),
                dto.getUserNickname(),
                dto.getBusinessName()
        );
        kafkaProducerService.sendBusinessRegistrationEvent(event);

        return BusinessRegistrationResult.builder()
                .id(result.getId())
                .businessName(result.getBusinessName())
                .address(result.getAddress())
                .status(result.getStatus())
                .createdAt(result.getCreatedAt())
                .userName(result.getUser().getName())
                .userId(result.getUser().getEmail())
                .fileName(fileName) // Assuming fileName is the name of the uploaded business license file
                .fileUri(fileUri) // Assuming fileUri is the URL to access the uploaded file
                .build();
    }

    @Transactional(readOnly = true)
    public List<BusinessRegistrationResult> getMyRegistrations(Long userId) {
        List<BusinessRegistration> registrations = registrationRepo.findByUserId(userId);

        return registrations.stream()
                .map(reg -> BusinessRegistrationResult.builder()
                        .id(reg.getId())
                        .businessName(reg.getBusinessName())
                        .address(reg.getAddress())
                        .status(reg.getStatus())
                        .createdAt(reg.getCreatedAt())
                        .userName(reg.getUser().getName())
                        .userId(reg.getUser().getEmail())
                        .build())
                .toList();
    }

    public BusinessRegistration getBusinessRegistrationById(Long id) {
        return registrationRepo.findBusinessRegistrationById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사업자 등록입니다."));
    }

    @Transactional
    public BusinessRegistration approveBusinessRegistration(Long registrationId) {
        BusinessRegistration registration = getBusinessRegistrationById(registrationId);
        if(!registration.getStatus().equals(BusinessRegistrationStatus.PENDING)) {
            throw new IllegalArgumentException("이미 승인된 사업자 등록입니다.");
        }
        registration.setStatus(BusinessRegistrationStatus.APPROVED);
        registration.setUpdatedAt(LocalDateTime.now());
        return registration;
    }

    @Transactional
    public void approve(Long registrationId) {
        BusinessRegistration reg = registrationRepo.findById(registrationId)
                .orElseThrow(() -> new IllegalArgumentException("Registration not found"));
        reg.setStatus(BusinessRegistrationStatus.APPROVED);
        reg.setUpdatedAt(LocalDateTime.now());
    }

    @Transactional
    public void reject(Long registrationId) {
        BusinessRegistration reg = registrationRepo.findById(registrationId)
                .orElseThrow(() -> new IllegalArgumentException("Registration not found"));
        reg.setStatus(BusinessRegistrationStatus.REJECTED);
        reg.setUpdatedAt(LocalDateTime.now());
    }
}