package com.th.learningenglish.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.th.learningenglish.pojo.LessonTypes;
import com.th.learningenglish.pojo.Lessons;

public interface LessonRepository extends JpaRepository<Lessons, Long> {
	List<Lessons> findByLessonType_Skill(LessonTypes.Skill skill);

	List<Lessons> findByCategory_Id(Long categoryId);

	@Query("SELECT l FROM Lessons l JOIN FETCH l.lessonType WHERE l.id = :id")
	Optional<Lessons> findByIdWithLessonType(@Param("id") Long id);

}
