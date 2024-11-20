package stmalldemo.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import stmalldemo.domain.*;
import stmalldemo.infra.AbstractEvent;

//<<< DDD / Domain Event
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

    public DeliveryStarted(Delivery aggregate) {
        super(aggregate);
    }

    public DeliveryStarted() {
        super();
    }
}
//>>> DDD / Domain Event
