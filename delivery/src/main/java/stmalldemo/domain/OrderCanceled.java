package stmalldemo.domain;

import java.util.*;
import lombok.*;
import stmalldemo.domain.*;
import stmalldemo.infra.AbstractEvent;

@Data
@ToString
public class OrderCanceled extends AbstractEvent {

    private Long id;
    private String status;
}
