package com.akaene.eccairs.values;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Value list element.
 * <p>
 * References to descendants and parent are stored as lists of their identifiers.
 */
public class ValueListElement implements Serializable {

    private Integer id;

    private String label;

    private String description;

    private List<Integer> descendants;

    private Integer parent;

    public ValueListElement(Integer id, String label, String description) {
        this.id = id;
        this.label = label;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Integer> getDescendants() {
        return descendants;
    }

    public void setDescendants(List<Integer> descendants) {
        this.descendants = descendants;
    }

    public Integer getParent() {
        return parent;
    }

    public void setParent(Integer parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ValueListElement that)) {
            return false;
        }
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ValueListElement{" +
                "id=" + id +
                ", label='" + label + '\'' +
                '}';
    }
}
