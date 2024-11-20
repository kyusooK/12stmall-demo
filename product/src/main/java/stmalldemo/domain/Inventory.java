package stmalldemo.domain;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Data;
import stmalldemo.ProductApplication;
import stmalldemo.domain.StockDecreased;

@Entity
@Table(name = "Inventory_table")
@Data
//<<< DDD / Aggregate Root
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Integer stock;

    // @PostPersist
    // public void onPostPersist() {
    //     StockDecreased stockDecreased = new StockDecreased(this);
    //     stockDecreased.publishAfterCommit();
    // }

    public static InventoryRepository repository() {
        InventoryRepository inventoryRepository = ProductApplication.applicationContext.getBean(
            InventoryRepository.class
        );
        return inventoryRepository;
    }

    //<<< Clean Arch / Port Method
    public static void decreaseStock(DeliveryStarted deliveryStarted) {
       
        repository().findById(Long.valueOf(deliveryStarted.getProductId())).ifPresent(inventory->{
            
            inventory.setStock(inventory.getStock() - deliveryStarted.getQty()); // 상품팀에서 등록한 초기재고에서 배송시작됨(주문됨)에 따른 상품수량만큼 차감
            repository().save(inventory);

            StockDecreased stockDecreased = new StockDecreased(inventory);
            stockDecreased.publishAfterCommit();

         });

    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
