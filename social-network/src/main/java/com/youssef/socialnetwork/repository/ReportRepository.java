package com.youssef.socialnetwork.repository;

import com.youssef.socialnetwork.Enums.ReportCategory;
import com.youssef.socialnetwork.model.Post;
import com.youssef.socialnetwork.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
    long countByPostAndCategory(Post post, ReportCategory category);
}
