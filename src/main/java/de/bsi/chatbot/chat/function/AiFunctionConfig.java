package de.bsi.chatbot.chat.function;

import de.bsi.chatbot.connectioncheck.Address;
import de.bsi.chatbot.connectioncheck.ConnectionCheckService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;


@Configuration
public class AiFunctionConfig {

    @Bean
    @Description("Checks the availability of internet connection for a given city name.")
    public Function<Address, String> checkSaltwaterConnectionWithCityName(ConnectionCheckService connectionCheckService) {
        return connectionCheckService::checkSaltwaterConnectionWithCityName;
    }

    @Bean
    @Description("Checks the availability of internet connection for a given postal code.")
    public Function<Address, String> checkSaltwaterConnectionWithPostalCode(ConnectionCheckService connectionCheckService) {
        return connectionCheckService::checkSaltwaterConnectionWithPostalCode;
    }

}
