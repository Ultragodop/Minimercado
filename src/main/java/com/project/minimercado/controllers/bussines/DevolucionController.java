package com.project.minimercado.controllers.bussines;

import com.project.minimercado.model.bussines.Devolucion;
import com.project.minimercado.services.bussines.DevolucionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/devoluciones")
public class DevolucionController {

    private final DevolucionService devolucionService;

    @Autowired
    public DevolucionController(DevolucionService devolucionService) {
        this.devolucionService = devolucionService;
    }

    @PostMapping
    public ResponseEntity<Devolucion> crearDevolucion(@RequestBody Devolucion devolucion) {
        try {
            Devolucion nuevaDevolucion = devolucionService.crearDevolucion(devolucion);
            return ResponseEntity.ok(nuevaDevolucion);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/aprobar")
    public ResponseEntity<Devolucion> aprobarDevolucion(
            @PathVariable Integer id,
            @RequestBody Map<String, String> request
    ) {
        try {
            String usuarioAprobacion = request.get("usuarioAprobacion");
            String comentario = request.get("comentario");
            Devolucion devolucion = devolucionService.aprobarDevolucion(id, usuarioAprobacion, comentario);
            return ResponseEntity.ok(devolucion);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/rechazar")
    public ResponseEntity<Devolucion> rechazarDevolucion(
            @PathVariable Integer id,
            @RequestBody Map<String, String> request
    ) {
        try {
            String usuarioAprobacion = request.get("usuarioAprobacion");
            String comentario = request.get("comentario");
            Devolucion devolucion = devolucionService.rechazarDevolucion(id, usuarioAprobacion, comentario);
            return ResponseEntity.ok(devolucion);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<Devolucion>> obtenerDevolucionesPendientes() {
        List<Devolucion> devoluciones = devolucionService.obtenerDevolucionesPendientes();
        return ResponseEntity.ok(devoluciones);
    }

    @GetMapping("/fecha")
    public ResponseEntity<List<Devolucion>> obtenerDevolucionesPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fechaFin
    ) {
        List<Devolucion> devoluciones = devolucionService.obtenerDevolucionesPorFecha(fechaInicio, fechaFin);
        return ResponseEntity.ok(devoluciones);
    }

    @GetMapping("/venta/{idVenta}")
    public ResponseEntity<List<Devolucion>> obtenerDevolucionesPorVenta(@PathVariable Integer idVenta) {
        List<Devolucion> devoluciones = devolucionService.obtenerDevolucionesPorVenta(idVenta);
        return ResponseEntity.ok(devoluciones);
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Devolucion>> obtenerDevolucionesPorUsuario(@PathVariable Integer idUsuario) {
        List<Devolucion> devoluciones = devolucionService.obtenerDevolucionesPorUsuario(idUsuario);
        return ResponseEntity.ok(devoluciones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Devolucion> obtenerDevolucionPorId(@PathVariable Integer id) {
        return devolucionService.obtenerDevolucionPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
} 