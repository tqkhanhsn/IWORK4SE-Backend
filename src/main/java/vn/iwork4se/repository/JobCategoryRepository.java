package vn.iwork4se.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.iwork4se.model.JobCategory;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobCategoryRepository extends JpaRepository<JobCategory, Long> {
    
    // Find category by name (case insensitive)
    Optional<JobCategory> findByCategoryNameIgnoreCase(String categoryName);
    
    // Find categories by name containing (case insensitive)
    Page<JobCategory> findByCategoryNameContainingIgnoreCase(String categoryName, Pageable pageable);
    
    // Find categories by description containing (case insensitive)
    Page<JobCategory> findByDescriptionContainingIgnoreCase(String description, Pageable pageable);
    
    // Search categories by keyword (name or description)
    @Query("SELECT jc FROM JobCategory jc WHERE " +
           "LOWER(jc.categoryName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(jc.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<JobCategory> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    // Find categories with job post count
    @Query("SELECT jc FROM JobCategory jc LEFT JOIN jc.jobPosts jp GROUP BY jc.id")
    Page<JobCategory> findAllWithJobPostCount(Pageable pageable);
    
    // Count job posts by category
    @Query("SELECT COUNT(jp) FROM JobPost jp WHERE jp.category.id = :categoryId")
    long countJobPostsByCategoryId(@Param("categoryId") Long categoryId);
    
    // Find categories with most job posts
    @Query("SELECT jc FROM JobCategory jc LEFT JOIN jc.jobPosts jp GROUP BY jc.id ORDER BY COUNT(jp) DESC")
    List<JobCategory> findCategoriesWithMostJobPosts(Pageable pageable);
    
    // Check if category name exists (excluding current category for update)
    @Query("SELECT COUNT(jc) > 0 FROM JobCategory jc WHERE LOWER(jc.categoryName) = LOWER(:categoryName) AND jc.id != :excludeId")
    boolean existsByCategoryNameIgnoreCaseAndIdNot(@Param("categoryName") String categoryName, @Param("excludeId") Long excludeId);
    
    // Check if category name exists
    boolean existsByCategoryNameIgnoreCase(String categoryName);
}
