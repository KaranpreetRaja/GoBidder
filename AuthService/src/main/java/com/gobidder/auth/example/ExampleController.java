package com.gobidder.auth.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ExampleController {

    private final ExampleService exampleService;

    @Autowired
    public ExampleController(ExampleService exampleService) {
        this.exampleService = exampleService;
    }

    @PostMapping("example")
    public Example createExample(@RequestBody String name) {
        return this.exampleService.create(name);
    }

    @GetMapping("example/{id}")
    public Example getExample(@PathVariable Long id) {
        return this.exampleService.get(id);
    }

}
