package stmalldemo.domain;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import stmalldemo.domain.*;

//<<< PoEAA / Repository
@RepositoryRestResource(
    collectionResourceRel = "inventories",
    path = "inventories"
)
public interface InventoryRepository
    extends PagingAndSortingRepository<Inventory, Long> {}
