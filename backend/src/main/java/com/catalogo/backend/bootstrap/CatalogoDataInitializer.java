package com.catalogo.backend.bootstrap;

import com.catalogo.backend.entity.Catalogo;
import com.catalogo.backend.entity.Categoria;
import com.catalogo.backend.entity.Producto;
import com.catalogo.backend.entity.Rol;
import com.catalogo.backend.entity.Stock;
import com.catalogo.backend.entity.Usuario;
import com.catalogo.backend.entity.UsuarioRol;
import com.catalogo.backend.enums.TipoProducto;
import com.catalogo.backend.repository.CatalogoRepository;
import com.catalogo.backend.repository.CategoriaRepository;
import com.catalogo.backend.repository.ProductoRepository;
import com.catalogo.backend.repository.RolRepository;
import com.catalogo.backend.repository.StockRepository;
import com.catalogo.backend.repository.UsuarioRepository;
import com.catalogo.backend.repository.UsuarioRolRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Configuration
public class CatalogoDataInitializer {
    @Bean
    CommandLineRunner seedCatalogo(UsuarioRepository usuarios, RolRepository roles, UsuarioRolRepository usuariosRoles,
            CatalogoRepository catalogos, CategoriaRepository categorias, ProductoRepository productos,
            StockRepository stocks, @Value("${app.seed.admin-username:admin}") String adminUsername,
            @Value("${app.seed.admin-password:admin1234}") String adminPassword) {
        return args -> initialize(usuarios, roles, usuariosRoles, catalogos, categorias, productos, stocks,
                adminUsername, adminPassword);
    }

    @Transactional
    void initialize(UsuarioRepository usuarios, RolRepository roles, UsuarioRolRepository usuariosRoles,
            CatalogoRepository catalogos, CategoriaRepository categorias, ProductoRepository productos,
            StockRepository stocks, String adminUsername, String adminPassword) {
        Rol adminRole = roles.findAll().stream().filter(role -> "ADMIN".equalsIgnoreCase(role.getNombre())).findFirst()
                .orElseGet(() -> {
                    Rol role = new Rol();
                    role.setNombre("ADMIN");
                    role.setDescripcion("Administra catalogos, productos, stock e imagenes.");
                    return roles.save(role);
                });
        Usuario admin = usuarios.findByNombreUsuarioAndActivoTrue(adminUsername).orElseGet(() -> {
            Usuario user = new Usuario();
            user.setNombreUsuario(adminUsername);
            user.setContrasena(adminPassword);
            user.setActivo(true);
            return usuarios.save(user);
        });
        admin.setContrasena(adminPassword);
        admin.setActivo(true);
        usuarios.save(admin);
        if (!usuariosRoles.findAll().stream().anyMatch(link -> link.getUsuario().getId().equals(admin.getId())
                && link.getRol().getId().equals(adminRole.getId()))) {
            UsuarioRol link = new UsuarioRol();
            link.setUsuario(admin);
            link.setRol(adminRole);
            usuariosRoles.save(link);
        }
        Catalogo puertas = findOrCreateCatalogo(catalogos, "Catalogo Digital - Puertas",
                "Puertas de acceso e interiores.");
        Catalogo ventanas = findOrCreateCatalogo(catalogos, "Catalogo Digital - Ventanas",
                "Ventanas de aluminio y vidrio.");
        Categoria puertasCategory = findOrCreateCategoria(categorias, "Puertas", puertas,
                "Soluciones de acceso e interiores.");
        Categoria ventanasCategory = findOrCreateCategoria(categorias, "Ventanas", ventanas,
                "Soluciones de iluminación y ventilación.");
        for (int index = 1; index <= 5; index++) {
            createProductIfMissing(productos, stocks, "PUERTA-" + String.format("%02d", index),
                    "Puerta de diseño " + index, puertas, puertasCategory, TipoProducto.PUERTA, 10);
            createProductIfMissing(productos, stocks, "VENTANA-" + String.format("%02d", index),
                    "Ventana de diseño " + index, ventanas, ventanasCategory, TipoProducto.VENTANA, 10);
        }
    }

    private Catalogo findOrCreateCatalogo(CatalogoRepository repository, String name, String description) {
        return repository.findAll().stream().filter(item -> name.equals(item.getNombre())).findFirst().orElseGet(() -> {
            Catalogo catalogo = new Catalogo();
            catalogo.setNombre(name);
            catalogo.setDescripcion(description);
            return repository.save(catalogo);
        });
    }

    private Categoria findOrCreateCategoria(CategoriaRepository repository, String name, Catalogo catalogo,
            String description) {
        return repository.findAll().stream()
                .filter(item -> name.equals(item.getNombre()) && item.getCatalogo().getId().equals(catalogo.getId()))
                .findFirst().orElseGet(() -> {
                    Categoria categoria = new Categoria();
                    categoria.setNombre(name);
                    categoria.setDescripcion(description);
                    categoria.setActiva(true);
                    categoria.setCatalogo(catalogo);
                    return repository.save(categoria);
                });
    }

    private void createProductIfMissing(ProductoRepository productos, StockRepository stocks, String code, String name,
            Catalogo catalogo, Categoria categoria, TipoProducto type, int available) {
        if (productos.findAll().stream().anyMatch(item -> code.equals(item.getCodigo())))
            return;
        Producto producto = new Producto();
        producto.setCodigo(code);
        producto.setNombre(name);
        producto.setDescripcion("Producto de la familia " + type.name().toLowerCase() + ".");
        producto.setPrecio(BigDecimal.ZERO);
        producto.setCatalogo(catalogo);
        producto.setCategoria(categoria);
        producto.setTipoProducto(type);
        Producto saved = productos.save(producto);
        Stock stock = new Stock();
        stock.setProducto(saved);
        stock.setCantidadDisponible(available);
        stock.setCantidadMinima(1);
        stock.setPermiteBajoPedido(true);
        stocks.save(stock);
    }
}
