package com.github.gr.aurora_bookstore.service;

import com.github.gr.aurora_bookstore.dto.genreDto.GenreCreateDto;
import com.github.gr.aurora_bookstore.dto.genreDto.GenreReadDto;
import com.github.gr.aurora_bookstore.model.entity.Genre;
import com.github.gr.aurora_bookstore.model.mapper.GenreMapper;
import com.github.gr.aurora_bookstore.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;

    public List<GenreReadDto> findAll() {
        return genreRepository.findAll().stream()
                .map(GenreMapper::toReadDto)
                .collect(Collectors.toList());
    }

    public GenreReadDto findById(Long id) {
        return genreRepository.findById(id)
                .map(GenreMapper::toReadDto)
                .orElseThrow(() -> new RuntimeException("Genre not found with id: " + id));
    }

    public GenreReadDto create(GenreCreateDto dto) {
        Genre genre = GenreMapper.toEntity(dto);
        Genre savedGenre = genreRepository.save(genre);
        return GenreMapper.toReadDto(savedGenre);
    }

    public GenreReadDto update(Long id, GenreCreateDto dto) {
        Genre existingGenre = genreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Genre not found with id: " + id));
        existingGenre.setName(dto.getName());
        Genre updatedGenre = genreRepository.save(existingGenre);
        return GenreMapper.toReadDto(updatedGenre);
    }

    public void delete(Long id) {
        if (!genreRepository.existsById(id)) {
            throw new RuntimeException("Genre not found with id: " + id);
        }
        genreRepository.deleteById(id);
    }
}
