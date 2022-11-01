package recipe_application.application.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import recipe_application.application.data.service.RecipeService;


@Controller
@RequestMapping("/user")
public class UserController {

    private final RecipeService recipeService;

    public UserController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping("/list")
    public String getRecipeList(Model model) {
        model.addAttribute("recipeList", recipeService.findAll());

        return "user/recipe_collection";
    }

    @GetMapping("/get/{id}")
    public String getRecipeById(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("recipe", recipeService.findById(id));

        return "user/recipe";
    }
}
