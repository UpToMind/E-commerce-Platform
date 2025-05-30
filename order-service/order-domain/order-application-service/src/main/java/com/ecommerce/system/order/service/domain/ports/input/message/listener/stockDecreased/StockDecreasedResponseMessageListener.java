package com.ecommerce.system.order.service.domain.ports.input.message.listener.stockDecreased;

import com.ecommerce.system.order.service.domain.dto.message.StockDecreasedResponse;

public interface StockDecreasedResponseMessageListener {
    void StockDecreased(StockDecreasedResponse stockDecreasedResponse);

    void StockDecreasedFailed(StockDecreasedResponse stockDecreasedResponse);
    
}
