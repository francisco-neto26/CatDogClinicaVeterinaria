package br.com.catdogclinicavet.backend_api.controller;

import br.com.catdogclinicavet.backend_api.dto.request.AnimalRequestDTO;
import br.com.catdogclinicavet.backend_api.dto.response.AnimalResponseDTO;
import br.com.catdogclinicavet.backend_api.service.AnimalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/animais")
public class AnimalController {

    @Autowired
    private AnimalService animalService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<AnimalResponseDTO> createAnimal(
            @Valid @RequestPart("animal") AnimalRequestDTO dto,
            @RequestPart(value = "foto", required = false) MultipartFile foto) {
        AnimalResponseDTO response = animalService.createAnimal(dto, foto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Page<AnimalResponseDTO>> findAllAnimals(@PageableDefault(size = 10) Pageable pageable) {
        Page<AnimalResponseDTO> animais = animalService.findAllAnimals(pageable);
        return ResponseEntity.ok(animais);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<AnimalResponseDTO> findAnimalById(@PathVariable Long id) {
        AnimalResponseDTO animal = animalService.findAnimalById(id);
        return ResponseEntity.ok(animal);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<AnimalResponseDTO> updateAnimal(
            @PathVariable Long id,
            @Valid @RequestPart("animal") AnimalRequestDTO dto,
            @RequestPart(value = "foto", required = false) MultipartFile foto) {
        AnimalResponseDTO updatedAnimal = animalService.updateAnimal(id, dto, foto);
        return ResponseEntity.ok(updatedAnimal);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('CLIENTE')")
    public void deleteAnimal(@PathVariable Long id) {
        animalService.deleteAnimal(id);
    }
}