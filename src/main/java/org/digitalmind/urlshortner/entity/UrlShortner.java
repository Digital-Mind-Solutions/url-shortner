package org.digitalmind.urlshortner.entity;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.digitalmind.buildingblocks.core.jpautils.entity.ContextVersionableAuditModel;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

import static org.digitalmind.urlshortner.entity.UrlShortner.TABLE_NAME;

@Entity
@Table(
        name = TABLE_NAME,
        uniqueConstraints = {
                @UniqueConstraint(
                        name = TABLE_NAME + "_ux1",
                        columnNames = {"short_url"}
                )
        },
        indexes = {
                @Index(
                        name = TABLE_NAME + "_ix1",
                        columnList = "expiration_date"
                )
        }
)
@EntityListeners({AuditingEntityListener.class})
@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@ApiModel(value = "UrlShortner", description = "Url shorter mapping to large urls")
@JsonPropertyOrder(
        {
                "id", "shortUrl", "longUrl", "httpStatus", "iteration", "expirationDate",
                "createdAt", "createdBy", "updatedAt", "updatedBy"
        }
)

public class UrlShortner extends ContextVersionableAuditModel {
    public static final String TABLE_NAME = "url_shortner";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    @ApiModelProperty(value = "Unique id of the url", required = false)
    private Long id;

    @Column(name = "short_url", nullable = false, length = 250)
    @ApiModelProperty(value = "Unique short url", required = false)
    private String shortUrl;

    @Column(name = "long_url", nullable = false, length = 4000)
    @ApiModelProperty(value = "Long url", required = false)
    private String longUrl;

    @Column(name = "http_status", nullable = false)
    @ApiModelProperty(value = "Http redirect status", required = false)
    private int httpStatus;

    @Column(name = "iteration", nullable = false)
    @ApiModelProperty(value = "The iteration value used to find unique short url", required = false)
    private int iteration;

    @Column(name = "expiration_date", nullable = false)
    @ApiModelProperty(value = "Expiration date", required = false)
    private Date expirationDate;

}
