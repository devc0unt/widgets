package com.miro.dev.widgets.controller;

import com.miro.dev.widgets.model.Limit;
import com.miro.dev.widgets.model.Widget;
import com.miro.dev.widgets.service.WidgetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/widgets")
public class WidgetController {

    private final WidgetService service;

    public WidgetController(WidgetService service) {
        this.service = service;
    }

    @GetMapping()
    public ResponseEntity<List<Widget>> getAll(
            @RequestParam(name = "limit", required = false) final Integer limit,
            @RequestParam(name = "offset", required = false) final Integer offset
    ) {
        return ResponseEntity.ok(service.getAll(new Limit(limit, offset)));
    }

    @GetMapping("{id}")
    public ResponseEntity<Widget> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping()
    public ResponseEntity<Widget> create(@RequestBody Widget widget) {
        return ResponseEntity.ok(service.create(widget));
    }

    @PutMapping()
    public ResponseEntity<Widget> update(@RequestBody Widget widget) {
        return ResponseEntity.ok(service.update(widget));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return ResponseEntity.ok(service.delete(id));
    }

}
