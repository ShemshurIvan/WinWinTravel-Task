package com.winwin.dataapi.controller;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transform")
public class TransformController {

    @PostMapping
    public TransformResponse transform(@RequestBody TransformRequest req) {
        String result = (req.getText() != null)
                ? new StringBuilder(req.getText()).reverse().toString().toUpperCase()
                : "no text provided";
        return new TransformResponse(result);
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class TransformRequest { private String text; }

    @Data @AllArgsConstructor @NoArgsConstructor
    public static class TransformResponse { private String result; }
}
