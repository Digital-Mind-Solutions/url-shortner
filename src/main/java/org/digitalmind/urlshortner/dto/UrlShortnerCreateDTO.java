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
@ApiModel(value = "}", description = "The short url create request")
public class UrlShortnerCreateDTO {

    @ApiModelProperty(value = "Long url", required = true)
    String longUrl;

    @ApiModelProperty(value = "Http redirect status", required = true)
    int httpStatus;

    @ApiModelProperty(value = "Short url ttl", required = true)
    int ttlSeconds;

    @ApiModelProperty(value = "Short url parts", required = true)
    int parts;

    @ApiModelProperty(value = "Min iteration in case of hash code exists", required = true)
    int iterationMin;

    @ApiModelProperty(value = "Max iteration in case of hash code exists", required = true)
    int iterationMax;

}
