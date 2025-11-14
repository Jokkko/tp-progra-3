package com.uade.tp.controllers;

import com.uade.tp.services.PathService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/path")
public class PathController {

    private final PathService pathService;

    public PathController(PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping("/exists/{from}/{to}")
    public ResponseEntity<Boolean> existsPath(@PathVariable String from, @PathVariable String to) {
        boolean exists = pathService.existsPathDFS(from, to);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/all-paths/{from}/{to}")
    public ResponseEntity<List<List<String>>> getAllSimplePaths(
            @PathVariable String from,
            @PathVariable String to,
            @RequestParam(required = false) Integer maxDepth
    ) {
        List<List<String>> paths = pathService.findAllSimplePaths(from, to, maxDepth);
        return ResponseEntity.ok(paths);
    }

}
