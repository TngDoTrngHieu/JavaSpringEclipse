package com.th.learningenglish.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.th.learningenglish.dto.LessonDetailDTO;
import com.th.learningenglish.dto.LessonUpdateDTO;
import com.th.learningenglish.dto.ResultDTO;
import com.th.learningenglish.dto.SectionDTO;
import com.th.learningenglish.dto.SectionUpdateDTO;
import com.th.learningenglish.dto.SubmitRequest;
import com.th.learningenglish.pojo.Categories;
import com.th.learningenglish.pojo.LessonTypes;
import com.th.learningenglish.pojo.Lessons;
import com.th.learningenglish.pojo.SectionTypes;
import com.th.learningenglish.pojo.Sections;
import com.th.learningenglish.repository.CategoryRepository;
import com.th.learningenglish.repository.LessonRepository;
import com.th.learningenglish.repository.LessonTypeRepository;
import com.th.learningenglish.repository.SectionRepository;
import com.th.learningenglish.repository.SectionTypeRepository;

@Service
public class LessonsService {

	private static final Logger log = LoggerFactory.getLogger(LessonsService.class);

	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private LessonRepository lessonRepo;

	@Autowired
	private SectionRepository sectionRepo;

	@Autowired
	private SectionTypeRepository sectionTypeRepo;

	@Autowired
	private CategoryRepository categoryRepo;

	@Autowired
	private LessonTypeRepository lessonTypeRepo;

	public List<Lessons> findAllLesson() {
		return lessonRepo.findAll();
	}

	@Transactional(readOnly = true)
	public List<Lessons> getLessons(Map<String, String> params) {
		if (params == null || params.isEmpty()) {
			return lessonRepo.findAll();
		}

		String skill = params.get("skill");
		String categoryIdStr = params.get("categoryId");

		if (StringUtils.hasText(skill)) {
			String normalized = skill.trim().toUpperCase();
			try {
				LessonTypes.Skill skillEnum = LessonTypes.Skill.valueOf(normalized);
				return lessonRepo.findByLessonType_Skill(skillEnum);
			} catch (IllegalArgumentException ex) {
				throw new IllegalArgumentException("Invalid skill parameter: '" + skill
						+ "'. Expected one of: READING, LISTENING, WRITING, SPEAKING", ex);
			}
		}

		if (StringUtils.hasText(categoryIdStr)) {
			Long categoryId = parseCategoryId(categoryIdStr, "query param categoryId");
			return lessonRepo.findByCategory_Id(categoryId);
		}

		return lessonRepo.findAll();
	}

	@Transactional(readOnly = true)
	public Lessons getLessonById(Long id) {
		validateLessonId(id, "getLessonById");
		return lessonRepo.findById(id).orElseThrow(() -> new RuntimeException("Lesson not found for id=" + id));
	}

	@Transactional(readOnly = true)
	public Lessons getLessonByIdWithLessonType(Long id) {
		validateLessonId(id, "getLessonByIdWithLessonType");
		return lessonRepo.findByIdWithLessonType(id)
				.orElseThrow(() -> new RuntimeException("Lesson not found for id=" + id));
	}

	@Transactional(readOnly = true)
	public LessonDetailDTO getLessonDetail(Long lessonId) {
		validateLessonId(lessonId, "getLessonDetail");
		Lessons lesson = lessonRepo.findById(lessonId)
				.orElseThrow(() -> new RuntimeException("Lesson not found for id=" + lessonId));

		List<Sections> sections = sectionRepo.findByLesson_IdOrderByPositionAsc(lessonId);
		if (sections == null) {
			sections = Collections.emptyList();
		}

		return buildLessonDetailDto(lesson, sections);
	}

	@Transactional(readOnly = true)
	public ResultDTO submit(SubmitRequest req) {
		if (req == null) {
			throw new IllegalArgumentException("SubmitRequest must not be null");
		}
		validateLessonId(req.getLessonId(), "submit");

		List<Sections> sections = sectionRepo.findByLesson_IdOrderByPositionAsc(req.getLessonId());
		if (sections == null) {
			sections = Collections.emptyList();
		}

		int score = 0;
		int total = 0;

		for (Sections s : sections) {
			if (s == null) {
				continue;
			}
			if (s.getCorrectAnswer() != null) {
				total++;

				String userAnswer = req.getAnswers() != null ? req.getAnswers().get(String.valueOf(s.getId())) : null;

				if (Objects.equals(s.getCorrectAnswer(), userAnswer)) {
					score++;
				}
			}
		}

		return new ResultDTO(score, total);
	}

	@Transactional
	public Lessons createLesson(Lessons lesson) {
		if (lesson == null) {
			throw new IllegalArgumentException("Lesson entity must not be null");
		}
		return lessonRepo.save(lesson);
	}

	@Transactional
	public Lessons createLessonFromForm(String title, String content, Long categoryId, Long lessonTypeId, String imageUrl) {
		validatePositiveId(categoryId, "categoryId");
		validatePositiveId(lessonTypeId, "lessonTypeId");
		Categories category = categoryRepo.findById(categoryId)
				.orElseThrow(() -> new RuntimeException("Category not found for categoryId=" + categoryId));
		LessonTypes lessonType = lessonTypeRepo.findById(lessonTypeId)
				.orElseThrow(() -> new RuntimeException("Lesson type not found for lessonTypeId=" + lessonTypeId));

		Lessons lesson = new Lessons();
		lesson.setTitle(title);
		lesson.setContent(content);
		lesson.setImageUrl(imageUrl);
		lesson.setCategory(category);
		lesson.setLessonType(lessonType);
		return lessonRepo.save(lesson);
	}

	@Transactional
	public Lessons updateLesson(Long lessonId, LessonUpdateDTO dto) {
		validateLessonId(lessonId, "updateLesson");
		if (dto == null) {
			throw new IllegalArgumentException("LessonUpdateDTO must not be null for lessonId=" + lessonId);
		}

		Lessons lesson = lessonRepo.findById(lessonId)
				.orElseThrow(() -> new RuntimeException("Lesson not found for id=" + lessonId));

		applyLessonMetadata(lesson, dto);
		lessonRepo.save(lesson);

		if (dto.getSections() == null) {
			return lesson;
		}

		syncSectionsWithPayload(lessonId, lesson, dto.getSections());

		return lessonRepo.findById(lessonId)
				.orElseThrow(() -> new RuntimeException("Lesson disappeared after update for id=" + lessonId));
	}

	@Transactional
	public Lessons updateLessonEntity(Long id, Lessons lesson) {
		validateLessonId(id, "updateLessonEntity");
		if (lesson == null) {
			throw new IllegalArgumentException("Lesson payload must not be null for id=" + id);
		}
		Lessons l = lessonRepo.findById(id).orElseThrow(() -> new RuntimeException("Lesson not found for id=" + id));

		l.setTitle(lesson.getTitle());
		l.setContent(lesson.getContent());
		l.setImageUrl(lesson.getImageUrl());
		l.setCategory(lesson.getCategory());
		l.setLessonType(lesson.getLessonType());

		return lessonRepo.save(l);
	}

	@Transactional
	public Lessons updateLessonFromForm(Long id, String title, String content, Long categoryId, Long lessonTypeId,
			String imageUrlOrNull) {
		validateLessonId(id, "updateLessonFromForm");
		validatePositiveId(categoryId, "categoryId");
		validatePositiveId(lessonTypeId, "lessonTypeId");

		Lessons lesson = lessonRepo.findById(id).orElseThrow(() -> new RuntimeException("Lesson not found for id=" + id));
		Categories category = categoryRepo.findById(categoryId)
				.orElseThrow(() -> new RuntimeException("Category not found for categoryId=" + categoryId));
		LessonTypes lessonType = lessonTypeRepo.findById(lessonTypeId)
				.orElseThrow(() -> new RuntimeException("Lesson type not found for lessonTypeId=" + lessonTypeId));

		lesson.setTitle(title);
		lesson.setContent(content);
		lesson.setCategory(category);
		lesson.setLessonType(lessonType);
		if (imageUrlOrNull != null) {
			lesson.setImageUrl(imageUrlOrNull);
		}
		return lessonRepo.save(lesson);
	}

	@Transactional
	public void deleteLesson(Long id) {
		validateLessonId(id, "deleteLesson");
		if (!lessonRepo.existsById(id)) {
			throw new RuntimeException("Cannot delete: lesson not found for id=" + id);
		}
		lessonRepo.deleteById(id);
	}

	private void applyLessonMetadata(Lessons lesson, LessonUpdateDTO dto) {
		if (dto.getTitle() != null) {
			lesson.setTitle(dto.getTitle());
		}
		if (dto.getImageUrl() != null) {
			lesson.setImageUrl(dto.getImageUrl());
		}
		if (dto.getContent() != null) {
			lesson.setContent(dto.getContent());
		}
		if (dto.getCategoryId() != null) {
			Long cid = dto.getCategoryId();
			validatePositiveId(cid, "categoryId");
			Categories cat = categoryRepo.findById(cid)
					.orElseThrow(() -> new RuntimeException("Category not found for categoryId=" + cid));
			lesson.setCategory(cat);
		}
		if (dto.getLessonTypeId() != null) {
			Long ltid = dto.getLessonTypeId();
			validatePositiveId(ltid, "lessonTypeId");
			LessonTypes lt = lessonTypeRepo.findById(ltid)
					.orElseThrow(() -> new RuntimeException("Lesson type not found for lessonTypeId=" + ltid));
			lesson.setLessonType(lt);
		}
	}

	private void syncSectionsWithPayload(Long lessonId, Lessons lesson, List<SectionUpdateDTO> incoming) {
		if (incoming == null) {
			return;
		}

		List<Sections> oldSections = sectionRepo.findByLesson_IdOrderByPositionAsc(lessonId);
		if (oldSections == null) {
			oldSections = new ArrayList<>();
		}

		Map<Long, Sections> oldById = oldSections.stream().filter(s -> s != null && s.getId() != null)
				.collect(Collectors.toMap(Sections::getId, s -> s, (a, b) -> {
					log.warn("Duplicate section id in DB for lessonId={}: keeping first id={}", lessonId, a.getId());
					return a;
				}));

		List<Sections> merged = mergeIncomingSectionsIntoEntities(lesson, incoming, oldById);

		List<Long> keptIds = merged.stream().map(Sections::getId).filter(Objects::nonNull).toList();

		deleteSectionsNotKept(oldSections, keptIds);
		sectionRepo.flush();

		applyTemporaryPositions(merged);
		sectionRepo.saveAll(merged);
		sectionRepo.flush();

		restorePositionsFromPayload(merged, incoming);
		sectionRepo.saveAll(merged);
	}

	private List<Sections> mergeIncomingSectionsIntoEntities(Lessons lesson, List<SectionUpdateDTO> incoming,
			Map<Long, Sections> oldById) {

		List<Sections> out = new ArrayList<>();
		int index = 0;
		for (SectionUpdateDTO s : incoming) {
			index++;
			if (s == null) {
				throw new IllegalArgumentException("Section at index " + index + " must not be null");
			}
			if (s.getSectionTypeId() == null) {
				throw new IllegalArgumentException("sectionTypeId is required for each section (lessonId="
						+ lesson.getId() + ", index=" + index + ")");
			}
			validatePositiveId(s.getSectionTypeId(), "sectionTypeId");

			SectionTypes st = sectionTypeRepo.findById(s.getSectionTypeId()).orElseThrow(
					() -> new RuntimeException("Section type not found for sectionTypeId=" + s.getSectionTypeId()));

			Sections entity;
			if (s.getId() != null && oldById.containsKey(s.getId())) {
				entity = oldById.get(s.getId());
			} else {
				entity = new Sections();
				entity.setLesson(lesson);
			}

			entity.setSectionType(st);
			entity.setPosition(s.getPosition() != null ? s.getPosition() : 0);
			entity.setContent(wrapJsonField(s.getContent(), lesson.getId(), s.getId()));
			entity.setQuestion(wrapJsonField(s.getQuestion(), lesson.getId(), s.getId()));
			if (s.getOptions() != null && !s.getOptions().isEmpty()) {
				entity.setOptions(toJsonArray(s.getOptions(), lesson.getId(), s.getId()));
			} else {
				entity.setOptions(null);
			}
			entity.setAnswer(wrapJsonField(s.getAnswer(), lesson.getId(), s.getId()));
			entity.setCorrectAnswer(toCorrectAnswerJson(s.getCorrectAnswer(), lesson.getId(), s.getId()));

			out.add(entity);
		}
		return out;
	}

	private void deleteSectionsNotKept(List<Sections> oldSections, List<Long> keptIds) {
		for (Sections old : oldSections) {
			if (old == null || old.getId() == null) {
				continue;
			}
			if (!keptIds.contains(old.getId())) {
				log.debug("Deleting section id={} (not present in update payload)", old.getId());
				sectionRepo.delete(old);
			}
		}
	}

	private void applyTemporaryPositions(List<Sections> merged) {
		int tmp = 100_000;
		for (Sections e : merged) {
			e.setPosition(tmp++);
		}
	}

	private void restorePositionsFromPayload(List<Sections> merged, List<SectionUpdateDTO> incoming) {
		for (int i = 0; i < merged.size(); i++) {
			SectionUpdateDTO su = incoming.get(i);
			int pos = su.getPosition() != null ? su.getPosition() : 0;
			merged.get(i).setPosition(pos);
		}
	}

	private LessonDetailDTO buildLessonDetailDto(Lessons lesson, List<Sections> sections) {
		LessonDetailDTO dto = new LessonDetailDTO();
		dto.setId(lesson.getId());
		dto.setTitle(lesson.getTitle());

		LessonTypes lt = lesson.getLessonType();
		if (lt == null) {
			throw new IllegalStateException("Lesson id=" + lesson.getId() + " has no lessonType (data inconsistent)");
		}
		dto.setSkill(lt.getSkill() != null ? lt.getSkill().name() : null);

		dto.setImageUrl(lesson.getImageUrl());
		dto.setContent(lesson.getContent());

		Categories cat = lesson.getCategory();
		if (cat != null) {
			dto.setCategoryId(cat.getId());
			dto.setCategoryName(cat.getName());
		}

		dto.setLessonTypeId(lt.getId());
		dto.setLessonTypeName(lt.getName());

		dto.setSections(mapSectionsToDtos(lesson.getId(), sections));
		return dto;
	}

	private List<SectionDTO> mapSectionsToDtos(Long lessonId, List<Sections> sections) {
		List<SectionDTO> sectionDTOs = new ArrayList<>();
		for (Sections s : sections) {
			if (s == null) {
				log.warn("Null section row skipped for lessonId={}", lessonId);
				continue;
			}
			SectionTypes st = s.getSectionType();
			if (st == null) {
				throw new IllegalStateException(
						"Section id=" + s.getId() + " (lessonId=" + lessonId + ") has no sectionType");
			}

			SectionDTO sd = new SectionDTO();
			sd.setId(s.getId());
			sd.setType(st.getName());
			sd.setPosition(s.getPosition());
			sd.setContent(unwrapJsonTextColumn(s.getContent(), s.getId()));
			sd.setQuestion(unwrapJsonTextColumn(s.getQuestion(), s.getId()));
			sd.setOptions(parseOptionsForDto(s.getOptions(), s.getId()));

			sectionDTOs.add(sd);
		}
		return sectionDTOs;
	}

	private static void validateLessonId(Long lessonId, String context) {
		if (lessonId == null) {
			throw new IllegalArgumentException("lessonId must not be null (" + context + ")");
		}
		if (lessonId <= 0) {
			throw new IllegalArgumentException("lessonId must be positive, got " + lessonId + " (" + context + ")");
		}
	}

	private static void validatePositiveId(Long id, String fieldName) {
		if (id == null || id <= 0) {
			throw new IllegalArgumentException(fieldName + " must be a positive number, got: " + id);
		}
	}

	private static Long parseCategoryId(String raw, String context) {
		if (!StringUtils.hasText(raw)) {
			throw new IllegalArgumentException("categoryId string is blank (" + context + ")");
		}
		String trimmed = raw.trim();
		try {
			long v = Long.parseLong(trimmed);
			if (v <= 0) {
				throw new IllegalArgumentException("categoryId must be positive, got " + v + " (" + context + ")");
			}
			return v;
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException(
					"Invalid categoryId value '" + trimmed + "' cannot be parsed as Long (" + context + ")", ex);
		}
	}

	private String toJsonArray(List<String> options, Long lessonId, Long sectionId) {
		try {
			return objectMapper.writeValueAsString(options);
		} catch (Exception e) {
			log.error("Failed to serialize options for lessonId={}, sectionId={}: {}", lessonId, sectionId,
					e.getMessage());
			throw new RuntimeException("Cannot serialize options for lessonId=" + lessonId + ", sectionId=" + sectionId,
					e);
		}
	}

	private String wrapJsonField(String raw, Long lessonId, Long sectionId) {
		if (raw == null || raw.isBlank()) {
			return null;
		}
		String t = raw.trim();
		try {
			JsonNode n = objectMapper.readTree(t);
			if (n.isObject() || n.isArray()) {
				return t;
			}
		} catch (Exception e) {
			log.debug("wrapJsonField: not JSON object/array, wrapping as text (lessonId={}, sectionId={}): {}",
					lessonId, sectionId, e.getMessage());
		}
		try {
			ObjectNode o = objectMapper.createObjectNode();
			o.put("text", raw);
			return objectMapper.writeValueAsString(o);
		} catch (Exception e) {
			log.warn("wrapJsonField failed for lessonId={}, sectionId={}: {}", lessonId, sectionId, e.getMessage());
			return raw;
		}
	}

	private String toCorrectAnswerJson(String raw, Long lessonId, Long sectionId) {
		if (raw == null || raw.isBlank()) {
			return null;
		}
		String t = raw.trim();
		if (t.startsWith("{") || t.startsWith("[")) {
			try {
				objectMapper.readTree(t);
				return t;
			} catch (Exception e) {
				log.warn("toCorrectAnswerJson: invalid JSON for lessonId={}, sectionId={}: {}", lessonId, sectionId,
						e.getMessage());
			}
		}
		try {
			ObjectNode o = objectMapper.createObjectNode();
			o.put("value", t);
			return objectMapper.writeValueAsString(o);
		} catch (Exception e) {
			log.warn("toCorrectAnswerJson: fallback for lessonId={}, sectionId={}: {}", lessonId, sectionId,
					e.getMessage());
			return t;
		}
	}

	private String unwrapJsonTextColumn(String json, Long sectionId) {
		if (json == null || json.isBlank()) {
			return null;
		}
		try {
			JsonNode n = objectMapper.readTree(json.trim());
			if (n.isObject() && n.has("text")) {
				return n.get("text").asText();
			}
			if (n.isTextual()) {
				return n.asText();
			}
			return objectMapper.writeValueAsString(n);
		} catch (Exception e) {
			log.debug("unwrapJsonTextColumn: returning raw string for sectionId={}: {}", sectionId, e.getMessage());
			return json;
		}
	}

	private List<String> parseOptionsForDto(String json, Long sectionId) {
		if (json == null || json.isBlank()) {
			return Collections.emptyList();
		}
		try {
			JsonNode n = objectMapper.readTree(json);
			if (n.isArray()) {
				List<String> out = new ArrayList<>();
				for (JsonNode x : n) {
					out.add(x.asText());
				}
				return out;
			}
			if (n.isObject()) {
				// Dùng fields() thay vì fieldNames() — JsonNode không luôn có fieldNames()
				// (Eclipse/Jackson 2.x)
				List<String> keys = new ArrayList<>();
				for (Iterator<Entry<String, JsonNode>> it = n.fields(); it.hasNext();) {
					keys.add(it.next().getKey());
				}
				keys.sort(String::compareTo);
				List<String> out = new ArrayList<>();
				for (String k : keys) {
					JsonNode v = n.get(k);
					out.add(v != null ? v.asText() : "");
				}
				return out;
			}
		} catch (Exception e) {
			log.debug("parseOptionsForDto: first parse failed for sectionId={}: {}", sectionId, e.getMessage());
		}
		try {
			JavaType listOfStrings = objectMapper.getTypeFactory().constructCollectionType(List.class, String.class);
			List<String> list = objectMapper.readValue(json, listOfStrings);
			return list != null ? list : Collections.emptyList();
		} catch (Exception e) {
			log.warn("parseOptionsForDto: could not parse options JSON for sectionId={}: {}", sectionId,
					e.getMessage());
			return Collections.emptyList();
		}
	}
}
