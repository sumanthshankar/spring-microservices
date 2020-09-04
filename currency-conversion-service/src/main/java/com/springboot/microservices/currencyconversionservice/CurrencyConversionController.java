package com.springboot.microservices.currencyconversionservice;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CurrencyConversionController {
	
	@Autowired
	private CurrencyExchangeServiceProxy currencyExchangeServiceProxy;
	
	@GetMapping("/currency-converter/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversionBean convertCurrency(@PathVariable String from, 
			                                      @PathVariable String to, 
			                                      @PathVariable BigDecimal quantity) {
		
		Map<String, String> uriVariables = new HashMap<String, String>();
		uriVariables.put("from", from);
		uriVariables.put("to", to);
		
		ResponseEntity<CurrencyConversionBean> responseEntity = new RestTemplate().getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}", CurrencyConversionBean.class, uriVariables);
		CurrencyConversionBean currencyConversionBean = responseEntity.getBody(); //new CurrencyConversionBean(1L, from, to, BigDecimal.ONE, quantity, quantity, 0); 
		return new CurrencyConversionBean(currencyConversionBean.getId(), 
				                          currencyConversionBean.getFrom(), 
				                          currencyConversionBean.getTo(), 
				                          currencyConversionBean.getConversionMultiple(), 
				                          quantity, 
				                          quantity.multiply(currencyConversionBean.getConversionMultiple()), 
				                          currencyConversionBean.getPort());
	}
	
	@GetMapping("/currency-converter-feign/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversionBean convertCurrencyFeign(@PathVariable String from, 
			                                      @PathVariable String to, 
			                                      @PathVariable BigDecimal quantity) {
		CurrencyConversionBean currencyConversionBean = currencyExchangeServiceProxy.retrieveExchangeValue(from, to); 
		return new CurrencyConversionBean(currencyConversionBean.getId(), 
				                          currencyConversionBean.getFrom(), 
				                          currencyConversionBean.getTo(), 
				                          currencyConversionBean.getConversionMultiple(), 
				                          quantity, 
				                          quantity.multiply(currencyConversionBean.getConversionMultiple()), 
				                          currencyConversionBean.getPort());
	}
}