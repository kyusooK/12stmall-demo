package stmalldemo.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import stmalldemo.domain.*;
import stmalldemo.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class DeliveryCanceled extends AbstractEvent {

    private Long id;
    private String status;

    public DeliveryCanceled(Delivery aggregate) {
        super(aggregate);
    }

    public DeliveryCanceled() {
        super();
    }
}
//>>> DDD / Domain Event
