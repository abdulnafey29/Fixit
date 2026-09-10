package com.fixit.repository;

import com.fixit.entity.Complaint;
import com.fixit.enums.ComplaintStatus;
import com.fixit.enums.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    List<Complaint> findByStudentIdOrderByCreatedAtDesc(Long studentId);

    List<Complaint> findByAssignedTechnicianIdOrderByCreatedAtDesc(Long technicianId);

    long countByStatus(ComplaintStatus status);

    long countByPriority(Priority priority);

    long countByStudentId(Long studentId);

    long countByStudentIdAndStatus(Long studentId, ComplaintStatus status);

    long countByAssignedTechnicianId(Long technicianId);

    long countByAssignedTechnicianIdAndStatusIn(Long technicianId, List<ComplaintStatus> statuses);

    List<Complaint> findByStatus(ComplaintStatus status);

    @Query("SELECT c FROM Complaint c WHERE " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:priority IS NULL OR c.priority = :priority) AND " +
           "(:categoryId IS NULL OR c.category.id = :categoryId) AND " +
           "(:search IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.location) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY c.createdAt DESC")
    List<Complaint> filterComplaints(
            @Param("status") ComplaintStatus status,
            @Param("priority") Priority priority,
            @Param("categoryId") Long categoryId,
            @Param("search") String search
    );

    @Query("SELECT c.category.name, COUNT(c) FROM Complaint c GROUP BY c.category.name")
    List<Object[]> countComplaintsByCategory();

    @Query("SELECT c.location, COUNT(c) FROM Complaint c GROUP BY c.location")
    List<Object[]> countComplaintsByBlock();

    @Query("SELECT c.status, COUNT(c) FROM Complaint c GROUP BY c.status")
    List<Object[]> countComplaintsByStatus();
}
