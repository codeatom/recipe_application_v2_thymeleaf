package recipe_application.application.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import recipe_application.application.data.service.RecipeService;


@Controller
@RequestMapping("/home")
public class HomeController {

    private final RecipeService recipeService;

    public HomeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping({"", "/index"})
    public String index(){
        return "home/index";
    }

    @GetMapping("/about")
    public String about(){
        return "home/about";
    }

}
