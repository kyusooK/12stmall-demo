package stmalldemo.domain;

import java.util.*;
import lombok.*;
import stmalldemo.domain.*;
import stmalldemo.infra.AbstractEvent;

@Data
@ToString
public class OrderPlaced extends AbstractEvent {

    private Long id;
    private String userId;
    private String productName;
    private String productId;
    private Integer qty;
    private String address;
    private String status;
}
