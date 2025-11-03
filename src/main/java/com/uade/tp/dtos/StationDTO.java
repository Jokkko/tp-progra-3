package com.uade.tp.dtos;

import java.util.Objects;

public class StationDTO {
    private String name;
    private String line;
    private String id;
    public StationDTO() {
    }

    public StationDTO(String name, String line, String id) {
        this.name = name;
        this.line = line;
        this.id = id;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLine() { return line; }
    public void setLine(String line) { this.line = line; }
    public String getId() {return id;}
    public void setId(String id) {this.id = id;}
}