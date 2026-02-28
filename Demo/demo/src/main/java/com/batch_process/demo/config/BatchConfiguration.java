package com.batch_process.demo.config;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.validator.BeanValidatingItemProcessor;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import com.batch_process.demo.domain.OSProductItemProcessor;
import com.batch_process.demo.domain.ProductFieldSetMapper;
import com.batch_process.demo.domain.ProductPreparedStatementSetter;
import com.batch_process.demo.domain.ProductRowMapper;
import com.batch_process.demo.domain.ProductValidator;
import com.batch_process.demo.dto.OSProductDTO;
import com.batch_process.demo.dto.ProductDTO;
import com.batch_process.demo.entity.Product;
import com.batch_process.demo.processor.FilterProductItemProcessor;
import com.batch_process.demo.processor.MyProductItemProcessor;
import com.batch_process.demo.reader.ProductNameItemReader;

@Configuration
public class BatchConfiguration {
	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private PlatformTransactionManager transactionManager;

	@Autowired
	private DataSource dataSource;

	@Bean
	public ItemReader<String> itemReader() {
		List<String> productList = new ArrayList<String>();
		productList.add("Product 1");
		productList.add("Product 2");
		productList.add("Product 3");
		productList.add("Product 4");
		productList.add("Product 5");
		productList.add("Product 6");
		productList.add("Product 7");
		productList.add("Product 8");
		return new ProductNameItemReader(productList);
	}

	@Bean
	public ItemReader<Product> flatFileItemReader() {
		FlatFileItemReader<Product> flatFileItemReader = new FlatFileItemReader<Product>();
		flatFileItemReader.setLinesToSkip(1);
		flatFileItemReader.setResource(new ClassPathResource("/data/Product_Details.csv"));

		DefaultLineMapper<Product> defaultLineMapper = new DefaultLineMapper<Product>();
		DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
		delimitedLineTokenizer.setNames("product_id", "product_name", "product_category", "product_price");

		defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);
		defaultLineMapper.setFieldSetMapper(new ProductFieldSetMapper());

		flatFileItemReader.setLineMapper(defaultLineMapper);

		return flatFileItemReader;
	}

	@Bean
	public ItemReader<ProductDTO> jdbcItemReader() {
		JdbcCursorItemReader<ProductDTO> jdbcCursorItemReader = new JdbcCursorItemReader<ProductDTO>();
		jdbcCursorItemReader.setDataSource(dataSource);
		jdbcCursorItemReader.setSql("SELECT * FROM product order by product_id");
		jdbcCursorItemReader.setRowMapper(new ProductRowMapper());
		return jdbcCursorItemReader;
	}

	@Bean
	public ItemReader<ProductDTO> jdbcPageItemReader() throws Exception {
		JdbcPagingItemReader<ProductDTO> jdbcPagingItemReader = new JdbcPagingItemReader<ProductDTO>();
		jdbcPagingItemReader.setDataSource(dataSource);

		SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
		queryProvider.setSelectClause("SELECT product_id, product_name, product_category, product_price");
		queryProvider.setFromClause("FROM product");
		queryProvider.setSortKey("product_id");
		queryProvider.setDataSource(dataSource);
		jdbcPagingItemReader.setQueryProvider(queryProvider.getObject());
		jdbcPagingItemReader.setRowMapper(new ProductRowMapper());
		jdbcPagingItemReader.setPageSize(3); // kept same as chunk size

		return jdbcPagingItemReader;
	}

	@Bean
	public ItemWriter<ProductDTO> flatFileItemWriter() {
		FlatFileItemWriter<ProductDTO> flatFileItemWriter = new FlatFileItemWriter<ProductDTO>();
		flatFileItemWriter.setResource(new FileSystemResource("src/main/resources/data/Product_Details_Output.csv"));
		DelimitedLineAggregator<ProductDTO> delimitedLineAggregator = new DelimitedLineAggregator<ProductDTO>();
		delimitedLineAggregator.setDelimiter(",");

		BeanWrapperFieldExtractor<ProductDTO> beanWrapperFieldExtractor = new BeanWrapperFieldExtractor<ProductDTO>();
		beanWrapperFieldExtractor
				.setNames(new String[] { "productId", "productName", "productCategory", "productPrice" });
		delimitedLineAggregator.setFieldExtractor(beanWrapperFieldExtractor);
		flatFileItemWriter.setLineAggregator(delimitedLineAggregator);
		return flatFileItemWriter;
	}

	@Bean
	public ItemWriter<OSProductDTO> flatFileItemWriter2() {
		FlatFileItemWriter<OSProductDTO> flatFileItemWriter = new FlatFileItemWriter<OSProductDTO>();
		flatFileItemWriter.setResource(new FileSystemResource("src/main/resources/data/OS_Product_Details_Output.csv"));
		DelimitedLineAggregator<OSProductDTO> delimitedLineAggregator = new DelimitedLineAggregator<OSProductDTO>();
		delimitedLineAggregator.setDelimiter(",");

		BeanWrapperFieldExtractor<OSProductDTO> beanWrapperFieldExtractor = new BeanWrapperFieldExtractor<OSProductDTO>();
		beanWrapperFieldExtractor.setNames(new String[] { "productId", "productName", "productCategory", "productPrice",
				"taxPercent", "sku", "shippingRate" });
		delimitedLineAggregator.setFieldExtractor(beanWrapperFieldExtractor);
		flatFileItemWriter.setLineAggregator(delimitedLineAggregator);
		return flatFileItemWriter;

	}

	@Bean
	public JdbcBatchItemWriter<ProductDTO> jdbcBatchItemWriter() throws Exception {
		JdbcBatchItemWriter<ProductDTO> jdbcBatchItemWriter = new JdbcBatchItemWriter<ProductDTO>();
		jdbcBatchItemWriter.setDataSource(dataSource);
		jdbcBatchItemWriter.setSql("INSERT INTO product_output values(?, ?, ?, ?)");
		jdbcBatchItemWriter.setItemPreparedStatementSetter(new ProductPreparedStatementSetter());
		return jdbcBatchItemWriter;
	}

	@Bean
	public JdbcBatchItemWriter<ProductDTO> jdbcBatchItemWriterNew() throws Exception {
		JdbcBatchItemWriter<ProductDTO> jdbcBatchItemWriter = new JdbcBatchItemWriter<ProductDTO>();
		jdbcBatchItemWriter.setDataSource(dataSource);
		jdbcBatchItemWriter
				.setSql("INSERT INTO product_output values(:productId, :productName, :productCategory, :productPrice)");
		jdbcBatchItemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider());
		return jdbcBatchItemWriter;
	}

	@Bean
	public JdbcBatchItemWriter<OSProductDTO> jdbcBatchItemWriterNew1() throws Exception {
		JdbcBatchItemWriter<OSProductDTO> jdbcBatchItemWriter = new JdbcBatchItemWriter<OSProductDTO>();
		jdbcBatchItemWriter.setDataSource(dataSource);
		jdbcBatchItemWriter.setSql(
				"INSERT INTO os_product values(:productId, :productName, :productCategory, :productPrice,:taxPercent,:sku,:shippingRate)");
		jdbcBatchItemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider());
		return jdbcBatchItemWriter;
	}

	@Bean
	public ItemProcessor<ProductDTO, ProductDTO> itemProcessor() {
		return new MyProductItemProcessor();
	}

	@Bean
	public ItemProcessor<ProductDTO, OSProductDTO> itemProcessor2() {
		return new OSProductItemProcessor();
	}

	@Bean
	public ItemProcessor<ProductDTO, ProductDTO> filterDataItemProcessor() {
		return new FilterProductItemProcessor();
	}

	@Bean
	public ValidatingItemProcessor<ProductDTO> validatingItemProcessor() {
		ValidatingItemProcessor<ProductDTO> validatingItemProcessor = new ValidatingItemProcessor<ProductDTO>(
				new ProductValidator());
		validatingItemProcessor.setFilter(true); // filters item which is not valid as per conditions
		return validatingItemProcessor;
	}

	@Bean
	public BeanValidatingItemProcessor<ProductDTO> beanValidatingProcessor() {
		BeanValidatingItemProcessor<ProductDTO> validatingItemProcessor = new BeanValidatingItemProcessor<>();
		validatingItemProcessor.setFilter(true);
		return validatingItemProcessor;
	}

	@Bean
	public Job firstJob() throws Exception {
		return new JobBuilder("firstJob", jobRepository).start(firstStep()).build();
	}

//	// ItemReader reading list of productNames and ItemWriter printing Chunk
//	@Bean
//	public Step firstStep() {
//		return new StepBuilder("firstStep", jobRepository).<String, String>chunk(3, transactionManager)
//				.reader(itemReader()).writer(new ItemWriter<String>() {
//
//					@Override
//					public void write(Chunk<? extends String> chunk) throws Exception {
//						// TODO Auto-generated method stub
//						System.out.println("Chunk Processing Started ====>");
//						chunk.forEach(System.out::println);
//						System.out.println("Chunk Processing Ended <====");
//					}
//
//				}).build();
//	}

//	// ItemReader reading CSV file and ItemWriter printing product
//	@Bean
//	public Step firstStep() {
//		return new StepBuilder("firstStep", jobRepository).<Product, Product>chunk(3, transactionManager)
//				.reader(flatFileItemReader()).writer(new ItemWriter<Product>() {
//
//					@Override
//					public void write(Chunk<? extends Product> chunk) throws Exception {
//						// TODO Auto-generated method stub
//						System.out.println("Flat-File CSV Chunk Processing Started ====>");
//						chunk.forEach(System.out::println);
//						System.out.println("Flat-File CSV Chunk Processing Ended <====");
//					}
//
//				}).build();
//	}

//	// ItemReader reading database and ItemWriter printing ProductDTO
//	@Bean
//	public Step firstStep() {
//		return new StepBuilder("firstStep", jobRepository).<Product, Product>chunk(3, transactionManager)
//				.reader(jdbcItemReader()).writer(new ItemWriter<Product>() {
//
//					@Override
//					public void write(Chunk<? extends Product> chunk) throws Exception {
//						// TODO Auto-generated method stub
//						System.out.println("JDBC Cursor Chunk Processing Started ====>");
//						chunk.forEach(System.out::println);
//						System.out.println("JDBC Cursor Chunk Processing Ended <====");
//					}
//
//				}).build();
//	}

//	// ItemReader reading database and ItemWriter printing ProductDTO
//	@Bean
//	public Step firstStep() throws Exception {
//		return new StepBuilder("firstStep", jobRepository).<ProductDTO, ProductDTO>chunk(3, transactionManager)
//				.reader(jdbcPageItemReader()).writer(new ItemWriter<ProductDTO>() {
//
//					@Override
//					public void write(Chunk<? extends ProductDTO> chunk) {
//						System.out.println("JDBC Page Item Chunk Processing Started ====>");
//						chunk.forEach(System.out::println);
//						System.out.println("JDBC Page Item Chunk Processing Ended <====");
//					}
//				}).build();
//	}

//	// ItemReader reading database and FlatFileItemWriter saving Product
//	@Bean
//	public Step firstStep() throws Exception {
//		return new StepBuilder("firstStep", jobRepository).<ProductDTO, Product>chunk(3, transactionManager)
//				.reader(jdbcPageItemReader()).writer(flatFileItemWriter()).build();
//	}

//	// ItemReader reading database and JDBCBatchItemWriter saving Product to DB
//	@Bean
//	public Step firstStep() throws Exception {
//		return new StepBuilder("firstStep", jobRepository).<ProductDTO, ProductDTO>chunk(3, transactionManager)
//				.reader(jdbcPageItemReader()).writer(jdbcBatchItemWriter()).build();
//	}

//	// ItemReader reading database and JDBCBatchItemWriter saving product to output DB
//	@Bean
//	public Step firstStep() throws Exception {
//		return new StepBuilder("firstStep", jobRepository).<ProductDTO, ProductDTO>chunk(3, transactionManager)
//				.reader(jdbcPageItemReader()).writer(jdbcBatchItemWriterNew()).build();
//	}

//	// ItemReader reading database, ItemProcessor processing it and
//	// JDBCBatchItemWriter saving product to output DB
//	@Bean
//	public Step firstStep() throws Exception {
//		return new StepBuilder("firstStep", jobRepository).<ProductDTO, ProductDTO>chunk(3, transactionManager)
//				.reader(jdbcPageItemReader()).processor(itemProcessor()).writer(jdbcBatchItemWriterNew()).build();
//	}

//	// ItemReader reading database, ItemProcessor processing it and
//	// JDBCBatchItemWriter saving osproduct to output DB
//	@Bean
//	public Step firstStep() throws Exception {
//		return new StepBuilder("firstStep", jobRepository).<ProductDTO, OSProductDTO>chunk(3, transactionManager)
//				.reader(jdbcPageItemReader()).processor(itemProcessor2()).writer(flatFileItemWriter2()).build();
//	}

//	// ItemReader reading database, ItemProcessor processing it and
//	// JDBCBatchItemWriter saving product to output DB
//	@Bean
//	public Step firstStep() throws Exception {
//		return new StepBuilder("firstStep", jobRepository).<ProductDTO, ProductDTO>chunk(3, transactionManager)
//				.reader(jdbcPageItemReader()).processor(filterDataItemProcessor()).writer(jdbcBatchItemWriterNew())
//				.build();
//	}

//	// ItemReader reading database, ValidatingItemProcessor processing it and
//	// JDBCBatchItemWriter saving product to output DB
//	@Bean
//	public Step firstStep() throws Exception {
//		return new StepBuilder("firstStep", jobRepository).<ProductDTO, ProductDTO>chunk(3, transactionManager)
//				.reader(jdbcPageItemReader()).processor(validatingItemProcessor()).writer(jdbcBatchItemWriterNew())
//				.build();
//	}

	// ItemReader reading database, ValidatingItemProcessor processing it and
	// JDBCBatchItemWriter saving product to output DB
	@Bean
	public Step firstStep() throws Exception {
		return new StepBuilder("firstStep", jobRepository).<ProductDTO, ProductDTO>chunk(3, transactionManager)
				.reader(jdbcPageItemReader()).processor(beanValidatingProcessor()).writer(jdbcBatchItemWriterNew())
				.build();
	}
}
