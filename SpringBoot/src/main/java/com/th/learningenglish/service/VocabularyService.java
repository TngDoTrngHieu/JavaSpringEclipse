package com.th.learningenglish.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.th.learningenglish.dto.VocabDTO;
import com.th.learningenglish.pojo.Users;
import com.th.learningenglish.pojo.Vocabularies;
import com.th.learningenglish.repository.UserRepository;
import com.th.learningenglish.repository.VocabularyRepository;

@Service
public class VocabularyService {
	@Autowired
	private VocabularyRepository vocabRepo;

	@Autowired
	private UserRepository userRepo;

	private Users getUserByUsername(String username) {
		if (!StringUtils.hasText(username)) {
			throw new RuntimeException("Username is required");
		}
		return userRepo.findByUsername(username.trim())
				.orElseThrow(() -> new RuntimeException("User not found for username=" + username));
	}

	@Transactional
	public Vocabularies add(VocabDTO dto, String username) {
		if (dto == null || !StringUtils.hasText(dto.getWord())) {
			throw new RuntimeException("Word is required");
		}
		Users user = getUserByUsername(username);
		String normalizedWord = dto.getWord().trim();

		if (vocabRepo.existsByUserAndWordIgnoreCase(user, normalizedWord)) {
			throw new RuntimeException("Word already exists: " + normalizedWord);
		}

		Vocabularies v = new Vocabularies();
		v.setUser(user);
		v.setWord(normalizedWord);
		v.setMeaning(dto.getMeaning());
		v.setExample(dto.getExample());
		v.setNote(dto.getNote());

		return vocabRepo.save(v);
	}

	@Transactional(readOnly = true)
	public List<Vocabularies> getByUser(String username) {
		Users user = getUserByUsername(username);
		return vocabRepo.findByUser(user);
	}

	@Transactional
	public void delete(Long id, String username) {
		Vocabularies v = vocabRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("Vocabulary not found for id=" + id));

		if (v.getUser() == null || !v.getUser().getUsername().equals(username)) {
			throw new RuntimeException("Unauthorized to delete vocabulary id=" + id);
		}

		vocabRepo.delete(v);
	}

	@Transactional(readOnly = true)
	public List<Vocabularies> search(String keyword, String username) {
		Users user = getUserByUsername(username);
		if (!StringUtils.hasText(keyword)) {
			return vocabRepo.findByUser(user);
		}
		return vocabRepo.findByUserAndWordContainingIgnoreCase(user, keyword.trim());
	}
}
