package com.ecommerce.system.order.service.domain.ports.input.message.listener.stockDecreased;

public interface StockDecreasedResponseMessageListener {
    void StockDecreased(StockDecreasedResponse stockDecreasedResponse);

    void StockDecreasedFailed(StockDecreasedResponse stockDecreasedResponse);
    
}
