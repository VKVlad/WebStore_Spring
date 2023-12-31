package khpi.kvp.webstore_spring.controllers;

import jakarta.servlet.http.HttpSession;
import khpi.kvp.webstore_spring.configuration.CustomUserDetails;
import khpi.kvp.webstore_spring.dto.OrderDTO;
import khpi.kvp.webstore_spring.services.CartService;
import khpi.kvp.webstore_spring.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CartController {
    @Autowired
    CartService cartService;
    @Autowired
    CategoryService categoryService;

    @PostMapping("/addToCart/{id}")
    public String addToCart(@PathVariable Long id, @RequestParam int quantity, HttpSession session) {
        cartService.addItem(session, id, quantity);
        return "redirect:/shop";
    }

    @GetMapping("/cart")
    public String cartGet(Model model, HttpSession session) {
        model.addAttribute("cartCount", cartService.getCartSize(session));
        model.addAttribute("total", cartService.calculateTotal(session));
        model.addAttribute("cart", cartService.getCart(session));
        model.addAttribute("categories", categoryService.getAll());
        return "cart";
    }

    @GetMapping("/cart/removeItem/{id}")
    public String removeItem(@PathVariable Long id, HttpSession session) {
        cartService.removeItem(session, id);
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String checkout(Model model, HttpSession session, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getPrincipal() instanceof DefaultOidcUser oidcUser) {
                OrderDTO orderDTO = new OrderDTO();
                orderDTO.setFirstName(oidcUser.getGivenName());
                orderDTO.setLastName(oidcUser.getFamilyName());
                orderDTO.setEmail(oidcUser.getEmail());
                model.addAttribute("orderDTO", orderDTO);
                model.addAttribute("cartCount", cartService.getCartSize(session));
                model.addAttribute("categories", categoryService.getAll());
                return "checkout";
            } else if (authentication.getPrincipal() instanceof CustomUserDetails user) {
                OrderDTO orderDTO = new OrderDTO();
                orderDTO.setFirstName(user.getFirstName());
                orderDTO.setLastName(user.getLastName());
                orderDTO.setEmail(user.getEmail());
                model.addAttribute("orderDTO", orderDTO);
                model.addAttribute("cartCount", cartService.getCartSize(session));
                model.addAttribute("categories", categoryService.getAll());
                return "checkout";
            }
        }
        return "redirect:/login";
    }
    @PostMapping("/cart/updateQuantity/{id}")
    public String updateQuantity(@PathVariable Long id, @RequestParam int newQuantity, HttpSession session) {
        cartService.updateQuantity(session, id, newQuantity);
        return "redirect:/cart";
    }
}

