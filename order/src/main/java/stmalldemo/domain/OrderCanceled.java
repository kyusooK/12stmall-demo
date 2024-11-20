package stmalldemo.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import stmalldemo.domain.*;
import stmalldemo.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class OrderCanceled extends AbstractEvent {

    private Long id;
    private String status;

    public OrderCanceled(Order aggregate) {
        super(aggregate);
    }

    public OrderCanceled() {
        super();
    }
}
//>>> DDD / Domain Event
