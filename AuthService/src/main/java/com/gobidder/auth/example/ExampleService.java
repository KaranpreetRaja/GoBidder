package com.gobidder.auth.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExampleService {

    private final ExampleRepository exampleRepository;

    @Autowired
    public ExampleService(ExampleRepository exampleRepository) {
        this.exampleRepository = exampleRepository;
    }

    public Example create(String name) {
        Example example = new Example(name);
        this.exampleRepository.save(example);
        return example;
    }

    public Example get(Long id) {
        return this.exampleRepository.findById(id).orElseThrow(
            () -> new RuntimeException("Example not found")
        );
    }

}
