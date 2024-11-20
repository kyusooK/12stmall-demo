package stmalldemo.domain;

import java.util.*;
import lombok.*;
import stmalldemo.domain.*;
import stmalldemo.infra.AbstractEvent;

@Data
@ToString
public class DeliveryStarted extends AbstractEvent {

    private Long id;
    private Long orderId;
    private String userId;
    private String productId;
    private String productName;
    private String address;
    private String status;
}
