package com.uade.tp.controllers;

import com.uade.tp.dtos.KnapsackItemDTO;
import com.uade.tp.services.KnapsackService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/knapsack")
public class KnapsackController {

    private final KnapsackService knapsackService;

    public KnapsackController(KnapsackService knapsackService) {
        this.knapsackService = knapsackService;
    }

    @GetMapping("/{timeLimit}")
    public ResponseEntity<List<KnapsackItemDTO>> solve(
            @PathVariable int timeLimit
    ) {
        return ResponseEntity.ok(
                knapsackService.maximizeInterest(timeLimit)
        );
    }
}

