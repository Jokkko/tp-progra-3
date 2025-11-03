package com.uade.tp.controllers;

import com.uade.tp.services.LineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/lines")
public class LineController {
    private final LineService lineService;

    public LineController(LineService lineService){
        this.lineService = lineService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<String>> getAllLines() {
        return lineService.GetAllLines()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.internalServerError().build());
    }
}
