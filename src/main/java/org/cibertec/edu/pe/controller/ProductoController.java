package org.cibertec.edu.pe.controller;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.cibertec.edu.pe.modelo.Detalle;
import org.cibertec.edu.pe.modelo.Producto;
import org.cibertec.edu.pe.modelo.Venta;
import org.cibertec.edu.pe.repository.IDetalleRepository;
import org.cibertec.edu.pe.repository.IProductoRepository;
import org.cibertec.edu.pe.repository.IVentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes({"carrito","total"})
public class ProductoController {
	
	@Autowired
	private IProductoRepository productoRepository;
	@Autowired
	private IVentaRepository ventaRepository;
	@Autowired
	private IDetalleRepository detalleRepository;
	
	//Inicialización del campo "carrito"
	@ModelAttribute("carrito")
	public List<Detalle> getCarrito(){
		return new ArrayList<Detalle>();
	}
	
	//Inicialización del campo "total"
	@ModelAttribute("total")
	public double getTotal() {
		return 0;
	}
	// Método para calcular los totales
    private double calcularTotal(List<Detalle> carrito) {
        double total = 0.0;
        for (Detalle detalle : carrito) {
            total += detalle.getSubtotal();
        }
        return total;
    }

    // Método del precio de envío
    private double calcularPrecioEnvio(double total) {
        return 0.075 * total;
    }

    // Método para el descuento
    private double calcularDescuento(double total) {
        double descuento = 0.0;
        if (total >= 500 && total <= 1000) {
            descuento = 0.02 * total;
        } else if (total > 1000 && total <= 2000) {
            descuento = 0.03 * total;
        } else if (total > 2000 && total <= 3000) {
            descuento = 0.04 * total;
        } else if (total > 3000) {
            descuento = 0.05 * total;
        }
        return descuento;
    }
    
	//INDEX
	@GetMapping("/index")
	public String listado(Model model) {
		List<Producto> lista = new ArrayList<>();
		lista = productoRepository.findAll();
		model.addAttribute("productos",lista);
		return "index";
	}
	//CARRITO
	@GetMapping("/carrito")
		public String carrito(Model model) {
	    List<Detalle> carrito = (List<Detalle>) model.getAttribute("carrito");
	    double total = calcularTotal(carrito);
	    double precioEnvio = calcularPrecioEnvio(total);
	    double descuento = calcularDescuento(total);

	    model.addAttribute("total", total);
	    model.addAttribute("precioEnvio", precioEnvio);
	    model.addAttribute("descuento", descuento);

	    return "carrito";
	}

	//AGREGAR PRODUCTOS
	@GetMapping("/agregar/{IdProducto}")
	public String agregar(Model model,@PathVariable(name="IdProducto",required=true)int  IdProducto) {
		Producto p = productoRepository.findById(IdProducto).orElse(null);
		List<Detalle> carrito = (List<Detalle>)model.getAttribute("carrito");
		double total = 0.0;
		boolean existe = false;
		Detalle detalle = new Detalle();
		//si el producto se encontro añadirlo al detalle
		if (p !=null) {
			detalle.setProducto(p);
			detalle.setCantidad(1);
			detalle.setSubtotal(detalle.getProducto().getPrecio()*detalle.getCantidad());
			
		}
		//Verificar si el carrito de compras ya tiene productos
		if(carrito.size()==0) {
			carrito.add(detalle);
		}else {
			for(Detalle d :carrito) {
				if(d.getProducto().getIdProducto() == p.getIdProducto()) {
					d.setCantidad(d.getCantidad()+1);
					d.setSubtotal(d.getProducto().getPrecio()*d.getCantidad());
					existe = true;
				}
			}
			if(!existe)carrito.add(detalle);
		}
		//Acumular la suma de subtotales
		for(Detalle d :carrito) total+= d.getSubtotal();
		//Pasar a la vista los datos a visualizar
		model.addAttribute("total",total);
		model.addAttribute("carrito",carrito);
		return "redirect:/index";
	}
	
	//PRATICA 2
	// Método para realizar el pago y registrar en la base de datos
	@GetMapping("/pagar")
	public String pagar(Model model) {
	    List<Detalle> carrito = (List<Detalle>) model.getAttribute("carrito");
	    double subtotal = calcularTotal(carrito);
	    double precioEnvio = calcularPrecioEnvio(subtotal);
	    double descuento = calcularDescuento(subtotal);
	    double total = subtotal + precioEnvio - descuento;

	    Venta venta = new Venta();
	    venta.setFechaRegistro(new Date());
	    venta.setMontoTotal(total);
	    
	    // Guardar la venta en la tabla Venta de la BD
	    Venta ventaGuardada = ventaRepository.save(venta);

	    // Asociar detalles de venta (productos) a la venta guardada
	    for (Detalle detalle : carrito) {
	        detalle.setVenta(ventaGuardada); // Asociar detalle con la venta guardada
	        detalleRepository.save(detalle); // Guardar detalle en la base de datos
	    }

	    // Limpiar el carrito después de guardar los detalles
	    carrito.clear();

	    // Actualizar los valores del carrito en el modelo
	    model.addAttribute("total", 0.0);
	    model.addAttribute("carrito", carrito);

	    // Redirigir a algún lugar, por ejemplo, a una página de confirmación
	    return "mensaje";
	}

		
	// Método para actualizar el carrito		
		@GetMapping("/actualizarCarrito")
		public String actualizarCarrito(Model model) {
		    List<Detalle> carrito = (List<Detalle>)model.getAttribute("carrito");
		    double subtotal = 0.0;
		    double total = 0.0;
		    double precioEnvio = 0.0;
		    double descuento = 0.0;
		    
		    for (Detalle d : carrito) subtotal += d.getSubtotal();
		    
		    if (subtotal != 0) {
		        precioEnvio = calcularPrecioEnvio(subtotal);
		        descuento = calcularDescuento(subtotal);
		        
		        total = subtotal + precioEnvio - descuento;
		    }

		    model.addAttribute("total", total);
		    model.addAttribute("precioEnvio", precioEnvio);
		    model.addAttribute("descuento", descuento);

		    return "carrito";
			}

    //Método para eliminar un producto del Carrito  
    @GetMapping("/eliminar/{IdProducto}")
    public String eliminarDelCarrito(@PathVariable(name = "IdProducto", required = true) int IdProducto, Model model) {
        List<Detalle> carrito = (List<Detalle>) model.getAttribute("carrito");

        // Eliminar el producto del carrito correspondiente
        carrito.removeIf(detalle -> detalle.getProducto().getIdProducto() == IdProducto);

        // Recalcular los montos despues de eliminar un producto
        double total = calcularTotal(carrito);
        double precioEnvio = calcularPrecioEnvio(total);
        double descuento = calcularDescuento(total);

        model.addAttribute("total", total);
        model.addAttribute("precioEnvio", precioEnvio);
        model.addAttribute("descuento", descuento);
        return "carrito";
    }
}


