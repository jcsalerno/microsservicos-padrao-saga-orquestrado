package br.com.microservices.orchestrated.inventoryservice.core.dto;
import br.com.microservices.orchestrated.productvalidationservice.core.dto.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderProducts {

    private Product product;
    private int quantity;
}
