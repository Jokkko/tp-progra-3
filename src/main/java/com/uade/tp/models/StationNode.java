package com.uade.tp.models;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

import java.util.List;
import java.util.Objects;

@Node("Station")
public class StationNode {

    @Id @GeneratedValue
    private Long graphId;

    @Property("name")
    private String name;

    public StationNode() {
    }

    public Long getGraphId() {
        return graphId;
    }

    public void setGraphId(Long graphId) {
        this.graphId = graphId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "StationNode{" +
                "graphId=" + graphId +
                ", name='" + name + '\'' +
                '}';
    }
}