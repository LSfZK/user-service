package lsfzk.userservice.service;

import lsfzk.userservice.dto.BusinessRegistrationDTO;
import lsfzk.userservice.enums.BusinessRegistrationStatus;
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
public class BusinessRegistrationService {

    private final BusinessRegistrationRepository registrationRepo;
    private final UserRepository userRepo;

    public BusinessRegistrationService(BusinessRegistrationRepository registrationRepo, UserRepository userRepo) {
        this.registrationRepo = registrationRepo;
        this.userRepo = userRepo;
    }

    @Transactional
    public BusinessRegistrationDTO register(Long userId, BusinessRegistrationRequestDTO dto) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        BusinessRegistration registration = new BusinessRegistration();
        registration.setUser(user);
        registration.setBusinessName(dto.getBusinessName());
        registration.setAddress(dto.getAddress());
        registration.setStatus(BusinessRegistrationStatus.PENDING);
        //registration.setImage(imageFileName);
        //registration.setDirectory(uploadPath);

        BusinessRegistration saved = registrationRepo.save(registration);
        return BusinessRegistrationDTO.builder()
                .id(saved.getId())
                .businessName(saved.getBusinessName())
                .address(saved.getAddress())
                .status(saved.getStatus())
                .createdAt(saved.getCreatedAt())
                .userName(saved.getUser().getName())
                .userLoginId(saved.getUser().getLoginId())
                .build();
    }

    @Transactional(readOnly = true)
    public List<BusinessRegistrationDTO> getMyRegistrations(Long userId) {
        List<BusinessRegistration> registrations = registrationRepo.findByUserId(userId);

        return registrations.stream()
                .map(reg -> BusinessRegistrationDTO.builder()
                        .id(reg.getId())
                        .businessName(reg.getBusinessName())
                        .address(reg.getAddress())
                        .status(reg.getStatus())
                        .createdAt(reg.getCreatedAt())
                        .userName(reg.getUser().getName())
                        .userLoginId(reg.getUser().getLoginId())
                        .build())
                .toList();
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