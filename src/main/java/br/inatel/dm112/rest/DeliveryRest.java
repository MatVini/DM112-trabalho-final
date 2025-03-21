package br.inatel.dm112.rest;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.inatel.dm112.model.Order;
import br.inatel.dm112.services.OrderService;

@RestController
@RequestMapping("/api/entregas")
public class DeliveryRest {
	
	private final OrderService service;
	
	public DeliveryRest(OrderService service) {
		this.service = service;
	}
	
	@GetMapping()
	public List<Order> checkForOrders() {

		System.out.println("Verificando se há pedidos");
		
		List<Order> orders = service.getAllOrders().stream()
				.filter(o -> o.getStatus() == 0).collect(Collectors.toList());
		
		if(orders.isEmpty()) {
			System.out.println("Nenhum pedido pronto para entrega.");
		} else {
			System.out.println("Pedidos encontrados.");
		}
		return orders;
	}
	
	@PutMapping("/start-delivery/{orderNum}")
	public void initiateDelivery(@PathVariable("orderNum") Integer orderNum) {
		service.orderDeliveryStart(orderNum);
	}
	
	@PutMapping("/end-delivery/{orderNum}")
	public void finalizeDelivery(@PathVariable("orderNum") Integer orderNum) {
		
		String clienteCPF = service.getOrder(orderNum).getCPF();
		Date horario = new Date();
		
		service.orderDeliveryEnd(orderNum);
		
		String emailApiUrl = "http://localhost:7070/UtilityDM112/api/mail";
		
		Map<String, Object> email = Map.of(
				"orderNumber", orderNum,
	            "from", "delivery@DM112.com",
	            "password", "1234",
	            "to", "user@email.com",
	            "content", "Cliente do CPF " + clienteCPF + "\n"
	            		+ "Seu pedido com código " + orderNum + " foi entregue.\n"
	            		+ "Horário da entrega: " + horario.toString()
		);
		
		HttpEntity<Map<String, Object>> request = new HttpEntity<>(email);
		service.postForEntity(emailApiUrl, request, Void.class);
	}
}
