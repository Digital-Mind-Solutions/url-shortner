package org.digitalmind.urlshortner.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@ApiModel(value = "Redirect", description = "The redirect command")
public class Redirect {

    @ApiModelProperty(value = "Redirect location", required = true)
    private String location;

    @ApiModelProperty(value = "Redirect http status", required = true)
    private int httpStatus;

}
