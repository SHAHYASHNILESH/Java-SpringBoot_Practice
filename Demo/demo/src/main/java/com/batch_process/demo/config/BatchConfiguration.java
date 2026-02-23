package com.batch_process.demo.config;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import com.batch_process.demo.domain.ProductFieldSetMapper;
import com.batch_process.demo.domain.ProductPreparedStatementSetter;
import com.batch_process.demo.domain.ProductRowMapper;
import com.batch_process.demo.dto.ProductDTO;
import com.batch_process.demo.entity.Product;
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
	public ItemWriter<Product> flatFileItemWriter() {
		FlatFileItemWriter<Product> flatFileItemWriter = new FlatFileItemWriter<Product>();
		flatFileItemWriter.setResource(new FileSystemResource("src/main/resources/data/Product_Details_Output.csv"));
		DelimitedLineAggregator<Product> delimitedLineAggregator = new DelimitedLineAggregator<Product>();
		delimitedLineAggregator.setDelimiter(",");

		BeanWrapperFieldExtractor<Product> beanWrapperFieldExtractor = new BeanWrapperFieldExtractor<Product>();
		beanWrapperFieldExtractor
				.setNames(new String[] { "productId", "productName", "productCategory", "productPrice" });
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
	public Job firstJob() throws Exception {
		return new JobBuilder("firstJob", jobRepository).start(firstStep()).build();
	}

//	// Item Reader reading List of Product Names and Item Writer Printing Chunk
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

//	// Item Reader reading CSV File and Item Writer Printing Product
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

//	// Item Reader reading Database and Item Writer Printing ProductDTO
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

//	// Item Reader reading Database and Item Writer Printing ProductDTO
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

//	// Item Reader reading Database and Flat File Item Writer Saving Product
//	@Bean
//	public Step firstStep() throws Exception {
//		return new StepBuilder("firstStep", jobRepository).<ProductDTO, Product>chunk(3, transactionManager)
//				.reader(jdbcPageItemReader()).writer(flatFileItemWriter()).build();
//	}

//	// Item Reader reading Database and JDBC Batch Item Writer Saving Product to DB
//	@Bean
//	public Step firstStep() throws Exception {
//		return new StepBuilder("firstStep", jobRepository).<ProductDTO, ProductDTO>chunk(3, transactionManager)
//				.reader(jdbcPageItemReader()).writer(jdbcBatchItemWriter()).build();
//	}

	// Item Reader reading Database and JDBC Batch Item Writer Saving Product to DB
	@Bean
	public Step firstStep() throws Exception {
		return new StepBuilder("firstStep", jobRepository).<ProductDTO, ProductDTO>chunk(3, transactionManager)
				.reader(jdbcPageItemReader()).writer(jdbcBatchItemWriterNew()).build();
	}
}
