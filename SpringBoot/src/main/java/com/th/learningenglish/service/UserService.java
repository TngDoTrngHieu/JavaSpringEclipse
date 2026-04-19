package com.th.learningenglish.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.th.learningenglish.dto.LoginRequest;
import com.th.learningenglish.dto.RegisterRequest;
import com.th.learningenglish.pojo.Users;
import com.th.learningenglish.repository.UserRepository;
import com.th.learningenglish.security.JwtUtils;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private Cloudinary cloudinary;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	public Users register(RegisterRequest req, MultipartFile avatar) {

		if (userRepository.findByEmail(req.getEmail()).isPresent()) {
			throw new RuntimeException("Email already exists");
		}
		if (userRepository.findByUsername(req.getUsername()).isPresent()) {
			throw new RuntimeException("Username already exists");
		}

		Users user = new Users();
		user.setFirstname(req.getFirstname());
		user.setLastname(req.getLastname());
		user.setEmail(req.getEmail());
		user.setUsername(req.getUsername());
		// 🔐 encode password
		user.setPasswordHash(passwordEncoder.encode(req.getPassword()));

		// 📸 xử lý avatar
		if (avatar != null && !avatar.isEmpty()) {
			try {
				Map uploadResult = cloudinary.uploader().upload(avatar.getBytes(),
						ObjectUtils.asMap("resource_type", "auto"));

				user.setAvatarUrl(uploadResult.get("secure_url").toString());
			} catch (IOException e) {
				throw new RuntimeException("Upload avatar failed", e);
			}
		}

		return userRepository.save(user);
	}

	public String login(LoginRequest req) {
		Users user = userRepository.findByUsername(req.getUsername())
				.orElseThrow(() -> new RuntimeException("User not found"));

		if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
			throw new RuntimeException("Wrong password");
		}

		try {
			return JwtUtils.generateToken(user.getUsername());
		} catch (Exception e) {
			throw new RuntimeException("Error generating token", e);
		}
	}

	public boolean authenticate(String username, String password) {
		Users u = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

		return passwordEncoder.matches(password, u.getPasswordHash());
	}

	public Users getProfile(String username) {
		return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
	}

	public Users updateProfile(String username, Map<String, String> params, MultipartFile avatar) {

		Users user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

		if (params.get("firstname") != null)
			user.setFirstname(params.get("firstname"));

		if (params.get("lastname") != null)
			user.setLastname(params.get("lastname"));

		if (params.get("password") != null && !params.get("password").isEmpty()) {
			user.setPasswordHash(passwordEncoder.encode(params.get("password")));
		}

		// 📸 upload avatar
		if (avatar != null && !avatar.isEmpty()) {
			try {
				Map uploadResult = cloudinary.uploader().upload(avatar.getBytes(),
						ObjectUtils.asMap("resource_type", "auto"));

				user.setAvatarUrl(uploadResult.get("secure_url").toString());

			} catch (IOException e) {
				throw new RuntimeException("Upload avatar failed");
			}
		}

		return userRepository.save(user);
	}

	public List<Users> getAllUsers() {
		return userRepository.findAll();
	}

	public Users getUserById(Long id) {
		return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
	}

	public Users updateUserById(Long id, Map<String, String> params) {
		Users user = getUserById(id);

		if (params.get("firstname") != null) {
			user.setFirstname(params.get("firstname"));
		}
		if (params.get("lastname") != null) {
			user.setLastname(params.get("lastname"));
		}
		if (params.get("email") != null) {
			user.setEmail(params.get("email"));
		}
		if (params.get("username") != null) {
			user.setUsername(params.get("username"));
		}
		if (params.get("password") != null && !params.get("password").isEmpty()) {
			user.setPasswordHash(passwordEncoder.encode(params.get("password")));
		}

		return userRepository.save(user);
	}

	public void deleteUserById(Long id) {
		userRepository.deleteById(id);
	}
}