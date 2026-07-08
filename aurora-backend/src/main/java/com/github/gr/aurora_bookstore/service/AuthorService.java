package com.github.gr.aurora_bookstore.service;

import com.github.gr.aurora_bookstore.dto.authorDto.AuthorCreateDto;
import com.github.gr.aurora_bookstore.dto.authorDto.AuthorReadDto;
import com.github.gr.aurora_bookstore.model.entity.Author;
import com.github.gr.aurora_bookstore.model.mapper.AuthorMapper;
import com.github.gr.aurora_bookstore.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;

    public List<AuthorReadDto> findAll() {
        return authorRepository.findAll().stream()
                .map(AuthorMapper::toReadDto)
                .collect(Collectors.toList());
    }

    public AuthorReadDto findById(Long id) {
        return authorRepository.findById(id)
                .map(AuthorMapper::toReadDto)
                .orElseThrow(() -> new RuntimeException("Author not found with id: " + id));
    }

    public AuthorReadDto create(AuthorCreateDto dto) {
        Author author = AuthorMapper.toEntity(dto);
        Author savedAuthor = authorRepository.save(author);
        return AuthorMapper.toReadDto(savedAuthor);
    }

    public AuthorReadDto update(Long id, AuthorCreateDto dto) {
        Author existingAuthor = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found with id: " + id));
        existingAuthor.setName(dto.getName());
        Author updatedAuthor = authorRepository.save(existingAuthor);
        return AuthorMapper.toReadDto(updatedAuthor);
    }

    public void delete(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new RuntimeException("Author not found with id: " + id);
        }
        authorRepository.deleteById(id);
    }
}
