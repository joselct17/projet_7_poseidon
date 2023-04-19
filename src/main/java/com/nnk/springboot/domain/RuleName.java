package com.nnk.springboot.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "rulename")
public class RuleName {
    // TODO: Map columns in data table RULENAME with corresponding java fields

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer Id;


    private String name;

    private String description;

    private String json;

    private String template;

    private String sqlStr;

    private String sqlPart;

}
