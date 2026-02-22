package com.batch_process.demo.reader;

import java.util.Iterator;
import java.util.List;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

public class ProductNameItemReader implements ItemReader<String> {

	private Iterator<String> productListIterator;

	public ProductNameItemReader(List<String> productList) {
		this.productListIterator = productList.iterator();
	}

	@Override
	public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		// TODO Auto-generated method stub
		return this.productListIterator.hasNext() ? this.productListIterator.next() : null;
	}

}
