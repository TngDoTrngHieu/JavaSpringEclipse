package com.th.learningenglish.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.th.learningenglish.pojo.LessonTypes;
import com.th.learningenglish.pojo.Lessons;

public interface LessonRepository extends JpaRepository<Lessons, Long> {
	List<Lessons> findByLessonType_Skill(LessonTypes.Skill skill);

	List<Lessons> findByCategory_Id(Long categoryId);

}
