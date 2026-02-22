package com.batch_process.demo.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BatchController {
	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	@Qualifier("firstJob")
	private Job job;

	@GetMapping("/launchJob/{id}")
	public void handle(@PathVariable("id") String id) throws Exception {

		JobParameters jobParameters = new JobParametersBuilder().addString("param", id).toJobParameters();
		jobLauncher.run(job, jobParameters);

	}
}
