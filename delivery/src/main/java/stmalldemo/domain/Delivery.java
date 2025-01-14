package stmalldemo.domain;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Data;
import stmalldemo.DeliveryApplication;
import stmalldemo.domain.DeliveryCanceled;
import stmalldemo.domain.DeliveryStarted;

@Entity
@Table(name = "Delivery_table")
@Data
//<<< DDD / Aggregate Root
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long orderId;

    private String userId;

    private String productId;

    private Integer qty;

    private String productName;

    private String address;

    private String status;

    // @PostPersist
    // public void onPostPersist() {
    //     DeliveryStarted deliveryStarted = new DeliveryStarted(this);
    //     deliveryStarted.publishAfterCommit();

    //     DeliveryCanceled deliveryCanceled = new DeliveryCanceled(this);
    //     deliveryCanceled.publishAfterCommit();
    // }

    public static DeliveryRepository repository() {
        DeliveryRepository deliveryRepository = DeliveryApplication.applicationContext.getBean(
            DeliveryRepository.class
        );
        return deliveryRepository;
    }

    public static void startDelivery(OrderPlaced orderPlaced) {

        // 주문이 발생함에 따라 등록한 주문정보와 매칭되는 정보를 배송정보에 등록
        Delivery delivery = new Delivery();
        delivery.setOrderId(orderPlaced.getId());
        delivery.setUserId(orderPlaced.getUserId());
        delivery.setProductName(orderPlaced.getProductName());
        delivery.setProductId(orderPlaced.getProductId());
        delivery.setAddress(orderPlaced.getAddress());
        delivery.setQty(orderPlaced.getQty());
        delivery.setStatus("배송시작됨");
        repository().save(delivery);
        
        DeliveryStarted deliveryStarted = new DeliveryStarted(delivery);
        deliveryStarted.publishAfterCommit();

    }

    public static void cancelDelivery(OrderCanceled orderCanceled) {
        //implement business logic here:

        /** Example 1:  new item 
        Delivery delivery = new Delivery();
        repository().save(delivery);

        DeliveryCanceled deliveryCanceled = new DeliveryCanceled(delivery);
        deliveryCanceled.publishAfterCommit();
        */

        /** Example 2:  finding and process
        
        repository().findById(orderCanceled.get???()).ifPresent(delivery->{
            
            delivery // do something
            repository().save(delivery);

            DeliveryCanceled deliveryCanceled = new DeliveryCanceled(delivery);
            deliveryCanceled.publishAfterCommit();

         });
        */

    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
