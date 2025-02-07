package br.com.microservices.orchestrated.paymentservice.core.dto;
import br.com.microservices.orchestrated.paymentservice.core.dto.Product;
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
