package com.uade.tp.dtos;

import java.util.Objects;

public class StationDTO {
    private String name;
    private String line;

    public StationDTO() {
    }

    public StationDTO(String name, String line) {
        this.name = name;
        this.line = line;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLine() { return line; }
    public void setLine(String line) { this.line = line; }

}