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
    public BusinessRegistration register(Long userId, BusinessRegistrationRequestDTO dto) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        BusinessRegistration registration = new BusinessRegistration();
        registration.setUser(user);
        registration.setBusinessName(dto.getBusinessName());
        registration.setBusinessNumber(dto.getBusinessNumber());
        registration.setAddress(dto.getAddress());
        registration.setStatus(BusinessRegistrationStatus.PENDING);
        registration.setCreatedAt(LocalDateTime.now());

        return registrationRepo.save(registration);
    }

    @Transactional(readOnly = true)
    public List<BusinessRegistrationDTO> getMyRegistrations(Long userId) {
        List<BusinessRegistration> registrations = registrationRepo.findByUserUserId(userId);

        return registrations.stream()
                .map(reg -> BusinessRegistrationDTO.builder()
                        .id(reg.getId())
                        .businessName(reg.getBusinessName())
                        .businessNumber(reg.getBusinessNumber())
                        .address(reg.getAddress())
                        .status(reg.getStatus())
                        .createdAt(reg.getCreatedAt())
                        .userName(reg.getUser().getNickname()) //임시로 nickname 저장한것임 name으로 교체예정
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