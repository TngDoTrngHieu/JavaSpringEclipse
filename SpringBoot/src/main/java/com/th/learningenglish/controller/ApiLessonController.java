package com.th.learningenglish.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.th.learningenglish.dto.LessonDetailDTO;
import com.th.learningenglish.dto.ResultDTO;
import com.th.learningenglish.dto.SubmitRequest;
import com.th.learningenglish.pojo.Lessons;
import com.th.learningenglish.service.LessonsService;

@RestController
@RequestMapping("/api")
public class ApiLessonController {
	@Autowired
	private LessonsService lessonService;

	@Autowired
	private Cloudinary cloudinary;

	@GetMapping("/lessons")
	public List<Lessons> getLessons(@RequestParam Map<String, String> params) {
		return lessonService.getLessons(params);
	}

	@GetMapping("/lessons/{id}")
	public LessonDetailDTO getLesson(@PathVariable Long id) {
		return lessonService.getLessonDetail(id);
	}

	@PostMapping("/lessons/submit")
	public ResultDTO submit(@RequestBody SubmitRequest req) {
		return lessonService.submit(req);
	}

	@PostMapping(value = "/lessons", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public Lessons createLesson(
			@RequestParam String title,
			@RequestParam(required = false) String content,
			@RequestParam Long categoryId,
			@RequestParam Long lessonTypeId,
			@RequestParam(required = false) MultipartFile image) throws IOException {

		String imageUrl = null;

		if (image != null && !image.isEmpty()) {
			Map upload = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
			imageUrl = upload.get("secure_url").toString();
		}

		return lessonService.createLessonFromForm(title, content, categoryId, lessonTypeId, imageUrl);
	}

	@PutMapping(value = "/lessons/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public LessonDetailDTO updateLesson(
			@PathVariable Long id,
			@RequestParam String title,
			@RequestParam(required = false) String content,
			@RequestParam Long categoryId,
			@RequestParam Long lessonTypeId,
			@RequestParam(required = false) MultipartFile image) throws IOException {

		String imageUrl = null;
		if (image != null && !image.isEmpty()) {
			Map upload = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
			imageUrl = upload.get("secure_url").toString();
		}

		lessonService.updateLessonFromForm(id, title, content, categoryId, lessonTypeId, imageUrl);

		return lessonService.getLessonDetail(id);
	}

	@DeleteMapping("/lessons/{id}")
	public String deleteLesson(@PathVariable Long id) {
		lessonService.deleteLesson(id);
		return "Deleted";
	}
}
