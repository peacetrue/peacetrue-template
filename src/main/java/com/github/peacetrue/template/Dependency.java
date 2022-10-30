package com.github.peacetrue.template;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author peace
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dependency {
    private String name;
    private String version;
}
