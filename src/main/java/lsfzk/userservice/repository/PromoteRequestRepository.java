package lsfzk.userservice.repository;

import lsfzk.userservice.dto.WaitingPromoteRequestDto;
import lsfzk.userservice.model.PromoteRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PromoteRequestRepository extends JpaRepository<PromoteRequest, Long> {

    @Query("""
        SELECT new lsfzk.userservice.dto.WaitingPromoteRequestDto(
            p.id,
            u.id,
            u.nickname,
            p.createdAt
        )
        FROM PromoteRequest p
        JOIN User u ON p.userId = u.id
        WHERE p.approved = false and p.adminId is null 
        ORDER BY p.createdAt DESC
    """)
    List<WaitingPromoteRequestDto> findAllWaitingRequests();

    // Reuse the constructor expression but add "WHERE r.id = :id"
    @Query("""
    SELECT new lsfzk.userservice.dto.WaitingPromoteRequestDto(
        r.id, u.id, u.nickname, r.createdAt
    )
    FROM PromoteRequest r
    JOIN User u ON r.userId = u.id
    WHERE r.id = :requestId
""")
    Optional<WaitingPromoteRequestDto> findRequestDtoById(@Param("requestId") Long id);
}
