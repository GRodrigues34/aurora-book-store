package com.github.gr.aurora_bookstore.service;

import com.github.gr.aurora_bookstore.dto.genreDto.GenreCreateDTO;
import com.github.gr.aurora_bookstore.dto.genreDto.GenreReadDTO;
import com.github.gr.aurora_bookstore.exception.bookException.GenreNotFoundException;
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

    public List<GenreReadDTO> findAll() {
        return genreRepository.findAll().stream()
                .map(GenreMapper::toReadDto)
                .collect(Collectors.toList());
    }

    public GenreReadDTO findById(Long id) {
        return genreRepository.findById(id)
                .map(GenreMapper::toReadDto)
                .orElseThrow(() -> new GenreNotFoundException("Genre not found with id: " + id));
    }

    public GenreReadDTO create(GenreCreateDTO dto) {
        Genre genre = GenreMapper.toEntity(dto);
        Genre savedGenre = genreRepository.save(genre);
        return GenreMapper.toReadDto(savedGenre);
    }

    public GenreReadDTO update(Long id, GenreCreateDTO dto) {
        Genre existingGenre = genreRepository.findById(id)
                .orElseThrow(() -> new GenreNotFoundException("Genre not found with id: " + id));
        existingGenre.setName(dto.getName());
        Genre updatedGenre = genreRepository.save(existingGenre);
        return GenreMapper.toReadDto(updatedGenre);
    }

    public void delete(Long id) {
        if (!genreRepository.existsById(id)) {
            throw new GenreNotFoundException("Genre not found with id: " + id);
        }
        genreRepository.deleteById(id);
    }
}
