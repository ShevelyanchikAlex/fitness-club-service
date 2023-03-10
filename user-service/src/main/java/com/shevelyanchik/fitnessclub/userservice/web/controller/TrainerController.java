package com.shevelyanchik.fitnessclub.userservice.web.controller;

import com.shevelyanchik.fitnessclub.userservice.model.dto.TrainerDto;
import com.shevelyanchik.fitnessclub.userservice.service.TrainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user-service/trainers")
public class TrainerController {
    private final TrainerService trainerService;

    @GetMapping
    public List<TrainerDto> findAllTrainers(@RequestParam(name = "page", defaultValue = "0") Integer page,
                                            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        Page<TrainerDto> trainerPage = trainerService.findAllTrainers(PageRequest.of(page, size));
        return new ArrayList<>(trainerPage.getContent());
    }

    @GetMapping("/{id}")
    public TrainerDto findTrainerById(@PathVariable Long id) {
        return trainerService.findTrainerById(id);
    }

    @GetMapping("/count")
    public Long countTrainers() {
        return trainerService.countTrainers();
    }
}
