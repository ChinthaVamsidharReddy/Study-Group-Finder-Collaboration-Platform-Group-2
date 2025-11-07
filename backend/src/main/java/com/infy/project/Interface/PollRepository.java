package com.infy.project.Interface;


import com.infy.project.model.*;

import com.infy.project.model.Poll;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PollRepository extends JpaRepository<Poll, Long> {
    // ✅ Spring Data JPA will auto-convert Long to int when querying
    List<Poll> findByGroupId(Long groupId);
}

